package com.heyanle.closure.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.heyanle.closure.R

/**
 * Created by HeYanLe on 2022/12/29 14:21.
 * https://github.com/heyanLE
 */

@Composable
fun OKImage(
    modifier: Modifier = Modifier,
    image: Any,
    contentDescription: String,
){
    Box(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(image).build(),
            contentDescription = contentDescription
        )
    }
}