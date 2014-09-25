package com.taobao.daogou.client;

import android.app.Activity;

import com.taobao.daogou.client.network.NetworkHelper;

/**
 * 理论上所有Activity都应继承此Activity
 */
public class BaseActivity extends Activity {
    @Override
    protected void onStop() {
        super.onStop();
        NetworkHelper.getInstance(this).cancelAll(this);
    }
}
