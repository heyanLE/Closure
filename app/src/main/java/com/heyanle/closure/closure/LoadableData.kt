package com.heyanle.closure.closure

/**
 * Created by heyanlin on 2024/1/18 17:07.
 */
  data class LoadableData<T> (
      val isLoading: Boolean = false,
      val isError: Boolean = false,
      val data: T? = null,
      val fromCache: Boolean = false,
      val errorMsg: String? = null,
      val throwable: Throwable? = null,
  )