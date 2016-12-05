package ch.fhnw.ip5.emotionhunt.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import ch.fhnw.ip5.emotionhunt.models.ReceivedExperience;

public class ApiService extends Service {
    private static final String TAG = ApiService.class.getSimpleName();
    private static final long REPEAT_TIME = 15 * 1000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Context context = getApplicationContext();
        new Thread(new Runnable(){
            public void run() {
                while(true) {
                    try {
                        loadExperiences(context);
                        Thread.sleep(REPEAT_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    /**
     * Load experiences into sql lite db.
     */
    private void loadExperiences(Context context) {
        Log.i(TAG, "load experiences");
        ReceivedExperience.loadExperiencesFromApi(context);
    }
}
