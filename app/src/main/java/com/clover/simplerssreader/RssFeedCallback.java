package com.clover.simplerssreader;

import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Connection;
import okhttp3.Request;
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
        Log.e(LOG_TAG, e.getMessage());
        rssFeed.description = e.getMessage();
        rssFeed.update();
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
