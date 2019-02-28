package com.otus.alexshr.tasks.geo;

import android.app.Activity;
import android.content.DialogInterface;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by alexshr on 21.02.2019.
 */
public class GoogleServicesChecker {
    private int requestCode;

    public GoogleServicesChecker(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public boolean check(Activity activity, DialogInterface.OnCancelListener onCancelListener) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, requestCode, onCancelListener)
                        .show();
            }
            return false;
        }
        return true;
    }
}
