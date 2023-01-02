package com.heyanle.closure.page.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.toUpperCase
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyanle.closure.model.StageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Created by HeYanLe on 2023/1/2 20:07.
 * https://github.com/heyanLE
 */
class BattleMapViewModel: ViewModel() {

    var keyword = mutableStateOf("")

    val result = MutableLiveData<List<StageModel.Stage>>(emptyList())

    val isLoading = mutableStateOf(false)

    suspend fun init(source: Map<String, StageModel.Stage>){
        val res = arrayListOf<StageModel.Stage>()
        source.forEach { (_, u) ->
            res.add(u)
        }
        result.value = res
    }

    suspend fun refresh(source: Map<String, StageModel.Stage>){
        isLoading.value = true
        withContext(Dispatchers.IO){
            val keyword = keyword.value.toUpperCase(Locale.getDefault())
            val res = arrayListOf<StageModel.Stage>()
            Log.d("BattleMapViewModel",keyword)
            source.forEach { (_, u) ->

                if(u.name.toUpperCase(Locale.getDefault()).contains(keyword)
                    || u.code.toUpperCase(Locale.getDefault()).contains(keyword)){
                    res.add(u)
                }
            }
            withContext(Dispatchers.Main){
                res.forEach {
                    Log.d("BattleMapViewModel", it.toString())
                }

                result.value = res
                isLoading.value = false
            }
        }

    }

}