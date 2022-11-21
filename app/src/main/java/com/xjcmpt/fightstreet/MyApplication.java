package com.xjcmpt.fightstreet;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


import androidx.multidex.MultiDex;

import com.xjcmpt.mylibrary.http.MySdk;
import com.xjcmpt.mylibrary.utils.MyLogUtils;


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
