package com.heyanle.closure.utils

import android.media.tv.TvContract.Channels.Logo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.heyanle.closure.R
import com.heyanle.closure.theme.ColorScheme
import com.heyanle.closure.ui.ErrorIcon
import com.heyanle.closure.ui.OKImage
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.ReleaseDetails
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by HeYanLe on 2023/3/13 20:39.
 * https://github.com/heyanLE
 */
object AppCenterManager {

    val ifCheck = AtomicBoolean(false)
    fun onLaunch(){
        if(ifCheck.compareAndSet(false, true)){
            Distribute.checkForUpdate()
        }
    }

    val releaseDetail = mutableStateOf<ReleaseDetails?>(null)

    val showReleaseDialog = mutableStateOf<Boolean>(false)
}

@Composable
fun ReleaseDialog() {
    LaunchedEffect(key1 = Unit){
        AppCenterManager.onLaunch()
    }
    val release = AppCenterManager.releaseDetail.value
    val ctx = LocalContext.current
    if (AppCenterManager.showReleaseDialog.value && release != null) {
        AlertDialog(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OKImage(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                        image = R.drawable.logo,
                        contentDescription = "新版本"
                    )
                    ErrorIcon(Modifier.size(32.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("新版本更新！${release.shortVersion}(${release.version})")
                }
            },
            text = {
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(text = release.releaseNotes ?: "")
                }

            },
            onDismissRequest = { AppCenterManager.showReleaseDialog.value = false },
            confirmButton = {
                val uri = release.releaseNotesUrl ?: release.downloadUrl
                TextButton(
                    onClick = {
                        runCatching {
                            openUrl(ctx, uri.toString())
                        }
                    }, colors = ButtonDefaults.textButtonColors(
                        containerColor = ColorScheme.secondary,
                        contentColor = ColorScheme.onSecondary
                    )
                ) {
                    Text(text = "下载")
                }


            })
    }

}