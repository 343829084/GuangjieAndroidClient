package com.taobao.daogou.client.fragment.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 处于地图最上层的透明视图，用于触摸相应事件等
 */
public class MyMapFramelayout extends FrameLayout implements gesListener, Constants {

    ScaleGestureDetector mScaleGestureDetector;
    List<onDisplayMatrixChangeListener> mDisplayMatrixChangeListenerList
            = new ArrayList<onDisplayMatrixChangeListener>();
    List<onDisplayMatrixCheckListener> mDisplayMatrixCheckListener
            = new ArrayList<onDisplayMatrixCheckListener>();
    Matrix mDisplayMatrix = new Matrix();
    Matrix mTempMatrix = new Matrix();

    public MyMapFramelayout(Context context) {
        super(context);
    }

    public MyMapFramelayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    float mTouchSlop;
    public MyMapFramelayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init() {
        mScaleGestureDetector = new ScaleGestureDetector(
                getContext(), onScaleGestureListener
        );
        final ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    public void addOnDisplayMatrixChangeListsner(onDisplayMatrixChangeListener listener) {
        mDisplayMatrixChangeListenerList.add(listener);
    }

    public void addOnDisplayMatrixCheckListener(onDisplayMatrixCheckListener listener) {
        mDisplayMatrixCheckListener.add(listener);
    }

    boolean isDraging = false, fromSingle = false;
    float mLastX = 0, mLastY = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDraging = false;
                fromSingle = true;
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float sx = event.getX() - mLastX;
                float sy = event.getY() - mLastY;

                if (event.getPointerCount() != 1)
                    break;

                if (!isDraging) {
                    if (FloatMath.sqrt( sx * sx + sy * sy) >= mTouchSlop)
                        isDraging = true;
                    break;
                }

                if (isDraging && fromSingle) {
                    mLastX = event.getX();
                    mLastY = event.getY();
                    onTranslate(sx, sy);
                }

                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    ScaleGestureDetector.OnScaleGestureListener
            onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor))
                return false;
            MyMapFramelayout.this.onScale(scaleFactor,
                    scaleGestureDetector.getFocusX(),
                    scaleGestureDetector.getFocusY());
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            fromSingle = false;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            fromSingle = false;
        }
    };

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        if (checkMatrix(Float.NaN, Float.NaN, scaleFactor, focusX, focusY)) {
            mDisplayMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            fireDisplayChangeListener();
        }
    }

    @Override
    public void onTranslate(float dx, float dy) {
        if (checkMatrix(dx, dy, Float.NaN, Float.NaN, Float.NaN)) {
            mDisplayMatrix.postTranslate(dx, dy);
            fireDisplayChangeListener();
        }
    }

    private void fireDisplayChangeListener() {
        for (onDisplayMatrixChangeListener listener : mDisplayMatrixChangeListenerList)
            listener.onDisplayMatrixChange(mDisplayMatrix);
    }

    private boolean checkMatrix(float dx, float dy, float scale, float focusX, float focusY) {
        mTempMatrix.set(mDisplayMatrix);
        if (!Float.isNaN(scale))
            mTempMatrix.postScale(scale, scale, focusX, focusY);
        if (!Float.isNaN(dx))
            mTempMatrix.postTranslate(dx, dy);

        for (onDisplayMatrixCheckListener listener : mDisplayMatrixCheckListener) {
            if (!listener.checkDisplayMatrix(mTempMatrix))
                return false;
        }
        return true;
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fireDisplayChangeListener();
        }
    };
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(mReceiver);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getContext().registerReceiver(mReceiver, new IntentFilter(BROADCAST_MAP_RELOAD));

    }
}
