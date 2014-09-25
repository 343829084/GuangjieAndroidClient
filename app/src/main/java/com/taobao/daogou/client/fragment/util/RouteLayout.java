package com.taobao.daogou.client.fragment.util;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.taobao.daogou.client.R;
import com.taobao.daogou.client.fragment.MapFragment;
import com.taobao.daogou.client.util.CommonHelper;

import org.apache.commons.lang3.ArrayUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rd on 14-8-18.
 */
public class RouteLayout extends LinearLayout
        implements RouteHelper.onRouteListener {
    public RouteLayout(Context context) {
        super(context);
    }

    public RouteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RouteLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    Paint mPaint, mArrowPaint;
    FragmentManager fManager;
    float[] point1 = new float[2], point2 = new float[2];

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        fManager = ((Activity) getContext()).getFragmentManager();

        mPaint = new Paint();
        mPaint.setColor(getContext().getResources().getColor(R.color.route_line));
        mPaint.setStrokeWidth(3f * getResources().getDisplayMetrics().density);
        mPaint.setStyle(Paint.Style.FILL);

        mArrowPaint = new Paint();
        mArrowPaint.setStyle(Paint.Style.STROKE);
        mArrowPaint.setStrokeWidth(5);
        mArrowPaint.setColor(mPaint.getColor());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mRouteList == null) return;
        for (int i = 0; i < mRouteList.size() - 1; ++i) {
            final RoutePosStructure structure1 = mRouteList.get(i);
            final RoutePosStructure structure2 = mRouteList.get(i + 1);
            final MapFragment fragment1, fragment2;
            fragment1 = (MapFragment) fManager.findFragmentByTag(RouteHelper.levelToName(structure1.level));
            fragment2 = (MapFragment) fManager.findFragmentByTag(RouteHelper.levelToName(structure2.level));

            final RectF rect1, rect2;
            rect1 = fragmentPosMap.get(ArrayUtils.indexOf(mLevels, structure1.level));
            rect2 = fragmentPosMap.get(ArrayUtils.indexOf(mLevels, structure2.level));

            fragment1.mapPoint(structure1.x, structure1.y, point1);
            fragment2.mapPoint(structure2.x, structure2.y, point2);
            canvas.drawLine(
                    point1[0] + rect1.left,
                    point1[1] + rect1.top,
                    point2[0] + rect2.left,
                    point2[1] + rect2.top,
                    mPaint);
        }

        calcArrow(canvas);
    }

    Map<Integer, RectF> fragmentPosMap = new LinkedHashMap<Integer, RectF>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        fragmentPosMap.clear();
        int count = getChildCount();
        float accuHeight = 0;
        for (int i = 0; i != count; ++i) {
            final View child = getChildAt(i);
            RectF rectF = new RectF();
            rectF.left = l;
            rectF.top = accuHeight;
            rectF.right = child.getMeasuredWidth() + rectF.left;
            rectF.bottom = child.getMeasuredHeight() + rectF.top;
            fragmentPosMap.put(i, rectF);

            accuHeight = rectF.bottom;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }


    private Integer mLevels[];
    private List<RoutePosStructure> mRouteList;

    @Override
    public void onRoute(List<RoutePosStructure> mList) {
        this.mRouteList = mList;
        mLevels = RouteHelper.getDiffLevel(mList);
    }

    private void calcArrow(Canvas canvas) {
        if (mRouteList == null || mRouteList.size() < 2) return;
        RoutePosStructure structure1 = mRouteList.get(mRouteList.size() - 2);
        RoutePosStructure structure2 = mRouteList.get(mRouteList.size() - 1);

        final MapFragment fragment1, fragment2;
        fragment1 = (MapFragment) fManager.findFragmentByTag(RouteHelper.levelToName(structure1.level));
        fragment2 = (MapFragment) fManager.findFragmentByTag(RouteHelper.levelToName(structure2.level));

        final RectF rect1, rect2;
        rect1 = fragmentPosMap.get(ArrayUtils.indexOf(mLevels, structure1.level));
        rect2 = fragmentPosMap.get(ArrayUtils.indexOf(mLevels, structure2.level));

        fragment1.mapPoint(structure1.x, structure1.y, point1);
        fragment2.mapPoint(structure2.x, structure2.y, point2);

        final PointF pointF1 = new PointF(), pointF2 = new PointF();
        pointF1.x = point1[0] + rect1.left;
        pointF1.y = point1[1] + rect1.top;
        pointF2.x = point2[0] + rect2.left;
        pointF2.y = point2[1] + rect2.top;

        double arrowAngle = Math.atan2((pointF2.y - pointF1.y), (pointF2.x - pointF1.x));
        Log.v("arrow", String.valueOf(arrowAngle));
        drawArrow(canvas, arrowAngle, pointF2.x, pointF2.y);
    }

    private void drawArrow(Canvas canvas, double angle, float x, float y) {
        final double h = 25;
        final double t = Math.toRadians(35);
        final double angle1 = Math.toRadians(180) + angle + t;
        final double angle2 = angle - Math.toRadians(35);

        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(
                (float) (h * Math.cos(angle1)) + x,
                (float) (h * Math.sin(angle1)) + y
        );
        canvas.drawPath(path, mArrowPaint);

        path.moveTo(x, y);
        path.lineTo(
                - (float) (h * Math.cos(angle2)) + x,
                - (float) (h * Math.sin(angle2)) + y
        );
        canvas.drawPath(path, mArrowPaint);
    }
}
