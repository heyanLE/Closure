package com.heyanle.closure.closure.log

import com.heyanle.closure.compose.common.moeSnackBar
import com.heyanle.closure.net.api.GameAPI
import com.heyanle.closure.net.model.GameLogItem
import com.heyanle.closure.utils.awaitResponseOK
import com.heyanle.closure.utils.onFailed
import com.heyanle.closure.utils.onSuccessful
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.File
import java.util.concurrent.Executors

/**
 * Created by HeYanLe on 2023/8/19 19:40.
 * https://github.com/heyanLE
 */
class ClosureLogControllerImpl(
    private val file: File,
    private val account: String,
    private val platform: Long,
    private val gameAPI: GameAPI,
) : ClosureLogController {

    private val bkFile = File(file.parentFile, file.name+".bk")

    private val logFlow = MutableStateFlow<List<GameLogItem>>(emptyList())
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val scope = MainScope()

    private var lastSaveJob: Job? = null

    init {
        file.parentFile?.mkdirs()
        scope.launch(dispatcher) {
            if (file.exists()) {
                loadFromFile(file)
            }else if(bkFile.exists()){
                bkFile.renameTo(file)
                loadFromFile(file)
            }
        }
        scope.launch {
            logFlow.collectLatest {
                save()
            }
        }
    }

    override fun flow(): Flow<List<GameLogItem>> {
        return logFlow.map { it.sortedBy { it.ts } }
    }

    override fun update(token: String) {
        scope.launch {
            gameAPI.getLog(token, platform, account, System.currentTimeMillis()).awaitResponseOK()
                .onSuccessful { res ->
                    logFlow.update {
                        it + res
                    }
                }
                .onFailed { b, s ->
                    s.moeSnackBar()
                }
        }

    }

    override fun refresh(token: String) {
        logFlow.update {
            emptyList()
        }
        update(token)
    }
    private fun save(){
        lastSaveJob?.cancel()
        lastSaveJob = scope.launch(dispatcher) {
            var save = logFlow.value.sortedByDescending { it.ts }
            if(save.size > 64)
                save = save.subList(0, 64)

            bkFile.delete()
            bkFile.createNewFile()
            val writer = bkFile.bufferedWriter()
            runCatching {
                save.forEach {
                    writer.write(it.ts.toString())
                    writer.newLine()
                    writer.write(it.info)
                    writer.newLine()
                    // 给一个 cancel 时点
                    yield()
                }
            }.onFailure {
                if(it is CancellationException ){
                    it.printStackTrace()
                }else{
                    throw it
                }
            }

            file.delete()
            bkFile.renameTo(file)
        }
    }

    private fun loadFromFile(f: File){
        val list = arrayListOf<GameLogItem>()
        val lines = f.readLines().iterator()
        while (lines.hasNext()) {
            val time = lines.next().toDoubleOrNull() ?: break
            if (lines.hasNext()) {
                val content = lines.next()
                list.add(GameLogItem(time, content))
            } else {
                break
            }
        }
        logFlow.update {
            it + list
        }
    }
}