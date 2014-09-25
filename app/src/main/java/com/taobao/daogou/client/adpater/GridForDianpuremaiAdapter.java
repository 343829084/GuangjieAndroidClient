package com.taobao.daogou.client.adpater;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taobao.daogou.client.MyActivity;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by jian on 14-8-9.
 */
public class GridForDianpuremaiAdapter extends MyBaseAdapter {


    public GridForDianpuremaiAdapter(Context c, List<JSONObject> d)
    {
        super(c, d);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null)
            vi = inflater.inflate(R.layout.grid_dianpuremai, null);

        ImageView image = (ImageView) vi.findViewById(R.id.image);
        TextView name = (TextView) vi.findViewById(R.id.name);
        TextView price = (TextView) vi.findViewById(R.id.price);
        final JSONObject jsonObject = data.get(i);
        try {
            imageLoader.displayImage(jsonObject.getString("itemPic"), image);
            name.setText(jsonObject.getString("itemName"));
            price.setText(jsonObject.getString("itemPrice"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  vi;
    }

    public String getBigPic(int i) {
        JSONObject jsonObject = data.get(i);
        try {
            String bigPic = jsonObject.getString("itemBigPic");
            return bigPic;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getItemName(int i) {
        JSONObject jsonObject = data.get(i);
        try {
            String itemName = jsonObject.getString("itemName");
            return itemName;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
