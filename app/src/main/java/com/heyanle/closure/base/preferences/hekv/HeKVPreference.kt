package com.heyanle.closure.base.preferences.hekv

import com.heyanle.closure.base.hekv.HeKV
import com.heyanle.closure.base.preferences.Preference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Created by HeYanLe on 2023/8/5 19:20.
 * https://github.com/heyanLE
 */
class HeKVPreference<T>(
    private val heKV: HeKV,
    private val key: String,
    private val def: T,
    private val serializer: (T) -> String,
    private val deserializer: (String) -> T
): Preference<T> {
    private val flow = MutableStateFlow(deserializer(heKV.get(key, serializer(def))))

    override fun key(): String {
        return key
    }

    override fun get(): T {
        return flow.value
    }

    override fun set(value: T) {
        flow.update { value }
        heKV.put(key, serializer(value))
    }

    override fun defaultValue(): T {
        return def
    }

    override fun isSet(): Boolean {
        return heKV.get(key, "") != ""
    }

    override fun delete() {
        heKV.put(key, "")
    }

    override fun flow(): Flow<T> {
        return flow
    }

    override fun stateIn(scope: CoroutineScope): StateFlow<T> {
        return flow().stateIn(scope, SharingStarted.Eagerly, get())
    }
}