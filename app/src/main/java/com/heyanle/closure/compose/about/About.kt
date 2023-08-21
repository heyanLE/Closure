package com.heyanle.closure.compose.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.heyanle.closure.BuildConfig
import com.heyanle.closure.C
import com.heyanle.closure.R
import com.heyanle.closure.compose.LocalNavController
import com.heyanle.closure.compose.common.OKImage
import com.heyanle.closure.compose.common.moeSnackBar
import com.heyanle.closure.utils.openUrl
import com.microsoft.appcenter.distribute.Distribute

/**
 * Created by HeYanLe on 2023/4/1 23:06.
 * https://github.com/heyanLE
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About() {

    val nav = LocalNavController.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Surface(
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = {
                    Text(text = stringResource(id = R.string.about))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        nav.popBackStack()
                    }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.cancel)
                        )
                    }

                },
                scrollBehavior = scrollBehavior
            )



            Column(
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState())
            ) {
                EasyBangumiCard()

                Divider()

                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    headlineContent = {
                        Text(text = stringResource(id = R.string.version))
                    },
                    leadingContent = {
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = stringResource(id = R.string.version)
                        )
                    },
                    trailingContent = {
                        Text(text = BuildConfig.VERSION_NAME)
                    }

                )

                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.clickable {
                        Distribute.checkForUpdate()
                    },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.check_update))
                    },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Upload,
                            contentDescription = stringResource(id = R.string.check_update)
                        )
                    },
                )

                Divider()

                val manager: ClipboardManager = LocalClipboardManager.current

                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.clickable {
                        manager.setText(AnnotatedString("https://github.com/heyanLE/Closure"))
                        "复制成功".moeSnackBar()
                    },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.github))
                    },
                    trailingContent = {
                        Text(text = stringResource(id = R.string.click_to_explore))
                    },
                    leadingContent = {
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.github),
                            contentDescription = stringResource(id = R.string.github)
                        )
                    },
                )

            }
        }
    }

}

@Composable
fun EasyBangumiCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OKImage(
            modifier = Modifier.size(64.dp),
            image = R.drawable.logo,
            contentDescription = stringResource(R.string.app_name)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = stringResource(id = R.string.app_name))
    }

}
