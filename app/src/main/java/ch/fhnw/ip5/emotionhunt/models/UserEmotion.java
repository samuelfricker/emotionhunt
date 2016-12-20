package ch.fhnw.ip5.emotionhunt.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */

public class UserEmotion extends Emotion {
    private String name;
    @SerializedName("profile_picture")
    private String profilePicture;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("is_sender")
    private int isSender;

    public boolean isSender() {
        return isSender == 1;
    }
    public String getName() {
        return name;
    }
}
