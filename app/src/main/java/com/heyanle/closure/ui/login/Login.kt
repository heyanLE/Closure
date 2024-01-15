package com.heyanle.closure.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyanle.closure.R
import com.heyanle.closure.closure.auth.AuthController
import com.heyanle.closure.ui.common.moeSnackBar
import com.heyanle.closure.utils.koin
import com.heyanle.closure.utils.openUrl

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

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(id = com.heyanle.i18n.R.string.app_name)
        )
        Row(
            Modifier
                .padding(top = 5.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = com.heyanle.i18n.R.string.login),
                fontSize = 20.sp,
            )
            Text(text = stringResource(id = com.heyanle.i18n.R.string.closure),
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .clickable {
                        kotlin
                            .runCatching {
                                "https://arknights.host/".openUrl()
                            }
                            .onFailure {
                                it.moeSnackBar()
                                it.printStackTrace()
                            }

                    })
        }



    }

}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xff1F1F1F,
    showSystemUi = true
)
fun LoginPreview(){
    LoginPage(
        onClosure = {  },
        onLogin = { email, password -> },
        onRegister = { email, password -> }
    )
}
