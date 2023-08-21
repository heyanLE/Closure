package com.heyanle.closure.compose.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.heyanle.closure.R
import com.heyanle.closure.base.theme.ClosureThemeMode
import com.heyanle.closure.base.theme.DarkMode
import com.heyanle.closure.base.theme.ThemeController
import com.heyanle.closure.base.theme.getColorScheme
import com.heyanle.closure.compose.LocalNavController
import com.heyanle.closure.compose.common.OKImage
import com.heyanle.closure.utils.stringRes
import com.heyanle.injekt.core.Injekt

/**
 * Created by HeYanLe on 2023/8/20 22:27.
 * https://github.com/heyanLE
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Setting() {
    val themeController: ThemeController by Injekt.injectLazy()
    val themeState = themeController.flow.collectAsState()
    val nav = LocalNavController.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Column {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            title = {
                Text(text = stringResource(id = R.string.appearance_setting))
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
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.dark_mode),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W900,
            )
            DarkModeItem()

            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.theme),
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.W900,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                ClosureThemeMode.values().forEach {
                    Box(modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            it.getColorScheme(themeState.value.isDark()).secondary
                        )
                        .clickable {
                            themeController.changeThemeMode(it)
                        }) {
                        if (it.name == themeState.value.themeMode.name) {
                            Icon(
                                modifier = Modifier.align(Alignment.Center),
                                imageVector = Icons.Filled.Check,
                                contentDescription = stringResource(id = R.string.theme),
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }
        }


    }
}

@Composable
fun DarkModeItem() {
    val themeController: ThemeController by Injekt.injectLazy()
    val themeState = themeController.flow.collectAsState()
    val theme = themeState.value
    val list = listOf(
        Triple(Icons.Filled.Android, stringRes(R.string.dark_auto), DarkMode.Auto),
        Triple(Icons.Filled.WbSunny, stringRes(R.string.dark_off), DarkMode.Light),
        Triple(Icons.Filled.NightsStay, stringRes(R.string.dark_on), DarkMode.Dark)
    )

    val enableColor = MaterialTheme.colorScheme.primary
    val disableColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
    ) {
        list.forEachIndexed { index, (image, text, mode) ->
            val currentColor = if (theme.darkMode == mode) enableColor else disableColor
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
                    .border(
                        width = 1.dp,
                        color = currentColor,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                        if (theme.darkMode != mode) {
                            themeController.changeDarkMode(mode)
                        }
                    }
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = image,
                    contentDescription = text,
                    tint = currentColor,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(text = text, color = currentColor)
            }
        }
    }


}