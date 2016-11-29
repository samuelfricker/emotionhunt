package ch.fhnw.ip5.emotionhunt.models;

import com.google.gson.annotations.SerializedName;

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

}
