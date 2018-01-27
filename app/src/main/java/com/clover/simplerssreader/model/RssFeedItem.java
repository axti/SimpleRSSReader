package com.clover.simplerssreader.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.UUID;

/**
 * Created by aleksandrgranin on 25/01/2018.
 */

@Table(database = RssDB.class)
public class RssFeedItem  extends BaseModel implements Parcelable {

    @PrimaryKey
    public UUID id;

    @Column(defaultValue = "title")
    public String title;

    @Column(defaultValue = "description")
    public String description;

    @Column(defaultValue = "publicationDate")
    public String pubDate;

    @Column(defaultValue = "link")
    public String link;

    @Column
    @ForeignKey(tableClass = RssFeed.class, onDelete = ForeignKeyAction.CASCADE,
            references = @ForeignKeyReference(columnName = "feedId", foreignKeyColumnName = "id"))
    public UUID feedId;


    private void idCheck() {
        if(id == null)
            id = UUID.randomUUID();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.pubDate);
        dest.writeString(this.link);
        dest.writeSerializable(this.feedId);
    }

    public RssFeedItem() {
    }

    protected RssFeedItem(Parcel in) {
        this.id = (UUID) in.readSerializable();
        this.title = in.readString();
        this.description = in.readString();
        this.pubDate = in.readString();
        this.link = in.readString();
        this.feedId = (UUID) in.readSerializable();
    }

    public static final Creator<RssFeedItem> CREATOR = new Creator<RssFeedItem>() {
        @Override
        public RssFeedItem createFromParcel(Parcel source) {
            return new RssFeedItem(source);
        }

        @Override
        public RssFeedItem[] newArray(int size) {
            return new RssFeedItem[size];
        }
    };
}
