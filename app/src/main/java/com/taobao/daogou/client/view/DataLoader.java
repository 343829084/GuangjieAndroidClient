package com.taobao.daogou.client.view;

/**
 * Created by rd on 14-8-7.
 * Adapter的回调接口，加载listview,gridview的数据
 */
public interface DataLoader {
    public void loadData();
    public int getCount();
}
