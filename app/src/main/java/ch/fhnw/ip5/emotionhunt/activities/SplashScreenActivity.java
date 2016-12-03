package ch.fhnw.ip5.emotionhunt.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helpers.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.models.User;
import ch.fhnw.ip5.emotionhunt.tasks.RestUserLoginTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends AppCompatActivity {

    ArrayList<User> users;
    private View mContentView;
    private String androidId;
    private final static long sleepTime = 1000*1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        mContentView = (ImageView) findViewById(R.id.fullscreen_content);
        mContentView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        new Thread(new Runnable() {
            public void run() {
            try {
                Thread.sleep(sleepTime);
                checkUserId();
            } catch (InterruptedException e) {

            }

            }
        }).start();




    }

    private void checkUserId() {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        androidId = getAndroidId();
        nameValuePairs.add(new BasicNameValuePair("androidId", androidId));
        String url = Params.getApiActionUrl(getApplicationContext(), "user.login");
        RestUserLoginTask restTask = new RestUserLoginTask(this,url,nameValuePairs);
        restTask.execute();
    }

    public String getAndroidId() {
        return DeviceHelper.getDeviceId(getApplicationContext());
    }
}
