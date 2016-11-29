package ch.fhnw.ip5.emotionhunt.tasks;

import android.content.Context;

import android.os.AsyncTask;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.tasks
 *
 * @author Benjamin Bur
 */

public abstract class RestTask extends AsyncTask<String, Void, Boolean> {
    public static final String TAG = "RestTask";
    public static final String HTTP_RESPONSE = "httpResponse";
    public static final String API_KEY = "bENFnP63CqNFDSucAFguwj7p685Z2eh3";

    protected Context mContext;
    protected String mUrl;
    protected List<NameValuePair> mNameValuePairs;

    public RestTask(Context context, String url, List<NameValuePair> nameValuePairs)
    {
        mContext = context;
        mUrl = url;
        if (nameValuePairs == null) {
            nameValuePairs = new ArrayList<>();
        }
        mNameValuePairs = nameValuePairs;
        mNameValuePairs.add(new BasicNameValuePair("apiKey", API_KEY));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
