package com.heyanle.closure.page.warehouse

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.heyanle.closure.R
import com.heyanle.closure.model.ItemModel
import com.heyanle.closure.net.Net
import com.heyanle.closure.net.model.GetGameResp
import com.heyanle.closure.page.MainController
import com.heyanle.closure.page.data
import com.heyanle.closure.page.error
import com.heyanle.closure.page.loading
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import com.heyanle.closure.utils.stringRes
import com.heyanle.closure.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by HeYanLe on 2022/12/31 23:10.
 * https://github.com/heyanLE
 */
class WarehouseViewModel: ViewModel() {

    val ocrBtnEnable = mutableStateOf(true)

    suspend fun loadGetGameResp(){
        val current = MainController.current.value
        val account = current?.account?:""
        val platform = current?.platform?:-1L
        val token = MainController.token.value?:""
        MainController.currentGetGame.loading()
        Net.game.game(token, platform, account).awaitResponseOK()
            .onSuccessful {
                withContext(Dispatchers.Main){
                    if(it == null){
                        MainController.currentGetGame.error(stringRes(R.string.load_error))
                    }else{
                        MainController.currentGetGame.data(it)
                    }

                }
            }.onFailed { b, s ->
                MainController.currentGetGame.error(s)
            }
    }

    suspend fun pushOcr(){
        val current = MainController.current.value
        val account = current?.account?:""
        val platform = current?.platform?:-1L
        val token = MainController.token.value?:""
        ocrBtnEnable.value = false
        Net.game.ocr(token, platform, account).awaitResponseOK()
            .onSuccessful {
                withContext(Dispatchers.Main){
                    ocrBtnEnable.value = true
                    stringRes(R.string.ocr_completely).toast()
                }
            }.onFailed { b, s ->
                withContext(Dispatchers.Main){
                    ocrBtnEnable.value = true
                    s.toast()
                }
            }
    }

    data class ItemIcon(
        val iconUrl: String,
        val count: Long,
    )

    fun getItems(getGameResp: GetGameResp, item: Map<String, ItemModel.ItemBean>): List<ItemIcon>{
        val items = arrayListOf<ItemIcon>()
        getGameResp.consumable?.forEach { (t, u) ->
            if(item.containsKey(t)){
                var count = 0L
                u.forEach {
                    count += it.count
                }
                if(count > 0){
                    items.add(
                        ItemIcon(
                            item[t]?.getIconUrl()?:"",
                            count
                        )
                    )
                }

            }
        }
        getGameResp.inventory?.forEach { (t, u) ->
            if(item.containsKey(t)){
                if(u > 0) {
                    items.add(
                        ItemIcon(
                            item[t]?.getIconUrl() ?: "",
                            u
                        )
                    )
                }
            }
        }
        return items
    }

}