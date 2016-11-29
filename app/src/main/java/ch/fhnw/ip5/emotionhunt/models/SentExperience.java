package ch.fhnw.ip5.emotionhunt.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.activities.MainActivity;
import ch.fhnw.ip5.emotionhunt.helper.DbHelper;
import ch.fhnw.ip5.emotionhunt.helper.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.tasks.RestExperienceCreateTask;
import ch.fhnw.ip5.emotionhunt.tasks.RestExperienceListTask;
import ch.fhnw.ip5.emotionhunt.tasks.RestTask;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */

public class SentExperience extends Experience {
    public Bitmap image;

    private static final String TAG = MainActivity.class.getSimpleName();

    public static SentExperience findById(Context context, int id) {
        return null;
    }

    public boolean saveDb(Context context) {
        Log.d(TAG, "saveDb");
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Experience.ExperienceDbContract.COL_ID, id);
        contentValues.put(Experience.ExperienceDbContract.COL_LAT, lat);
        contentValues.put(Experience.ExperienceDbContract.COL_LON, lon);
        contentValues.put(Experience.ExperienceDbContract.COL_IS_PUBLIC, isPublic);
        contentValues.put(Experience.ExperienceDbContract.COL_IS_SENT, true);
        contentValues.put(Experience.ExperienceDbContract.COL_TEXT, text);
        contentValues.put(Experience.ExperienceDbContract.COL_FILENAME, filename);
        contentValues.put(Experience.ExperienceDbContract.COL_CREATED_AT, createdAt);
        contentValues.put(Experience.ExperienceDbContract.COL_VISIBILITY_DURATION, visibilityDuration);

        return db.insert(ExperienceDbContract.TABLE_NAME, null, contentValues) != -1;
    }

    public static void loadExperiencesFromApi(Context context, boolean isPublic) {
        String url = Params.getApiActionUrl(context, "experience.get");

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("lat", "8.00"));
        nameValuePairs.add(new BasicNameValuePair("lon", "43.00"));

        RestTask task = new RestExperienceListTask(context, url, nameValuePairs, isPublic);
        task.execute();
    }

    public void sendApi(Context context) {
        String url = Params.getApiActionUrl(context, "experience.create");
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        LocationHistory lh = LocationHistory.getLastPositionHistory(context);
        if (lh == null) return;
        String lat = String.valueOf(lh.lat);
        String lon = String.valueOf(lh.lon);
        nameValuePairs.add(new BasicNameValuePair("lat", lat));
        nameValuePairs.add(new BasicNameValuePair("lon", lon));
        nameValuePairs.add(new BasicNameValuePair("text", text));
        //TODO make visibilityduration optional for private experiences
        nameValuePairs.add(new BasicNameValuePair("visibilityDuration", String.valueOf(visibilityDuration)));
        nameValuePairs.add(new BasicNameValuePair("imei", DeviceHelper.getDeviceId(context)));
        //TODO change recipient - load from selected checkboxes
        nameValuePairs.add(new BasicNameValuePair("recipients", "1"));
        //TODO change backend to accept imei instead of sender id
        nameValuePairs.add(new BasicNameValuePair("sender", "2"));
        nameValuePairs.add(new BasicNameValuePair("expectedEmotion", "{\"anger\":0.00,\"contempt\":0.0,\"disgust\":0.0,\"fear\":0.0,\"happiness\":0.99,\"neutral\":0.0,\"sadness\":0.0,\"surprise\":0.0}"));

        RestExperienceCreateTask experienceCreateTask = new RestExperienceCreateTask(context, url,nameValuePairs, this);
        experienceCreateTask.execute();
    }
}
