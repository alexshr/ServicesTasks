package com.otus.alexshr.tasks.battery;

import android.app.ActivityManager;
import android.os.Bundle;

import com.orhanobut.hawk.Hawk;
import com.otus.alexshr.tasks.R;
import com.otus.alexshr.tasks.databinding.ActivityBatteryBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import static com.otus.alexshr.tasks.Config.BATTERY_LOW_LEVEL_DEFAULT;
import static com.otus.alexshr.tasks.Constants.BATTERY_LOW_LEVEL_KEY;
import static timber.log.Timber.d;

public class BatteryActivity extends AppCompatActivity {

    private ActivityBatteryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_battery);

        binding.levelNumberPicker.setValue(Hawk.get(BATTERY_LOW_LEVEL_KEY, BATTERY_LOW_LEVEL_DEFAULT));
        binding.levelNumberPicker.setListener((oldValue, newValue) -> Hawk.put(BATTERY_LOW_LEVEL_KEY, newValue));

        binding.serviceSw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) BatteryService.start(getApplicationContext());
            else BatteryService.stop(getApplicationContext());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.serviceSw.setChecked(isServiceRunning());
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        boolean isRunning = false;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BatteryService.class.getName().equals(service.service.getClassName())) {
                isRunning = true;
            }
        }
        d("isRunning=%b", isRunning);
        return isRunning;
    }
}
