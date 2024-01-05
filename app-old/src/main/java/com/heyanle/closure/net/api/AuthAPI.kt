package com.heyanle.closure.net.api

import com.heyanle.closure.net.model.LoginReq
import com.heyanle.closure.net.model.Response
import com.heyanle.closure.net.model.WebsiteUser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by HeYanLe on 2022/12/23 14:44.
 * https://github.com/heyanLE
 */
interface AuthAPI {



    @POST("Auth")
    fun register(@Body login: LoginReq): Call<Response<WebsiteUser>>

    @GET("Auth/{email}/{password}")
    fun login(@Path("email", ) email: String,
              @Path("password", ) password: String
    ): Call<Response<WebsiteUser>>

    @GET("Auth")
    fun auth(
        @Header("Authorization") token: String,
    ):Call<Response<WebsiteUser>>

    @GET("Auth/QQBind")
    fun bindQQ(
        @Header("Authorization") token: String,
    ): Call<ResponseBody>


}