package com.heyanle.closure.utils

import com.heyanle.closure.R
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by HeYanLe on 2022/12/28 18:06.
 * https://github.com/heyanLE
 */

class RetrofitResult <T> (
    val response: Response<T>? = null,
    val t: Throwable? = null
)

suspend fun <T> Call<T>.awaitResponseOK(): RetrofitResult<T> {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                continuation.resume(RetrofitResult(response = response))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                t.printStackTrace()
                continuation.resume(RetrofitResult(t = t))
            }
        })
    }
}


inline fun <T> RetrofitResult<com.heyanle.closure.net.model.Response<T>>.onSuccessful (block: (T?)->Unit): RetrofitResult<com.heyanle.closure.net.model.Response<T>>{
    response?.apply {
        if(isSuccessful){
            body()?.let {
                if(it.code != 0){
                    block(it.data)
                }

            }
        }
    }

    return this
}

inline fun <T> RetrofitResult<com.heyanle.closure.net.model.Response<T>>.onFailed (block: (Boolean, String)->Unit): RetrofitResult<com.heyanle.closure.net.model.Response<T>>{
    response?.apply {
        val res = body()
        if(!isSuccessful || res == null){
            block(true, stringRes(R.string.net_error))
        }else{
            if(res.code == 0){
                block(false, res.message)
            }
        }
    }
    if(response == null){
        block(true, stringRes(R.string.net_error))
    }
    return this
}