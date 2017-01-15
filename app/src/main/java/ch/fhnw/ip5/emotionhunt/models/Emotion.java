package ch.fhnw.ip5.emotionhunt.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.fhnw.ip5.emotionhunt.R;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */
public class Emotion {
    /** double value as percent within the range [0,1) that indicates anger */
    private double anger;
    /** double value as percent within the range [0,1) that indicates contempt */
    private double contempt;
    /** double value as percent within the range [0,1) that indicates disgust */
    private double disgust;
    /** double value as percent within the range [0,1) that indicates fear */
    private double fear;
    /** double value as percent within the range [0,1) that indicates happiness */
    private double happiness;
    /** double value as percent within the range [0,1) that indicates neutral */
    private double neutral;
    /** double value as percent within the range [0,1) that indicates sadness */
    private double sadness;
    /** double value as percent within the range [0,1) that indicates surprise */
    private double surprise;

    /**
     * C'tor that initializes all emotion attributes with default value 0.0
     */
    public Emotion() {
        //init all values
        anger = 0.0;
        contempt = 0.0;
        disgust = 0.0;
        fear = 0.0;
        happiness = 0.0;
        neutral = 0.0;
        sadness = 0.0;
        surprise = 0.0;
    }

    public static Emotion getEmotionFromJson(String json) {
        //read json values from response
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Emotion emotion = gson.fromJson(json, Emotion.class);
        return emotion;
    }

    public double getAnger() {
        return anger;
    }

    public void setAnger(double anger) {
        this.anger = anger;
    }

    public double getContempt() {
        return contempt;
    }

    public void setContempt(double contempt) {
        this.contempt = contempt;
    }

    public double getDisgust() {
        return disgust;
    }

    public void setDisgust(double disgust) {
        this.disgust = disgust;
    }

    public double getFear() {
        return fear;
    }

    public void setFear(double fear) {
        this.fear = fear;
    }

    public double getHappiness() {
        return happiness;
    }

    public void setHappiness(double happiness) {
        this.happiness = happiness;
    }

    public double getNeutral() {
        return neutral;
    }

    public void setNeutral(double neutral) {
        this.neutral = neutral;
    }

    public double getSadness() {
        return sadness;
    }

    public void setSadness(double sadness) {
        this.sadness = sadness;
    }

    public double getSurprise() {
        return surprise;
    }

    public void setSurprise(double surprise) {
        this.surprise = surprise;
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public int getEmotionLabelResId() {
        int resId = 0;
        double value = 0.0;

        if (anger > value) {
            resId = R.string.emotion_anger;
            value = anger;
        }
        if (contempt > value) {
            resId = R.string.emotion_contempt;
            value = contempt;
        }
        if (disgust > value) {
            resId = R.string.emotion_disgust;
            value = disgust;
        }
        if (fear > value) {
            resId = R.string.emotion_fear;
            value = fear;
        }
        if (happiness > value) {
            resId = R.string.emotion_happiness;
            value = happiness;
        }
        if (neutral > value) {
            resId = R.string.emotion_neutral;
            value = neutral;
        }
        if (sadness > value) {
            resId = R.string.emotion_sadness;
            value = sadness;
        }
        if (surprise > value) {
            resId = R.string.emotion_surprise;
            value = surprise;
        }

        return resId;
    }

    public int getResourceId() {
        int resId = R.drawable.img_questionmark;
        double value = 0.0;

        if (anger > value) {
            resId = R.drawable.img_emotion_anger;
            value = anger;
        }
        if (contempt > value) {
            resId = R.drawable.img_emotion_contempt;
            value = contempt;
        }
        if (disgust > value) {
            resId = R.drawable.img_emotion_disgust;
            value = disgust;
        }
        if (fear > value) {
            resId = R.drawable.img_emotion_fear;
            value = fear;
        }
        if (happiness > value) {
            resId = R.drawable.img_emotion_happiness;
            value = happiness;
        }
        if (neutral > value) {
            resId = R.drawable.img_emotion_neutral;
            value = neutral;
        }
        if (sadness > value) {
            resId = R.drawable.img_emotion_sadness;
            value = sadness;
        }
        if (surprise > value) {
            resId = R.drawable.img_emotion_surprise;
        }
        return resId;
    }
}
