package com.heyanle.closure.closure.stage

import org.json.JSONObject

// 一个关卡
data class Stage(
    val id: String,
    val name: String,
    val code: String,
    val ap: Int,
    val items: List<String>,
) {
    companion object {


        // 解析 Json 数据
        fun parsonFromResp(resp: String): Map<String, Stage> {
            val jsonObject = JSONObject(resp)
            val res = HashMap<String, Stage>()
            jsonObject.keys().forEach {
                val o = jsonObject.getJSONObject(it)
                val name = o.getString("name")
                val code = o.getString("code")
                val ap = o.getInt("ap")
                val items = o.getJSONArray("items")
                val item = arrayListOf<String>()
                for (i in 0 until items.length()) {
                    item.add(items.getString(i))
                }
                val stage = Stage(
                    id = it,
                    name = name,
                    code = code,
                    ap = ap,
                    items = item,
                )
                res[it] = stage
            }
            return res
        }
    }

    override fun toString(): String {
        return "Stage(id='$id', name='$name', code='$code', ap=$ap, items=$items)"
    }

}

