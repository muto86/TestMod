package com.xjcmpt.fightstreet

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*

import com.xjcmpt.mylibrary.MyPaySDK
import com.xjcmpt.mylibrary.utils.MyLogUtils

class MainActivity : AppCompatActivity() {
    val TAG: String = "TAG_MOD"
    var mTv_buy_1: TextView? = null
    var mTv_buy_2: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTv_buy_1 = findViewById(R.id.mTv_buy_1)
        mTv_buy_2 = findViewById(R.id.mTv_buy_2)


        // ModTest.getInstance().initOaid(this)
        // ModTest.getInstance().getReferrer(this)

        mTv_buy_1?.setOnClickListener({
            MyPaySDK.strProductType = BillingClient.ProductType.INAPP
            MyPaySDK.queryProductDetails("com.xjcmpt.fightstreet.inapp01", MyPaySDK.strProductType,
                this@MainActivity, object : MyPaySDK.Companion.OnQueryProductDetails {
                    override fun onQueryProductDetailsFaile(code: Int, msg: String) {

                    }
                }
            );
        })
        mTv_buy_2?.setOnClickListener({
            MyPaySDK.strProductType = BillingClient.ProductType.INAPP

            MyPaySDK.queryProductDetails("com.xjcmpt.fightstreet.inapp01", MyPaySDK.strProductType,
                this@MainActivity, object : MyPaySDK.Companion.OnQueryProductDetails {
                    override fun onQueryProductDetailsFaile(code: Int, msg: String) {

                    }
                }
            );
        })

        MyPaySDK.initializeSdk(this, object : MyPaySDK.Companion.OnProductDetailsFaileListeners {
            override fun onBuyProductFaile(code: Int?, msg: String?) {

            }


        })
        MyLogUtils.d("MANUFACTURER=" + Build.MANUFACTURER);
        MyLogUtils.d("MODEL=" + Build.MODEL)
        MyLogUtils.d("VERSION.RELEASE=" + Build.VERSION.RELEASE);


    }


    override fun onResume() {
        super.onResume()
        MyLogUtils.w("onResume")
       // MyPaySDK.checkGoodsForSale()

    }


}