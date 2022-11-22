package com.xiaoju.fightstreet.net


import android.os.Environment
import android.util.Log

import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitUtils {

    companion object {
        private var okHttpClient: OkHttpClient? = null
        private val rxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create()
        private var netAPi: ApiService? = null

        // 缓存文件最大限制大小20M
        val cacheSize = (1024 * 1024 * 20).toLong()
        val cachePath =
            Environment.getExternalStorageDirectory().toString() + "/fightstreet" // 设置缓存文件路径

        private val cache = Cache(File(cachePath), cacheSize)

        private fun getDis(): Dispatcher? {
            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 3
            return dispatcher
        }

        var interceptor = Interceptor { chain ->
            val request = chain.request()

            val build = request.newBuilder()
                .method(request.method(), request.body())
                .build()
            chain.proceed(build)
        }


        init {
            var builder = OkHttpClient.Builder()
            // 设置连接超时时间
            builder.connectTimeout(30, TimeUnit.SECONDS)
                // 设置写入超时时间
                .writeTimeout(30, TimeUnit.SECONDS)
                // 设置读取数据超时时间
                .readTimeout(30, TimeUnit.SECONDS)
                // 设置进行连接失败重试
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(interceptor)
                .addInterceptor(Objects.requireNonNull(getI())) //添加拦截器
//        builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));//拦截器
                .dispatcher(getDis())
                // 设置缓存
                .cache(cache)
            okHttpClient = builder.build()
        }

        fun getI(): Interceptor? {
            val level = HttpLoggingInterceptor.Level.BODY
            val interceptor =
                HttpLoggingInterceptor { message ->

                    //LogUtils.w("请求参数信息：" + message);
                    if (message.contains("&") || message.contains("{") || message.contains("}") || message.contains(
                            "GET") || message.contains("POST")
                    ) {
                        Log.w("TAG","请求参数信息：$message")
                    }
                }
            interceptor.level = level
            return interceptor
        }

        fun getApiService(): ApiService {
            if (netAPi == null) {
                val retrofit: Retrofit = Retrofit.Builder()
                    .client(okHttpClient).baseUrl("https")
                   // .addConverterFactory(EApi.create())
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build()
                netAPi = retrofit.create(ApiService::class.java)
            }
            return netAPi!!
        }
    }
}