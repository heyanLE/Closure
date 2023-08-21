package com.heyanle.closure.controller

import kotlinx.coroutines.MainScope

/**
 * 物品和关卡数据
 * Created by HeYanLe on 2023/8/12 12:20.
 * https://github.com/heyanLE
 */
class ModelController {

    companion object {
        // 理智图标
        const val AP_ICON_URL = "https://ak.dzp.me/dst/items/AP_GAMEPLAY.webp"

        // 源石图标
        const val DIAMOND_ICON_URL = "https://ak.dzp.me/dst/items/DIAMOND.webp"

        // 合成玉图标
        const val DIAMOND_SHD_ICON_URL = "https://ak.dzp.me/dst/items/DIAMOND_SHD.webp"

        // 龙门币图标
        const val GOLD_ICON_URL = "https://ak.dzp.me/dst/items/GOLD.webp"


        const val ITEM_URL = "https://arknights.host/data/Items.json"
        const val STAGE_URL = "https://arknights.host/data/Stage.json"
    }

    private val scope = MainScope()



    fun tryRefresh(){

    }



}