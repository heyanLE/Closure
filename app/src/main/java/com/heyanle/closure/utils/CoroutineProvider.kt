package com.heyanle.closure.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

object CoroutineProvider {

    val SINGLE = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    val mainScope = MainScope()

    val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

}