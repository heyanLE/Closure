package com.heyanle.closure.net.api

import com.heyanle.closure.net.model.Announcement
import com.heyanle.closure.net.model.Response
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by HeYanLe on 2023/3/11 22:36.
 * https://github.com/heyanLE
 */
interface CommonAPI {

    @GET("/Common/Announcement")
    fun getAnnouncement(): Call<Response<Announcement>>
}