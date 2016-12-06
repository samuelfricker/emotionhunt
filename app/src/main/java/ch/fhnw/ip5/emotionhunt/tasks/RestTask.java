package ch.fhnw.ip5.emotionhunt.tasks;

import android.app.ProgressDialog;
import android.content.Context;

import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ip5.emotionhunt.R;


/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.tasks
 *
 * @author Benjamin Bur
 */

public abstract class RestTask extends AsyncTask<String, Integer, Boolean> {
    public static final String TAG = "RestTask";
    public static final String HTTP_RESPONSE = "httpResponse";
    public static final String API_KEY = "bENFnP63CqNFDSucAFguwj7p685Z2eh3";

    protected Context mContext;
    protected String mUrl;
    protected List<NameValuePair> mNameValuePairs;

    public HttpPost setHeaderHttpPost(HttpPost httpPost) {
        httpPost.setHeader("Accept","application/json");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        return httpPost;
    }

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
