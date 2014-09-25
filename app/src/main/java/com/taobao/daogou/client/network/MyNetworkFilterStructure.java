package com.taobao.daogou.client.network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 一个简单的网络过滤类，用于将返回的数据解析成成功或失败，并获得数据或错误原因
 */
public class MyNetworkFilterStructure {
    String error;
    JSONObject data;

    public MyNetworkFilterStructure(JSONObject jsonObject) {
        if (jsonObject == null) {
            error = "网络错误";
            return;
        }

        try {
            boolean success = jsonObject.has("success") && jsonObject.getBoolean("success");
            data = jsonObject.getJSONObject("data");
            return;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            error = jsonObject.has("error") ? jsonObject.getString("error") : "未知错误";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        error = "未知错误";
    }
}
