package com.taobao.daogou.client.fragment.util;

import android.content.Context;
import android.graphics.Point;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rd on 14-8-18.
 */
public class RouteHelper {
    private static RouteHelper mInstance;

    public static synchronized RouteHelper getInstance(Context context) {
        if (mInstance == null)
            mInstance = new RouteHelper(context.getApplicationContext());
        return mInstance;
    }

    private Context mContext;

    private RouteHelper(Context context) {
        this.mContext = context;
    }

    /**
     * 将API中返回的楼层level数据转换成楼层名称
     *
     * @param level
     * @return
     */
    private static String name[] = {
            "", "B1", "BM", "L1", "L2", "L3", "L4"
    };

    public static String levelToName(int level) {
        return name[level];
    }

    public static int nameToLevel(String level) {
        return ArrayUtils.indexOf(name, level);
    }

    public static Integer[] getDiffLevel(List<RoutePosStructure> mList) {
        Set<Integer> set = new LinkedHashSet<Integer>();
        for (RoutePosStructure structure : mList)
            set.add(structure.level);
        Integer[] levels = set.toArray(new Integer[set.size()]);
        Arrays.sort(levels);
        ArrayUtils.reverse(levels);
        return levels;
    }

    public static List<RoutePosStructure> getRouteList(JSONObject jsonObject) {
        List<RoutePosStructure> list = new ArrayList<RoutePosStructure>();
        try {
            JSONArray routeList = jsonObject.getJSONObject("data").getJSONArray("routeList");
            for (int i = 0; i != routeList.length(); ++i)
                list.add(new RoutePosStructure(routeList.getJSONObject(i)));
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<RoutePosStructure> getRouteListWithLevel(
            String name, List<RoutePosStructure> posList) {
        int level = nameToLevel(name);
        if (posList == null)
            return null;
        List<RoutePosStructure> mList = new ArrayList<RoutePosStructure>();
        for (RoutePosStructure structure : posList) {
            if (structure.level == level)
                mList.add(structure);
        }
        return mList;
    }

    public static List<RouteCrossLevelStructure> getCrossLevelRoute(
            List<RoutePosStructure> mList) {
        List<RouteCrossLevelStructure> list = new ArrayList<RouteCrossLevelStructure>();
        for (int i = 0; i < mList.size() - 1; ++i) {
            if (mList.get(i).level != mList.get(i + 1).level) {
                RouteCrossLevelStructure structure = new RouteCrossLevelStructure();
                structure.fromLevel = mList.get(i).level;
                structure.toLevel = mList.get(i + 1).level;

                structure.fromPoint.x = mList.get(i).x;
                structure.fromPoint.y = mList.get(i).y;
                structure.toPoint.x = mList.get(i + 1).x;
                structure.toPoint.y = mList.get(i + 1).y;
                list.add(structure);
            }
        }
        return list;
    }

    /*
    获取路线的文字描述
     */
    public static PathStructure getPath(JSONObject jsonObject) {
        try {
            return new PathStructure(jsonObject.getJSONObject("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Created by rd on 14-8-18.
     */
    public static interface onRouteListener {
        public void onRoute(List<RoutePosStructure> mList);
    }

    public static interface onCrossLevelRouteListener {
        public void onCrossLevelRoute(
                List<RouteCrossLevelStructure> mList,
                Integer levels[]);
    }
}
