package com.taobao.daogou.client.network;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by rd on 14-8-6.
 * usage: NetworkHelper.getInstance(context)
 */
public class NetworkHelper {
    Context mContext;
    RequestQueue mQueue;

    private NetworkHelper(Context context) {
        this.mContext = context.getApplicationContext();
        mQueue = Volley.newRequestQueue(mContext);
    }


    static NetworkHelper mInstance;

    /**
     * 获取一个全局唯一的NetworkHelper
     * @param context ActivityContext
     * @return
     */
    public static synchronized NetworkHelper getInstance(Context context) {
        if (mInstance == null)
            mInstance = new NetworkHelper(context);
        return mInstance;
    }

    /**
     * 添加一个网络请求
     * @param request 一个Volley的网络请求
     * @param object 当前Activity的context，用来绑定TAG并在Activity结束时用于 cancel，也可以使用自定义对象
     */
    public void add(Request<?> request, Object object) {
        request.setTag(object);
        mQueue.add(request);
    }

    public void add(Request<?> request) {
        mQueue.add(request);
    }

    /**
     * 结束所有与当前Activity绑ntivity的onStop中调用）
     * @param context 当前Activity的context
     */
    public void cancelAll(Context context) {
        mQueue.cancelAll(context);
    }

    public void cancle(Object tag) {
        mQueue.cancelAll(tag);
    }


}
