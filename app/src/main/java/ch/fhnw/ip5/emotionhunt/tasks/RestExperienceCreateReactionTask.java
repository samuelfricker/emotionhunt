package ch.fhnw.ip5.emotionhunt.tasks;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

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
import java.util.List;

/**
 * This RestTask creates a new experience reaction on the Server's API.
 */
public class RestExperienceCreateReactionTask extends RestTask {
    private static final String TAG = "RestExperienceReact";

    public RestExperienceCreateReactionTask(Context context, String url, List<NameValuePair> nameValuePairs)
    {
        super(context, url, nameValuePairs);
    }

    @Override
    protected Boolean doInBackground(String... urls) {
        AndroidHttpClient httpclient = null;
        try {
            Log.d(TAG, "Execute: " + mUrl);
            HttpPost httppost = new HttpPost(mUrl);
            httppost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));
            httppost = setHeaderHttpPost(httppost);
            httpclient = AndroidHttpClient.newInstance("Android");
            HttpResponse response = httpclient.execute(httppost);

            int status = response.getStatusLine().getStatusCode();

            Log.d(TAG, "Status: " + status);

            if (status == 201) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
                    JSONArray jData = jsonObject.getJSONArray("data");
                    String data = jData.toString();
                    Log.d(TAG, data);
                    httpclient.close();
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpclient != null) httpclient.close();
        }
        return false;
    }
}
