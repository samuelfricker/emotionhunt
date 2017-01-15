package ch.fhnw.ip5.emotionhunt.tasks;

import android.content.Context;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * This RestTask is a base class for all API-Call Task classes.
 */
public abstract class RestTask extends AsyncTask<String, Integer, Boolean> {
    public static final String TAG = "RestTask";
    public static final String HTTP_RESPONSE = "httpResponse";
    public static final String API_KEY = "bENFnP63CqNFDSucAFguwj7p685Z2eh3";

    protected Context mContext;
    protected String mUrl;
    protected List<NameValuePair> mNameValuePairs;

    /**
     * Defines a standard http header for http requests to the server.
     * Cookies and keep-alive headers are set because of server restrictions.
     *
     * IMPORTANT: Make sure to validate changed headers with multiple devices at the same time
     * from the same network (public IP). It may could happen that the Server Provider will
     * block your public IP because of a potential "attack" detected by it's firewall.
     *
     * @param httpPost
     * @return
     */
    public HttpPost setHeaderHttpPost(HttpPost httpPost) {
        httpPost.setHeader("Accept","application/json");
        httpPost.setHeader("Host", "emotionhunt.com");
        httpPost.setHeader("Keep-Alive", "timeout=5, max 15");
        httpPost.setHeader("Cookie","_ga=GA1.2.445309305.1484040632; _gat=1");
        httpPost.setHeader("Cache-Control", "max-age=0");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        return httpPost;
    }

    /**
     * C'tor
     * @param context Context
     * @param url URL for API Call
     * @param nameValuePairs NameValuePairs (params)
     */
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
