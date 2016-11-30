package ch.fhnw.ip5.emotionhunt.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.models.Experience;
import ch.fhnw.ip5.emotionhunt.models.ReceivedExperience;
import ch.fhnw.ip5.emotionhunt.models.SentExperience;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.tasks
 *
 * @author Benjamin Bur
 */

public class RestExperienceCreateTask extends RestTask {

    SentExperience experience;

    public RestExperienceCreateTask(Context context, String url, List<NameValuePair> nameValuePairs, SentExperience experience)
    {
        super(context, url, nameValuePairs);
        this.experience = experience;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if(progress[0] == 1){
            Handler handler =  new Handler(mContext.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast.makeText(mContext, R.string.experience_successfully_created, Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(progress[0] == 2){
            Handler handler =  new Handler(mContext.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast.makeText(mContext, R.string.network_problem, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    protected Boolean doInBackground(String... urls) {
        try {
            Log.d(TAG, "Execute: " + mUrl);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            HttpPost httppost = new HttpPost(mUrl);

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            experience.image.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte [] ba = bao.toByteArray();
            entity.addPart("media", new ByteArrayBody(ba, "emo-upload.jpg"));

            for (NameValuePair nvp : this.mNameValuePairs) {
                entity.addPart(nvp.getName(), new StringBody(nvp.getValue()));
            }

            httppost.setEntity(entity);
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httppost);

            int status = response.getStatusLine().getStatusCode();

            Log.d(TAG, "Status: " + status);

            if (status == 201 || status == 200) {
                //TODO create update onProgressUpdate function for Toast
                publishProgress(1);
                HttpEntity hentity = response.getEntity();
                JSONObject jsonObject = new JSONObject(EntityUtils.toString(hentity));
                JSONArray jData = jsonObject.getJSONArray("data");

                String data = jData.toString();

                //read json values from response
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                //TODO: Implement logic to save sent experience in local db
                /*List<SentExperience> experiences = Arrays.asList(gson.fromJson(data, SentExperience[].class));

                //save all received experiences into db
                for (Experience experience : experiences) {
                    experience.saveDb(mContext);
                }*/

                //TODO redirect user to mainactivity

                return true;
            } else if (status == 415) {
                HttpEntity hentity = response.getEntity();
                JSONObject jsonObject = new JSONObject(EntityUtils.toString(hentity));
                JSONArray jData = jsonObject.getJSONArray("data");
                String data = jData.toString();
                publishProgress(2);
                Log.e(TAG, "ERROR " + data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
