package ch.fhnw.ip5.emotionhunt.tasks;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helpers.UserList;
import ch.fhnw.ip5.emotionhunt.models.Experience;
import ch.fhnw.ip5.emotionhunt.models.ReceivedExperience;
import ch.fhnw.ip5.emotionhunt.models.User;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.tasks
 *
 * @author Benjamin Bur
 */

public class RestUserListTask extends RestTask {
    WeakReference<Activity> mWeakActivity;

    public RestUserListTask(Context context, String url, List<NameValuePair> nameValuePairs, Activity activity)
    {
        super(context, url, nameValuePairs);
        mWeakActivity = new WeakReference<Activity>(activity);
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

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
                JSONArray jData = jsonObject.getJSONArray("data");

                String data = jData.toString();

                //read json values from response
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                List<User> users = Arrays.asList(gson.fromJson(data, User[].class));

                final Activity activity = mWeakActivity.get();
                final LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.layout_experience_private);

                UserList userList = UserList.getInstance();
                userList.users = new ArrayList<>();

                //save all received experiences into db
                for (final User user : users) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View v = activity.getLayoutInflater().inflate(R.layout.activity_experience_create_contact_item, null);
                            CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
                            checkBox.setText(user.name);

                            UserList userList = UserList.getInstance();
                            userList.users.add(user);

                            linearLayout.addView(v);
                        }
                    });
                }

                return true;
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
