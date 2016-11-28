package ch.fhnw.ip5.emotionhunt.helper;

import android.content.Context;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import static java.security.AccessController.getContext;


/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.helper
 *
 * @author Benjamin Bur
 */

public class DeviceHelper {

    public static String getDeviceId (Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }
}
