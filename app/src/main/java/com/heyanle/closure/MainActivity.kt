package com.heyanle.closure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heyanle.closure.theme.EasyTheme
import com.heyanle.closure.ui.common.LoadingImage
import com.heyanle.closure.ui.common.MoeDialog
import com.heyanle.closure.ui.common.MoeSnackBar
import com.heyanle.closure.utils.MediaUtils
import com.heyanle.okkv2.core.okkv

/**
 * Created by heyanlin on 2023/12/31.
 */
class MainActivity: ComponentActivity() {

    private var first by okkv("first_visible", def = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Scheduler.runOnMainActivityCreate(this, first)
        MediaUtils.setIsDecorFitsSystemWindows(this, false)

        setContent {
            EasyTheme {
               Migrating()
                val focusManager = LocalFocusManager.current
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { focusManager.clearFocus() })
                ) {
                    Host()
                    MoeSnackBar(Modifier.statusBarsPadding())
                    MoeDialog()
                }

            }
        }

    }

    @Composable
    fun Migrating() {
        val isMigrating by Migrate.isMigrating.collectAsState()
        if(isMigrating){
            AlertDialog(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LoadingImage(
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = stringResource(id = com.heyanle.i18n.R.string.migrating))
                    }
                },
                confirmButton = {},
                onDismissRequest = {  },
            )
        }
    }
}