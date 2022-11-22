package com.xiaoju.fightstreet;

import android.app.Application;
import android.content.Context;


import androidx.multidex.MultiDex;

import com.xiaoju.mylibrary.http.MySdk;


public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MySdk.configureSDK(this, "121235");

    }

}
