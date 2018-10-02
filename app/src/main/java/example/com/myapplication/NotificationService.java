package example.com.myapplication;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationService extends Service {

    private static final String TAG = NotificationService.class.getName();
    private static final String CHANNEL_ID = "muz_media_playback_channel";
    private static final String CHANNEL_NAME = "MUZ Media Playback";
    private static NotificationManager manager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        buildNotification();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e(TAG, "onTaskRemoved");
        cancelNotification();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        cancelNotification();
        super.onDestroy();
    }

    private void buildNotification() {

        Log.e(TAG, "buildNotification");

        createChannel();
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        intent.putExtra("stop", 0);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);

        Intent nowPlayingIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_NO_CREATE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(clickIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle("MUZ.UZ")
                .setContentText("ContentText")
                .setSubText("SubText")
                .setDeleteIntent(pendingIntent)
                .setOngoing(true)
                .setChannelId(CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setColorized(true);
        }
        manager.notify(1, builder.build());
    }

    @SuppressLint("NewApi")
    private void createChannel() {
        Log.e(TAG, "createChannel");


        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Media Playback Controls");
        channel.setShowBadge(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    private void cancelNotification() {

        Log.d(TAG, "cancelNotification");

        if (manager != null) {
            manager.cancel(1);
            manager.cancelAll();
        }

        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        stopService(intent);
    }
}
