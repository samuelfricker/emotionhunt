package ch.fhnw.ip5.emotionhunt.tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.http.AndroidHttpClient;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import agency.tango.android.avatarview.views.AvatarView;
import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.activities.ExperienceDetailActivity;
import ch.fhnw.ip5.emotionhunt.helpers.UserList;
import ch.fhnw.ip5.emotionhunt.models.User;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.tasks
 *
 * @author Benjamin Bur
 */

public class RestUserListTask extends RestTask {
    WeakReference<Activity> mWeakActivity;
    public static final String TAG = "RestUserListTask";

    public RestUserListTask(Context context, String url, List<NameValuePair> nameValuePairs, Activity activity)
    {
        super(context, url, nameValuePairs);
        mWeakActivity = new WeakReference<Activity>(activity);
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

                final UserList userList = UserList.getInstance();
                userList.users = new ArrayList<>();
                userList.recipients = new ArrayList<>();

                //save all received experiences into db
                for (final User user : users) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View v = activity.getLayoutInflater().inflate(R.layout.activity_experience_create_contact_item, null);
                            final LinearLayout innerLayout = (LinearLayout) v.findViewById(R.id.layout_inner);
                            TextView tvUser = (TextView) v.findViewById(R.id.txt_username);
                            final AvatarView avatarView = (AvatarView) v.findViewById(R.id.avatar_view);
                            tvUser.setText(user.name);
                            v.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    if (!userList.recipients.contains(user)) {
                                        userList.recipients.add(user);
                                        innerLayout.setBackgroundColor(Color.argb(20,0,0,0));
                                    } else {
                                        innerLayout.setBackgroundColor(Color.argb(0,0,0,0));
                                        try {
                                            userList.recipients.remove(user);
                                        } catch (Exception e) {
                                            Log.e(TAG, "Could not remove user from recipients ... \n" + e.toString());
                                        }
                                    }
                                }
                            });

                            UserList userList = UserList.getInstance();
                            userList.users.add(user);

                            //load avatar
                            Picasso.with(mContext).load(user.getAvatarURL(mContext)).into(avatarView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.d(TAG, "Successfully loaded image for user " + user.name);
                                }
                                @Override
                                public void onError() {
                                    Log.v(TAG,"Could not fetch image");
                                }
                            });

                            linearLayout.addView(v);
                        }
                    });
                }
                httpclient.close();
                return true;
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
