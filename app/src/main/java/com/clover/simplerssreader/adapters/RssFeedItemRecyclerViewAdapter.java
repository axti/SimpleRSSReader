package com.clover.simplerssreader.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clover.simplerssreader.helper.HTMLImageGetter;
import com.clover.simplerssreader.R;
import com.clover.simplerssreader.model.RssFeedItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aleksandrgranin on 26/01/2018.
 */

public class RssFeedItemRecyclerViewAdapter extends RecyclerView.Adapter<RssFeedItemRecyclerViewAdapter.ViewHolder>  {
    private List<RssFeedItem> mItems = new ArrayList<>();
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public RssFeedItem mBoundString;

        final View mRootView;
        @BindView(R.id.tv_feed_item_title)
        TextView mFeedItemTitle;
        @BindView(R.id.tv_feed_item_description)
        TextView mFeedItemDescription;
        @BindView(R.id.tv_feed_item_pubdate)
        TextView mFeedItemPubDate;


        ViewHolder(View view) {
            super(view);
            mRootView = view;
            ButterKnife.bind(this, view);
            // Make this view clickable
            view.setClickable(true);
        }
    }

    public RssFeedItemRecyclerViewAdapter(Context context, List<RssFeedItem> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    @Override
    public int getItemViewType(int position) {
        return 0;
    }
    @Override
    public RssFeedItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new RssFeedItemRecyclerViewAdapter.ViewHolder(inflater.inflate(R.layout.rssfeeditem_list_item, parent, false));
    }
    @Override
    public void onBindViewHolder(final RssFeedItemRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.mBoundString = mItems.get(position);

        holder.mFeedItemTitle.setText(holder.mBoundString.title);
        HTMLImageGetter imageGetter = new HTMLImageGetter(mContext, holder.mFeedItemDescription);
        holder.mFeedItemDescription.setText(Html.fromHtml(holder.mBoundString.description, imageGetter, null));
        holder.mFeedItemPubDate.setText(holder.mBoundString.pubDate);
    }
}