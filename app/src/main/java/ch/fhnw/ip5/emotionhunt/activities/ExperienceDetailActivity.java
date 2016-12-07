package ch.fhnw.ip5.emotionhunt.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helpers.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.helpers.EmotionPickerDialog;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.models.Emotion;
import ch.fhnw.ip5.emotionhunt.models.Experience;
import ch.fhnw.ip5.emotionhunt.models.ReceivedExperience;
import ch.fhnw.ip5.emotionhunt.tasks.RestExperienceMediaTask;

public class ExperienceDetailActivity extends AppCompatActivity {
    public static final String TAG = "ExperienceDetailActi";
    public static final String EXTRA_EXPERIENCE_ID = "EXTRA_EXPERIENCE_ID";
    private long mExperienceId;
    private Experience mExperience;
    private TextView mTxtExperienceText;
    private ImageView mImageView;
    private ImageView mImageViewMyReaction;
    private Emotion mMyReaction;
    private LinearLayout layoutReactions;
    private LinearLayout layoutMyReaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_detail);

        mExperienceId = getIntent().getLongExtra(EXTRA_EXPERIENCE_ID,0);
        if (mExperienceId != 0) {
            mExperience = ReceivedExperience.findById(getApplicationContext(), mExperienceId);
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.experience_detail_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        //TODO Remove hacks
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0f);

        initView();
    }

    /**
     * Initializes the view including all view elements and its required data.
     */
    private void initView() {
        mTxtExperienceText = (TextView) findViewById(R.id.text_experience_detail_comment);
        mImageView = (ImageView) findViewById(R.id.img_experience_preview);
        layoutMyReaction = (LinearLayout) findViewById(R.id.activity_experience_detail_my_reaction);
        layoutReactions = (LinearLayout) findViewById(R.id.activity_experience_detail_reaction_view);

        if (mExperience.isRead) {
            layoutMyReaction.setVisibility(View.INVISIBLE);
            layoutReactions.setVisibility(View.VISIBLE);
        } else {
            layoutMyReaction.setVisibility(View.VISIBLE);
            layoutReactions.setVisibility(View.INVISIBLE);
        }

        mImageViewMyReaction = (ImageView) findViewById(R.id.img_experience_icon);
        mImageViewMyReaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick");
                new EmotionPickerDialog(ExperienceDetailActivity.this) {
                    @Override
                    public void onImgClick(int emotion) {
                        mMyReaction = new Emotion();
                        double expValue = 0.9999999999999;
                        switch (emotion) {
                            case EmotionPickerDialog.EMOTION_ANGER :
                                mMyReaction.setAnger(expValue);
                                mImageViewMyReaction.setImageResource(R.drawable.img_emotion_anger);
                                break;
                            case EmotionPickerDialog.EMOTION_CONTEMPT :
                                mMyReaction.setContempt(expValue);
                                mImageViewMyReaction.setImageResource(R.drawable.img_emotion_contempt);
                                break;
                            case EmotionPickerDialog.EMOTION_DISGUST :
                                mMyReaction.setDisgust(expValue);
                                mImageViewMyReaction.setImageResource(R.drawable.img_emotion_disgust);
                                break;
                            case EmotionPickerDialog.EMOTION_FEAR :
                                mMyReaction.setFear(expValue);
                                mImageViewMyReaction.setImageResource(R.drawable.img_emotion_fear);
                                break;
                            case EmotionPickerDialog.EMOTION_HAPPINESS :
                                mMyReaction.setHappiness(expValue);
                                mImageViewMyReaction.setImageResource(R.drawable.img_emotion_happiness);
                                break;
                            case EmotionPickerDialog.EMOTION_NEUTRAL :
                                mMyReaction.setNeutral(expValue);
                                mImageViewMyReaction.setImageResource(R.drawable.img_emotion_neutral);
                                break;
                            case EmotionPickerDialog.EMOTION_SADNESS :
                                mImageViewMyReaction.setImageResource(R.drawable.img_emotion_sadness);
                                mMyReaction.setSadness(expValue);
                                break;
                            case EmotionPickerDialog.EMOTION_SURPRISE :
                                mImageViewMyReaction.setImageResource(R.drawable.img_emotion_surprise);
                                mMyReaction.setSurprise(expValue);
                                break;
                            default:
                                mImageViewMyReaction.setImageResource(R.drawable.img_questionmark);
                                mMyReaction = null;
                                break;
                        }
                        dismiss();
                    }
                }.show();
            }
        });

        //load experience data
        if (mExperience != null) {
            mTxtExperienceText.setText(mExperience.text);
        }

        //execute async task to load media file
        String url = Params.getApiActionUrl(getApplicationContext(), "experience.media");
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("media", mExperience.filename));

        Bitmap mediaBitmap = DeviceHelper.loadImageFromStorage(mExperience.filename, ExperienceDetailActivity.this);
        if (mediaBitmap != null) {
            Log.d(TAG, "image already stored on device...");
            mImageView.setImageBitmap(mediaBitmap);
        } else {
            Log.d(TAG, "image is going to be loaded from api...");
            RestExperienceMediaTask restExperienceMediaTask = new RestExperienceMediaTask(this,
                    url,nameValuePairs,mExperience.filename);
            restExperienceMediaTask.execute();
        }

        Log.d(TAG, "Experience " + mExperience.id + " " + (mExperience.isRead ? "is read" : "is not read"));
        Log.d(TAG, "Experience " + mExperience.id + " " + (mExperience.isSent ? "is sent" : "is received"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        //update the isRead flag after first opening of this experience
        if (!mExperience.isRead && !mExperience.isSent) {
            Log.d(TAG, "mark experience as read");
            mExperience.updateIsRead(getApplicationContext());
            if (mMyReaction != null) {
                mExperience.emotion = mMyReaction.toString();
                mExperience.updateEmotion(getApplicationContext());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

}
