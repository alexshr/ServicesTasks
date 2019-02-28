package com.otus.alexshr.tasks;

import com.google.android.gms.location.LocationRequest;

/**
 * Created by alexshr on 12.02.2019.
 */
public interface Config {
    int BATTERY_LOW_LEVEL_DEFAULT = 15;

    long GEO_INTERVAL = 1000 * 60;//interval at which you would like location to be computed (min interval)
    long GEO_FASTEST_INTERVAL = 1000 * 20;//fastest interval for location updates (max interval)
    long GEO_EXPIRATION_DURATION = 1000 * 60 * 60 * 2;//The location client will automatically stop updates after the request expires

    int GEO_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;//high resolution for testing
}
