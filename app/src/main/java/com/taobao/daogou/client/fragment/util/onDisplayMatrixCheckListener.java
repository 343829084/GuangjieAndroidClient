package com.taobao.daogou.client.fragment.util;

import android.graphics.Matrix;

/**
 * 判断能否完成此次修改的listener
 */
public interface onDisplayMatrixCheckListener {
    public boolean checkDisplayMatrix(Matrix postMatrix);
}
