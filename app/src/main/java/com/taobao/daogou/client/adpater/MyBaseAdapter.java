package com.taobao.daogou.client.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.taobao.daogou.client.util.InflaterHelper;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by jian on 14-8-9.
 * 所有Adapter的基类，利用ImageLoader加载图片
 */
public class MyBaseAdapter extends BaseAdapter {

    protected List<JSONObject> data;
    protected Context context;
    protected LayoutInflater inflater;
    protected ImageLoader imageLoader;

    public MyBaseAdapter(Context c, List<JSONObject> d) {
        context = c;
        data = d;
        inflater = InflaterHelper.getInstance(context);
        imageLoader = ImageLoader.getInstance();
    }

    public JSONObject getData(int i) {
        return data.get(i);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    public void addItem(JSONObject item) {
        data.add(item);
    }
}
