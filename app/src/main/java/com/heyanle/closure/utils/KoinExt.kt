package com.heyanle.closure.utils

import org.koin.core.Koin
import org.koin.core.context.GlobalContext

/**
 * Created by heyanlin on 2023/12/31.
 */
val koin: Koin
    get() = GlobalContext.get()