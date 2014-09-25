package com.taobao.daogou.client.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taobao.daogou.client.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by jian on 14-8-9.
 */
public class GridForPinpaileimuAdapter extends MyBaseAdapter {

    public GridForPinpaileimuAdapter(Context c, List<JSONObject> d)
    {
        super(c, d);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null)
            vi = inflater.inflate(R.layout.grid_pinpaileimu, null);

        ImageView image = (ImageView) vi.findViewById(R.id.image);
        TextView name = (TextView) vi.findViewById(R.id.name);
        JSONObject jsonObject = data.get(i);
        try {
            imageLoader.displayImage(jsonObject.getString("pic"), image);
            name.setText(jsonObject.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  vi;
    }

    public String getShopId(int i) {
        JSONObject jsonObject = data.get(i);
        try {
            String id = jsonObject.getString("shopid");
            return id;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }}
