package ch.fhnw.ip5.emotionhunt.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helpers.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.tasks.RestUserRegisterTask;

/**
 * A login screen that offers login via name/phonenumber.
 */
public class OnBoardActivity extends AppCompatActivity {
    private static final String TAG = OnBoardActivity.class.getSimpleName();
    private EditText mName;
    private EditText mPhoneNumber;
    private LinearLayout loginForm;
    private Button mLoginButton;
    private String androidId;
    public String name;
    public String phoneNumber;
    public RestUserRegisterTask restTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        loginForm = (LinearLayout) findViewById(R.id.superLinerLayout);
        mName = (EditText) findViewById(R.id.text_login_name);
        mPhoneNumber = (EditText) findViewById(R.id.text_login_phone_number);
        mLoginButton = (Button) findViewById(R.id.login_button);

        int mUIFlag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        getWindow().getDecorView().setSystemUiVisibility(mUIFlag);

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
        //reset errors.
        mName.setError(null);
        mPhoneNumber.setError(null);

        //store values at the time of the login attempt.
        name = mName.getText().toString();
        phoneNumber = mPhoneNumber.getText().toString();

        boolean cancel = false;

        //check for a valid name, if the user entered one.
        if (!isNameValid(name)) {
            cancel = true;
        }

        //check for a valid email address.
        if (!isPhoneNumberValid(phoneNumber)) {
            Toast.makeText(this, R.string.empty_phonenumber, Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        if(!cancel) createNewUser();

    }

    /**
     * Creates a new user on the API by a given name and phone number and the
     * device id.
     */
    private void createNewUser() {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        androidId = DeviceHelper.getDeviceId(getApplicationContext());
        nameValuePairs.add(new BasicNameValuePair("androidId", androidId));
        nameValuePairs.add(new BasicNameValuePair("name", name));
        nameValuePairs.add(new BasicNameValuePair("phoneNumber", phoneNumber));

        String url = Params.getApiActionUrl(getApplicationContext(), "user.register");
        restTask = new RestUserRegisterTask(this,url,nameValuePairs);
        restTask.execute();
    }

    /**
     * Validates the phone number
     * @param phoneNumber
     * @return either the number is valid or not
     */
    private boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.length() == 10;
    }

    /**
     * Validates the user name
     * @param name
     * @return either the user name is valid or not (allowed chars: a-zA-Z0-9)
     */
    private boolean isNameValid(String name) {
        Pattern p = Pattern.compile("[a-zA-Z0-9]+");
        if(!p.matcher(name).matches()){
            Toast.makeText(this, R.string.no_umlauts, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(name.length() < 1 || name.length() > 10){
            Toast.makeText(this, R.string.empty_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}

