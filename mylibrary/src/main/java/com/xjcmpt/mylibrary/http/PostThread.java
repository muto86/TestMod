package com.xjcmpt.mylibrary.http;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.xjcmpt.mylibrary.utils.MyLogUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class PostThread extends Thread {

    String MANUFACTURER = Build.MANUFACTURER;
    String MODEL = Build.MODEL;
    String VERSION_RELEASE = Build.VERSION.RELEASE;
    public static final int Log_Install_Event = 0;
    public static final int Log_Star_Event = 1;
    public static final int Log_Click_Event = 2;
    public static final int Log_Class_Event = 3;
    private String data = "";


    int urlType = 0;

    public PostThread(int urlType) {
        this.urlType = urlType;
        MyLogUtils.INSTANCE.w("urlType=" + urlType);

    }

    //首次安装
    public String setInstallData(String utm_source, String click_time, String installVersion, String device_id, String appid) {
        MyLogUtils.INSTANCE.d("setInstallData");

        try {
            data = "{\n" +
                    " \"app_id\":\"" + appid + "\",\n" +
                    " \"device_id\": \"" + device_id + "\",\n" +
                    " \"utm_source\":  \"" + utm_source + "\",\n" +
                    " \"channel\":  \"\" ,\n" +
                    " \"device_type\":  \"" + MODEL + "\",\n" +
                    " \"system\":  \"" + MANUFACTURER + "\",\n" +
                    " \"version\": \" " + VERSION_RELEASE + "\"\n" +
                    "}";
//            data = "appid=" + URLEncoder.encode(appid, "UTF-8")
//                    + "&device_id=" + URLEncoder.encode(device_id, "UTF-8")
//                    + "&utm_source=" + URLEncoder.encode(utm_source, "UTF-8")
//                    + "&channel=" + URLEncoder.encode("", "UTF-8")
//                    // + "&click_time=" + URLEncoder.encode(click_time, "UTF-8")
//                    //  + "&version=" + URLEncoder.encode(installVersion, "UTF-8")
//                    + "&device_type=" + URLEncoder.encode(MODEL, "UTF-8")//M2007J3SC
//                    + "&system=" + URLEncoder.encode(MANUFACTURER, "UTF-8")//Xiaomi
//                    + "&version=" + URLEncoder.encode(VERSION_RELEASE, "UTF-8")//11
//            ;
        } catch (Exception e) {

        }
        return data;

    }

    //日常启动
    public String setStarData(String appId, String utm_source, String device_id) {
        MyLogUtils.INSTANCE.d("setStarData");

        String click_time = System.currentTimeMillis() + "";
        try {

            data = "{\n" +
                    " \"app_id\":\"" + appId + "\",\n" +
                    " \"device_id\": \"" + device_id + "\",\n" +
                    " \"utm_source\":  \"" + utm_source + "\",\n" +
                    " \"channel\":  \"\" ,\n" +
                    " \"device_type\":  \"" + MODEL + "\",\n" +
                    " \"system\":  \"" + MANUFACTURER + "\",\n" +
                    " \"version\": \" " + VERSION_RELEASE + "\"\n" +
                    "}";
//            data = "appId=" + URLEncoder.encode(appId, "UTF-8")
//                    + "&device_id=" + URLEncoder.encode(device_id, "UTF-8")
//                    + "&utm_source=" + URLEncoder.encode(utm_source, "UTF-8")
//                    + "&channel=" + URLEncoder.encode("", "UTF-8")
//                    + "&click_time=" + URLEncoder.encode(click_time, "UTF-8")
//                    + "&device_type=" + URLEncoder.encode(MODEL, "UTF-8")//M2007J3SC
//                    + "&system=" + URLEncoder.encode(MANUFACTURER, "UTF-8")//Xiaomi
//                    + "&version=" + URLEncoder.encode(VERSION_RELEASE, "UTF-8")//11
//            ;
        } catch (Exception e) {

        }

        return data;
    }

    //点击事件
    public String setClickData(String appId, String eventName, String eventData, String installVersion, String oaid) {
        String click_time = System.currentTimeMillis() + "";
        try {


//            data = "appId=" + URLEncoder.encode(appId, "UTF-8")
//                    + "&eventName=" + URLEncoder.encode(eventName, "UTF-8")
//                    + "&eventData=" + URLEncoder.encode(eventData, "UTF-8")
//                    + "&installVersion=" + URLEncoder.encode(installVersion, "UTF-8")
//                    + "&click_time=" + URLEncoder.encode(click_time, "UTF-8")
//                    + "&MANUFACTURER=" + URLEncoder.encode(MANUFACTURER, "UTF-8")
//                    + "&MODEL=" + URLEncoder.encode(MODEL, "UTF-8")
//                    + "&VERSION_RELEASE=" + URLEncoder.encode(VERSION_RELEASE, "UTF-8")
//                    + "&oaid=" + URLEncoder.encode(oaid, "UTF-8");
        } catch (Exception e) {

        }
        return data;
    }

    @Override
    public void run() {
        super.run();
        MyLogUtils.INSTANCE.w("run");
        postUrl(urlType);

    }


    public String postUrl(int urlType) {
        if (TextUtils.isEmpty(MySdk.getOaid())) {
            MyLogUtils.INSTANCE.e("Oaid==null");

            return null;
        }
        String httpUrl = "http://173.82.153.79:8081/api/v1/report/device";
        switch (urlType) {
            case 0:
                httpUrl = "http://173.82.153.79:8081/api/v1/report/device";
                break;
            case 1:
                httpUrl = "http://173.82.153.79:8081/api/v1/report/launch";
                break;
        }
        //获得的数据
        String resultData = "";
        URL url = null;
        try {
            //构造一个URL对象
            url = new URL(httpUrl);
        } catch (MalformedURLException e) {
            MyLogUtils.INSTANCE.e("MalformedURLException_error=" + e.getLocalizedMessage());
        } catch (Exception e) {
            MyLogUtils.INSTANCE.e("Exception_error=" + e.getLocalizedMessage());
        }
        if (url != null) {
            try {
                // 使用HttpURLConnection打开连接
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                //因为这个是post请求,设立需要设置为true
                urlConn.setDoOutput(true);
                urlConn.setDoInput(true);
                // 设置以POST方式
                urlConn.setRequestMethod("POST");
                // Post 请求不能使用缓存
                urlConn.setUseCaches(false);
                urlConn.setInstanceFollowRedirects(true);
                // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
                urlConn.setRequestProperty("Content-Type", "application/json");
                // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
                // 要注意的是connection.getOutputStream会隐含的进行connect。
                urlConn.connect();
                //DataOutputStream流
                DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
                //要上传的参数  "gb2312" 编码
                //  String content = "par=" + URLEncoder.encode("ABCDEFG", "gb2312");
                //我们请求的数据:
//                String data = "utm_source=" + URLEncoder.encode(utm_source, "UTF-8")
//                        + "&click_time=" + URLEncoder.encode(click_time, "UTF-8")
//                        + "&installVersion=" + URLEncoder.encode(installVersion, "UTF-8")
//                        + "&MANUFACTURER=" + URLEncoder.encode(MANUFACTURER, "UTF-8")
//                        + "&MODEL=" + URLEncoder.encode(MODEL, "UTF-8")
//                        + "&VERSION_RELEASE=" + URLEncoder.encode(VERSION_RELEASE, "UTF-8")
//                        + "&oaid=" + URLEncoder.encode(oaid, "UTF-8");
                MyLogUtils.INSTANCE.d("data=" + data);

                //将要上传的内容写入流中
                out.writeBytes(data);
                //刷新、关闭
                out.flush();
                out.close();
                MyLogUtils.INSTANCE.w("postUrl_ResponseCode=" + urlConn.getResponseCode() + ",ResponseMessage=" + urlConn.getResponseMessage());

                if (urlConn.getResponseCode() == 200) {
                    // 获取响应的输入流对象
                    InputStream is = urlConn.getInputStream();
                    // 创建字节输出流对象
                    ByteArrayOutputStream message = new ByteArrayOutputStream();
                    // 定义读取的长度
                    int len = 0;
                    // 定义缓冲区
                    byte buffer[] = new byte[1024];
                    // 按照缓冲区的大小，循环读取
                    while ((len = is.read(buffer)) != -1) {
                        // 根据读取的长度写入到os对象中
                        message.write(buffer, 0, len);
                    }
                    // 释放资源
                    is.close();
                    message.close();
                    // 返回字符串
                    resultData = new String(message.toByteArray());
                    return resultData;
                }
            } catch (Exception e) {
                MyLogUtils.INSTANCE.e("postUrl_Exception=" + e.getLocalizedMessage());
            }
        }
        return resultData;
    }

    public void getUrl() {
        String resultData = "";
        try {
            URL url = new URL("http://www.baidu.com/index.jsp?par=123456");

            //使用HttpURLConnection打开连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            //得到读取的内容(流)
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
            // 为输出创建BufferedReader
            BufferedReader buffer = new BufferedReader(in);
            String inputLine = null;
            //使用循环来读取获得的数据
            while (((inputLine = buffer.readLine()) != null)) {
                //我们在每一行后面加上一个"\n"来换行
                resultData += inputLine + "\n";
            }
            //关闭InputStreamReader
            in.close();
            //关闭http连接
            urlConn.disconnect();
        } catch (Exception e) {

        }

    }


}
