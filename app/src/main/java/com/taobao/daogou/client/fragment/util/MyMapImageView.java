package com.taobao.daogou.client.fragment.util;

import android.animation.Animator;
import android.animation.FloatEvaluator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.taobao.daogou.client.R;
import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;
import com.taobao.daogou.client.util.OrientationHelper;
import com.taobao.daogou.client.util.onOrientationListener;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rd on 14-8-11.
 */
public class MyMapImageView extends ImageView implements
        onDisplayMatrixChangeListener,
        onDisplayMatrixCheckListener,
        ViewTreeObserver.OnGlobalLayoutListener,
        Constants,
        onUserSelectionListener,
        WifiHelper.onWifiLocation,
        mapPoint,
        onOrientationListener {

    Matrix mDisplayMatrix;
    private Matrix mBaseMatrix = new Matrix();
    private Matrix mDrawingMatrix = new Matrix();
    private Matrix mTextDrawingMatrix = new Matrix();
    private Matrix mTempMatrix = new Matrix();

    public MyMapImageView(Context context) {
        super(context);
    }

    public MyMapImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMapImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    Point screenSize = new Point();

    public void init() {
        //TODO
        setDrawingCacheEnabled(true);
        setScaleType(ScaleType.MATRIX);
        ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnGlobalLayoutListener(this);
    }

    int mFileScale = 1;

    public void setBitmapWithScale(Bitmap bitmap, int scale) {
        if (bitmap == null) return;
        setImageBitmap(bitmap);
        mFileScale = scale;
        update();
    }

    public void update() {
        int drawableHeight = getDrawable().getIntrinsicHeight();
        int drawableWidth = getDrawable().getIntrinsicWidth();

        float viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        float widthScale = viewWidth / drawableWidth;
        float heightScale = viewHeight / drawableHeight;

        mBaseMatrix.reset();

        float scale = Math.min(widthScale, heightScale) * 1.4f;
        mBaseMatrix.postScale(scale, scale);
        mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                (viewHeight - drawableHeight * scale) / 2F);
        requestMapReload();
    }

    Paint mPaint;
    Paint mLinePaint;
    private int mTextSize;
    private RectF mRectF;
    private Bitmap locationBitmap, orientationBitmap;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.DKGRAY);
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.SERIF);

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(getContext().getResources().getColor(R.color.route_line));
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(3f * getResources().getDisplayMetrics().density);

        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                25,
                getContext().getResources().getDisplayMetrics());
        mPaint.setTextSize(mTextSize);

        mRectF = new RectF();

        locationBitmap = BitmapFactory
                .decodeResource(getContext().getResources(), R.drawable.iconfont_ico09);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outHeight = 30;
        options.outWidth = 30;
        orientationBitmap = BitmapFactory
                .decodeResource(getContext().getResources(), R.drawable.icon_location, options);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    List<MapTextStructure> mTextStructureList;

    public void setTextList(List<MapTextStructure> mList) {
        this.mTextStructureList = mList;
        points = new float[mTextStructureList.size() * 2];
        requestMapReload();
    }

    float logoSize;
    final float scaleDivider = 2.2F;
    float points[];
    float p[] = new float[2];

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawingMatrix == null || mTextStructureList == null) return;
        mTextDrawingMatrix.set(mDrawingMatrix);

        for (int i = 0; i != mTextStructureList.size(); ++i) {
            points[i * 2] = mTextStructureList.get(i).X;
            points[i * 2 + 1] = mTextStructureList.get(i).Y;
        }
        mTextDrawingMatrix.mapPoints(points);

        for (int i = 0; i != mTextStructureList.size(); ++i) {
            MapTextStructure structure = mTextStructureList.get(i);

            if (logoMap != null && logoMap.get(structure.icon) != null) {
                logoSize = 20;
                mPaint.setTextSize(mTextSize);
                if (CommonHelper.getValues(mDrawingMatrix, Matrix.MSCALE_X) < scaleDivider) {
                    logoSize = 30;
                }
                if (CommonHelper.getValues(mDrawingMatrix, Matrix.MSCALE_X) > scaleDivider) {
                    logoSize *= CommonHelper.getValues(mDrawingMatrix, Matrix.MSCALE_X);

                }
                if (CommonHelper.getValues(mDrawingMatrix, Matrix.MSCALE_X) > 3.5f) {
                    logoSize = 70;
                    mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                            35,
                            getContext().getResources().getDisplayMetrics()));
                }

                // 判断如果是选择了的就放大图标
                if (userSelectionMap != null) {
                    String name = nameLogoMap.get(structure.icon.toLowerCase());
                    if (name != null) {
                        if (userSelectionMap.get(name) != null && userSelectionMap.get(name)) {
                            logoSize *= 2;
                        }
                    }
                }

                mRectF.left = points[i * 2];
                mRectF.top = points[i * 2 + 1];
                mRectF.right = mRectF.left + logoSize;
                mRectF.bottom = mRectF.top + logoSize;
                canvas.drawBitmap(logoMap.get(structure.icon),
                        null,
                        mRectF,
                        null);
            }
            if (CommonHelper.getValues(mDrawingMatrix, Matrix.MSCALE_X) > scaleDivider)
                canvas.drawText(
                        structure.text,
                        points[i * 2],
                        points[i * 2 + 1],
                        mPaint
                );
        }

        if (mLocationPointf != null) {

            p[0] = mLocationPointf.x;
            p[1] = mLocationPointf.y;
            mDrawingMatrix.mapPoints(p);

            float size = 35 * CommonHelper.getValues(mDrawingMatrix, Matrix.MSCALE_X);

            mRectF.left = p[0] - size / 3;
            mRectF.top = p[1] - size / 3;
            mRectF.right = mRectF.left + size / 3 * 2;
            mRectF.bottom = mRectF.top + size / 3 * 2;

            if (Float.isNaN(mOrientation) || isAnimating)
                canvas.drawBitmap(locationBitmap, null, mRectF, null);


            if (!isAnimating) {
                //没有播放动画，此时画出定位
                mTempMatrix.reset();
//                mTempMatrix.setScale(30 / orientationBitmap.getWidth(), 30 / orientationBitmap.getWidth());
                mTempMatrix.setTranslate(
                        mRectF.left - orientationBitmap.getWidth() / 2,
                        mRectF.top - orientationBitmap.getHeight() / 2);
                mTempMatrix.postScale(
                        60f * CommonHelper.getValues(mDrawingMatrix, Matrix.MSCALE_X) / orientationBitmap.getWidth(),
                        60f * CommonHelper.getValues(mDrawingMatrix, Matrix.MSCALE_X) / orientationBitmap.getWidth(),
                        mRectF.left,
                        mRectF.top);
                mTempMatrix.postRotate(mOrientation,
                        mRectF.left,
                        mRectF.top);

                canvas.drawBitmap(orientationBitmap, mTempMatrix, null);
            }
        }
    }


    private void setMatrix(Matrix displayMatrix) {
        mDisplayMatrix = displayMatrix;
        resetDrawingMatrix();
        setImageMatrix(mDrawingMatrix);
        invalidate();
    }

    private void resetDrawingMatrix() {
        mDrawingMatrix.reset();
        mDrawingMatrix.set(mBaseMatrix);
        mDrawingMatrix.postConcat(mDisplayMatrix);
    }

    @Override
    public void onDisplayMatrixChange(Matrix displayMatrix) {
        setMatrix(displayMatrix);
    }

    @Override
    public boolean checkDisplayMatrix(Matrix postMatrix) {
        mTempMatrix.reset();
        mTempMatrix.set(mBaseMatrix);
        mTempMatrix.postConcat(postMatrix);
        return checkMatrixBounds(mTempMatrix);
    }

    private RectF mDisplayRect = new RectF();

    private boolean checkMatrixBounds(Matrix matrix) {
        mDisplayRect.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        matrix.mapRect(mDisplayRect);

        //判断缩放超过
        if (mDisplayRect.height() < getImageViewHeight() && mDisplayRect.width() < getImageViewWidth())
            return false;
        //位移超过
        if (mDisplayRect.right < getImageViewWidth() / 2)
            return false;
        if (mDisplayRect.bottom < getImageViewHeight() / 2)
            return false;
        if (mDisplayRect.left > getImageViewWidth() / 2)
            return false;
        if (mDisplayRect.top > getImageViewHeight() / 2)
            return false;

        if (CommonHelper.getValues(matrix, Matrix.MSCALE_X) > 5f)
            return false;

        return true;
    }

    @Override
    public void onGlobalLayout() {
        update();
    }

    /**
     * 通知上层视图重新调用matrix change 事件
     */
    private void requestMapReload() {
        Intent intent = new Intent(BROADCAST_MAP_RELOAD);
        getContext().sendBroadcast(intent);
    }

    Map<String, Bitmap> logoMap = null;

    public void setLogoMap(Map<String, Bitmap> logomap) {
        this.logoMap = logomap;
        invalidate();
    }

    private static Map<String, String> nameLogoMap = new HashMap<String, String>();
    private Map<String, Boolean> userSelectionMap;

    static {
        nameLogoMap.put("stairs.png", "步行梯");
        nameLogoMap.put("elevator.png", "垂直电梯");
        nameLogoMap.put("escalator.png", "自动扶梯");
        nameLogoMap.put("toilet.png", "洗手间");
        nameLogoMap.put("parking.png", "停车场");
        nameLogoMap.put("entrance.png", "商场入口");
        nameLogoMap.put("infomation.png", "资讯台");
    }

    @Override
    public void onUserSelection(Map<String, Boolean> selection) {
        this.userSelectionMap = selection;
        Log.v("userSelection", this.userSelectionMap.toString());
        invalidate();
    }

    PointF mLocationPointf;
    boolean isAnimating = false;

    @Override
    public void onWifiLocation(String location) {
        PointF targetWifiLocationPoint = getLocationPoint(location);
        if (targetWifiLocationPoint == null) return;
        ValueAnimator valueAnimator = ValueAnimator.ofObject(
                new LocationEvaluator(),
                new PointF(0, 0),
                targetWifiLocationPoint);
        valueAnimator.setDuration(750);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mLocationPointf = (PointF) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.start();
        invalidate();
    }

    private PointF getLocationPoint(String location) {
        if (location == null || mTextStructureList == null) return null;
        location = location.toLowerCase();
        for (MapTextStructure structure : mTextStructureList) {
            if (structure.icon.toLowerCase().startsWith(location)) {
                return new PointF(structure.X, structure.Y);
            }
        }
        return null;
    }

    private float getImageViewWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private float getImageViewHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    float[] tmpPoint = new float[2];
    @Override
    public void mapPoint(float x, float y, float[] dst) {
        if (mDrawingMatrix != null) {
            tmpPoint[0] = x;
            tmpPoint[1] = y;
            mDrawingMatrix.mapPoints(dst, tmpPoint);
        }
    }

    private float mOrientation = Float.NaN;

    @Override
    public void onOrientation(float value) {
        if (Float.isNaN(value)) return;
        this.mOrientation = value - 30;
        postInvalidate();
    }


    private static class LocationEvaluator implements TypeEvaluator<PointF> {
        private FloatEvaluator evaluator = new FloatEvaluator();
        private PointF currentPoint = new PointF();

        @Override
        public PointF evaluate(float v, PointF pointF, PointF pointF2) {
            currentPoint.y = evaluator.evaluate(v, pointF.y, pointF2.y);
            currentPoint.x = pointF2.x;
            return currentPoint;
        }
    }
}
