package com.heyanle.closure.closure.repository

import android.content.Context
import com.heyanle.closure.base.DataResult
import com.heyanle.closure.closure.entity.GameInfo
import com.heyanle.closure.closure.entity.GameSummary
import com.heyanle.closure.net.model.GameLogItem
import com.heyanle.closure.utils.FileDataUtils
import com.heyanle.closure.utils.getCachePath
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.util.concurrent.Executors

/**
 * Created by HeYanLe on 2023/8/19 19:57.
 * https://github.com/heyanLE
 */
class InstanceRepository(
    context: Context,
    moshi: Moshi,
) {

    private val rootDictionary = context.getCachePath("Closure")

    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val gameInfoJson = moshi.adapter<GameInfo>(GameInfo::class.java)


    suspend fun getInstanceInfoLocal(summary: GameSummary): DataResult<GameInfo> {
        return withContext(dispatcher) {
            val file = getInstanceInfoFile(summary)
            val json = FileDataUtils.read(file, "")
            if (json.isNotEmpty()) {
                val info = gameInfoJson.fromJson(json)
                if (info == null) {
                    DataResult.error("moshi error")
                } else {
                    DataResult.ok(info)
                }
            } else {
                DataResult.error("file not found")
            }
        }
    }

    fun updateInstanceInfoLocal(summary: GameSummary, gameInfo: GameInfo) {
        scope.launch(dispatcher) {
            val file = getInstanceInfoFile(summary)
            val json = gameInfoJson.toJson(gameInfo)
            if (json.isNotEmpty()) {
                FileDataUtils.write(file, json)
            }
        }
    }

    private fun getInstanceInfoFile(summary: GameSummary): File {
        return File(rootDictionary, "$summary.info")
    }


    suspend fun getLogLocal(summary: GameSummary): DataResult<List<GameLogItem>> {
        return DataResult.ok(withContext(dispatcher) {
            val file = getLogFile(summary)
            val lines = FileDataUtils.read(file, "").split("\n").iterator()
            val res = arrayListOf<GameLogItem>()
            while (lines.hasNext()) {
                val time = lines.next().toDoubleOrNull()
                if (time == null || !lines.hasNext()) {
                    break
                }
                val content = lines.next()
                res.add(GameLogItem(time, content))
            }
            res
        })
    }

    private var lastJob: Job? = null
    fun updateLogLocal(summary: GameSummary, log: List<GameLogItem>) {
        lastJob?.cancel()
        lastJob = scope.launch {
            val file = getLogFile(summary)
            var list = log.sortedByDescending { it.ts }
            if(list.size > 64){
                list = list.subList(0, 64)
            }
            val sb = StringBuilder()
            list.forEach {
                sb.append(it.ts).append("\n").append(it.info).append("\n")
            }
            yield()
            FileDataUtils.write(file, sb.toString())
        }
    }

    private fun getLogFile(summary: GameSummary): File {
        return File(rootDictionary, "$summary.log")
    }


}