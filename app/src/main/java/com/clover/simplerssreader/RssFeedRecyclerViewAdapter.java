package com.clover.simplerssreader;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aleksandrgranin on 26/01/2018.
 */

public class RssFeedRecyclerViewAdapter  extends RecyclerView.Adapter<RssFeedRecyclerViewAdapter.ViewHolder>  {
    private List<RssFeed> mItems = new ArrayList<>();
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public RssFeed mBoundString;

        final View mRootView;
        @BindView(R.id.tv_feed_title)
        TextView mFeedTitle;
        @BindView(R.id.tv_feed_description)
        TextView mFeedDescription;
        @BindView(R.id.tv_feed_count)
        TextView mFeedCount;
        @BindView(R.id.iv_feed_image)
        ImageView mFeedImage;

        ViewHolder(View view) {
            super(view);
            mRootView = view;
            ButterKnife.bind(this, view);
            // Make this view clickable
            view.setClickable(true);
        }
    }

    public RssFeedRecyclerViewAdapter(Context context, List<RssFeed> items) {
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
    public RssFeedRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new RssFeedRecyclerViewAdapter.ViewHolder(inflater.inflate(R.layout.rssfeed_list_item, parent, false));
    }
    @Override
    public void onBindViewHolder(final RssFeedRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.mBoundString = mItems.get(position);

        holder.mFeedTitle.setText(TextUtils.isEmpty(holder.mBoundString.title) ? holder.mBoundString.feedUrl : holder.mBoundString.title);
        holder.mFeedDescription.setText(TextUtils.isEmpty(holder.mBoundString.description) ? "description not available" : holder.mBoundString.description);
        if (holder.mBoundString.getFeedItems() != null && holder.mBoundString.getFeedItems().size() > 0){
            holder.mFeedCount.setText(String.valueOf(holder.mBoundString.getFeedItems().size()));
            holder.mFeedCount.setVisibility(View.VISIBLE);
        } else {
            holder.mFeedCount.setVisibility(View.GONE);
        }
        try {
            Glide.with(mContext)

                    .load(holder.mBoundString.image)
                    .into(holder.mFeedImage);
        }catch (Exception e){
            Log.e("RssFeedRecyclerAdapter", e.getMessage(), e);
        }

    }
}
