package ch.fhnw.ip5.emotionhunt.models;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.activities.MainActivity;
import ch.fhnw.ip5.emotionhunt.helpers.DbHelper;
import ch.fhnw.ip5.emotionhunt.helpers.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.tasks.RestExperienceListTask;
import ch.fhnw.ip5.emotionhunt.tasks.RestTask;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */

public class ReceivedExperience extends Experience {
    private static final String TAG = ReceivedExperience.class.getSimpleName();

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
        ReceivedExperience experience = ReceivedExperience.findById(context, this.id);
        if (experience != null) {
            Log.d(TAG, "received experience with id " + id + " already stored into sql db.");
            return false;
        }

        Log.d(TAG, "saveDb");
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ExperienceDbContract.COL_ID, id);
        contentValues.put(ExperienceDbContract.COL_LAT, lat);
        contentValues.put(ExperienceDbContract.COL_LON, lon);
        contentValues.put(ExperienceDbContract.COL_IS_PUBLIC, isPublic);
        contentValues.put(ExperienceDbContract.COL_IS_SENT, 0);
        contentValues.put(ExperienceDbContract.COL_IS_READ, isRead ? 1 : 0);
        contentValues.put(ExperienceDbContract.COL_EMOTION, "");
        contentValues.put(ExperienceDbContract.COL_TEXT, text);
        contentValues.put(ExperienceDbContract.COL_FILENAME, filename);
        contentValues.put(ExperienceDbContract.COL_CREATED_AT, createdAt);
        contentValues.put(ExperienceDbContract.COL_VISIBILITY_DURATION, visibilityDuration);

        //show notification if this is a new experience
        if (!isPublic) ReceivedExperience.showNotification(context, (int) id);

        boolean validation = db.insert(ExperienceDbContract.TABLE_NAME, null, contentValues) != -1;
        db.close();

        return validation;
    }

    public static void showNotification(Context context, int id) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setContentTitle(context.getString(R.string.new_experience_available))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText("Dimitri " + context.getString(R.string.has_left_something_for_you));
        //creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());
    }

    public static void loadExperiencesFromApi(Context context) {
        String url = Params.getApiActionUrl(context, "experience.get");

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        LocationHistory lh = LocationHistory.getLastPositionHistory(context);
        if (lh == null) return;
        String lat = String.valueOf(lh.lat);
        String lon = String.valueOf(lh.lon);
        nameValuePairs.add(new BasicNameValuePair("lat", lat));
        nameValuePairs.add(new BasicNameValuePair("lon", lon));
        nameValuePairs.add(new BasicNameValuePair("imei", DeviceHelper.getDeviceId(context)));

        RestTask task = new RestExperienceListTask(context, url, nameValuePairs);
        task.execute();
    }

    public static ArrayList<ReceivedExperience> getAllRead(Context context, boolean isRead){
        return Experience.getAll(context, false, isRead);
    }

    public static ArrayList<ReceivedExperience> getAll(Context context){
        return Experience.getAll(context, false, null);
    }
}
