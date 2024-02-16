package com.heyanle.closure.closure.assets

import android.content.Context
import com.heyanle.closure.closure.net.Net
import com.heyanle.closure.utils.logi
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/**
 * Created by heyanle on 2024/1/27.
 * https://github.com/heyanLE
 */
class AssetsItem(
    private val context: Context,
    private val net: Net,
    private val scope: CoroutineScope,
    private val netUrl: String,
    private val rootFolder: String,
    private val fileName: String,
    private val assetsName: String,
) {

    private val _res = MutableStateFlow("")
    val res = _res.asStateFlow()

    private val root = File(rootFolder)
    private val file = File(rootFolder, fileName)
    private val tempFile = File(rootFolder, "${fileName}.temp")

    fun init(){
        scope.launch {
            loadFromNet()
        }

    }


    private fun loadFromAssets(){
        val stageText = context.assets?.open(assetsName)?.bufferedReader()?.use {
            it.readText()
        } ?: return
        _res.update {
            stageText
        }
    }

    private fun loadFromFile(){
        if(file.exists()){
            val text = file.readText()
            _res.update {
                text
            }
        }else{
            tempFile.delete()
            loadFromAssets()
        }
    }

    private suspend fun loadFromNet(){
        netUrl.logi("AssetsItem")
        net.send<String> {
            get {
                url(netUrl)
                accept(ContentType.Application.Json)
            }
        }.await()
            .okWithData { res ->
                _res.update {
                    res
                }

                tempFile.delete()
                root.mkdirs()
                tempFile.createNewFile()
                tempFile.writeText(res)
                file.delete()
                tempFile.renameTo(file)
            }
            .error {
                //it.snackWhenError()
                it.throwable?.printStackTrace()
                loadFromFile()
            }
    }


}