package com.heyanle.closure.net.model

import com.google.gson.annotations.SerializedName

/**
 * Created by HeYanLe on 2022/12/23 15:15.
 * https://github.com/heyanLE
 */
data class GameResp(
    var config: Config,
    var status: GameStatus,
    @SerializedName("captcha_info")
    var captchaInfo: CaptchaInfo,
    @SerializedName("game_config")
    var gameConfig: GameConfig,
) {

}