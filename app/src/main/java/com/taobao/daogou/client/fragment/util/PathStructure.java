package com.taobao.daogou.client.fragment.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian on 14-8-18.
 */
public class PathStructure {

    List<String> pathPos;
    String msg;
    public String html;
    public PathStructure(JSONObject jsonObject) {
        try {
            this.msg = jsonObject.getString("msg");
            JSONArray jsonArray = jsonObject.getJSONArray("passedPoints");
            pathPos = new ArrayList<String>();
            for(int i=0; i<jsonArray.length(); i++)
                pathPos.add(jsonArray.getString(i));
            StringBuilder sb = new StringBuilder();

            int length = pathPos.size();

            msg = msg.replace("从 " + pathPos.get(0), "从 " + "<font color='#00ff5000'>" + pathPos.get(0) + "</font>");
            msg = msg.replace("已经在 " + pathPos.get(0), "已经在 " + "<font color='#00ff5000'>" + pathPos.get(0) + "</font>");
            msg = msg.replace("到达 " + pathPos.get(length-1), "到达 " + "<font color=\"#00ff5000\">" + pathPos.get(length-1) + "</font>");
            for(int i=0; i<length; i++) {
                String s = pathPos.get(i);
                msg = msg.replace(s, "<b>" + s + "</b>");
            }
            sb.append("<big>" + "路线" + "</big><br>");
            sb.append(msg);

            html = sb.toString();

            //
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
