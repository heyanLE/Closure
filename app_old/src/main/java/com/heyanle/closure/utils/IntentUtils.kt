package com.heyanle.closure.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Created by HeYanLe on 2023/1/2 22:24.
 * https://github.com/heyanLE
 */

fun openUrl(context: Context, url: String){
    val intent = Intent(Intent.ACTION_VIEW,Uri.parse(url))
    context.startActivity(intent)
}