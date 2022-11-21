package com.xjcmpt.mylibrary;

import android.app.Application;
import android.content.Context;

import com.xjcmpt.mylibrary.oaidsystem.DeviceID;
import com.xjcmpt.mylibrary.oaidsystem.DeviceIdentifier;
import com.xjcmpt.mylibrary.utils.MyLogUtils;

public class ModTest {
    public static volatile ModTest modTest = null;
    public static final String TAG = "TAG_MOD";

    public static ModTest getInstance() {
        if (modTest == null) {
            modTest = new ModTest();
        }
        return modTest;
    }

    public void initSDK(Application application) {
        MyLogUtils.INSTANCE.d("initSDK_CODE=200");

        DeviceIdentifier.register(application);
    }


    public void initOaid(Context context) {
        // 获取IMEI，只支持Android 10之前的系统，需要READ_PHONE_STATE权限，可能为空
        //    DeviceIdentifier.getIMEI(this);
        // 获取安卓ID，可能为空
        String androidID = DeviceIdentifier.getAndroidID(context);
        MyLogUtils.INSTANCE.d("androidID=" + androidID);
        // 获取数字版权管理ID，可能为空
        // DeviceIdentifier.getWidevineID();
        // 获取伪造ID，根据硬件信息生成，不会为空，有大概率会重复
        //    DeviceIdentifier.getPseudoID()
        // 获取GUID，随机生成，不会为空
        //    DeviceIdentifier.getGUID(this);
        // 是否支持OAID/AAID
        boolean supportedOAID = DeviceID.supportedOAID(context);
        MyLogUtils.INSTANCE.d("supportedOAID=" + supportedOAID);

        // 获取OAID/AAID，同步调用
        String oaid = DeviceIdentifier.getOAID(context);
        MyLogUtils.INSTANCE.d("oaid=" + oaid);

        // 获取OAID/AAID，异步回调
//        DeviceID.getOAID(this, object : IGetter {
//            override fun onOAIDGetComplete(result: String) {
//                // 不同厂商的OAID/AAID格式是不一样的，可进行MD5、SHA1之类的哈希运算统一
//                Log.d("TAG_MOD","OAID="+result)
//
//            }
//
//            override fun onOAIDGetError(error: Exception?) {
//                // 获取OAID/AAID失败
//                Log.d("TAG_MOD","OAID_error="+error.toString())
//
//            }
//
//        });


    }

}
