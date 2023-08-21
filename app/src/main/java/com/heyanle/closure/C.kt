package com.heyanle.closure

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import com.heyanle.closure.utils.stringRes

/**
 * Created by HeYanLe on 2023/4/4 21:11.
 * https://github.com/heyanLE
 */
object C {

    const val extensionUrl = "https://easybangumi.org/extensions/"

    sealed class About {

        data class Copy(
            val icon: Any?,
            val title: String,
            val msg: String,
            val copyValue: String,
        ): About()

        data class Url(
            val icon: Any?,
            val title: String,
            val msg: String,
            val url: String,
        ): About()
    }

    val aboutList: List<About> by lazy {
        listOf<About>(

            About.Url(
                icon = R.drawable.github,
                title = stringRes(R.string.github),
                msg = stringRes(R.string.click_to_explore),
                url = "https://github.com/heyanLE/Closure"
            ),
        )
    }

}