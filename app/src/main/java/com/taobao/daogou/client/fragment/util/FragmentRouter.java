package com.taobao.daogou.client.fragment.util;

import android.os.Bundle;

/**
 * Created by rd on 14-8-7.
 */
public interface FragmentRouter {
    public void onFragmentChange(int fragmentID);
    public void onFragmentChangeWithBundle(int fragmentID, Bundle bundle);
}

