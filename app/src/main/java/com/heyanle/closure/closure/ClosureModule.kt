package com.heyanle.closure.closure

import com.heyanle.closure.closure.net.Net
import com.heyanle.closure.utils.CoroutineProvider
import com.hypercubetools.ktor.moshi.moshi
import com.squareup.moshi.Moshi
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import org.koin.dsl.module

/**
 * Created by heyanlin on 2024/1/15 14:44.
 */
val closureModule = module {
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                moshi(it.get<Moshi>())
            }
        }
    }

    single {
        Net(CoroutineProvider.mainScope, it.get<HttpClient>())
    }
}