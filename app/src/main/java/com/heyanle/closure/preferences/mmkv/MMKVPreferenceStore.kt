package com.heyanle.closure.preferences.mmkv

import android.content.Context
import com.heyanle.closure.preferences.Preference
import com.heyanle.closure.preferences.PreferenceStore
import com.heyanle.closure.utils.jsonTo
import com.heyanle.closure.utils.toJson

/**
 * Created by HeYanLe on 2023/7/30 19:20.
 * https://github.com/heyanLE
 */
class MMKVPreferenceStore(
    context: Context
): PreferenceStore {
//    init {
//        Okkv.Builder(MMKVStore(context)).cache().build().init().default()
//        Okkv.Builder(MMKVStore(context)).build().init().default("no_cache")
//    }

    override fun getString(key: String, default: String): Preference<String> {
        return MMKVPreference(key, default)
    }

    override fun getInt(key: String, default: Int): Preference<Int> {
        return MMKVPreference(key, default)
    }

    override fun getLong(key: String, default: Long): Preference<Long> {
        return MMKVPreference(key, default)
    }

    override fun getFloat(key: String, default: Float): Preference<Float> {
        return MMKVPreference(key, default)
    }

    override fun getBoolean(key: String, default: Boolean): Preference<Boolean> {
        return MMKVPreference(key, default)
    }

    override fun getStringSet(key: String, defaultValue: Set<String>): Preference<Set<String>> {
        return getObject(
            key,
            defaultValue,
            serializer = {
                it.toList().toJson()
            },
            deserializer = {
                val d: List<String>? = it.jsonTo()
                d?.toSet()?:defaultValue
            }
        )
    }

    override fun <T> getObject(
        key: String,
        defaultValue: T,
        serializer: (T) -> String,
        deserializer: (String) -> T
    ): Preference<T> {
        return MMKVObjectPreference(
            key,
            defaultValue,
            serializer,
            deserializer,
        )
    }
}