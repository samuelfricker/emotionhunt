package ch.fhnw.ip5.emotionhunt.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.models.Experience;
import ch.fhnw.ip5.emotionhunt.models.ReceivedExperience;

public class ExperienceDetailActivity extends AppCompatActivity {
    public static final String EXTRA_EXPERIENCE_ID = "EXTRA_EXPERIENCE_ID";
    private long mExperienceId;
    private Experience mExperience;
    private TextView mTxtExperienceText;

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
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        initView();
    }

    /**
     * Initializes the view including all view elements and its required data.
     */
    private void initView() {
        mTxtExperienceText = (TextView) findViewById(R.id.text_experience_detail_comment);

        //load experience data
        if (mExperience != null) {
            mTxtExperienceText.setText(mExperience.text);
        }
    }

}
