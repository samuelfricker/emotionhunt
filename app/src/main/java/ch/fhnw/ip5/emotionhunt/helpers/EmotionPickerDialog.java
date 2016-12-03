package ch.fhnw.ip5.emotionhunt.helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;

import ch.fhnw.ip5.emotionhunt.R;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.helpers
 *
 * @author Benjamin Bur
 */

public abstract class EmotionPickerDialog extends Dialog {

    public static final int EMOTION_ANGER = 1;
    public static final int EMOTION_CONTEMPT = 2;
    public static final int EMOTION_DISGUST = 3;
    public static final int EMOTION_FEAR = 4;
    public static final int EMOTION_HAPPINESS = 5;
    public static final int EMOTION_NEUTRAL = 6;
    public static final int EMOTION_SADNESS = 7;
    public static final int EMOTION_SURPRISE = 8;

    ImageView mImgAnger;
    ImageView mImgContempt;
    ImageView mImgDisgust;
    ImageView mImgFear;
    ImageView mImgHappiness;
    ImageView mImgNeutral;
    ImageView mImgSadness;
    ImageView mImgSurprise;

    public EmotionPickerDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_emotion_picker);
        setTitle("Pick emotion...");

        View.OnClickListener onClickListener= new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int id = view.getId();
                    int emotion = 0;
                    switch (id) {
                        case R.id.img_anger:
                            emotion = EMOTION_ANGER;
                            break;
                        case R.id.img_contempt:
                            emotion = EMOTION_CONTEMPT;
                            break;
                        case R.id.img_disgust:
                            emotion = EMOTION_DISGUST;
                            break;
                        case R.id.img_fear:
                            emotion = EMOTION_FEAR;
                            break;
                        case R.id.img_happiness:
                            emotion = EMOTION_HAPPINESS;
                            break;
                        case R.id.img_neutral:
                            emotion = EMOTION_NEUTRAL;
                            break;
                        case R.id.img_sadness:
                            emotion = EMOTION_SADNESS;
                            break;
                        case R.id.img_surprise:
                            emotion = EMOTION_SURPRISE;
                            break;
                    }
                    onImgClick(emotion);
                }
        };

        mImgAnger = (ImageView) findViewById(R.id.img_anger);
        mImgAnger.setOnClickListener(onClickListener);

        mImgContempt = (ImageView) findViewById(R.id.img_contempt);
        mImgContempt.setOnClickListener(onClickListener);

        mImgDisgust = (ImageView) findViewById(R.id.img_disgust);
        mImgDisgust.setOnClickListener(onClickListener);

        mImgFear = (ImageView) findViewById(R.id.img_fear);
        mImgFear.setOnClickListener(onClickListener);

        mImgHappiness = (ImageView) findViewById(R.id.img_happiness);
        mImgHappiness.setOnClickListener(onClickListener);

        mImgNeutral = (ImageView) findViewById(R.id.img_neutral);
        mImgNeutral.setOnClickListener(onClickListener);

        mImgSadness = (ImageView) findViewById(R.id.img_sadness);
        mImgSadness.setOnClickListener(onClickListener);

        mImgSurprise = (ImageView) findViewById(R.id.img_surprise);
        mImgSurprise.setOnClickListener(onClickListener);

    }

    public abstract void onImgClick(int emotion);
}
