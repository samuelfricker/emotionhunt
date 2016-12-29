package ch.fhnw.ip5.emotionhunt.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import ch.fhnw.ip5.emotionhunt.helpers.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.models.ReceivedExperience;

public class ApiService extends Service {
    private static final String TAG = ApiService.class.getSimpleName();
    private static final long REPEAT_TIME = 15 * 1000;
    private static final long REPEAT_TIME_FAIL_SOFT = 1 * 60 * 1000;
    private static final long REPEAT_TIME_FAIL_HARD = 5 * 60 * 1000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Context context = getApplicationContext();
        new Thread(new Runnable(){
            public void run() {
                int connFailCount = 0;
                while(true) {
                    try {
                        boolean isNetworkAvailable = DeviceHelper.isNetworkAvailable(context);
                        if (isNetworkAvailable) {
                            Log.d(TAG, "Api Call. Load experiences from API.");
                            connFailCount = 0;
                            ReceivedExperience.loadExperiencesFromApi(context);
                        } else {
                            Log.d(TAG, "No network. No Api Call. Sleep.");
                            if (++connFailCount < 10) {
                                Thread.sleep(REPEAT_TIME_FAIL_SOFT);
                            } else {
                                Thread.sleep(REPEAT_TIME_FAIL_HARD);
                            }
                        }
                        Thread.sleep(REPEAT_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}
