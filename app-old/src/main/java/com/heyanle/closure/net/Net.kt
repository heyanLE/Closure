package com.heyanle.closure.net

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.heyanle.closure.BuildConfig
import com.heyanle.closure.net.api.AuthAPI
import com.heyanle.closure.net.api.CommonAPI
import com.heyanle.closure.net.api.GameAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

/**
 * Created by HeYanLe on 2022/12/23 14:44.
 * https://github.com/heyanLE
 */
class Net {

    val okHttpClient = OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS).apply {
        if(BuildConfig.DEBUG){
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            }
            addNetworkInterceptor(httpLoggingInterceptor)
        }
    }.build()

    private val retrofit: Retrofit by lazy {
        val baseUrl = "https://devapi.arknights.host"
        val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
        builder.build()
    }

    val auth: AuthAPI by lazy {
        retrofit.create(AuthAPI::class.java)
    }

    val game: GameAPI by lazy {
        retrofit.create(GameAPI::class.java)
    }

    val common: CommonAPI by lazy {
        retrofit.create(CommonAPI::class.java)
    }



}