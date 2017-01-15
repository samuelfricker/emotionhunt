package ch.fhnw.ip5.emotionhunt.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import agency.tango.android.avatarview.views.AvatarView;
import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helpers.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.helpers.UserList;
import ch.fhnw.ip5.emotionhunt.models.LocationHistory;
import ch.fhnw.ip5.emotionhunt.models.ReceivedExperience;
import ch.fhnw.ip5.emotionhunt.models.SentExperience;
import ch.fhnw.ip5.emotionhunt.models.User;
import ch.fhnw.ip5.emotionhunt.tasks.RestAvatarCreateTask;
import ch.fhnw.ip5.emotionhunt.tasks.RestExperienceCreateTask;

public class ProfileActivity extends AppCompatActivity {
    private AvatarView avatarView;
    private KenBurnsView kenBurnsView;
    boolean isOwnUser;
    private TextView txtUserName;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static final String EXTRA_IS_OWN_USER = "IS_OWN_USER";
    public static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initExtras(savedInstanceState);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        initView();
    }

    /**
     * Initializes the passed params (extras)
     * @param savedInstanceState
     */
    private void initExtras(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                isOwnUser = false;
            } else {
                isOwnUser = extras.getBoolean(EXTRA_IS_OWN_USER);
            }
        } else {
            isOwnUser = savedInstanceState.getBoolean(EXTRA_IS_OWN_USER);
        }
    }

    /**
     * Initializes the view and its children incl. required listeners.
     */
    private void initView() {
        kenBurnsView = (KenBurnsView) findViewById(R.id.burns_view);
        avatarView = (AvatarView) findViewById(R.id.avatar_view);
        txtUserName = (TextView) findViewById(R.id.txt_username);
        txtUserName.setText(UserList.getInstance().me != null ? UserList.getInstance().me.name : getString(R.string.me));
        String url = User.getOwnAvatarURL(ProfileActivity.this);
        Log.d(TAG, url);

        if (isOwnUser) {
            Picasso.with(getApplicationContext()).load(url).into(avatarView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Successfully loaded image");
                }

                @Override
                public void onError() {
                    Log.v(TAG,"Could not fetch image");
                }
            });
            loadLastSentExperienceHeader();
            avatarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "on avatar click");
                    callImagePicker();
                }
            });
        }
    }

    /**
     * Gets the last sent experience to replace the diagonal header's background image.
     */
    private void loadLastSentExperienceHeader() {
        ArrayList<ReceivedExperience> experiences = SentExperience.getAll(getApplicationContext());
        if (experiences.size() > 0) {
            ReceivedExperience experience = experiences.get(0);
            Bitmap mediaBitmap = DeviceHelper.loadImageFromStorage(experience.filename,
                    ProfileActivity.this);
            if (mediaBitmap != null) kenBurnsView.setImageBitmap(mediaBitmap);
        }
    }

    /**
     * Generates and shows an alert dialog to select a photo either
     * from camera or from the device's gallery.
     */
    private void callImagePicker() {
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/emotionhunt/Pictures",
                "/sdcard/emotionhunt/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(ProfileActivity.this);
        CroperinoFileUtil.setupDirectory(ProfileActivity.this);

        final CharSequence[] items = {
                getString(R.string.take_photo),
                getString(R.string.choose_from_library),
                getString(R.string.cancel)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle(R.string.add_photo);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    //Prepare Camera
                    prepareCamera();
                } else if (items[item].equals(getString(R.string.choose_from_library))) {
                    //Prepare Gallery
                    Croperino.prepareGallery(ProfileActivity.this);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * Prepares the camera and checks the required camera permissions.
     */
    private void prepareCamera() {
        int hasWriteCameraPermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasWriteCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            if (hasWriteCameraPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        try {
            Croperino.prepareCamera(ProfileActivity.this);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepareCamera();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == ExperienceCreateActivity.RESULT_OK) {
                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), ProfileActivity.this,
                            true, 1, 1, 0, 0);
                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == ExperienceCreateActivity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, ProfileActivity.this);
                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), ProfileActivity.this,
                            true, 1, 1, 0, 0);
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == ExperienceCreateActivity.RESULT_OK) {
                    File imageFile = CroperinoFileUtil.getmFileTemp();
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),bmOptions);
                    avatarView.setImageBitmap(bitmap);
                    sendApi(bitmap);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Sends the profile picture to the server API and updates the current
     * profile picture.
     * @param bitmap
     */
    private void sendApi(Bitmap bitmap) {
        String url = Params.getApiActionUrl(ProfileActivity.this, "avatar.create");
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("androidId",
                String.valueOf(DeviceHelper.getDeviceId(ProfileActivity.this))));
        RestAvatarCreateTask restTask = new RestAvatarCreateTask(ProfileActivity.this,
                url, nameValuePairs, bitmap);
        restTask.execute();
    }
}
