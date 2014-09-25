package com.taobao.daogou.client.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.util.InflaterHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian on 14-8-8.
 */
public class ListForPinpaihuodongAdapter extends MyBaseAdapter {

    public ListForPinpaihuodongAdapter(Context c, List<JSONObject> d) {
        super(c, d);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null)
            vi = inflater.inflate(R.layout.list_pinpaihuodong, null);

        ImageView image = (ImageView) vi.findViewById(R.id.image);
        TextView name = (TextView) vi.findViewById(R.id.name);
        TextView youhui = (TextView) vi.findViewById(R.id.youhui);
        TextView position = (TextView) vi.findViewById(R.id.position);
        TextView miaoshu = (TextView) vi.findViewById(R.id.miaoshu);
        JSONObject jsonObject = data.get(i);
        try {
            imageLoader.displayImage(jsonObject.getString("pic"), image);
            name.setText(jsonObject.getString("name"));
            position.setText(jsonObject.getString("location"));
            String coupondesc = jsonObject.getString("coupondesc");
            coupondesc = coupondesc.substring(coupondesc.indexOf(":") + 1);
            youhui.setText(coupondesc);
            String desc = jsonObject.getString("startTime") + "至" + jsonObject.getString("endTime");
            miaoshu.setText(desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return vi;
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
    }
}
