package com.heyanle.closure.closure

/**
 * Created by heyanlin on 2024/1/18 17:07.
 */
data class LoadableData<T>(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val data: T? = null,
    val fromCache: Boolean = false,
    val errorMsg: String? = null,
    val throwable: Throwable? = null,
) {

    fun netEnable(): Boolean = !isLoading && !isError && data != null && !fromCache

    inline  fun<R> map(block: (T?) -> R?): LoadableData<R> {
        return LoadableData(isLoading, isError, block(data), fromCache, errorMsg, throwable)
    }

}