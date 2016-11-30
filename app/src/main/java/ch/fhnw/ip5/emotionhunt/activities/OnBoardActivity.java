package ch.fhnw.ip5.emotionhunt.activities;

import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ch.fhnw.ip5.emotionhunt.R;

/**
 * A login screen that offers login via name/phonenumber.
 */
public class OnBoardActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private TextView mName;
    private TextView mPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        // Set up the login form.
        mName = (TextView) findViewById(R.id.text_login_name);
        mPhoneNumber = (TextView) findViewById(R.id.text_login_phone_number);

        Button mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
            }
        });

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mName.setError(null);
        mPhoneNumber.setError(null);

        // Store values at the time of the login attempt.
        String name = mName.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();

        boolean cancel = false;

        // Check for a valid name, if the user entered one.
        if (!TextUtils.isEmpty(name) && !isPasswordValid(name)) {
            Toast.makeText(this, R.string.empty_name, Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(phoneNumber) && isPhoneNumberValid(phoneNumber)) {
            Toast.makeText(this, R.string.empty_phonenumber, Toast.LENGTH_SHORT).show();
            cancel = true;
        }


        mAuthTask = new UserLoginTask(name , phoneNumber);
        mAuthTask.execute((Void) null);

    }

    private boolean isPhoneNumberValid(String phonenumber) {
        //TODO: Replace this with your own logic
        return phonenumber.length() > 10;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 1;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    /**
     * Shows the progress UI and hides the login form.
     */


    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mPhoneNumber;

        UserLoginTask(String name, String phonenumber) {
            mName = name;
            mPhoneNumber = phonenumber;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: Create User on API.

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                finish();
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }
}

