package ch.fhnw.ip5.emotionhunt.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.google.gson.JsonParseException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.util.List;
import ch.fhnw.ip5.emotionhunt.activities.MainActivity;
import ch.fhnw.ip5.emotionhunt.activities.OnBoardActivity;
import ch.fhnw.ip5.emotionhunt.activities.SplashScreenActivity;
import ch.fhnw.ip5.emotionhunt.helpers.UserList;
import ch.fhnw.ip5.emotionhunt.models.User;

/**
 * This class validates the credentials (android device id) via the server's API.
 */
public class RestUserLoginTask extends RestTask {

    public static final String TAG = RestUserLoginTask.class.getSimpleName();

    public RestUserLoginTask(Context context, String url, List<NameValuePair> nameValuePairs)
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
            if (status == 200) {
                User user = User.getFirstUserFromResponse(response);
                UserList.getInstance().me = user;

                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);
                ((SplashScreenActivity) mContext).finish();

            } else if ( status == 403 ) {
                Intent intent = new Intent(mContext, OnBoardActivity.class);
                mContext.startActivity(intent);
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
}
