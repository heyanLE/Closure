package com.heyanle.closure.net.model

/**
 * Created by HeYanLe on 2022/12/23 15:14.
 * https://github.com/heyanLE
 */
data class GameStatus(
    var code: Int,
    var text: String,
) {

    /**
     * WebGame.Status.Code 表示当前用户状态
    WebGame.Status.Code = -1 登陆失败
    WebGame.Status.Code = 0 未开启/未初始化/正在初始化但未登录
    WebGame.Status.Code = 1 登录中
    WebGame.Status.Code = 2 登陆完成/运行中
    WebGame.Status.Code = 3 游戏错误
     */

}