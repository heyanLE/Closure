package com.heyanle.closure.auth

import com.heyanle.closure.auth.model.LoginBody
import com.heyanle.closure.auth.model.LoginResp
import com.heyanle.closure.auth.model.RegisterBody
import com.heyanle.closure.auth.model.RegisterResp
import com.heyanle.closure.net.Net
import com.heyanle.closure.net.NetResponse
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

    fun register(registerBody: RegisterBody): Deferred<NetResponse<RegisterResp>> {
        return net.send {
            post {
                url("${net.rootUrl}/register")
                contentType(ContentType.Application.Json)
                setBody(registerBody)
            }
        }
    }

    suspend fun awaitRegister(registerBody: RegisterBody): NetResponse<RegisterResp> {
        return register(registerBody).await()
    }

    fun login(loginBody: LoginBody): Deferred<NetResponse<LoginResp>> {
        return net.send {
            post {
                url("${net.rootUrl}/login")
                contentType(ContentType.Application.Json)
                setBody(loginBody)
            }
        }
    }

    suspend fun awaitLogin(loginBody: LoginBody): NetResponse<LoginResp> {
        return login(loginBody).await()
    }



}