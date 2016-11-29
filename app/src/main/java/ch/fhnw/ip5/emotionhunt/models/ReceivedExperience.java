package ch.fhnw.ip5.emotionhunt.models;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.activities.MainActivity;
import ch.fhnw.ip5.emotionhunt.helper.DbHelper;
import ch.fhnw.ip5.emotionhunt.helper.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.tasks.RestExperienceListTask;
import ch.fhnw.ip5.emotionhunt.tasks.RestTask;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */

public class ReceivedExperience extends Experience {
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Returns a ReceivedExperience instance from the experience sql lite database.
     * @param context
     * @param id PK of the required experience entry
     * @return ReceivedExperience or null if no matching experience was found
     */
    public static ReceivedExperience findById(Context context, long id) {
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ExperienceDbContract.TABLE_NAME + " WHERE " +
                ExperienceDbContract.COL_ID + "=" + id, null);
        if(c.moveToFirst()){
            do{
                ReceivedExperience experience = new ReceivedExperience();
                experience = (ReceivedExperience) loadFromCursor(c, experience);
                c.close();
                db.close();
                return experience;
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return null;
    }

    public boolean saveDb(Context context) {
        if (ReceivedExperience.findById(context, this.id) != null) {
            Log.d(TAG, "received experience with id " + id + " already stored into sql db.");
            return false;
        }

        //show notification if this is a new experience
        ReceivedExperience.showNotification(context);

        Log.d(TAG, "saveDb");
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ExperienceDbContract.COL_ID, id);
        contentValues.put(ExperienceDbContract.COL_LAT, lat);
        contentValues.put(ExperienceDbContract.COL_LON, lon);
        contentValues.put(ExperienceDbContract.COL_IS_PUBLIC, isPublic);
        contentValues.put(ExperienceDbContract.COL_IS_SENT, false);
        contentValues.put(ExperienceDbContract.COL_TEXT, text);
        contentValues.put(ExperienceDbContract.COL_FILENAME, filename);
        contentValues.put(ExperienceDbContract.COL_CREATED_AT, createdAt);
        contentValues.put(ExperienceDbContract.COL_VISIBILITY_DURATION, visibilityDuration);

        return db.insert(ExperienceDbContract.TABLE_NAME, null, contentValues) != -1;
    }

    public static void showNotification(Context context) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("new experience available")
                .setContentText("There's a new experience to discover. Check it out now!");
        //creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //mId allows you to update the notification later on.
        mNotificationManager.notify(1231, mBuilder.build());
    }

    public static void loadExperiencesFromApi(Context context, boolean isPublic) {
        String url = Params.getApiActionUrl(context, "experience.get");

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("lat", "8.00"));
        nameValuePairs.add(new BasicNameValuePair("lon", "43.00"));
        nameValuePairs.add(new BasicNameValuePair("imei", DeviceHelper.getDeviceId(context)));

        RestTask task = new RestExperienceListTask(context, url, nameValuePairs, isPublic);
        task.execute();
    }
}