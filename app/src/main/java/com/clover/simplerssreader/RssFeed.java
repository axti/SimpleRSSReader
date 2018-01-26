package com.clover.simplerssreader;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.List;
import java.util.UUID;

/**
 * Created by aleksandrgranin on 25/01/2018.
 */

@Table(database = RssDB.class)
public class RssFeed  extends BaseModel implements Parcelable {

    @PrimaryKey
    public UUID id;

    @Column
    public String title;

    @Column(defaultValue = "non description")
    public String description;

    @Column(defaultValue = "https://developer.android.com/images/brand/Android_Robot_100.png")
    public String image;

    @Column
    public String feedUrl;

    public List<RssFeedItem> feedItems;

    @OneToMany(methods = {OneToMany.Method.ALL})
    List<RssFeedItem> getFeedItems() {
        feedItems =  SQLite.select().from(RssFeedItem.class)
                .where(RssFeedItem_Table.feedId.eq(id)).queryList();
        return feedItems;
    }



    @Override
    public boolean save() {
        idCheck();
        return super.save();
    }

    @Override
    public boolean save(@NonNull DatabaseWrapper databaseWrapper) {
        idCheck();
        return super.save(databaseWrapper);
    }

    private void idCheck() {
        if(id == null)
            id = UUID.randomUUID();
    }

    public void storeFeed() {
        DatabaseWrapper wrapper = FlowManager.getWritableDatabase(RssDB.class);
        wrapper.beginTransaction();
        idCheck();
        if (this.feedItems != null) {
            for (RssFeedItem feedItem : this.feedItems) {
                feedItem.feedId = id;
                feedItem.save(wrapper);
            }
        }
        save(wrapper);
        wrapper.setTransactionSuccessful();
        wrapper.endTransaction();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.image);
        dest.writeString(this.feedUrl);
        dest.writeTypedList(this.feedItems);
    }

    public RssFeed() {
    }

    protected RssFeed(Parcel in) {
        this.id = (UUID) in.readSerializable();
        this.title = in.readString();
        this.description = in.readString();
        this.image = in.readString();
        this.feedUrl = in.readString();
        this.feedItems = in.createTypedArrayList(RssFeedItem.CREATOR);
    }

    public static final Creator<RssFeed> CREATOR = new Creator<RssFeed>() {
        @Override
        public RssFeed createFromParcel(Parcel source) {
            return new RssFeed(source);
        }

        @Override
        public RssFeed[] newArray(int size) {
            return new RssFeed[size];
        }
    };

    @Override
    public String toString() {
        return "RssFeed{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", feedUrl='" + feedUrl + '\'' +
                ", feedItems=" + (feedItems != null ? feedItems.size() : "null") +
                '}';
    }
}
