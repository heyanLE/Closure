package com.heyanle.closure

import android.content.Context
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by heyanlin on 2023/12/31.
 */
object Migrate {

    private val _isMigrating = MutableStateFlow<Boolean>(true)
    val isMigrating = _isMigrating.asStateFlow()

    private val scope = MainScope()

    fun update(context: Context) {

    }
}