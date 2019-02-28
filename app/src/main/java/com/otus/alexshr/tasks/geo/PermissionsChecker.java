package com.otus.alexshr.tasks.geo;

import android.app.Activity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by alexshr on 20.02.2019.
 */
public class PermissionsChecker {
    private int requestCode;
    private String[] permissions;

    public PermissionsChecker(int requestCode, String... permissions) {
        this.requestCode = requestCode;
        this.permissions = permissions;
    }

    public boolean check(Activity activity) {
        String[] missingPerms = getMissingPermissions(activity);
        if (missingPerms.length != 0) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
        return missingPerms.length == 0;
    }

    private String[] getMissingPermissions(Activity activity) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : permissions) {
            if (!hasPermission(activity, perm)) {
                result.add(perm);
            }
        }

        return (result.toArray(new String[result.size()]));
    }

    private boolean hasPermission(Activity activity, String perm) {
        return (ContextCompat.checkSelfPermission(activity, perm) ==
                PERMISSION_GRANTED);
    }

    public String getMissingPermissionsString(@NonNull String[] permissions, @NonNull int[] grantResults) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[0] != PERMISSION_GRANTED) {
                sb.append(" " + permissions[i]);
            }
        }
        return sb.toString();
    }
}
