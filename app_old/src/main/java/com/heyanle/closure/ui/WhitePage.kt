package com.heyanle.closure.ui

import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.heyanle.closure.R
import com.heyanle.closure.theme.ColorScheme

/**
 * Created by HeYanLe on 2022/12/28 18:26.
 * https://github.com/heyanLE
 */

@Composable
fun WhitePage(
    modifier: Modifier = Modifier,
    image: Any = R.drawable.error,
    message: String = "",
    other: @Composable ()->Unit = {},
){
    Box(
        modifier = Modifier.then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OKImage(image = image, contentDescription = message)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                color = Color.Gray.copy(alpha = 0.6f),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            other()
        }
    }


}



@Composable
fun LoadingPage(
    modifier: Modifier = Modifier,
    other: @Composable ()->Unit = {},
){
    Box(
        modifier = Modifier.then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(id = R.string.loading),
                color = Color.Gray.copy(alpha = 0.6f),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            other()
        }
    }
    // WhitePage(modifier, "file:///android_asset/loading.gif", stringResource(id = R.string.loading), other = other)
}

@Composable
fun ErrorPage(
    modifier: Modifier = Modifier,
    image: Any = R.drawable.error,
    errorMsg: String = "",
    clickEnable: Boolean = false,
    other: @Composable ()->Unit = {},
    onClick: ()->Unit = {},

    ){
    WhitePage(
        modifier.let {
            if(clickEnable){
                it.clickable {
                    onClick()
                }
            }else {
                it
            }
    }, image, errorMsg, other)
}