package com.taobao.daogou.client.util;

import android.content.Context;
import android.view.LayoutInflater;

/**
 * Created by jian on 14-8-8.
 */
public class InflaterHelper {

    static LayoutInflater mInstance;

    public static synchronized LayoutInflater getInstance(Context context) {
        if(mInstance == null)
            mInstance = LayoutInflater.from(context);
        return mInstance;
    }
}
