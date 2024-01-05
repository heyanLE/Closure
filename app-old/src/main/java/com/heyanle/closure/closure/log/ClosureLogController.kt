package com.heyanle.closure.closure.log

import com.heyanle.closure.net.model.GameLogItem
import kotlinx.coroutines.flow.Flow

/**
 * Created by HeYanLe on 2023/8/19 19:36.
 * https://github.com/heyanLE
 */
interface ClosureLogController {

    fun flow(): Flow<List<GameLogItem>>

    fun update(token: String)

    fun refresh(token: String)

}