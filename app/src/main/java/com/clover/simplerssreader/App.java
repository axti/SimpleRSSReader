package com.clover.simplerssreader;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by aleksandrgranin on 26/01/2018.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }
}