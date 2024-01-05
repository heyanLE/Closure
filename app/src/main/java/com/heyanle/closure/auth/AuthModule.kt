package com.heyanle.closure.auth

import org.koin.dsl.module

/**
 * Created by heyanlin on 2023/12/31.
 */
val authModule = module {
    single {
        AuthRepository(it.get())
    }
    single {
        AuthController(it.get(), it.get(), it.get())
    }
}