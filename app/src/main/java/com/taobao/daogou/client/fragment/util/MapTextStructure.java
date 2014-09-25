package com.taobao.daogou.client.fragment.util;

/**
 * Created by rd on 14-8-7.
 */
public class MapTextStructure {
    public String text, icon;
    public float X,Y;

    public MapTextStructure(float x, float y, String text, String icon) {
        this.X = x;
        this.Y = y;
        this.text = text;
        this.icon = icon;
    }

}
