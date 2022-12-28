package com.heyanle.closure.net.model

import com.google.gson.annotations.SerializedName
import com.heyanle.closure.app
import com.heyanle.closure.net.Data

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

    fun getMapCode(): String {
        return Data.getStageTable(app).getJSONObject(gameConfig.battleMaps[0]).getString("code")
    }

    fun getMapName(): String {
        return Data.getStageTable(app).getJSONObject(gameConfig.battleMaps[0]).getString("name")
    }

}