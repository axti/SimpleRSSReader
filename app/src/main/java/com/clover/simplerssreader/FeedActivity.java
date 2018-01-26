package com.clover.simplerssreader;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aleksandrgranin on 26/01/2018.
 */

public class FeedActivity  extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<RssFeedItem>> {
    final String LOG_TAG = FeedActivity.class.getSimpleName();
    private static final String FEED_ID = "FEED_ID";
    private static final String FEED_TITLE = "FEED_TITLE";
    private static final String FEED_LOADING = "FEED_LOADING";
    ArrayList<RssFeedItem> mRssFeedItemList = new ArrayList<>();
    RssFeedItemRecyclerViewAdapter rvAdapter;

    @BindView(R.id.rvRssFeed)
    RecyclerView rvRssFeed;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.toolbar_progress_bar)
    ProgressBar progressBar2;

    private UUID feedID;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    public static void openFeedFor(Context context,
                                   UUID feedID,
                                   String feedTitle,
                                   boolean isLodaing) {
        Intent intent = new Intent(context, FeedActivity.class);
        intent.putExtra(FEED_ID, feedID);
        intent.putExtra(FEED_TITLE, feedTitle);
        intent.putExtra(FEED_LOADING, isLodaing);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra(FEED_TITLE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getBooleanExtra(FEED_LOADING, false))
            progressBar2.setVisibility(View.VISIBLE);

        rvAdapter = new RssFeedItemRecyclerViewAdapter(this, mRssFeedItemList);
        rvRssFeed.setHasFixedSize(true);
        rvRssFeed.addItemDecoration(new MarginDecoration(this));
        rvRssFeed.setAdapter(rvAdapter);

        fab.setVisibility(View.GONE);
        feedID = (UUID) getIntent().getSerializableExtra(FEED_ID);

        getLoaderManager().initLoader(0, null, this).forceLoad();
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadFinishedReceiver
                , new IntentFilter(BaseLoader.DOWNLOAD_ACTION));
    }

    private void refreshList(){
        Log.d(LOG_TAG, "refreshList");
        getLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public Loader<List<RssFeedItem>> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        return new RssDB.DBFListLoader<>(this, SQLite.select()
                .from(RssFeedItem.class)
                .where(RssFeedItem_Table.feedId.eq(feedID)));
    }

    @Override
    public void onLoadFinished(Loader<List<RssFeedItem>> loader, List<RssFeedItem> data) {
        Log.d(LOG_TAG, "onLoadFinished");
        if (data != null) {
            mRssFeedItemList.clear();
            mRssFeedItemList.addAll(data);
            Log.d(LOG_TAG, mRssFeedItemList.size() + " rows");
        } else {
            Log.d(LOG_TAG, "0 rows");
        }
        rvAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<RssFeedItem>> loader) {

    }

    BroadcastReceiver downloadFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onReceive");
            refreshList();
            progressBar2.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadFinishedReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
