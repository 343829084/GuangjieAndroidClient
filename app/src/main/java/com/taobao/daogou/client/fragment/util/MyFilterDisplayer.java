package com.taobao.daogou.client.fragment.util;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

/**
 * Created by rd on 14-8-14.
 */
public class MyFilterDisplayer implements BitmapDisplayer {
    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        Log.v("display", "self display called");
        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        drawable.setColorFilter(0xFFd1d1d1, PorterDuff.Mode.MULTIPLY);
        imageAware.setImageBitmap(bitmap);
    }
}
