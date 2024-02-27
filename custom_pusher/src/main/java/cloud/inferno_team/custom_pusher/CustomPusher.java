package cloud.inferno_team.custom_pusher;

import static cloud.inferno_team.custom_pusher.PusherUtil.pusher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;

import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.PresenceChannelEventListener;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;

import cloud.inferno_team.custom_pusher.exceptions.CustomPusherAlreadySubscriptedException;
import cloud.inferno_team.custom_pusher.exceptions.CustomPusherInitException;
import cloud.inferno_team.custom_pusher.exceptions.CustomPusherNotConnectedException;
import cloud.inferno_team.custom_pusher.exceptions.CustomPusherNotInitException;

public class CustomPusher {
    private static CustomPusher instance;

    private CustomPusher(Context context) throws CustomPusherInitException {
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo =
                        packageManager.getApplicationInfo(context.getPackageName(),
                                PackageManager.GET_META_DATA);
                if (applicationInfo.metaData != null) {
                    String key = applicationInfo.metaData.getString(Constants.KEY_META, null);
                    String cluster = applicationInfo.metaData.getString(Constants.CLUSTER_META, null);
                    String host = applicationInfo.metaData.getString(Constants.HOST_META, null);
                    int port = applicationInfo.metaData.getInt(Constants.PORT_META, 6001);
                    boolean useTLS = applicationInfo.metaData.getBoolean(Constants.USE_TLS_META, false);
                    if (key == null || cluster == null || host == null)
                        throw new CustomPusherInitException("key , cluster , " +
                                "host must be defined in Android Manifest.");
                    PusherUtil.init(key, host, port, cluster, useTLS);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static CustomPusher init(Context context) throws CustomPusherInitException {
        if (instance == null)
            instance = new CustomPusher(context);
        return instance;
    }

    public void connect(@Nullable ConnectionEventListener listener,
                        @Nullable ConnectionState... connectionStates)
            throws CustomPusherNotInitException {
        if (pusher == null)
            throw new CustomPusherNotInitException("CustomPusher.init(Context) must be called" +
                    "before calling connect method.");
        pusher.connect(listener, connectionStates);
    }

    public boolean isConnected() {
        return pusher.getConnection().getState() == ConnectionState.CONNECTED;
    }

    public boolean isListening(String channelName)
            throws CustomPusherNotConnectedException {
        if (pusher.getConnection().getState() != ConnectionState.CONNECTED)
            throw new CustomPusherNotConnectedException("pusher must be " +
                    "connected before listen to channel.");
        return pusher.getChannel(channelName).isSubscribed();
    }

    public void listenPresence(String channelName, String eventName,
                               PresenceChannelEventListener listener)
            throws CustomPusherNotConnectedException {
        if (pusher.getConnection().getState() != ConnectionState.CONNECTED)
            throw new CustomPusherNotConnectedException("pusher must be " +
                    "connected before listen to channel.");
        Channel channel = pusher.subscribePresence(channelName);
        listenToChannel(channel, eventName, listener);
    }

    public void listenPrivate(String channelName, String eventName,
                              PrivateChannelEventListener listener)
            throws CustomPusherNotConnectedException {
        if (pusher.getConnection().getState() != ConnectionState.CONNECTED)
            throw new CustomPusherNotConnectedException("pusher must be " +
                    "connected before listen to channel.");
        Channel channel = pusher.subscribePrivate(channelName);
        listenToChannel(channel, eventName, listener);
    }


    public void listenPublic(String channelName, String eventName,
                             ChannelEventListener listener)
            throws CustomPusherNotConnectedException, CustomPusherAlreadySubscriptedException {
        if (pusher.getConnection().getState() != ConnectionState.CONNECTED)
            throw new CustomPusherNotConnectedException("pusher must be " +
                    "connected before listen to channel.");
        Channel channel = pusher.getChannel(channelName);
        if (channel != null) {
            if (pusher.getChannel(channelName).isSubscribed()) {
                throw new CustomPusherAlreadySubscriptedException
                        (String.format("%s already subscribed", channelName));
            }
        } else {
            channel = pusher.subscribe(channelName,listener,eventName);
//            listenToChannel(channel, eventName, listener);
        }
    }


    private void listenToChannel(Channel channel, String eventName,
                                 ChannelEventListener listener) {
        channel.bind(eventName, listener);
//        pusher.disconnect();
//        pusher.connect();
    }

    public void unListen(String channelName)
            throws CustomPusherNotConnectedException {
        if (pusher.getConnection().getState() != ConnectionState.CONNECTED)
            throw new CustomPusherNotConnectedException("pusher must be " +
                    "connected before listen to channel.");
        pusher.unsubscribe(channelName);
    }
}
