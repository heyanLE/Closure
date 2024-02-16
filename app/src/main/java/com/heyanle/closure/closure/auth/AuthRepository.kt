package com.heyanle.closure.closure.auth

import com.heyanle.closure.closure.auth.model.AuthResp
import com.heyanle.closure.closure.auth.model.LoginBody
import com.heyanle.closure.closure.auth.model.LoginResp
import com.heyanle.closure.closure.auth.model.RegisterBody
import com.heyanle.closure.closure.auth.model.RegisterResp
import com.heyanle.closure.closure.net.Net
import com.heyanle.closure.closure.net.NetResponse
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Deferred

/**
 * Created by heyanlin on 2023/12/31.
 */
class AuthRepository(
    private val net: Net,
) {

    val authUrl = "https://passport.arknights.host/api/v1"

    fun register(registerBody: RegisterBody): Deferred<NetResponse<AuthResp<RegisterResp>>> {
        return net.send {
            post {
                url("${net.authUrl}/register")
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(registerBody)
            }
        }
    }

    suspend fun awaitRegister(registerBody: RegisterBody): NetResponse<AuthResp<RegisterResp>> {
        return register(registerBody).await()
    }

    fun login(loginBody: LoginBody): Deferred<NetResponse<AuthResp<LoginResp>>> {
        return net.send {
            post {
                url("${net.authUrl}/login")
                contentType(ContentType.Application.Json)
                //accept(ContentType.Application.Json)
                setBody(loginBody)
            }
        }
    }

    suspend fun awaitLogin(loginBody: LoginBody): NetResponse<AuthResp<LoginResp>> {
        return login(loginBody).await()
    }



}