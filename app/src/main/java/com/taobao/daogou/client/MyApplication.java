package com.taobao.daogou.client;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.taobao.daogou.client.network.NetworkHelper;
import com.taobao.daogou.client.util.InflaterHelper;
import com.taobao.daogou.client.util.ThreadPoolHelper;


/**
 * Created by rd on 14-8-6.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetworkHelper.getInstance(this);
        ThreadPoolHelper.getInstance();

        //开启内存和硬盘的图片缓存，可以根据具体情况进行大小等配置
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        InflaterHelper.getInstance(this);
    }


}
