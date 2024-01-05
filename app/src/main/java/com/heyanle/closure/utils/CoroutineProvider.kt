package com.heyanle.closure.utils

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

object CoroutineProvider {

    val SINGLE = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    val mainScope = MainScope()

}