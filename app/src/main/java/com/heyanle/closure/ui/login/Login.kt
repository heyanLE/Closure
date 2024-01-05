package com.heyanle.closure.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.R
import com.heyanle.closure.auth.AuthController
import com.heyanle.closure.ui.common.OkImage
import com.heyanle.closure.utils.koin
import com.heyanle.closure.utils.stringRes

/**
 * Created by heyanlin on 2023/12/31.
 */
@Composable
fun Login() {

    val authController: AuthController by koin.inject()
    val status = authController.status.collectAsState()
    val sta = status

    val loginViewModel = viewModel<LoginViewModel>()


}

@Composable
fun LoginPage(
    onClosure: () -> Unit,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String) -> Unit,
) {
    
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xffEFEFEF))){
        OkImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .aspectRatio(720f / 615f),
            image = R.drawable.bg_main_top,
            contentDescription = stringResource(id = com.heyanle.i18n.R.string.closure),
        )

        Text(
            modifier = Modifier.align(Alignment.TopStart),
            color = Color.Black,
            text = stringResource(id = com.heyanle.i18n.R.string.welcome_en),
            fontSize = 32.sp,
            fontWeight = FontWeight(800)
        )
    }

}

@Composable
@Preview
fun LoginPreview(){
    LoginPage(
        onClosure = {  },
        onLogin = { email, password -> },
        onRegister = { email, password -> }
    )
}
