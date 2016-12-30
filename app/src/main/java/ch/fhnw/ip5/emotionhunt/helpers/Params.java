package ch.fhnw.ip5.emotionhunt.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.helpers
 *
 * @author Benjamin Bur
 */

public class Params {

    /**
     * Static method that is used for Settings-Methods to get properties from the manifest.
     * @param context Application Context
     * @return Bundle
     */
    private static Bundle getBundle(Context context){
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return ai.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("getBundle", "Failed to load meta-data, NameNotFound: " + e.getMessage());
            return null;
        } catch (NullPointerException e) {
            Log.e("getBundle", "Failed to load meta-data, NullPointer: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns the apiURL depending on the preferences or the meta-information from the
     * AndroidManifest.xml.
     * @param context
     * @return Api-Host-URL
     */
    private static String getApiUrl(Context context){
        return getBundle(context).getString("api.url");
    }

    /**
     * Returns the api action URL.
     * @param context
     * @param actionString e.g. experience.create
     * @return
     */
    public static String getApiActionUrl(Context context,String actionString){
        String apiUrl = getApiUrl(context);
        String action = getBundle(context).getString("api.action." + actionString);
        return apiUrl + action;
    }

    public static Date getDateFromTime(long time) {
        return new java.util.Date(time);
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
