package com.taobao.daogou.client.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taobao.daogou.client.R;
import com.taobao.daogou.client.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jian on 14-8-8.
 */
public class ListForSearchResultAdapter extends MyBaseAdapter {

    public ListForSearchResultAdapter(Context c, List<JSONObject> d) {
        super(c, d);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null)
            vi = inflater.inflate(R.layout.list_dianpu, null);

        ImageView image = (ImageView) vi.findViewById(R.id.image);
        TextView name = (TextView) vi.findViewById(R.id.name);
        TextView miaoshu = (TextView) vi.findViewById(R.id.miaoshu);
        TextView position = (TextView) vi.findViewById(R.id.position);
        TextView youhui = (TextView) vi.findViewById(R.id.youhui);
        JSONObject jsonObject = data.get(i);
        try {
            imageLoader.displayImage(Constants.SERVER_IP + "static/logo/" + jsonObject.getJSONObject("summaryHit").getJSONObject("hitSummarySchema").getString("shopPos")+".png", image);
            name.setText(jsonObject.getJSONObject("summaryHit").getJSONObject("hitSummarySchema").getString("shopName"));
            miaoshu.setText(jsonObject.getJSONObject("summaryHit").getJSONObject("hitSummarySchema").getString("shopDesc"));
            position.setText(jsonObject.getJSONObject("summaryHit").getJSONObject("hitSummarySchema").getString("shopPos"));
            String coupondesc = jsonObject.getJSONObject("summaryHit").getJSONObject("hitSummarySchema").getString("shopInfo");
            if(coupondesc != null && coupondesc.length() > 0) {
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
            String id = jsonObject.getJSONObject("summaryHit").getJSONObject("hitSummarySchema").getString("_id");
            return id;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
