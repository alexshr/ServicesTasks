package com.otus.alexshr.tasks.geo;

import android.app.Activity;
import android.content.IntentSender;
import android.location.LocationManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.core.util.Consumer;
import timber.log.Timber;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by alexshr on 21.02.2019.
 */
public class LocationProviderChecker {
    private int requestCode;
    private String[] providers;

    public LocationProviderChecker(int requestCode, String... providers) {
        this.requestCode = requestCode;
        this.providers = providers;
    }

    public boolean check(Activity activity, OnSuccessListener<LocationSettingsResponse> onSuccessListener, Consumer<String> onErrorListener) {
        if (!hasLocationProviders(activity)) {
            promptUserToEnableLocation(activity, onSuccessListener, onErrorListener);
            return false;
        } else return true;
    }

    private boolean hasLocationProviders(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        for (String provider : providers) {
            if (!locationManager.isProviderEnabled(provider)) return false;
        }
        return true;
    }

    private void promptUserToEnableLocation(Activity activity, OnSuccessListener<LocationSettingsResponse> onSuccessListener, Consumer<String> onErrorListener) {
        LocationRequest locationRequest = LocationRequest
                .create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setFastestInterval(0);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        LocationServices
                .getSettingsClient(activity)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(onSuccessListener)

                .addOnFailureListener(ex -> {
                    int status = ((ApiException) ex).getStatusCode();
                    switch (status) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) ex;
                                resolvableApiException.startResolutionForResult(activity, requestCode);
                            } catch (IntentSender.SendIntentException exception) {
                                Timber.e(exception);
                                onErrorListener.accept(ex.toString());
                            }
                            break;
                    }
                });
    }

    public int getRequestCode() {
        return requestCode;
    }
}
