package com.heyanle.closure.compose.home.map_dialog

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.toUpperCase
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.closure.stage.Stage
import com.heyanle.closure.closure.stage.StageController
import com.heyanle.injekt.core.Injekt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Created by HeYanLe on 2023/1/2 20:07.
 * https://github.com/heyanLE
 */
class BattleMapViewModel: ViewModel() {

    private val stageController: StageController by Injekt.injectLazy()
    val allList = stageController.map

    var keyword = MutableStateFlow<String>("")


    val result = MutableLiveData<List<Stage>>(emptyList())

    val isLoading = mutableStateOf(false)

    init {
        viewModelScope.launch {
            combine(
                allList,
                keyword
            ){source, key ->
                val res = arrayListOf<Stage>()
                source.forEach { (_, u) ->

                    if(u.name.toUpperCase(Locale.getDefault()).contains(key.toUpperCase(Locale.getDefault()))
                        || u.code.toUpperCase(Locale.getDefault()).contains(key.toUpperCase(Locale.getDefault()))){
                        res.add(u)
                    }
                }
                res
            }.collectLatest {
                result.value = it
            }
        }
    }


}