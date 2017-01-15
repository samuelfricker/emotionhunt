package ch.fhnw.ip5.emotionhunt.models;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.activities.ExperienceDetailActivity;
import ch.fhnw.ip5.emotionhunt.helpers.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.helpers.UserList;
import ch.fhnw.ip5.emotionhunt.tasks.RestTask;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */
public class User {
    private static final String TAG = User.class.getSimpleName();

    @SerializedName("id")
    public long id;
    @SerializedName("phone_number")
    public String phoneNumber;
    @SerializedName("android_id")
    public String androidId;
    public String name;
    @SerializedName("profile_picture")
    public String profilePicture;

    /**
     * Either a given object is equal to the current instance or not.
     * @param object object to compare with this
     * @return is equal
     */
    @Override
    public boolean equals(Object object)
    {
        boolean isEqual = false;
        if (object != null && object instanceof User) {
            isEqual = this.id == ((User) object).id;
        }
        return isEqual;
    }

    /**
     * Returns the URL of this user's avatar.
     * @param context
     * @return
     */
    public String getAvatarURL(Context context) {
        String apiUrl = Params.getApiActionUrl(context,"avatar");
        return apiUrl + "&id=" + id + "&apiKey=" + RestTask.API_KEY;
    }

    /**
     * Returns the avatar URL by a given Android device id.
     * @param context
     * @return
     */
    public String getAvatarURLByAndroidId(Context context) {
        String apiUrl = Params.getApiActionUrl(context,"avatar");
        return apiUrl + "&user=" + androidId + "&apiKey=" + RestTask.API_KEY;
    }

    /**
     * Returns the avatar URL by a given user id.
     * @param context
     * @param senderId
     * @return
     */
    public static String getAvatarURLByUserId(Context context, long senderId) {
        String apiUrl = Params.getApiActionUrl(context,"avatar");
        return apiUrl + "&id=" + senderId + "&apiKey=" + RestTask.API_KEY;
    }

    /**
     * Returns the current user's avatar URL.
     * @param context
     * @return
     */
    public static String getOwnAvatarURL(Context context) {
        User u = new User();
        u.androidId = DeviceHelper.getDeviceId(context);
        return u.getAvatarURLByAndroidId(context);
    }

    /**
     * Returns the first User instance that is received by the server's response.
     * @param response
     * @return
     */
    public static User getFirstUserFromResponse(HttpResponse response) {
        List<User> users = User.getUsersFromResponse(response);
        return users != null ? users.get(0) : null;
    }

    /**
     * Returns all User instances that are received by the server's response.
     * @param response
     * @return
     */
    public static List<User> getUsersFromResponse(HttpResponse response) {
        List<User> users = null;
        HttpEntity hentity = response.getEntity();
        try {
            String json = EntityUtils.toString(hentity);
            JSONObject jsonObject = new JSONObject(json);
            json = jsonObject.getJSONArray("data").toString();
            Log.d(TAG, "Response: " + json);
            //read json values from response
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            users = Arrays.asList(gson.fromJson(json, User[].class));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return users;
    }
}
