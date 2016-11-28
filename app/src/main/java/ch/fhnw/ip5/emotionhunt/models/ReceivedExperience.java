package ch.fhnw.ip5.emotionhunt.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.tasks.RestExperienceListTask;
import ch.fhnw.ip5.emotionhunt.tasks.RestTask;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */

public class ReceivedExperience extends Experience {

    @Override
    public ReceivedExperience findById(SQLiteDatabase db, int id) {
        return null;
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
