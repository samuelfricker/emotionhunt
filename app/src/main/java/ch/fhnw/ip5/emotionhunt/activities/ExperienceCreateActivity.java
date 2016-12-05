package ch.fhnw.ip5.emotionhunt.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.File;
import java.util.ArrayList;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helpers.EmotionPickerDialog;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.helpers.UserList;
import ch.fhnw.ip5.emotionhunt.models.Emotion;
import ch.fhnw.ip5.emotionhunt.models.Experience;
import ch.fhnw.ip5.emotionhunt.models.LocationHistory;
import ch.fhnw.ip5.emotionhunt.models.SentExperience;
import ch.fhnw.ip5.emotionhunt.models.User;
import ch.fhnw.ip5.emotionhunt.tasks.RestUserListTask;

public class ExperienceCreateActivity extends AppCompatActivity {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    TabHost mTabHost;
    ImageView imgPreview;
    ImageView imgIcon;
    LinearLayout linearLayoutPrivate;
    ArrayList<User> users;
    TextView textView;
    Bitmap experienceImage;
    boolean isPublic = false;

    private Emotion mExpectedEmotion;

    public static final int REQUEST_CODE_IMAGE_PICKER = 1;
    public static final String TAG = "ExperienceCreateAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_create);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_experience_create);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mTabHost = (TabHost)findViewById(R.id.tab_host_experience);
        mTabHost.setup();

        mTabHost.addTab(mTabHost.newTabSpec("tab_private").setIndicator(getString(R.string.experience_private)).setContent(R.id.tab_experience_private));
        mTabHost.addTab(mTabHost.newTabSpec("tab_public").setIndicator(getString(R.string.experience_public)).setContent(R.id.tab_experience_public));

        mTabHost.setCurrentTab(0);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                isPublic = mTabHost.getCurrentTab() == 1;
            }
        });

        initView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_create_experience, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            switch (item.getItemId()) {
                case R.id.btn_create_experience_send:
                    if (validateExperience()) {
                        sendExperience();
                    }
                    return true;
                case android.R.id.home:
                    finish();
                    return true;
                default:
                    throw new IllegalArgumentException("Invalid Action Menu Item");
            }
        } catch (Exception e) {
            Log.d(TAG, "Could not found appropriate Menu Action. Error Message: "+e.getMessage());
        }
        return true;
    }

    private void initView() {
        imgIcon = (ImageView) findViewById(R.id.img_experience_icon);
        imgPreview = (ImageView) findViewById(R.id.img_experience_preview);
        linearLayoutPrivate = (LinearLayout) findViewById(R.id.layout_experience_private);
        textView = (TextView) findViewById(R.id.txt_experience_create_text);

        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new EmotionPickerDialog(ExperienceCreateActivity.this) {
                    @Override
                    public void onImgClick(int emotion) {
                        mExpectedEmotion = new Emotion();
                        double expValue = 0.9999999999999;
                        switch (emotion) {
                            case EmotionPickerDialog.EMOTION_ANGER :
                                mExpectedEmotion.setAnger(expValue);
                                imgIcon.setImageResource(R.drawable.img_emotion_anger);
                                break;
                            case EmotionPickerDialog.EMOTION_CONTEMPT :
                                mExpectedEmotion.setContempt(expValue);
                                imgIcon.setImageResource(R.drawable.img_emotion_contempt);
                                break;
                            case EmotionPickerDialog.EMOTION_DISGUST :
                                mExpectedEmotion.setDisgust(expValue);
                                imgIcon.setImageResource(R.drawable.img_emotion_disgust);
                                break;
                            case EmotionPickerDialog.EMOTION_FEAR :
                                mExpectedEmotion.setFear(expValue);
                                imgIcon.setImageResource(R.drawable.img_emotion_fear);
                                break;
                            case EmotionPickerDialog.EMOTION_HAPPINESS :
                                mExpectedEmotion.setHappiness(expValue);
                                imgIcon.setImageResource(R.drawable.img_emotion_happiness);
                                break;
                            case EmotionPickerDialog.EMOTION_NEUTRAL :
                                mExpectedEmotion.setNeutral(expValue);
                                imgIcon.setImageResource(R.drawable.img_emotion_neutral);
                                break;
                            case EmotionPickerDialog.EMOTION_SADNESS :
                                imgIcon.setImageResource(R.drawable.img_emotion_sadness);
                                mExpectedEmotion.setSadness(expValue);
                                break;
                            case EmotionPickerDialog.EMOTION_SURPRISE :
                                imgIcon.setImageResource(R.drawable.img_emotion_surprise);
                                mExpectedEmotion.setSurprise(expValue);
                                break;
                            default:
                                imgIcon.setImageResource(R.drawable.img_questionmark);
                                mExpectedEmotion = null;
                                break;
                        }
                        dismiss();
                    }
                };
                dialog.show();
            }
        });
        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callImagePicker();
            }
        });

        initUserList();
    }

    private void prepareCamera() {
        int hasWriteCameraPermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasWriteCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            if (hasWriteCameraPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        try {
            Croperino.prepareCamera(ExperienceCreateActivity.this);
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

                } else {
                    Toast.makeText(this, "Nice try ;) This doesn't work!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void callImagePicker() {
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/emotionhunt/Pictures", "/sdcard/emotionhunt/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(ExperienceCreateActivity.this);
        CroperinoFileUtil.setupDirectory(ExperienceCreateActivity.this);

        final CharSequence[] items = {
                getString(R.string.take_photo),
                getString(R.string.choose_from_library),
                getString(R.string.cancel)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ExperienceCreateActivity.this);
        builder.setTitle(R.string.add_photo);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    //Prepare Camera
                    prepareCamera();
                } else if (items[item].equals(getString(R.string.choose_from_library))) {
                    //Prepare Gallery
                    Croperino.prepareGallery(ExperienceCreateActivity.this);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == ExperienceCreateActivity.RESULT_OK) {
                    /* Parameters of runCropImage = File, Activity Context, Image is Scalable or Not, Aspect Ratio X, Aspect Ratio Y, Button Bar Color, Background Color */
                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), ExperienceCreateActivity.this, true, 1, 1, 0, 0);
                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == ExperienceCreateActivity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, ExperienceCreateActivity.this);
                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), ExperienceCreateActivity.this, true, 1, 1, 0, 0);
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == ExperienceCreateActivity.RESULT_OK) {
                    File imageFile = CroperinoFileUtil.getmFileTemp();
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),bmOptions);
                    experienceImage = bitmap;
                    imgPreview.setImageBitmap(bitmap);
                }
                break;
            default:
                break;
        }

        /*if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK && data != null) {
            Log.d(TAG, "onActivityResult");
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            for (Image image : images) {
                File imageFile = new File(image.getPath());
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),bmOptions);
                experienceImage = bitmap;
                imgPreview.setImageBitmap(bitmap);
            }
            // do your logic ....
        }*/
    }

    /**
     * Initializes the user list (checkbox list)
     */
    private void initUserList() {
        users = new ArrayList<>();
        Context context = getApplicationContext();
        String url = Params.getApiActionUrl(context, "user.get");
        RestUserListTask restTask = new RestUserListTask(context,url,null,this);
        restTask.execute();
    }

    /**
     * Validates the experience form and returns if the validation was successful or not.
     * @return validation
     */
    private boolean validateExperience() {
        boolean validation = true;
        ArrayList<String> toastErrorMessages = new ArrayList<>();

        //validate text input
        if (textView.getText().toString().length() == 0) {
            textView.setError(getString(R.string.text_is_empty));
            validation = false;
        } else {
            textView.setError(null);
        }

        //validate recipients
        if (!isPublic && getRecipients().size() == 0) {
            toastErrorMessages.add(getString(R.string.recipients_are_empty));
        }

        //validate expected emotion
        if (getExpectedEmotion() == null) {
            toastErrorMessages.add(getString(R.string.expected_emotion_is_empty));
        }

        //validate media
        if (experienceImage == null) {
            toastErrorMessages.add(getString(R.string.media_is_empty));
        }

        if (toastErrorMessages.size() > 0) {
            Toast.makeText(this, android.text.TextUtils.join(" ", toastErrorMessages), Toast.LENGTH_SHORT).show();
            validation = false;
        }

        return validation;
    }

    /**
     * Sends the experience trough the Server API
     */
    private void sendExperience() {
        SentExperience sentExperience = new SentExperience();
        //TODO add option for visibility duration
        sentExperience.visibilityDuration = 24;
        sentExperience.createdAt = (int) (System.currentTimeMillis() / 1000L);
        sentExperience.text = textView.getText().toString();
        sentExperience.recipients = getRecipients();

        //get location from last stored location
        LocationHistory location = LocationHistory.getLastPositionHistory(getApplicationContext());
        sentExperience.lat = location.lat;
        sentExperience.lon = location.lon;
        sentExperience.isPublic = isPublic;
        sentExperience.image = experienceImage;
        //set expected emotion
        sentExperience.expectedEmotion = getExpectedEmotion();
        sentExperience.sendApi(this);
    }

    /**
     * Returns an array with the selected recipient user ids.
     * @return recipients
     */
    private ArrayList<Integer> getRecipients() {
        ArrayList<Integer> recipients = new ArrayList<>();
        UserList userList = UserList.getInstance();
        for (User user : userList.recipients) {
            recipients.add((int) user.id);
        }

        return recipients;
    }

    /**
     * Returns the expected emotion set by the emotion in the header part.
     * @return expected emotion object
     */
    private Emotion getExpectedEmotion() {
        return mExpectedEmotion;
    }
}
