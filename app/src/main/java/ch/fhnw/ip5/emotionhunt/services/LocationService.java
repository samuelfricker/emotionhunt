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
    private static final long REPEAT_TIME = 3 * 1000;
    private GPSTracker GPSTracker;

    public LocationService(){
        super("location-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        GPSTracker = new GPSTracker(this);

        Log.i(TAG, "on HandleIntentd");
        while (true) {
            getCurrentLocation();
            try{
                Thread.sleep(REPEAT_TIME);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets current Location into slq lite db
     */

    private void getCurrentLocation() {
        Log.i(TAG, "Gets current Location");
        //GPSTracker.updateLocation();
    }


}
