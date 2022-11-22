package com.xiaoju.mylibrary.utils

import android.text.TextUtils
import android.util.Log

object MyLogUtils {
    var isLog = true

    //规定每段显示的长度
    private const val LOG_MAXLENGTH = 2000
    private const val TAG = "TAG_MOD"
    fun d(msg: String) { // 调试信息
        if (!isLog || TextUtils.isEmpty(msg)) {
            return
        }
        val strLength = msg.length
        var start = 0
        var end = LOG_MAXLENGTH
        for (i in 0..99) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.d(TAG + i, msg.substring(start, end))
                start = end
                end = end + LOG_MAXLENGTH
            } else {
                Log.d(TAG, msg.substring(start, strLength))
                break
            }
        }
    }

    fun w(msg: String) { // 调试信息
        if (!isLog || TextUtils.isEmpty(msg)) {
            return
        }
        val strLength = msg.length
        var start = 0
        var end = LOG_MAXLENGTH
        for (i in 0..99) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.w(TAG + i, msg.substring(start, end))
                start = end
                end = end + LOG_MAXLENGTH
            } else {
                Log.w(TAG, msg.substring(start, strLength))
                break
            }
        }
    }

    fun e(msg: String) {
        if (!isLog || TextUtils.isEmpty(msg)) {
            return
        }
        val strLength = msg.length
        var start = 0
        var end = LOG_MAXLENGTH
        for (i in 0..99) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.e(TAG + i, msg.substring(start, end))
                start = end
                end = end + LOG_MAXLENGTH
            } else {
                Log.e(TAG, msg.substring(start, strLength))
                break
            }
        }
    }
}