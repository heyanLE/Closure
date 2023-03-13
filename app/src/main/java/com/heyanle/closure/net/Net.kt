package com.heyanle.closure.net

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.heyanle.closure.BuildConfig
import com.heyanle.closure.net.api.AuthAPI
import com.heyanle.closure.net.api.CommonAPI
import com.heyanle.closure.net.api.GameAPI
import com.heyanle.closure.net.model.Announcement
import com.heyanle.closure.page.MainController
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.get
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.toast
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
object Net {

    private val scope = MainScope()
    private var lastJob: Job? = null

    val hostList = listOf<String>(
        "https://api-a.arknights.host",
        "https://api-b.arknights.host",
        "https://api-c.arknights.host",
        "https://devapi.arknights.host"
    )


    fun getBaseUrl() {
        lastJob?.cancel()
        lastJob = scope.launch {
            baseUrl.value = BaseUrlState.Loading
            var minTime = Long.MAX_VALUE
            var curHost = "none"

            hostList.map {
                async(Dispatchers.IO) {
                    val time = kotlin.runCatching {
                        measureTimeMillis {
                            okHttpClient.newCall(Request.Builder().get().url("${it}/nodes").build())
                                .execute()
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
                if (time.second < minTime) {
                    curHost = time.first
                    minTime = time.second
                }
            }
            Log.d("Net", "fastest Url ${curHost}")
            if (curHost == "none") {
                baseUrl.value = BaseUrlState.Error
            } else {
                baseUrl.value = BaseUrlState.Url(curHost)
            }

        }


    }

    sealed class BaseUrlState {
        object None : BaseUrlState()

        object Loading : BaseUrlState()

        object Error : BaseUrlState()

        class Url(val baseUrl: String) : BaseUrlState()
    }

    val baseUrl = mutableStateOf<BaseUrlState>(BaseUrlState.None)


    sealed class AnnouncementState {
        object None : AnnouncementState()

        object Loading : AnnouncementState()

        class Announcement(val announcement: com.heyanle.closure.net.model.Announcement) :
            AnnouncementState()

    }

    val announcement = mutableStateOf<AnnouncementState>(AnnouncementState.None)

    fun getAnon() {
        lastJob?.cancel()
        lastJob = scope.launch {
            announcement.value = AnnouncementState.Loading
            common.getAnnouncement().awaitResponseOK().onSuccessful {

                scope.launch {
                    if(MainController.token.value?.isNotEmpty() == true){
                        auth.auth(MainController.token.value?:"").awaitResponseOK().onSuccessful {
                            if(it != null){
                                MainController.token.value = it.token
                            }
                        }
                        announcement.value = AnnouncementState.Announcement(it?:Announcement())
                    }else{
                        announcement.value = AnnouncementState.Announcement(it?:Announcement())
                    }
                }
            }.onFailed { b, s ->
                s.toast()
                announcement.value = AnnouncementState.Announcement(Announcement())
            }


        }


    }

    val okHttpClient = OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS).apply {
        if(BuildConfig.DEBUG){
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            }
            addNetworkInterceptor(httpLoggingInterceptor)
        }
    }.build()

    private val retrofit: Retrofit by lazy {
        val baseUrl = (baseUrl.value as? BaseUrlState.Url) ?: throw IllegalStateException()
        val builder = Retrofit.Builder()
            .baseUrl(baseUrl.baseUrl)
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