package ch.fhnw.ip5.emotionhunt.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import ch.fhnw.ip5.emotionhunt.helper.PermissionHelper;
import ch.fhnw.ip5.emotionhunt.services.ApiService;
import ch.fhnw.ip5.emotionhunt.services.LocationService;

public class BootBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, ApiService.class);
        startWakefulService(context, startServiceIntent);

        if (PermissionHelper.checkLocationPermission(context)) {
            startServiceIntent = new Intent(context, LocationService.class);
            startWakefulService(context, startServiceIntent);
        }
    }
}
