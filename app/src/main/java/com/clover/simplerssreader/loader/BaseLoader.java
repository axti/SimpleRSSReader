package com.clover.simplerssreader.loader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.clover.simplerssreader.model.RssFeed;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by aleksandrgranin on 26/01/2018.
 */

public class BaseLoader extends IntentService implements ResponseListener {
    static final String LOG_TAG = BaseLoader.class.getSimpleName();
    public static final String DOWNLOAD_LIST = "DOWNLOAD_LIST";
    public static final String DOWNLOAD_ACTION = "DOWNLOAD_ACTION";
    OkHttpClient client;
    int itemsCount = 0;
    int responseCount = 0;

    public static void startDownload(Context context, ArrayList<RssFeed> mRssFeedList) {
        Log.i(LOG_TAG, "startDownload items " + mRssFeedList.size());
        Intent intent = new Intent(context, BaseLoader.class);
        intent.putParcelableArrayListExtra(DOWNLOAD_LIST, mRssFeedList);
        context.startService(intent);
    }

    public BaseLoader() {
        super("BaseLoader");
        client = new OkHttpClient();
    }
    ArrayList<RssFeed> items;
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {

            items = intent.getParcelableArrayListExtra(DOWNLOAD_LIST);
            if (items != null && items.size() > 0){
                Log.i(LOG_TAG, "onHandleIntent items " + items.size());
                responseCount = 0;
                itemsCount = items.size();
                updateItem();
//                for (RssFeed rssFeed : items){
//                    makeRequest(rssFeed);
//                    rssFeed.title = "new title";
//                    rssFeed.description = "new description";
//                    rssFeed.feedItems.add(new RssFeedItem());
//                    rssFeed.storeFeed();
//                }
            }
            else{
                sendMessage();
            }
        }
    }

    private void updateItem() {
        Log.i(LOG_TAG, "updateItem " + responseCount);
        if(responseCount>=0 && responseCount< items.size()) {
            makeRequest(items.get(responseCount));
        }
    }

    private void makeRequest(RssFeed rssFeed) {
        Request request = new Request.Builder()
                .url(rssFeed.feedUrl)
                .build();
        RssFeedCallback callback = new RssFeedCallback(rssFeed, this);
        client.newCall(request).enqueue(callback);
    }

    private void sendMessage() {
        Log.i(LOG_TAG, "sendMessage");
        Intent localIntent = new Intent(DOWNLOAD_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    @Override
    public void onResponse() {
        Log.i(LOG_TAG, responseCount + " item updated");
        responseCount++;
        if (itemsCount == responseCount)
            sendMessage();
        else
            updateItem();
    }


}
