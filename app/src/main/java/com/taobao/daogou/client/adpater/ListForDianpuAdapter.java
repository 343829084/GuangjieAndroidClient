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
 * Created by jian on 14-8-8.
 */
public class ListForDianpuAdapter extends MyBaseAdapter {

    public ListForDianpuAdapter(Context c, List<JSONObject> d) {
        super(c, d);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null)
            vi = inflater.inflate(R.layout.list_dianpu, null);

        ImageView image = (ImageView) vi.findViewById(R.id.image);
        TextView name = (TextView) vi.findViewById(R.id.name);
        TextView miaoshu = (TextView) vi.findViewById(R.id.miaoshu);
        TextView youhui = (TextView) vi.findViewById(R.id.youhui);
        TextView position = (TextView) vi.findViewById(R.id.position);
        JSONObject jsonObject = data.get(i);
        try {
            imageLoader.displayImage(jsonObject.getString("pic"), image);
            name.setText(jsonObject.getString("name"));
            miaoshu.setText(jsonObject.getString("desc"));
            position.setText(jsonObject.getString("location"));
            if(jsonObject.has("coupondesc")) {
                String coupondesc = jsonObject.getString("coupondesc");
                youhui.setText(coupondesc.substring(coupondesc.indexOf(":")+1));
            }
            else
                youhui.setText(R.string.hint_wuyouhui);
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
