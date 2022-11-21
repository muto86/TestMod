package com.xjcmpt.guiyin.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class SPHelper {


    /**
     * 单例模式
     */
    private static volatile SPHelper instance;//单例模式 双重检查锁定

    private SPHelper() {

    }

    public static SPHelper getInstance() {
        if (instance == null) {
            synchronized (SPHelper.class) {
                if (instance == null) {
                    instance = new SPHelper();
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
