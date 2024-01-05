package com.heyanle.closure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.heyanle.closure.ui.common.LoadingImage
import com.heyanle.closure.utils.MediaUtils
import com.heyanle.okkv2.core.okkv
import org.koin.android.ext.android.get

/**
 * Created by heyanlin on 2023/12/31.
 */
class MainActivity: ComponentActivity() {

    private var first by okkv("first_visible", def = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MediaUtils.setIsDecorFitsSystemWindows(this, false)
        Scheduler.runOnMainActivityCreate(this, first)

    }

    @Composable
    fun Migrating() {
        val isMigrating by Migrate.isMigrating.collectAsState()
        if(isMigrating){
            AlertDialog(
                text = {
                    Row {
                        LoadingImage()
                        Text(text = stringResource(id = com.heyanle.i18n.R.string.migrating))
                    }
                },
                confirmButton = {},
                onDismissRequest = {  },
            )
        }
    }
}