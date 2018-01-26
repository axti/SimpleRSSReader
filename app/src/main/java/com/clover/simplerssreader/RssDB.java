package com.clover.simplerssreader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.BaseModelQueriable;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.List;

/**
 * Created by aleksandrgranin on 25/01/2018.
 */

@Database(name = RssDB.NAME, version = RssDB.VERSION)
public class RssDB {
    public static final String NAME = "rss_db";

    public static final int VERSION = 1;

   /* public static void addTestFeed() {
        DatabaseWrapper wrapper = FlowManager.getDatabase(RssDB.class).getWritableDatabase();
        wrapper.beginTransaction();

        tryFetchNewFeed("https://www.risk.ru/rss/new");

        CardsWithVolume first_card = new CardsWithVolume();
        Long first_rfid = 14884946578172518L;
        first_card.IKID = first_rfid;
        first_card.rfid = first_rfid.toString();
        first_card.Type = CardsWithVolume.SDTD_M1K;
        first_card.save(wrapper);

        CardsWithVolume second_card = new CardsWithVolume();
        Long second_rfid = 14884946576982630L;
        second_card.rfid = second_rfid.toString();
        second_card.Type = CardsWithVolume.SDTD_M1K;
        second_card.save(wrapper);

        wrapper.setTransactionSuccessful();
        wrapper.endTransaction();
    }*/

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
