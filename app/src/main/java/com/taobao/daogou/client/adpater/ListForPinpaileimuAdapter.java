package com.taobao.daogou.client.adpater;

import android.content.Context;
import android.graphics.Color;
import android.text.style.DrawableMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.taobao.daogou.client.R;
import com.taobao.daogou.client.util.InflaterHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by jian on 14-8-14.
 */
public class ListForPinpaileimuAdapter extends MyBaseAdapter {

    private int mActive;
    private int mSelect, mUnselect;

    public ListForPinpaileimuAdapter(Context c, List<JSONObject> d) {
        super(c, d);
        mActive = 0;
        mSelect = 0xffed7551;
        mUnselect = 0xff5f646e;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null)
            vi = inflater.inflate(R.layout.list_pinpaileimu, null);


        TextView type = (TextView) vi.findViewById(R.id.type);
        JSONObject jsonObject = data.get(i);
        try {
            type.setText(jsonObject.getString("category"));
            if(mActive == i) {
                type.setBackgroundResource(R.drawable.pinpaileimu_list_bg_select);
                type.setTextColor(mSelect);
            }
            else {
                type.setBackgroundResource(R.drawable.pinpaileimu_list_bg_unselect);
                type.setTextColor(mUnselect);
            }
            } catch (JSONException e) {
            e.printStackTrace();
        }


        return vi;
    }

    public void setActive(int active) {
        mActive = active;
    }

    public String getTypeId(int i) {
        JSONObject jsonObject = data.get(i);
        try {
            String id = jsonObject.getString("categoryId");
            return id;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}

