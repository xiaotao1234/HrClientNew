package com.huari.tools;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionManager {
    private static PermissionManager permissionManager;


    private PermissionManager() {
    }

    public static PermissionManager getInastance() {
        if (permissionManager == null) {
            permissionManager = new PermissionManager();
        }
        return permissionManager;
    }

    public void requestPermission(Activity activity, String permission, int code, callBack callBack) {
        if (ContextCompat.checkSelfPermission(activity.getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, code);
        } else {
            callBack.doAfterGetPermission();
        }
    }

    public interface callBack {
        void doAfterGetPermission();
    }


}
