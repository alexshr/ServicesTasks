package com.otus.alexshr.tasks.geo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.otus.alexshr.tasks.Config.GEO_EXPIRATION_DURATION;
import static com.otus.alexshr.tasks.Config.GEO_FASTEST_INTERVAL;
import static com.otus.alexshr.tasks.Config.GEO_INTERVAL;
import static com.otus.alexshr.tasks.Config.GEO_PRIORITY;

public class LocationHelper {

    private FusedLocationProviderClient locationClient;
    private LocationRequest locationRequest;

    public LocationHelper(Context context) {
        locationClient = LocationServices.getFusedLocationProviderClient(context);
        locationRequest = LocationRequest
                .create()
                .setPriority(GEO_PRIORITY)
                .setInterval(GEO_INTERVAL)
                .setFastestInterval(GEO_FASTEST_INTERVAL)
                .setExpirationDuration(GEO_EXPIRATION_DURATION);
    }

    @SuppressLint("MissingPermission")
    public void requestUpdates(LocationCallback callback) {
        locationClient.requestLocationUpdates(locationRequest, callback, Looper.myLooper());
    }

    public void removeUpdates(LocationCallback callback) {
        locationClient.removeLocationUpdates(callback);
    }
}