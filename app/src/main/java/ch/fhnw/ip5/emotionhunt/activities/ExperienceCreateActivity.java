package ch.fhnw.ip5.emotionhunt.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import ch.fhnw.ip5.emotionhunt.R;

public class ExperienceCreateActivity extends AppCompatActivity {

    TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_create);

        mTabHost = (TabHost)findViewById(R.id.tab_host_experience);
        mTabHost.setup();

        mTabHost.addTab(mTabHost.newTabSpec("tab_private").setIndicator(getString(R.string.experience_private)).setContent(R.id.tab_experience_private));
        mTabHost.addTab(mTabHost.newTabSpec("tab_public").setIndicator(getString(R.string.experience_public)).setContent(R.id.tab_experience_public));

        mTabHost.setCurrentTab(0);
    }
}
