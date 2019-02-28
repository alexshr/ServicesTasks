package com.otus.alexshr.tasks.battery;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.BatteryManager;
import android.os.IBinder;

import com.orhanobut.hawk.Hawk;
import com.otus.alexshr.tasks.Constants;
import com.otus.alexshr.tasks.R;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import static androidx.core.app.NotificationCompat.DEFAULT_SOUND;
import static androidx.core.app.NotificationCompat.DEFAULT_VIBRATE;
import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static androidx.core.app.NotificationCompat.PRIORITY_LOW;
import static com.otus.alexshr.tasks.Config.BATTERY_LOW_LEVEL_DEFAULT;
import static com.otus.alexshr.tasks.Constants.BATTERY_LOW_LEVEL_KEY;
import static com.otus.alexshr.tasks.Constants.BATTERY_NORMAL_CHANNEL_ID;
import static com.otus.alexshr.tasks.Constants.BATTERY_NOTIFICATION_ID;
import static com.otus.alexshr.tasks.Constants.BATTERY_URGENT_CHANNEL_ID;
import static com.otus.alexshr.tasks.Utils.drawableToBitmap;
import static timber.log.Timber.d;

/**
 *
 */
public class BatteryService extends Service {

    public static final String ACTION_STOP = "ACTION_STOP";
    private Bitmap largeIconBitmap;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            d("started");
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            float batteryPct = level / (float) scale;
            int value = (int) (batteryPct * 100);

            boolean isLow = value <= Hawk.get(BATTERY_LOW_LEVEL_KEY, BATTERY_LOW_LEVEL_DEFAULT);

            boolean isUrgent = isLow && !isCharging;

            d("level=%d, scale=%d, value=%d, isCharging=%b, isUrgent=%b", level, scale, value, isCharging, isUrgent);

            sendNotification(buildBatteryNotification(value, isLow, isCharging, isUrgent));
            d("finished");
        }
    };

    public static void start(Context context) {
        Intent intent = new Intent(context, BatteryService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, BatteryService.class);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        largeIconBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.ic_battery_alert));

        startForeground(BATTERY_NOTIFICATION_ID, buildStartNotification());

        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        d(" ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        d(" ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_STOP.equals(intent.getAction())) {
            d("called to cancel service");
            cancelNotification(BATTERY_NOTIFICATION_ID);
            stopSelf();
        }
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification buildStartNotification() {
        d(" ");
        NotificationCompat.Builder b =
                new NotificationCompat.Builder(this, BATTERY_NORMAL_CHANNEL_ID);

        b.setAutoCancel(false)
                .setContentTitle(getString(R.string.battery_notification_title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getString(R.string.service_start));

        return (b.build());
    }

    private Notification buildBatteryNotification(int level, boolean isLow, boolean isCharging, boolean isUrgent) {
        d("level=%s%%, isLow=%b, isCharging=%b, isUrgent=%b", level, isLow, isCharging, isUrgent);

        String text = getString(R.string.battery_level, level);
        if (isLow) text += "   " + getString(R.string.battery_low_level);
        if (isCharging) text += "   " + getString(R.string.battery_charging);

        NotificationCompat.Builder b =
                new NotificationCompat.Builder(this, isUrgent ? BATTERY_URGENT_CHANNEL_ID : BATTERY_NORMAL_CHANNEL_ID);

        b.setPriority(isUrgent ? PRIORITY_HIGH : PRIORITY_LOW);

        b.setOngoing(true)
                .setAutoCancel(false)
                .setTicker(getString(R.string.battery_notification_title))
                .setContentTitle(getString(R.string.battery_notification_title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(text);

        if (isUrgent) {
            b.setLargeIcon(largeIconBitmap);
            b.setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE);
        }

        b.setContentIntent(getActivityPI());
        b.addAction(R.drawable.ic_settings_black_24dp, getString(R.string.settings), getActivityPI());
        b.addAction(R.drawable.ic_cancel_black_24dp, getString(R.string.cancel), getStopPI());

        return b.build();
    }

    private PendingIntent getStopPI() {
        Intent intent = new Intent(this, BatteryService.class);
        intent.setAction(ACTION_STOP);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent getActivityPI() {
        Intent resultIntent = new Intent(this, BatteryActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendNotification(Notification notification) {
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(Constants.BATTERY_NOTIFICATION_ID, notification);
    }

    private void cancelNotification(int id) {
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(id);
    }
}
