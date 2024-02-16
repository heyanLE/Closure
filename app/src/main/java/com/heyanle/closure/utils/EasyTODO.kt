package com.heyanle.closure.utils

import android.app.Application
import com.heyanle.closure.ClosureApp
import com.heyanle.closure.R
import com.heyanle.closure.ui.common.MoeSnackBarData
import com.heyanle.closure.ui.common.moeSnackBar
import com.heyanle.closure.ui.common.show
import com.heyanle.injekt.api.get
import com.heyanle.injekt.core.Injekt

/**
 * Created by heyanle on 2024/2/17.
 * https://github.com/heyanLE
 */
fun easyTODO(){
    "该功能还未支持，请打开网站处理！".moeSnackBar(
        confirmLabel = "访问官网",
        onConfirm = {
            "https://closure.ltsc.vip/".openUrl()

        }
    )
}