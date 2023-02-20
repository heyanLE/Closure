package com.heyanle.closure.net

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.heyanle.closure.net.api.AuthAPI
import com.heyanle.closure.net.api.GameAPI
import com.heyanle.closure.utils.get
import com.heyanle.closure.utils.onSuccessful
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.measureTimeMillis

/**
 * Created by HeYanLe on 2022/12/23 14:44.
 * https://github.com/heyanLE
 */
object Net {

    private val scope = MainScope()
    private var lastJob: Job? = null

    val hostList = listOf<String>(
        "https://api.arknights.host",
        "https://devapi.arknights.host",
        "https://sec.arknights.host"
    )

    fun getBaseUrl(){
        lastJob?.cancel()
        lastJob = scope.launch {
            baseUrl.value = BaseUrlState.Loading
            var minTime = Long.MAX_VALUE
            var curHost = "none"

            hostList.map {
                async (Dispatchers.IO){
                    val time = kotlin.runCatching {
                        measureTimeMillis {
                            okHttpClient.newCall(Request.Builder().get().url("${it}/nodes").build()).execute()
                        }
                    }.getOrElse {
                        it.printStackTrace()
                        Long.MAX_VALUE
                    }
                    it to time
                }
            }.forEach {
                val time = it.await()
                Log.d("Net", "${time.first} ${time.second}")
                if(time.second < minTime){
                    curHost = time.first
                    minTime = time.second
                }
            }
            Log.d("Net", "fastest Url ${curHost}")
            if(curHost == "none"){
                baseUrl.value = BaseUrlState.Error
            }else{
                baseUrl.value = BaseUrlState.Url(curHost)
            }

        }



    }

    sealed class BaseUrlState {
        object None: BaseUrlState()

        object Loading: BaseUrlState()

        object Error: BaseUrlState()

        class Url(val baseUrl: String): BaseUrlState()
    }

    val baseUrl = mutableStateOf<BaseUrlState>(BaseUrlState.None)


    val okHttpClient = OkHttpClient.Builder().build()

    private val retrofit: Retrofit by lazy {
        val baseUrl = (baseUrl.value as? BaseUrlState.Url)?:throw  IllegalStateException()
        Retrofit.Builder()
            .baseUrl(baseUrl.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    val auth: AuthAPI by lazy {
        retrofit.create(AuthAPI::class.java)
    }

    val game: GameAPI by lazy {
        retrofit.create(GameAPI::class.java)
    }

}