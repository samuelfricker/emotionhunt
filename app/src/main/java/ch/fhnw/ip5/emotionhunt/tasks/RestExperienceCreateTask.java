package ch.fhnw.ip5.emotionhunt.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helpers.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.models.Experience;
import ch.fhnw.ip5.emotionhunt.models.SentExperience;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.tasks
 *
 * @author Benjamin Bur
 */

public class RestExperienceCreateTask extends RestTask {
    private static final String TAG = RestExperienceCreateTask.class.getSimpleName();
    private static final int STATE_SHOW_PROGRESS_DIALOG = 1;
    private static final int STATE_SUCCESSFULL = 2;
    private static final int STATE_FAIL = 3;
    SentExperience experience;
    private ProgressDialog mProgressDialog;

    public RestExperienceCreateTask(Context context, String url, List<NameValuePair> nameValuePairs, SentExperience experience)
    {
        super(context, url, nameValuePairs);
        this.experience = experience;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getString(R.string.please_wait));
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Log.d(TAG, "onProgressUpdate " + progress[0]);
        Handler handler =  new Handler(mContext.getMainLooper());
        switch (progress[0]) {
            case STATE_SHOW_PROGRESS_DIALOG:
                handler.post(new Runnable() {
                    public void run() {
                        mProgressDialog.show();
                    }
                });

                break;
            case STATE_SUCCESSFULL:
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(mContext, R.string.experience_successfully_created, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case STATE_FAIL:
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(mContext, R.string.network_problem, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    @Override
    protected Boolean doInBackground(String... urls) {
        //show progress dialog
        publishProgress(STATE_SHOW_PROGRESS_DIALOG);

        AndroidHttpClient httpclient = null;

        Log.d(TAG, "Execute: " + mUrl);
        try {
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
            httppost = setHeaderHttpPost(httppost);
            httpclient = AndroidHttpClient.newInstance("Android");
            HttpResponse response = httpclient.execute(httppost);

            int status = response.getStatusLine().getStatusCode();

            Log.d(TAG, "Status: " + status);

            if (status == 201 || status == 200) {
                publishProgress(STATE_SUCCESSFULL);
                HttpEntity hentity = response.getEntity();
                JSONObject jsonObject = new JSONObject(EntityUtils.toString(hentity));
                JSONArray jData = jsonObject.getJSONArray("data");

                String data = jData.toString();

                //read json values from response
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                List<SentExperience> experiences = Arrays.asList(gson.fromJson(data, SentExperience[].class));

                //save all received experiences into db
                for (Experience e : experiences) {
                    if (experience.isPublic) e.isPublic = true;
                    e.isSent = true;
                    e.saveDb(mContext);
                    try {
                        DeviceHelper.saveBitmap(experience.image,mContext,e.filename);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                httpclient.close();
                ((Activity)mContext).finish();
                return true;
            } else if (status == 415) {
                publishProgress(STATE_FAIL);
                HttpEntity hentity = response.getEntity();
                JSONObject jsonObject = new JSONObject(EntityUtils.toString(hentity));
                JSONArray jData = jsonObject.getJSONArray("data");
                String data = jData.toString();
                Log.e(TAG, "REST ERROR " + data);
            } else {
                publishProgress(STATE_FAIL);
                HttpEntity hentity = response.getEntity();
                String json = EntityUtils.toString(hentity);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    json = jsonObject.toString();
                } catch (Exception e) {

                }
                Log.e(TAG, "REST ERROR " + json);
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

    @Override
    protected void onPreExecute() {
       mProgressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }
}
