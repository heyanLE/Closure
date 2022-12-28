package com.heyanle.closure.net;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;

public class Data {
    private static final String TAG = Data.class.getSimpleName();

    // http://ak.dzp.me/dst/items/SOCIAL_PT.webp
    // https://ak.dzp.me/dst/avatar/ASSISTANT/char_4043_erato.webp
    // https://assest.arknights.host/charpack/char_196_sunbr_summer_1.webp古米皮肤
    //    蒂蒂的图片网址
    //     https://github.com/Kengxxiao/ArknightsGameData/raw/master/en_US/gamedata/excel/item_table.json
    //    物品信息网址
    //     https://github.com/Kengxxiao/ArknightsGameData/raw/master/en_US/gamedata/excel/stage_table.json
    //    物品信息网址
    //     https://github.com/Kengxxiao/ArknightsGameData/raw/master/en_US/gamedata/excel/data_version.txt
    //    游戏数据版本地址


    // https://api.arknights.host/Game/11/1


    public static JSONObject getCharacterTable(Context context) throws JSONException, IOException {
        return getDataTable(context, "data/character_table.json");
    }

    public static JSONObject getItemTable(Context context) throws JSONException, IOException {
        return getDataTable(context, "data/item_table.json").getJSONObject("items");
    }

    public static JSONObject getStageTable(Context context) throws JSONException, IOException {
        return getDataTable(context, "data/stage_table.json").getJSONObject("stages");
    }

    public static JSONObject getDataTable(Context context, String name) throws JSONException, IOException {
        int size = (int) context.getAssets().openFd(name).getLength();
        byte[] cbuf = new byte[size];

        InputStream is = context.getAssets().open(name);
        int len = is.read(cbuf);
        String text = new String(cbuf, 0, len);
        JSONTokener tokener = new JSONTokener(text);
        JSONObject object = new JSONObject(tokener);
        // Log.d(TAG, "getCharacterTable: ");
        return object;
    }




}
