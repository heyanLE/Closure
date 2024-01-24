package com.heyanle.closure.closure

import android.app.Application
import android.content.Context
import com.heyanle.closure.closure.auth.AuthRepository
import com.heyanle.closure.closure.game.GameRepository
import com.heyanle.closure.closure.net.Net
import com.heyanle.closure.closure.quota.QuotaRepository
import com.heyanle.closure.utils.CoroutineProvider
import com.heyanle.closure.utils.getFilePath
import com.heyanle.injekt.api.InjektModule
import com.heyanle.injekt.api.InjektScope
import com.heyanle.injekt.api.addSingletonFactory
import com.heyanle.injekt.api.get
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
class ClosureModule(
    private val application: Application,
): InjektModule {
    override fun InjektScope.registerInjectables() {
        addSingletonFactory {
            val moshi = get<Moshi>()
            HttpClient(Android) {
                install(ContentNegotiation) {
                    moshi(moshi)
                }
            }
        }

        addSingletonFactory {
            Net(CoroutineProvider.mainScope, get<HttpClient>())
        }

        addSingletonFactory {
            AuthRepository(get())
        }

        addSingletonFactory {
            GameRepository(get())
        }

        addSingletonFactory {
            QuotaRepository(get())
        }

        addSingletonFactory {
            ClosureController(
                application.getFilePath("closure"),
                get(),
                get(),
                get(),
            )
        }

        addScopedPerKeyFactory<ClosurePresenter, String> {
            ClosurePresenter(it, application.getFilePath("closure"), get(), get(), get(), get())
        }
    }


}