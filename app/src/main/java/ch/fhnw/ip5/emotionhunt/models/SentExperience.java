package ch.fhnw.ip5.emotionhunt.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.activities.MainActivity;
import ch.fhnw.ip5.emotionhunt.helpers.DbHelper;
import ch.fhnw.ip5.emotionhunt.helpers.DeviceHelper;
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
    private static final String TAG = SentExperience.class.getSimpleName();

    public Bitmap image;
    public ArrayList<Integer> recipients;
    public Emotion expectedEmotion;

    public SentExperience() {
        recipients = new ArrayList<>();
    }

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
        contentValues.put(Experience.ExperienceDbContract.COL_IS_SENT, 1);
        contentValues.put(Experience.ExperienceDbContract.COL_IS_READ, 1);
        contentValues.put(Experience.ExperienceDbContract.COL_EMOTION, getExpectedEmotionJSON());
        contentValues.put(Experience.ExperienceDbContract.COL_TEXT, text);
        contentValues.put(Experience.ExperienceDbContract.COL_FILENAME, filename);
        contentValues.put(Experience.ExperienceDbContract.COL_CREATED_AT, createdAt);
        contentValues.put(Experience.ExperienceDbContract.COL_VISIBILITY_DURATION, visibilityDuration);

        boolean validation = db.insert(ExperienceDbContract.TABLE_NAME, null, contentValues) != -1;

        db.close();

        return validation;
    }

    public static void loadExperiencesFromApi(Context context) {
        String url = Params.getApiActionUrl(context, "experience.get");

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("lat", "8.00"));
        nameValuePairs.add(new BasicNameValuePair("lon", "43.00"));

        RestTask task = new RestExperienceListTask(context, url, nameValuePairs);
        task.execute();
    }

    public void sendApi(Context context) {
        String url = Params.getApiActionUrl(context, isPublic ? "experience.public.create" : "experience.create");
        List<NameValuePair> nameValuePairs = new ArrayList<>();

        //set current location for this experience
        LocationHistory lh = LocationHistory.getLastPositionHistory(context);
        if (lh == null) return;
        String lat = String.valueOf(lh.lat);
        String lon = String.valueOf(lh.lon);
        nameValuePairs.add(new BasicNameValuePair("lat", lat));
        nameValuePairs.add(new BasicNameValuePair("lon", lon));

        //set the experience's text
        nameValuePairs.add(new BasicNameValuePair("text", text));

        if (isPublic) {
            //set the duration of visibility
            nameValuePairs.add(new BasicNameValuePair("visibilityDuration", String.valueOf(visibilityDuration)));
        } else {
            //set the selected recipients
            nameValuePairs.add(new BasicNameValuePair("recipients", android.text.TextUtils.join(",", recipients)));
        }

        //android id (sender)
        nameValuePairs.add(new BasicNameValuePair("androidId", DeviceHelper.getDeviceId(context)));

        //expected emotion
        nameValuePairs.add(new BasicNameValuePair("expectedEmotion", getExpectedEmotionJSON()));

        RestExperienceCreateTask experienceCreateTask = new RestExperienceCreateTask(context, url,nameValuePairs, this);
        experienceCreateTask.execute();
    }

    public String getExpectedEmotionJSON() {
        Gson gson = new Gson();
        String sExpectedEmotion = gson.toJson(expectedEmotion);
        return sExpectedEmotion;
    }

    public ArrayList<ReceivedExperience> getAll(Context context){
        return super.getAll(context, true);
    }
}
