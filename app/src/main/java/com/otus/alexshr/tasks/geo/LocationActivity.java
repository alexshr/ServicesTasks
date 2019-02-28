package com.otus.alexshr.tasks.geo;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.otus.alexshr.tasks.R;
import com.otus.alexshr.tasks.databinding.ActivityLocationBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import timber.log.Timber;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

public class LocationActivity extends AppCompatActivity {

    private final static int LOCATION_REQUEST_CODE = 101;
    private final static int GOOGLE_SERVICES_REQUEST_CODE = 102;
    private static final int PERMISSIONS_REQUEST_CODE = 103;

    private ActivityLocationBinding binding;

    private LocationProviderChecker locationProviderChecker = new LocationProviderChecker(LOCATION_REQUEST_CODE, GPS_PROVIDER, NETWORK_PROVIDER);
    private GoogleServicesChecker googleServicesChecker = new GoogleServicesChecker(GOOGLE_SERVICES_REQUEST_CODE);
    private PermissionsChecker permissionsChecker = new PermissionsChecker(PERMISSIONS_REQUEST_CODE, ACCESS_FINE_LOCATION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location);

        syncUI();
        binding.serviceSw.setOnCheckedChangeListener((buttonView, isOn) -> {
            //if (!isChecking) {
            if (isOn) checkAndStart();
            else LocationService.stop(getApplicationContext());
            //}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void checkAndStart() {

        if (googleServicesChecker.check(
                this, dialog -> {
                    syncUI();
                    showToast(R.string.google_services_required);
                })
                && permissionsChecker.check(this)
                && locationProviderChecker.check(
                this,
                res -> checkAndStart(),
                eMes -> {
                    syncUI();
                    showToast(eMes);
                })) {
            LocationService.start(getApplicationContext());
        } else syncUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            checkAndStart();
        } else {
            syncUI();
            if (requestCode == googleServicesChecker.getRequestCode())
                showToast(R.string.google_services_required);
            else if (requestCode == locationProviderChecker.getRequestCode())
                showToast(R.string.location_provider_required);
            else Timber.e("unknown requestCode: %d", requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        String missingPermissions = permissionsChecker.getMissingPermissionsString(permissions, grantResults);
        if (missingPermissions.isEmpty()) checkAndStart();
        else {
            showToast(R.string.lack_permissions, missingPermissions);
            syncUI();
        }
    }

    private void syncUI() {
        binding.serviceSw.setChecked(isServiceRunning());
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        boolean isRunning = false;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                isRunning = true;
            }
        }
        Timber.d("isRunning=%b", isRunning);
        return isRunning;
    }

    private void showToast(@StringRes int res) {
        Toast.makeText(this, res, Toast.LENGTH_LONG).show();
    }

    private void showToast(@StringRes int res, String args) {
        String text = getString(res, args);
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
