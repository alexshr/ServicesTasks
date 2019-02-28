package com.otus.alexshr.tasks.geo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.otus.alexshr.tasks.Constants;
import com.otus.alexshr.tasks.R;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import static com.otus.alexshr.tasks.Constants.ACTION_STOP;
import static com.otus.alexshr.tasks.Constants.GEO_CHANNEL_ID;
import static com.otus.alexshr.tasks.Constants.GEO_NOTIFICATION_ID;
import static timber.log.Timber.d;

/**
 *
 */
public class LocationService extends Service {

    private LocationHelper locationHelper;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            new FetchAddressTask(LocationService.this,
                    mes -> sendNotification(buildNotification(mes)))
                    .execute(locationResult.getLastLocation());
        }
    };

    public static void start(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        d(" ");
        locationHelper = new LocationHelper(this);
        startForeground(GEO_NOTIFICATION_ID, buildNotification(getString(R.string.service_start)));
        locationHelper.requestUpdates(locationCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        d(" ");
        locationHelper.removeUpdates(locationCallback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_STOP.equals(intent.getAction())) {
            d("called to cancel service");
            cancelNotification(GEO_NOTIFICATION_ID);
            stopSelf();
        }
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification buildNotification(String message) {
        d("message: %s", message);

        NotificationCompat.Builder b =
                new NotificationCompat.Builder(this, GEO_CHANNEL_ID);

        b.setOngoing(true)
                .setAutoCancel(false)
                .setTicker(getString(R.string.geo_notification_title))
                .setContentTitle(getString(R.string.geo_notification_title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        b.setContentIntent(getActivityPI());

        b.addAction(R.drawable.ic_cancel_black_24dp, getString(R.string.cancel), getStopPI());

        return b.build();
    }

    private PendingIntent getStopPI() {
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(ACTION_STOP);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent getActivityPI() {
        Intent resultIntent = new Intent(this, LocationActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendNotification(Notification notification) {
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(Constants.GEO_NOTIFICATION_ID, notification);
    }

    private void cancelNotification(int id) {
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(id);
    }
}
