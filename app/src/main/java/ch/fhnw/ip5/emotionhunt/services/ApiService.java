package ch.fhnw.ip5.emotionhunt.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import ch.fhnw.ip5.emotionhunt.models.ReceivedExperience;

public class ApiService extends IntentService {
    private static final String TAG = ApiService.class.getSimpleName();
    private static final long REPEAT_TIME = 15 * 1000;

    public ApiService() {
        super("api-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        Log.i(TAG, "onHandleIntent");
        while (true) {
            loadExperiences();
            try {
                Thread.sleep(REPEAT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load experiences into sql lite db.
     */
    private void loadExperiences() {
        Log.i(TAG, "load experiences");
        ReceivedExperience.loadExperiencesFromApi(getApplicationContext());
    }
}
