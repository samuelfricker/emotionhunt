package ch.fhnw.ip5.emotionhunt.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Helper class for android permissions
 */
public abstract class PermissionHelper {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    /**
     * Checks the permissions that are required by any location request
     * @param context
     * @return
     */
    public static boolean checkLocationPermission (Context context) {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

}
