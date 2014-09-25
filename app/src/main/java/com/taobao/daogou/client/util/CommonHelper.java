package com.taobao.daogou.client.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.util.Log;

import com.iflytek.cloud.RecognizerResult;
import com.taobao.daogou.client.fragment.util.MapTextStructure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一些通用的，难以分类的工具函数
 */
public class CommonHelper {
    /**
     * 根据科大讯飞的语音识别SDK返回的分词结果，生成一个完整的搜索语句
     *
     * @param recognizerResult 识别结果
     * @return 如果解析失败返回null
     */
    public static String getRecogResult(RecognizerResult recognizerResult) {
        StringBuilder sb = new StringBuilder();
        try {
            JSONArray words = new JSONObject(recognizerResult.getResultString())
                    .getJSONArray("ws");
            for (int i = 0; i != words.length(); ++i) {
                JSONArray cw = words.getJSONObject(i).getJSONArray("cw");
                sb.append(cw.getJSONObject(0).getString("w"));
            }
            return sb.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<JSONObject> getAdapterData(JSONArray jsonArray) {
        List<JSONObject> data = new ArrayList<JSONObject>();
        try {
            for (int i = 0; i < jsonArray.length(); ++i) {
                data.add(jsonArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static void getValuesFromMatrix(Matrix matrix, float[] values) {
        matrix.getValues(values);
    }

    public static void logMatrix(String tag, Matrix matrix) {
        if (matrix != null)
            Log.v(tag, matrix.toString());
    }

    private static float[] mMatrixValues = new float[9];

    public static float getValues(Matrix matrix, int offset) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[offset];
    }

    public static List<MapTextStructure> parseMapJson(JSONObject jsonObject) {
        try {
            List<MapTextStructure> mList = new ArrayList<MapTextStructure>();

            Pattern pattern = Pattern.compile("^[\\u4E00-\\u9FA5]*");

            JSONArray labels = jsonObject.getJSONArray("labels");
            for (int i = 0; i != labels.length(); ++i) {
                JSONObject lableObject = labels.getJSONObject(i);
                MapTextStructure structure = new MapTextStructure(
                        (float) lableObject.getDouble("x"),
                        (float) lableObject.getDouble("y"),
                        lableObject.getString("text"),
                        lableObject.getString("icon")
                );

                if (structure.text.matches("^[\\u4E00-\\u9FA5].*")) {
                    Matcher matcher = pattern.matcher(structure.text);
                    matcher.find();
                    structure.text = matcher.group();
                } else {

                }
                mList.add(structure);
            }
            return mList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static File getCacheDir(Context context) {
        return context.getCacheDir();
    }

    public static File getCacheDir(Context context, String dir) {
        File file = new File(getCacheDir(context), dir);
        if (!file.exists())
            file.mkdirs();
        return file;
    }

    public static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences(Constants.PREFERENCE, 0);
    }
}
