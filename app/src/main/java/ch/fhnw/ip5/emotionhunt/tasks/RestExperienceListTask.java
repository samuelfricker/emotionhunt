package ch.fhnw.ip5.emotionhunt.tasks;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.models.Experience;
import ch.fhnw.ip5.emotionhunt.models.ReceivedExperience;

/**
 * This Class fetches all experiences for the current user via the server's API.
 */
public class RestExperienceListTask extends RestTask {

    public RestExperienceListTask(Context context, String url, List<NameValuePair> nameValuePairs)
    {
        super(context, url, nameValuePairs);
    }

    @Override
    protected Boolean doInBackground(String... urls) {
        AndroidHttpClient httpclient = null;
        try {
            Log.d(TAG, "API Experience Call - Execute: " + mUrl);
            HttpPost httppost = new HttpPost(mUrl);
            httppost.addHeader("Cache-Control", "no-cache");
            httppost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));
            httpclient = AndroidHttpClient.newInstance("Android");
            httppost = setHeaderHttpPost(httppost);
            HttpResponse response = httpclient.execute(httppost);

            int status = response.getStatusLine().getStatusCode();

            Log.d(TAG, "Status: " + status);

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
                JSONArray jData = jsonObject.getJSONArray("data");

                String data = jData.toString();

                //read json values from response
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                List<ReceivedExperience> experiences = Arrays.asList(gson.fromJson(data, ReceivedExperience[].class));

                //save all received experiences into db
                for (Experience experience : experiences) {
                    //convert int to boolean from API
                    experience.isPublic = experience.isPublicApi == 1;
                    experience.isLocationBased = experience.isLocationBasedApi == 1;
                    experience.isRead = false;
                    experience.saveDb(mContext);
                }
                httpclient.close();
                return true;
            }
        } catch (IOException e) {
            Log.e(TAG, "API Experience Call - Error: " + e.toString());
            e.printStackTrace();
        } catch (JsonParseException e) {
            Log.e(TAG, "API Experience Call - Error: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "API Experience Call - Error: " + e.toString());
            e.printStackTrace();
        } finally {
            if (httpclient != null) httpclient.close();
        }
        return false;
    }
}
