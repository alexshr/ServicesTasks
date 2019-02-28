package com.otus.alexshr.tasks;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.orhanobut.hawk.Hawk;

import androidx.annotation.NonNull;
import timber.log.Timber;

import static com.otus.alexshr.tasks.Constants.BATTERY_NORMAL_CHANNEL_ID;
import static com.otus.alexshr.tasks.Constants.BATTERY_URGENT_CHANNEL_ID;
import static com.otus.alexshr.tasks.Constants.GEO_CHANNEL_ID;

/**
 * Created by alexshr on 09.02.2019.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initTimber();
        initNotificationManager();
        initSettings();
    }

    private void initNotificationManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager mgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel(BATTERY_NORMAL_CHANNEL_ID,
                    getString(R.string.battery_normal_channel_name), NotificationManager.IMPORTANCE_LOW);
            mgr.createNotificationChannel(channel);

            //separate channel is needed for separate behaviour in newest android versions
            channel = new NotificationChannel(BATTERY_URGENT_CHANNEL_ID,
                    getString(R.string.battery_urgent_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            mgr.createNotificationChannel(channel);

            channel = new NotificationChannel(GEO_CHANNEL_ID,
                    getString(R.string.geo_channel_name), NotificationManager.IMPORTANCE_LOW);
            mgr.createNotificationChannel(channel);
        }
    }

    private void initTimber() {

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @SuppressLint("DefaultLocale")
                @Override
                protected String createStackElementTag(@NonNull StackTraceElement element) {
                    return String.format("timber %s: %s:%d (%s)",
                            super.createStackElementTag(element),
                            element.getMethodName(),
                            element.getLineNumber(),
                            Thread.currentThread().getName());
                }
            });
        }
    }

    private void initSettings() {
        Hawk.init(this).build();
    }
}
