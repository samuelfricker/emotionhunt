package ch.fhnw.ip5.emotionhunt.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.activities.MainActivity;
import ch.fhnw.ip5.emotionhunt.helper.DbHelper;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.tasks.RestExperienceListTask;
import ch.fhnw.ip5.emotionhunt.tasks.RestTask;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */

public class SentExperience extends Experience {
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
}
