package com.heyanle.closure.net

import com.heyanle.closure.net.api.AuthAPI
import com.heyanle.closure.net.api.GameAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by HeYanLe on 2022/12/23 14:44.
 * https://github.com/heyanLE
 */
object Net {

    private val base = "https://devapi.arknights.host"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(base)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val auth: AuthAPI by lazy {
        retrofit.create(AuthAPI::class.java)
    }

    val game: GameAPI by lazy {
        retrofit.create(GameAPI::class.java)
    }

}