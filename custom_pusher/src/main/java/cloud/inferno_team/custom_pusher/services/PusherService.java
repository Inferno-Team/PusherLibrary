package cloud.inferno_team.custom_pusher.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.lifecycle.LifecycleService;

import com.bumptech.glide.Glide;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import cloud.inferno_team.custom_pusher.R;
import cloud.inferno_team.custom_pusher.types.NotificationObject;
import cloud.inferno_team.custom_pusher.types.PusherEventType;
import cloud.inferno_team.custom_pusher.types.PusherPostData;

public class PusherService extends LifecycleService {
    private BackgroundServiceThread thread;
    private final static int NOTIFICATION_ID = 101;
    private final static String CHANNEL_ID = "101";

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (thread == null || !thread.isAlive()) {
            thread = new BackgroundServiceThread(getApplicationContext());
            thread.start();
            thread.getPusherEventLiveData()
                    .observe(this, this::createListener);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startForeground(NOTIFICATION_ID, createNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST);


        }
        return START_STICKY;
    }


    private void createListener(PusherPostData<?> livedata) {
        if (livedata.getType() == PusherEventType.CHANNEL_SUBSCRIBED) {
//            Object o = livedata.getData();
//            if (o instanceof String)
//                Toast.makeText(this, o.toString(), Toast.LENGTH_SHORT).show();
        } else if (livedata.getType() == PusherEventType.NEW_EVENT) {
            if (livedata.getData() instanceof PusherEvent event) {
                JSONObject obj;
                try {
                    obj = new JSONObject(event.getData());
                    System.out.println(event.getData());
                    NotificationObject object = map2option(obj);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Notification notification = createEventNotification(object);
                            NotificationManager manager =
                                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            manager.notify(NOTIFICATION_ID, notification);
                            interrupt();
                        }
                    }.start();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private NotificationObject map2option(JSONObject obj) {
        return new NotificationObject(
                obj.optString("title", "No Title"),
                obj.optString("desc", "No Description"),
                obj.optString("url", "")
        );
    }


    protected Notification createNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "name", importance);
            channel.setDescription("description");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        // Create an explicit intent for an Activity in your app.
        Intent intent = new Intent(this, getApplicationContext().getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentTitle("My notification")
//                .setContentText("Hello World!")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that fires when the user taps the notification.
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return builder.build();
    }

    protected Notification createEventNotification(NotificationObject object) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "notification-channel", importance);
            channel.setDescription("new notification event");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        // Create an explicit intent for an Activity in your app.
        Intent intent = new Intent(this, getApplicationContext().getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_launcher_background)

                .setContentTitle(object.getTitle())
                .setContentText(object.getDescription())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that fires when the user taps the notification.
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        if (object.getImageUrl() != null &&
                !object.getImageUrl().isEmpty()) {
            try {
                Bitmap bitmap = Glide.with(this)
                        .asBitmap()
                        .load(object.getImageUrl())
                        .submit().get();
                IconCompat iconCompat = IconCompat.createWithBitmap(bitmap);
                builder.setSmallIcon(iconCompat);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            Context context = getApplicationContext();
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
                Drawable appIcon = packageManager.getApplicationIcon(applicationInfo);
                Bitmap iconBitmap = ((BitmapDrawable) appIcon).getBitmap();
                IconCompat iconCompat = IconCompat.createWithBitmap(iconBitmap);
                builder.setSmallIcon(iconCompat);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(this.getClass().getName(), e.toString());
            }
        }
        return builder.build();
    }

//    @Override
//    public void onDestroy() {
//
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction("restartservice");
//        broadcastIntent.setClass(this, Restarter.class);
//        this.sendBroadcast(broadcastIntent);
//        super.onDestroy();
//
//    }
//
//    @Override
//    public void onTaskRemoved(Intent rootIntent){
//        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//
//        PendingIntent restartServicePendingIntent = PendingIntent.
//                getService(getApplicationContext(), 1,
//                        restartServiceIntent, PendingIntent.FLAG_IMMUTABLE);
//        AlarmManager alarmService = (AlarmManager)
//                getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(
//                AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime() + 1000,
//                restartServicePendingIntent);
//
//        super.onTaskRemoved(rootIntent);
//    }
}
