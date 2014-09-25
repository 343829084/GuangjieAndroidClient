package com.taobao.daogou.client.fragment.util;

import java.util.Map;

/**
 * Created by rd on 14-8-14.
 */
public interface onUserSelectionListener {
    /**
     * 当用户点击了下面的按钮选择什么功能时触发
     * @param selection
     */
    public void onUserSelection(Map<String, Boolean> selection);
}
