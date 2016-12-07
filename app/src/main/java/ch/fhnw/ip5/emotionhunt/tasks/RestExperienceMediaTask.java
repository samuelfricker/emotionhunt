package ch.fhnw.ip5.emotionhunt.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import com.google.gson.JsonParseException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helpers.DeviceHelper;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.tasks
 *
 * @author Benjamin Bur
 */

public class RestExperienceMediaTask extends RestTask {
    private static final int STATE_BITMAP_READY = 1;
    private Bitmap mImg = null;
    private ProgressDialog mProgressDialog;
    private String mFilename;

    public RestExperienceMediaTask(Context context, String url, List<NameValuePair> nameValuePairs, String filename)
    {
        super(context, url, nameValuePairs);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getString(R.string.please_wait));
        mFilename = filename;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Log.d(TAG, "onProgressUpdate " + progress[0]);
        Handler handler =  new Handler(mContext.getMainLooper());
        switch (progress[0]) {
            case STATE_BITMAP_READY:
                if (mImg == null) return;
                handler.post(new Runnable() {
                    public void run() {
                        ((ImageView)((Activity)mContext).findViewById(R.id.img_experience_preview)).setImageBitmap(mImg);
                    }
                });
                break;
        }
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

            if (status == 200) {
                //load image bitmap into view
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int len = 0;
                    try {
                        while ((len = instream.read(buffer)) != -1) {
                            baos.write(buffer, 0, len);
                        }
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] b = baos.toByteArray();
                    mImg = BitmapFactory.decodeByteArray(b, 0, b.length);
                    DeviceHelper.saveBitmap(mImg, mContext, mFilename);
                    publishProgress(STATE_BITMAP_READY);
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

    @Override
    protected void onPreExecute() {
        mProgressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }
}
