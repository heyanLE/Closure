package com.heyanle.closure.utils

import com.heyanle.closure.R
import com.heyanle.closure.base.DataResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume

/**
 * Created by HeYanLe on 2022/12/29 17:16.
 * https://github.com/heyanLE
 */

class OkhttpNormalResult<T>(
    val resp: T? = null,
    val isNetError:Boolean = false,
    val errMsg: String = "",
    val t: Throwable? = null
)

suspend fun OkHttpClient.get(url: String): OkhttpNormalResult<String>{
    return suspendCancellableCoroutine { continuation ->
        newCall(
            Request.Builder().url(url).get().build()
        ).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                continuation.resume(OkhttpNormalResult(isNetError = false, errMsg = e.message?:"", t = e))
            }

            override fun onResponse(call: Call, response: Response) {
                if(!response.isSuccessful){
                    continuation.resume(OkhttpNormalResult(isNetError = true, errMsg = stringRes(R.string.net_error) + "1"))
                }else{
                    val resp = response.body?.string()
                    if(resp == null){
                        continuation.resume(OkhttpNormalResult(isNetError = true, errMsg = stringRes(R.string.net_error) + "2"))
                    }else{
                        continuation.resume(OkhttpNormalResult(resp=resp))
                    }
                }


            }
        })
    }

}

inline fun <T>  OkhttpNormalResult<T>.onSuccessful(block: (T)->Unit): OkhttpNormalResult<T>{
    if(resp != null && t == null){
        block(resp)
    }
    return this
}

inline fun <T>  OkhttpNormalResult<T>.onFailed(block: (Boolean, String)->Unit): OkhttpNormalResult<T>{
    if(resp == null || t != null){
        block(isNetError, errMsg)
    }
    return this
}