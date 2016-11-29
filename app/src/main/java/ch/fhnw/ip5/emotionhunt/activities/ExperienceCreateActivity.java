package ch.fhnw.ip5.emotionhunt.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helper.DeviceHelper;
import ch.fhnw.ip5.emotionhunt.helpers.Params;
import ch.fhnw.ip5.emotionhunt.models.LocationHistory;
import ch.fhnw.ip5.emotionhunt.tasks.RestUserListTask;

public class ExperienceCreateActivity extends AppCompatActivity {

    TabHost mTabHost;
    ImageView imgPreview;
    ImageView imgIcon;
    LinearLayout linearLayoutPrivate;

    public static final int REQUEST_CODE_IMAGE_PICKER = 1;
    public static final String TAG = "ExperienceCreateAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_create);

        mTabHost = (TabHost)findViewById(R.id.tab_host_experience);
        mTabHost.setup();

        mTabHost.addTab(mTabHost.newTabSpec("tab_private").setIndicator(getString(R.string.experience_private)).setContent(R.id.tab_experience_private));
        mTabHost.addTab(mTabHost.newTabSpec("tab_public").setIndicator(getString(R.string.experience_public)).setContent(R.id.tab_experience_public));

        mTabHost.setCurrentTab(0);

        initView();
    }

    private void initView() {
        imgIcon = (ImageView) findViewById(R.id.img_experience_icon);
        imgPreview = (ImageView) findViewById(R.id.img_experience_preview);
        linearLayoutPrivate = (LinearLayout) findViewById(R.id.layout_experience_private);

        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    private void callImagePicker() {
        ImagePicker.create(this)
                .folderMode(true) // folder mode (false by default)
                .folderTitle("Folder") // folder selection title
                .imageTitle("Tap to select") // image selection title
                .single() // single mode
                .limit(1) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                .start(REQUEST_CODE_IMAGE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK && data != null) {
            Log.d(TAG, "onActivityResult");
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            for (Image image : images) {
                File imageFile = new File(image.getPath());
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),bmOptions);
                imgPreview.setImageBitmap(bitmap);
            }
            // do your logic ....
        }
    }

    private void initUserList() {
        Context context = getApplicationContext();
        String url = Params.getApiActionUrl(context, "user.get");
        RestUserListTask restTask = new RestUserListTask(context,url,null,this);
        restTask.execute();
    }
}
