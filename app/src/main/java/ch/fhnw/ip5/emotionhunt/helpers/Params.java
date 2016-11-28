package ch.fhnw.ip5.emotionhunt.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

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
}
