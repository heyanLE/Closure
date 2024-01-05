package com.heyanle.closure.appcenter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.heyanle.closure.R
import com.heyanle.closure.compose.common.OKImage
import com.heyanle.closure.utils.openUrl
import com.heyanle.injekt.core.Injekt
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.ReleaseDetails
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by HeYanLe on 2023/3/13 20:39.
 * https://github.com/heyanLE
 */
class AppCenterUpdateController {

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
    val updateController: AppCenterUpdateController by Injekt.injectLazy()
    LaunchedEffect(key1 = Unit){
        updateController.onLaunch()
    }
    val release = updateController.releaseDetail.value
    val ctx = LocalContext.current
    if (updateController.showReleaseDialog.value && release != null) {
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
                    //ErrorIcon(Modifier.size(32.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("新版本更新！${release.shortVersion}(${release.version})")
                }
            },
            text = {
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(text = release.releaseNotes ?: "")
                }

            },
            onDismissRequest = { updateController.showReleaseDialog.value = false },
            confirmButton = {
                val uri = release.releaseNotesUrl ?: release.downloadUrl
                TextButton(
                    onClick = {
                        runCatching {
                            openUrl(ctx, uri.toString())
                        }
                    }, colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text(text = "下载")
                }


            })
    }

}