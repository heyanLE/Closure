package com.heyanle.closure.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.heyanle.closure.R
import com.heyanle.closure.utils.stringRes

/**
 * Created by HeYanLe on 2022/12/28 18:26.
 * https://github.com/heyanLE
 */

@Composable
fun WhitePage(
    modifier: Modifier = Modifier,
    image: Any = R.drawable.loading,
    message: String = "",
    other: @Composable ()->Unit = {},
){
    Box(
        modifier = Modifier.then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(image).build(),
                contentDescription = message
            )
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
    WhitePage(modifier, R.drawable.loading, stringResource(id = R.string.loading), other)
}

@Composable
fun ErrorPage(
    modifier: Modifier = Modifier,
    image: Any = R.drawable.error,
    errorMsg: String = "",
    clickEnable: Boolean = false,
    other: @Composable ()->Unit = {},
    onClick: ()->Unit,

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