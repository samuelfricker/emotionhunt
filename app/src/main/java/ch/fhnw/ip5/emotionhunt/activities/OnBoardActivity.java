package ch.fhnw.ip5.emotionhunt.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;
import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helper.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.tasks.RestUserRegisterTask;

/**
 * A login screen that offers login via name/phonenumber.
 */
public class OnBoardActivity extends AppCompatActivity {
    private static final String TAG = OnBoardActivity.class.getSimpleName();
    private EditText mName;
    private EditText mPhoneNumber;
    private ScrollView loginForm;
    private Button mLoginButton;
    private String androidId;
    public String name;
    public String phoneNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        loginForm = (ScrollView) findViewById(R.id.login_form);
        loginForm.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        mName = (EditText) findViewById(R.id.text_login_name);
        mPhoneNumber = (EditText) findViewById(R.id.text_login_phone_number);

        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegistration() {
        // Reset errors.
        mName.setError(null);
        mPhoneNumber.setError(null);

        // Store values at the time of the login attempt.
        name = mName.getText().toString();
        phoneNumber = mPhoneNumber.getText().toString();

        boolean cancel = false;

        // Check for a valid name, if the user entered one.
        if (!TextUtils.isEmpty(name) && !isNameValid(name)) {
            Toast.makeText(this, R.string.empty_name, Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(phoneNumber) && isPhoneNumberValid(phoneNumber)) {
            Toast.makeText(this, R.string.empty_phonenumber, Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        createNewUser();

    }

    private void createNewUser() {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        androidId = DeviceHelper.getDeviceId(getApplicationContext());
        nameValuePairs.add(new BasicNameValuePair("androidId", androidId));
        nameValuePairs.add(new BasicNameValuePair("name", name));
        nameValuePairs.add(new BasicNameValuePair("phoneNumber", phoneNumber));

        String url = Params.getApiActionUrl(getApplicationContext(), "user.register");
        mLoginButton.setClickable(false);
        RestUserRegisterTask restTask = new RestUserRegisterTask(this,url,nameValuePairs);
        restTask.execute();
    }

    private boolean isPhoneNumberValid(String phonenumber) {
        return phonenumber.length() > 10;
    }

    private boolean isNameValid(String password) {
        return password.length() > 1;
    }

}

