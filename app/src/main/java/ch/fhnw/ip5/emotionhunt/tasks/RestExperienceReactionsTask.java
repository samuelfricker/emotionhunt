package ch.fhnw.ip5.emotionhunt.tasks;

import android.app.Activity;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.models.UserEmotion;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.tasks
 *
 * @author Benjamin Bur
 */

public class RestExperienceReactionsTask extends RestTask {
    private static final String TAG = "RestExperienceReact";
    private static final int STATE_START = 0;
    private static final int STATE_REACTIONS_READY = 1;
    private List<UserEmotion> userEmotions;
    private boolean onRefresh;

    public RestExperienceReactionsTask(Context context, String url, List<NameValuePair> nameValuePairs, boolean onRefresh)
    {
        super(context, url, nameValuePairs);
        this.onRefresh = onRefresh;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Log.d(TAG, "onProgressUpdate " + progress[0]);
        Handler handler =  new Handler(mContext.getMainLooper());
        switch (progress[0]) {
            case STATE_START:
                handler.post(new Runnable() {
                    public void run() {
                        LinearLayout layoutReactions = (LinearLayout) ((Activity)mContext).findViewById(R.id.layout_reactions);
                        layoutReactions.setVisibility(View.INVISIBLE);
                    }
                });
                break;
            case STATE_REACTIONS_READY:
                Log.d(TAG, userEmotions.size() + " UserEmotions loaded");

                //load reactions in view
                handler.post(new Runnable() {
                    public void run() {
                        LinearLayout layoutReactions = (LinearLayout) ((Activity)mContext).findViewById(R.id.layout_reactions);
                        layoutReactions.setVisibility(View.VISIBLE);

                        //set refreshing to false on swipe view if this is a refresh
                        if (onRefresh) {
                            SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) ((Activity)mContext).findViewById(R.id.contentView);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        if (userEmotions.size() > 1) {
                            layoutReactions.removeAllViews();
                            for (UserEmotion userEmotion : userEmotions) {
                                //skip expected reactions
                                if (userEmotion.isSender()) continue;

                                Log.d(TAG, "Received User Emotion from User " + userEmotion.getName());
                                View reactionView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.activity_experience_detail_reaction_item, null);
                                TextView txtUser = (TextView) reactionView.findViewById(R.id.txt_user_name);
                                ImageView imgReaction = (ImageView) reactionView.findViewById(R.id.img_reaction);
                                imgReaction.setImageResource(userEmotion.getResourceId());
                                imgReaction.setMaxHeight(60);
                                imgReaction.setMaxWidth(60);
                                txtUser.setText(userEmotion.getName());
                                layoutReactions.addView(reactionView);
                            }
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected Boolean doInBackground(String... urls) {
        publishProgress(STATE_START);
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
                    JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
                    JSONArray jData = jsonObject.getJSONArray("data");

                    String data = jData.toString();

                    //read json values from response
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    userEmotions = Arrays.asList(gson.fromJson(data, UserEmotion[].class));
                    publishProgress(STATE_REACTIONS_READY);
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
