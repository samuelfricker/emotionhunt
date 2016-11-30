package ch.fhnw.ip5.emotionhunt.tasks;

import android.content.Context;
import android.content.Intent;
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

import ch.fhnw.ip5.emotionhunt.activities.MainActivity;
import ch.fhnw.ip5.emotionhunt.activities.OnBoardActivity;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.tasks
 *
 * @author Benjamin Bur
 */

public class RestUserRegisterTask extends RestTask {

    public static final String TAG = RestUserRegisterTask.class.getSimpleName();

    public RestUserRegisterTask(Context context, String url, List<NameValuePair> nameValuePairs)
    {
        super(context, url, nameValuePairs);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if(progress[0] == 2){
            Handler handler =  new Handler(mContext.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast.makeText(mContext, "Username already exists", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    protected Boolean doInBackground(String... urls) {
        try {
            Log.d(TAG, "Execute: " + mUrl);
            HttpPost httppost = new HttpPost(mUrl);
            httppost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httppost);

            int status = response.getStatusLine().getStatusCode();

            Log.d(TAG, "Status: " + status);

            if ( status == 201 ) {
                // User succsesfully Created
                Toast.makeText(mContext,"User has been successfully created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);

            } else if ( status == 500 ) {
                // TODO Add functionality for already existing Users who changed their Phone (new Android I)
                // TODO Teas doesnt show up


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
        }
        return false;
    }
}
