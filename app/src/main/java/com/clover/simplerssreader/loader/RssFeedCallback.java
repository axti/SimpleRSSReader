package com.clover.simplerssreader.loader;

import android.util.Log;

import com.clover.simplerssreader.model.RssFeed;
import com.clover.simplerssreader.saxparse.RssReader;

import org.xml.sax.SAXException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by aleksandrgranin on 26/01/2018.
 */

public class RssFeedCallback implements Callback {
    final String LOG_TAG = RssFeedCallback.class.getSimpleName();
    private RssFeed rssFeed;
    private ResponseListener listener;

    public RssFeedCallback(RssFeed rssFeed, ResponseListener listener) {
        this.rssFeed = rssFeed;
        this.listener = listener;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.e(LOG_TAG, "ERROR");
//        rssFeed.description = e.getMessage();
//        rssFeed.update();
        this.listener.onResponse();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.d(LOG_TAG, "PASS");

        try {
            RssReader.read(response.body().byteStream(), rssFeed);
        } catch (SAXException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        this.listener.onResponse();
    }
}
