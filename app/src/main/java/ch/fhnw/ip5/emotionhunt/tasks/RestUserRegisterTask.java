package ch.fhnw.ip5.emotionhunt.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonParseException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.activities.MainActivity;
import ch.fhnw.ip5.emotionhunt.activities.OnBoardActivity;
import ch.fhnw.ip5.emotionhunt.helpers.UserList;
import ch.fhnw.ip5.emotionhunt.models.User;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.tasks
 *
 * @author Benjamin Bur
 */

public class RestUserRegisterTask extends RestTask {

    public static final String TAG = RestUserRegisterTask.class.getSimpleName();
    private ProgressDialog mProgressDialog;

    public RestUserRegisterTask(Context context, String url, List<NameValuePair> nameValuePairs)
    {
        super(context, url, nameValuePairs);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getString(R.string.please_wait));
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Handler handler =  new Handler(mContext.getMainLooper());
        if (progress[0] == 2) {
            handler.post( new Runnable(){
                public void run(){
                    Toast.makeText(mContext, R.string.username_already_exists, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (progress[0] == 1) {
            handler.post( new Runnable(){
                public void run(){
                    // User succsesfully Created
                    Toast.makeText(mContext, R.string.user_has_been_successfully_created, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                }
            });
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

            if ( status == 201 ) {
                User user = User.getFirstUserFromResponse(response);
                UserList.getInstance().me = user;
                publishProgress(1);
            } else if ( status == 500 ) {
                publishProgress(2);
            } else {
                Log.e(TAG, "Error while login. Status: " + status);
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
