package ch.fhnw.ip5.emotionhunt.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.preference.Preference;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.security.AccessController.getContext;


/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.helper
 *
 * @author Benjamin Bur
 */

public class DeviceHelper {
    private static final String PATH_EXPERIENCES_PHOTO = "experiences";

    public static String getDeviceId (Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static String getAppVersion (Activity activity) {
        PackageManager manager = activity.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
        String version = info.versionName;
        return version;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Returns a new File that contains the fully path to the experience photo
     * @param filename
     * @param context
     * @return
     */
    private static File getExperiencePhotoPath(String filename, Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(PATH_EXPERIENCES_PHOTO, Context.MODE_PRIVATE);
        return new File(directory, filename);
    }

    /**
     * Stores an experience bitmap on local storage.
     * @param bitmapImage
     * @param context
     * @param filename
     * @return
     */
    public static boolean saveBitmap(Bitmap bitmapImage, Context context, String filename) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(getExperiencePhotoPath(filename, context));
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fos.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Loads an experience photo from local storage.
     * @param filename
     * @param context
     * @return
     */
    public static Bitmap loadImageFromStorage(String filename, Context context) {
        try {
            File f=getExperiencePhotoPath(filename, context);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
    }
}
