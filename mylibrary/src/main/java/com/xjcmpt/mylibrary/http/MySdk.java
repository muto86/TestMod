package com.xjcmpt.mylibrary.http;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.xjcmpt.mylibrary.utils.MyLogUtils;
import com.xjcmpt.mylibrary.utils.SharedPreferencesHelper;
import com.xjcmpt.mylibrary.oaidsystem.DeviceIdentifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MySdk {


    private static String oaid;
    private static String appId;
    private static Context mContext;
    private static String curVersionName;

    public static String getOaid() {
        return oaid;
    }

    public static String getAppId() {
        return appId;
    }

    public static void configureSDK(Application application, String appId) {
        MySdk.appId = appId;
        DeviceIdentifier.register(application);

        String androidID = DeviceIdentifier.getAndroidID(application);
        MyLogUtils.INSTANCE.e("androidID=" + androidID);

        // 获取OAID/AAID，同步调用
        String oaid = DeviceIdentifier.getOAID(application);
        MyLogUtils.INSTANCE.e("oaid=" + oaid);

        SharedPreferencesHelper.getInstance().init(application);
        String id = "";
        if (TextUtils.isEmpty(oaid)) {
            id = androidID;
        } else {
            id = oaid;
        }
        MySdk.oaid = id;

        MyLogUtils.INSTANCE.d("id=" + id);
        mContext = application;
        getVersion(mContext);
        getReferrer(mContext);

    }

    private static void getVersion(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int curVersionCode = packageInfo.versionCode;
            curVersionName = packageInfo.versionName;

            MyLogUtils.INSTANCE.w("curVersionCode=" + curVersionCode);
            MyLogUtils.INSTANCE.w("curVersionName=" + curVersionName);
        } catch (PackageManager.NameNotFoundException e) {

            MyLogUtils.INSTANCE.d("PackageManager.NameNotFoundException_error=" + e.getLocalizedMessage());

        }
    }


    //安装应用
    public static void logInstallEvent(String utm_source, String time, String version) {

        PostThread thread = new PostThread(0);
        thread.setInstallData(utm_source, time, version, oaid,appId);
        thread.start();
    }

    //启动应用
    public static void logStarEvent(String utm_source) {
        PostThread thread = new PostThread(1);
        thread.setStarData(appId, utm_source, oaid);
        thread.start();
    }

    //点击事件
    public static void logCilckEvent(String eventName, String eventData) {
        PostThread thread = new PostThread(2);
        thread.setClickData(appId, eventName, eventData, curVersionName, oaid);
        thread.start();
    }

    //浏览页面
    public static void logClassEvent() {
    }

    //用户身份链接
    public static void logIdEvent() {

    }

    private static void getReferrer(Context context) {

        InstallReferrerClient client = InstallReferrerClient.newBuilder(context).build();
        client.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                try {
                    if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                        ReferrerDetails response = client.getInstallReferrer();
                        String installReferrer = response.getInstallReferrer();
                        if (!TextUtils.isEmpty(installReferrer)) {
                            MyLogUtils.INSTANCE.d("installreferrer=" + installReferrer);
                            MyLogUtils.INSTANCE.d("installVersion=" + response.getInstallVersion());
//                            MyLogUtils.INSTANCE.d("installBeginTimestampSeconds=" + response.getInstallBeginTimestampSeconds()
//                            );
//                            MyLogUtils.INSTANCE.d("referrerClickTimestampSeconds=" + response.getReferrerClickTimestampSeconds()
//                            );
//                            MyLogUtils.INSTANCE.d("googlePlayInstantParam= " + response.getGooglePlayInstantParam());
//                            MyLogUtils.INSTANCE.d("installBeginTimestampServerSeconds= " + response.getInstallBeginTimestampServerSeconds()
//                            );
//                            MyLogUtils.INSTANCE.d("referrerClickTimestampServerSeconds= " + response.getReferrerClickTimestampServerSeconds()
//                            );


//                            Bundle bundleEvent = new Bundle();
//                            bundleEvent.putLong("click_time",System.currentTimeMillis());
//                            bundleEvent.putString("referer",installReferrer);
                            String utm_source = "";
                            Map<String, String> map = parse(installReferrer);
                            if (!map.isEmpty()) {
                                utm_source = map.get("utm_source");   //下载来源
//                                if(!TextUtils.isEmpty(value)){
//                                    bundleEvent.putString("utm_source",value);
//                                }
                            }

                            //logEvent("click_event",bundleEvent);//上传渠道标识
                            String currentTimeMillis = System.currentTimeMillis() + "";
                            curVersionName = response.getInstallVersion();


                            boolean aBoolean = SharedPreferencesHelper.getInstance().getBoolean(SharedPreferencesHelper.isFirst_Key, true);
                            if (aBoolean) {
                                logInstallEvent(utm_source, currentTimeMillis, curVersionName);
                                SharedPreferencesHelper.getInstance().setBoolean(SharedPreferencesHelper.isFirst_Key, false);
                            }
                            logStarEvent(utm_source);

                        } else {
                            MyLogUtils.INSTANCE.d("installreferrer=null");
                        }
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onInstallReferrerServiceDisconnected() {

            }
        });

    }

    private static Map<String, String> parse(String data) {
        if (TextUtils.isEmpty(data)) {
            return Collections.emptyMap();
        }
        try {
            String[] kvs = data.split("&");
            HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i < kvs.length; i++) {
                String temp = kvs[i];
                if (TextUtils.isEmpty(temp)) {
                    continue;
                }
                String[] pair = temp.split("=");
                if (pair.length == 2) {
                    map.put(pair[0], pair[1]);
                }
            }
            return map;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

}
