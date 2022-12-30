package com.heyanle.closure.net.api

import com.heyanle.closure.net.model.CaptchaInfo
import com.heyanle.closure.net.model.CaptchaReq
import com.heyanle.closure.net.model.CreateGameReq
import com.heyanle.closure.net.model.GameConfig
import com.heyanle.closure.net.model.GameLogItem
import com.heyanle.closure.net.model.GameLoginReq
import com.heyanle.closure.net.model.GameResp
import com.heyanle.closure.net.model.GetGameResp
import com.heyanle.closure.net.model.Response
import com.heyanle.closure.net.model.ScreenshotRsp
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by HeYanLe on 2022/12/23 14:59.
 * https://github.com/heyanLE
 */
interface GameAPI {

    @GET("/Game")
    fun get(
        @Header("Authorization") token: String,
    ): Call<Response<List<GameResp>>>

    @POST("/Game")
    fun post(
        @Header("Authorization") token: String,
        @Body req: CreateGameReq,
    ): Call<Response<String>>

    @DELETE("/Game")
    fun delete(
        @Header("Authorization") token: String,
        @Body req: CreateGameReq,
    ): Call<Response<String>>

    @POST("/Game/Captcha/{account}/{platform}")
    fun postCaptcha(
        @Header("Authorization") token: String,
        @Path("platform") platform: Long,
        @Path("account") account: String,
        @Body captchaReq: CaptchaReq,
    ): Call<Response<CaptchaInfo>>

    @GET("/Game/Config/{account}/{platform}")
    fun getConfig(
        @Header("Authorization") token: String,
        @Path("platform") platform: Long,
        @Path("account") account: String,
    ): Call<Response<GameConfig>>

    @POST("/Game/Config/{account}/{platform}")
    fun postConfig(
        @Header("Authorization") token: String,
        @Path("platform") platform: Long,
        @Path("account") account: String,
        @Body config: GameConfig,
    ): Call<Response<GameConfig>>

    @GET("/Game/Log/{account}/{platform}/{timestamp}")
    fun getLog(
        @Header("Authorization") token: String,
        @Path("platform") platform: Long,
        @Path("account") account: String,
        @Path("timestamp") timestamp: Long,
    ): Call<Response<List<GameLogItem>>>

    @POST("/Game/Login")
    fun login(
        @Header("Authorization") token: String,
        @Body req: GameLoginReq
    ): Call<Response<String>>

    @POST("/Game/Ocr/{account}/{platform}")
    fun ocr(
        @Header("Authorization") token: String,
        @Path("platform") platform: Long,
        @Path("account") account: String,
    ): Call<Response<String>>

    @GET("/Game/Screenshots/{account}/{platform}")
    fun screenshots(
        @Header("Authorization") token: String,
        @Path("platform") platform: Long,
        @Path("account") account: String,
    ): Call<Response<ScreenshotRsp>>


    @GET("/Game/{account}/{platform}")
    fun game(
        @Header("Authorization") token: String,
        @Path("platform") platform: Long,
        @Path("account") account: String,
    ): Call<Response<GetGameResp>> // String 摆烂了，后面用 JSONObject 读

}