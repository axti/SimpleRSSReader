package com.clover.simplerssreader;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<RssFeed>> {
    final String LOG_TAG = MainActivity.class.getSimpleName();

    ArrayList<RssFeed> mRssFeedList = new ArrayList<>();
    RssFeedRecyclerViewAdapter rvAdapter;

    @BindView(R.id.rvRssFeed)
    RecyclerView rvRssFeed;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.toolbar_progress_bar)
    ProgressBar progressBar2;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private boolean needDownload = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


     /*   fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        rvAdapter = new RssFeedRecyclerViewAdapter(this, mRssFeedList);
        rvRssFeed.setHasFixedSize(true);
        rvRssFeed.addItemDecoration(new MarginDecoration(this));
        rvRssFeed.setAdapter(rvAdapter);

        ItemClickSupport.addTo(rvRssFeed).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
                Log.i(LOG_TAG, "OnItemClick " + position);
                if (position >= 0 && position < mRssFeedList.size()) {
                    RssFeed rssFeed = mRssFeedList.get(position);
                    FeedActivity.openFeedFor(MainActivity.this,
                            rssFeed.id,
                            TextUtils.isEmpty(rssFeed.title) ? rssFeed.feedUrl : rssFeed.title,
                            progressBar2.getVisibility() == View.VISIBLE);
                }
            }
        });

        ItemClickSupport.addTo(rvRssFeed).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                Log.i(LOG_TAG, "OnItemLongClick " + position);
                RssFeed rssFeed = mRssFeedList.get(position);
                mRssFeedList.remove(rssFeed);
                Log.i(LOG_TAG, "delete item: " + rssFeed.delete());
                rvAdapter.notifyItemRemoved(position);
                return false;
            }
        });

        getLoaderManager().initLoader(0, null, this); //.forceLoad();
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadFinishedReceiver
                , new IntentFilter(BaseLoader.DOWNLOAD_ACTION));
    }

    private void refreshList(){
        Log.d(LOG_TAG, "refreshList");
        getLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public Loader<List<RssFeed>> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        return new RssDB.DBFListLoader<>(this, SQLite.select().from(RssFeed.class));
    }

    @Override
    public void onLoadFinished(Loader<List<RssFeed>> loader, List<RssFeed> data) {
        Log.d(LOG_TAG, "onLoadFinished");
        if (data != null) {
            mRssFeedList.clear();
            mRssFeedList.addAll(data);
            Log.d(LOG_TAG, mRssFeedList.size() + " rows");
            if (needDownload)
            {
                needDownload = false;
                updateCache();
            }
        } else {
            Log.d(LOG_TAG, "0 rows");
        }
        rvAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private void updateCache() {
        Log.d(LOG_TAG, "updateCache");
        setProgressBarIndeterminateVisibility(true);
        progressBar2.setVisibility(View.VISIBLE);
        BaseLoader.startDownload(this, mRssFeedList);
        /*Snackbar.make(rvRssFeed, "Updating...", Snackbar.LENGTH_LONG)
                .setAction("Close", null).show();*/
    }

    @Override
    public void onLoaderReset(Loader<List<RssFeed>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_test) {
            createNewFeed("https://news.google.com/news/rss");

            return true;
        } else if (id == R.id.action_clear){
            for (RssFeed rssFeed : mRssFeedList){
                Log.i(LOG_TAG, "delete item: " + rssFeed.delete());
            }
            mRssFeedList.clear();
            rvAdapter.notifyDataSetChanged();
            return true;
        } else if(id == R.id.action_refresh){
            updateCache();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewFeed(String feedUrl) {
        boolean urlExist = urlIsExistInDB(feedUrl);
        if (urlExist){
            Snackbar.make(rvRssFeed, "This RSS url is exist in DB", Snackbar.LENGTH_SHORT).show();
        } else {
            RssFeed rssfeed = new RssFeed();
            rssfeed.feedUrl = feedUrl;
            rssfeed.storeFeed();
            mRssFeedList.add(rssfeed);
            needDownload = true;
            refreshList();
        }

    }

    private boolean urlIsExistInDB(String feedUrl) {
        RssFeed rssFeed = SQLite.select(RssFeed_Table.feedUrl).from(RssFeed.class).where(RssFeed_Table.feedUrl.eq(feedUrl)).querySingle();
        return rssFeed != null;
    }

    BroadcastReceiver downloadFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onReceive");
            refreshList();
            setProgressBarIndeterminateVisibility(false);
            progressBar2.setVisibility(View.INVISIBLE);
            /*Snackbar.make(rvRssFeed, "Updating has been ended", Snackbar.LENGTH_SHORT).show();*/
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadFinishedReceiver);
        super.onDestroy();
    }

    @OnClick(R.id.fab)
    void addCargoClick() {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.url_add_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.url);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String uri = userInput.getText().toString();
                                if (uri.length() == 0)
                                    Snackbar.make(rvRssFeed, "URL is empty", Snackbar.LENGTH_SHORT).show();
                                else if (uri.toLowerCase().contains("http://") ||
                                        uri.toLowerCase().contains("https://"))
                                    createNewFeed(uri);
                                else
                                    Snackbar.make(rvRssFeed, "URL is invalid format", Snackbar.LENGTH_SHORT).show();

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.feeditem_background));
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }
}
