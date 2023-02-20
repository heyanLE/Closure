package com.heyanle.closure.ui

import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.heyanle.closure.R
import com.heyanle.closure.theme.ColorScheme

/**
 * Created by HeYanLe on 2022/12/28 18:40.
 * https://github.com/heyanLE
 */

@Composable
fun LoadingIcon(
    modifier: Modifier = Modifier
){
    AsyncImage(
        model = ImageRequest
            .Builder(LocalContext.current)
            .data("file:///android_asset/loading.gif")
            .placeholder(ColorDrawable(ColorScheme.secondaryContainer.toArgb()))
            .crossfade(true)
            .apply {
                decoderFactory(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                        ImageDecoderDecoder.Factory()
                    else GifDecoder.Factory()
                )
            }
            .error(ColorDrawable(ColorScheme.error.toArgb()))
            .build(),
        contentDescription = stringResource(id = R.string.loading),
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.size(40.dp)
    )
}

@Composable
fun ErrorIcon(
    modifier: Modifier = Modifier
){
    Image(
        modifier = Modifier.size(40.dp).then(modifier),
        painter = painterResource(id = R.drawable.kaltsit),
        contentDescription = stringResource(id = R.string.error))
}