package com.taobao.daogou.client.fragment.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class RoutePosStructure {
    int level, x, y;
    String shopPos;
    public RoutePosStructure(JSONObject jsonObject) {
        try {
            this.level = jsonObject.getInt("level");
            this.x = jsonObject.getInt("x");
            this.y = jsonObject.getInt("y");
            this.shopPos = jsonObject.getString("shopPos");

            this.level += 2;
            if (this.level == 7) this.level = 1;
            if (this.level == 8) this.level = 2;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
