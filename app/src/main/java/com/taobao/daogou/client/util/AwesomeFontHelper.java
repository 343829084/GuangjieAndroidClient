package com.taobao.daogou.client.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by rd on 14-8-6.
 */
public class AwesomeFontHelper implements Constants{
    Context mContext;
    WeakReference<Typeface> mWRTypeFace;
    private AwesomeFontHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    static AwesomeFontHelper mInstance;
    public static synchronized AwesomeFontHelper getInstance(Context context) {
        if (mInstance == null)
            mInstance = new AwesomeFontHelper(context);
        return mInstance;
    }

    private void initTypeface() {
        if (mWRTypeFace == null || mWRTypeFace.get() == null) {
            mWRTypeFace = new WeakReference<Typeface>(
                    Typeface.createFromAsset(mContext.getAssets(),
                            FONT_AWESOME_FILE_NAME));
        }
    }

    public void setTypeFace(TextView textView) {
        initTypeface();
        textView.setTypeface(mWRTypeFace.get());
    }
}
