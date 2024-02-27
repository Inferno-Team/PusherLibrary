package cloud.inferno_team.custom_pusher.services;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import cloud.inferno_team.custom_pusher.Constants;
import cloud.inferno_team.custom_pusher.CustomPusher;
import cloud.inferno_team.custom_pusher.exceptions.CustomPusherAlreadySubscriptedException;
import cloud.inferno_team.custom_pusher.exceptions.CustomPusherInitException;
import cloud.inferno_team.custom_pusher.exceptions.CustomPusherNotConnectedException;
import cloud.inferno_team.custom_pusher.exceptions.CustomPusherNotInitException;
import cloud.inferno_team.custom_pusher.types.PusherEventType;
import cloud.inferno_team.custom_pusher.types.PusherPostData;

public class BackgroundServiceThread extends Thread {
    private CustomPusher pusher;
    private final Context context;
    private final MutableLiveData<PusherPostData<?>> pusherEventLiveData;

    public LiveData<PusherPostData<?>> getPusherEventLiveData() {
        return pusherEventLiveData;
    }

    public BackgroundServiceThread(Context context) {
        this.context = context;
        try {
            pusher = CustomPusher.init(context);
            pusher.connect(createPusherListener(), ConnectionState.ALL);

        } catch (CustomPusherInitException | CustomPusherNotInitException e) {
            Log.e("BackgroundServiceThread", "Exception: " + e.getMessage());
        }

        this.pusherEventLiveData = new MutableLiveData<>();
    }

    @Override
    public void run() {
        super.run();
        while (!Thread.interrupted()) {
            try {
                Thread.sleep((long) (1e3 * 60 * 5));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                if (!pusher.isConnected()) {
                    pusher.connect(createPusherListener(), ConnectionState.ALL);
                }
            } catch (Exception e) {
                // Log and handle the exception gracefully
                Log.e("BackgroundServiceThread", "Exception: " + e.getMessage());
            }

        }
    }

    private ChannelEventListener getListener() {
        return new ChannelEventListener() {
            @Override
            public void onSubscriptionSucceeded(String channelName) {
                pusherEventLiveData.postValue(
                        new PusherPostData<>(channelName, PusherEventType.CHANNEL_SUBSCRIBED)
                );

//                Toast.makeText(PusherService.this, channelName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEvent(PusherEvent event) {
                pusherEventLiveData.postValue(
                        new PusherPostData<>(event, PusherEventType.NEW_EVENT)
                );
//                Toast.makeText(PusherService.this, event.getEventName(), Toast.LENGTH_SHORT).show();
            }

        };
    }

    private ConnectionEventListener createPusherListener() {
        return new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                if (change.getCurrentState() == ConnectionState.CONNECTED) {
                    try {
                        if (pusher.isConnected()) {
                            pusher.listenPublic(resolveChannelName(),
                                    "App\\Events\\notification\\SendNotificationEvent",
                                    getListener());
                        } else {
                            pusherEventLiveData.postValue(
                                    new PusherPostData<>("Can't subscribe to channel E"
                                            , PusherEventType.CANT_SUBSCRIBE)
                            );
                        }
                    } catch (CustomPusherNotConnectedException |
                             CustomPusherAlreadySubscriptedException e) {
                        Log.e(getClass().getName(), e.getMessage());
                    }
                }
            }

            @Override
            public void onError(String message, String code, Exception e) {

            }
        };
    }

    private String resolveChannelName() {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            ApplicationInfo info;
            try {
                info = packageManager.getApplicationInfo(context.getPackageName(),
                        PackageManager.GET_META_DATA);
                String packageId = context.getPackageName();
                String projectId = info.metaData.getString(Constants.PROJECT_ID_META, "");
                return projectId + "-" + packageId;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(this.getClass().getName(), e.getMessage());
            }
        }
        return "";
    }
}
