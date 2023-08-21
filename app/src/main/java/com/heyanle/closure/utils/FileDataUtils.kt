package com.heyanle.closure.utils

import java.io.File

/**
 * Created by HeYanLe on 2023/8/20 12:42.
 * https://github.com/heyanLE
 */
object FileDataUtils {

    fun write(file: File, value: String): Boolean {
        try {
            val parent = file.parentFile ?: run {
                file.delete()
                file.createNewFile()
                file.writeText(value)
                return true
            }
            parent.mkdirs()
            val bkFile = File(parent, "${file.name}.bk")
            bkFile.delete()
            bkFile.createNewFile()
            bkFile.writeText(value)
            file.delete()
            bkFile.renameTo(file)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun read(file: File, def: String): String {
        try {
            if (file.exists()) {
                return file.readText()
            }
            val parent = file.parentFile ?: run {
                return def
            }
            parent.mkdirs()
            val bkFile = File(parent, "${file.name}.bk")
            if (bkFile.exists()) {
                bkFile.renameTo(file)
                return file.readText()
            }
            return def
        } catch (e: Exception) {
            e.printStackTrace()
            return def
        }

    }

}