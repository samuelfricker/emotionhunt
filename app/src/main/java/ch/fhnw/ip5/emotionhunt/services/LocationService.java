package ch.fhnw.ip5.emotionhunt.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import ch.fhnw.ip5.emotionhunt.models.GPSTracker;


/**
 * Created by dimitri on 28.11.2016.
 */

public class LocationService extends IntentService {
    private static final String TAG = LocationService.class.getSimpleName();
    private GPSTracker gpsTracker;

    public LocationService(){
        super("location-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent");

        WakefulBroadcastReceiver.completeWakefulIntent(intent);

        gpsTracker = new GPSTracker(getApplicationContext());
    }

}
