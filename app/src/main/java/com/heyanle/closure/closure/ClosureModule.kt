package com.heyanle.closure.closure

import android.content.Context
import com.heyanle.closure.closure.auth.AuthRepository
import com.heyanle.closure.closure.game.GameRepository
import com.heyanle.closure.closure.net.Net
import com.heyanle.closure.closure.quota.QuotaRepository
import com.heyanle.closure.utils.CoroutineProvider
import com.heyanle.closure.utils.getFilePath
import com.hypercubetools.ktor.moshi.moshi
import com.squareup.moshi.Moshi
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Created by heyanlin on 2024/1/15 14:44.
 */
val closureModule = module {
    single {
        val moshi = get<Moshi>()
        HttpClient(Android) {
            install(ContentNegotiation) {
                moshi(moshi)
            }
        }
    }

    single {
        Net(CoroutineProvider.mainScope, get<HttpClient>())
    }

    single {
        AuthRepository(get())
    }


    single {
        GameRepository(get())
    }

    single {
        QuotaRepository(get())
    }


    single {
        ClosureController(
            get<Context>().getFilePath("closure"),
            get(),
            get(),
            get(),
        )
    }

    factory(

    ) {
        ClosurePresenter(
            it.get(0), it.get(1), get(), get(), get(), get()
        )
    }
}