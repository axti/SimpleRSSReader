package com.clover.simplerssreader.model;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.sql.language.BaseModelQueriable;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by aleksandrgranin on 25/01/2018.
 */

@Database(name = RssDB.NAME, version = RssDB.VERSION)
public class RssDB {
    public static final String NAME = "rss_db";

    public static final int VERSION = 1;


    public static class DBFListLoader<T> extends AsyncTaskLoader<List<T>> {
        private BaseModelQueriable<T> query = null;
        private Class<T> clazz = null;
        private BaseModelQueriable queryQM = null;
        public DBFListLoader(Context context, BaseModelQueriable<T> query) {
            super(context);
            this.query = query;
        }

        public DBFListLoader(Context context, BaseModelQueriable query, Class<T> queryModel) {
            super(context);
            clazz = queryModel;
            queryQM = query;
        }

        @Override
        public List<T> loadInBackground() {
            if(clazz == null)
                return query.queryList();
            else
                return queryQM.queryCustomList(clazz);
        }
    }

    public static class DBFSingleLoader<T extends BaseModel> extends AsyncTaskLoader<T> {
        private BaseModelQueriable<T> query;

        public DBFSingleLoader(Context context, BaseModelQueriable<T> query) {
            super(context);
            this.query = query;
        }

        @Override
        public T loadInBackground() {
            return query.querySingle();
        }
    }
}
