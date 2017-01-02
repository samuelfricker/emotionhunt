package ch.fhnw.ip5.emotionhunt.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.florent37.diagonallayout.DiagonalLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import agency.tango.android.avatarview.views.AvatarView;
import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helpers.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.helpers.EmotionPickerDialog;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.helpers.SquareImageView;
import ch.fhnw.ip5.emotionhunt.helpers.UserList;
import ch.fhnw.ip5.emotionhunt.models.Emotion;
import ch.fhnw.ip5.emotionhunt.models.Experience;
import ch.fhnw.ip5.emotionhunt.models.ReceivedExperience;
import ch.fhnw.ip5.emotionhunt.models.User;
import ch.fhnw.ip5.emotionhunt.tasks.RestExperienceCreateReactionTask;
import ch.fhnw.ip5.emotionhunt.tasks.RestExperienceMediaTask;
import ch.fhnw.ip5.emotionhunt.tasks.RestExperienceReactionsTask;

public class ExperienceDetailActivity extends AppCompatActivity {
    public static final String TAG = "ExperienceDetailActi";
    public static final String EXTRA_EXPERIENCE_ID = "EXTRA_EXPERIENCE_ID";
    private long mExperienceId;
    private Experience mExperience;
    private TextView mTxtExperienceText;
    private ImageView mImageView;
    private ImageView mImageViewMyReaction;
    private DiagonalLayout diagonalLayout;
    private Emotion mMyReaction;
    private SquareImageView squareImageView;
    private LinearLayout layoutReactions;
    private LinearLayout layoutMyReaction;
    private LinearLayout layoutStrength;
    private SeekBar sbEmotionStrength;
    private TextView txtEmotionStrength;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int emotionStrength = 50;
    private AvatarView avatarView;

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();
    }

    /**
     * Initializes the view including all view elements and its required data.
     */
    private void initView() {
        mTxtExperienceText = (TextView) findViewById(R.id.text_experience_detail_comment);
        avatarView = (AvatarView) findViewById(R.id.avatar_view);
        TextView txtDate = (TextView) findViewById(R.id.txt_date);
        TextView txtSenderName = (TextView) findViewById(R.id.txt_sender_name);
        mImageView = (ImageView) findViewById(R.id.img_experience_preview);
        layoutMyReaction = (LinearLayout) findViewById(R.id.activity_experience_detail_my_reaction);
        layoutReactions = (LinearLayout) findViewById(R.id.activity_experience_detail_reaction_view);
        layoutStrength = (LinearLayout) findViewById(R.id.activity_experience_detail_emotion_strength);
        sbEmotionStrength = (SeekBar) findViewById(R.id.sb_emotion_strength);
        txtEmotionStrength = (TextView) findViewById(R.id.txt_emotion_strength);
        mImageViewMyReaction = (ImageView) findViewById(R.id.img_experience_icon);
        diagonalLayout = (DiagonalLayout) findViewById(R.id.diagonalLayout);
        squareImageView = (SquareImageView) findViewById(R.id.img_experience_preview);

        String avatarUrl = User.getAvatarURLByUserId(ExperienceDetailActivity.this, mExperience.senderId);
        if (mExperience.isSent) {
            Log.d(TAG, "Load own avatar");
            avatarUrl = User.getOwnAvatarURL(ExperienceDetailActivity.this);
        }
        Log.d(TAG, "Load avatar from " + avatarUrl);
        //load avatar
        Picasso.with(getApplicationContext())
                .load(avatarUrl)
                .into(avatarView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Successfully loaded image");
                    }

                    @Override
                    public void onError() {
                        Log.v(TAG,"Could not fetch image");
                    }
                });

        sbEmotionStrength.setProgress(emotionStrength);
        sbEmotionStrength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                emotionStrength = progress;
                mImageViewMyReaction.setAlpha(((float) emotionStrength/100) + 0.1f);
                Log.d(TAG, "SeekBar value : " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        layoutStrength.setVisibility(View.GONE);
        if (mExperience.isRead) {
            layoutMyReaction.setVisibility(View.INVISIBLE);
            layoutReactions.setVisibility(View.VISIBLE);
        } else {
            layoutMyReaction.setVisibility(View.VISIBLE);
            layoutReactions.setVisibility(View.INVISIBLE);
        }

        txtSenderName.setText((mExperience.isSent ? UserList.getInstance().getMyName() : mExperience.senderName));
        txtDate.setText(mExperience.getCreatedAt());

        mImageViewMyReaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick");
                new EmotionPickerDialog(ExperienceDetailActivity.this) {
                    @Override
                    public void onImgClick(int emotion) {
                        layoutStrength.setVisibility(View.VISIBLE);
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
                        String emotionLabel = getString(mMyReaction.getEmotionLabelResId());
                        txtEmotionStrength.setText(getString(R.string.txt_emotion_strength, emotionLabel));
                        mImageViewMyReaction.setAlpha(((float) emotionStrength/100) + 0.1f);
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

        loadReactions(false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.contentView);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadReactions(true);
            }
        });

        Log.d(TAG, "Experience " + mExperience.id + " " + (mExperience.isRead ? "is read" : "is not read"));
        Log.d(TAG, "Experience " + mExperience.id + " " + (mExperience.isSent ? "is sent" : "is received"));
    }

    private void loadReactions(boolean onRefresh) {
        //execute async task to load reactions
        String url = Params.getApiActionUrl(getApplicationContext(), "experience.reactions");
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(mExperience.id)));
        RestExperienceReactionsTask restExperienceReactionsTask = new RestExperienceReactionsTask(this,url,nameValuePairs,true);
        restExperienceReactionsTask.execute();
    }

    private void checkConfirmDialogOnLeave() {
        final Activity activity = ExperienceDetailActivity.this;
        if (!mExperience.isRead && !mExperience.isSent && mMyReaction == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExperienceDetailActivity.this);
            builder.setTitle(R.string.missing_reaction);
            builder.setMessage(R.string.leave_without_reaction);
            // Add the buttons
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    activity.finish();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            activity.finish();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        checkConfirmDialogOnLeave();
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

            String url = Params.getApiActionUrl(getApplicationContext(), "experience.reaction.create");
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("androidId", DeviceHelper.getDeviceId(getApplicationContext())));
            nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(mExperience.id)));
            nameValuePairs.add(new BasicNameValuePair("strength", String.valueOf(emotionStrength/100.0)));
            nameValuePairs.add(new BasicNameValuePair("emotion", mMyReaction != null ? mMyReaction.toString() : ""));
            RestExperienceCreateReactionTask restTask = new RestExperienceCreateReactionTask(this,url, nameValuePairs);
            restTask.execute();
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
                checkConfirmDialogOnLeave();
                break;
        }
        return true;
    }

}
