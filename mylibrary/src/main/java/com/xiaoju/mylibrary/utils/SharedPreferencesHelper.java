package com.xiaoju.mylibrary.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {


    /**
     * 单例模式
     */
    private static volatile SharedPreferencesHelper instance;//单例模式 双重检查锁定

    private SharedPreferencesHelper() {

    }

    public static SharedPreferencesHelper getInstance() {
        if (instance == null) {
            synchronized (SharedPreferencesHelper.class) {
                if (instance == null) {
                    instance = new SharedPreferencesHelper();
                }
            }
        }
        return instance;
    }

    //保存在手机里面的文件名
    public static final String FILE_NAME = "message_cache";
    public SharedPreferences sp;

    public SharedPreferences init(Context context) {
        return sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static final String isFirst_Key = "isFirst";

    public void setBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key,boolean defValue) {
        return sp.getBoolean(key, defValue);
    }


}
