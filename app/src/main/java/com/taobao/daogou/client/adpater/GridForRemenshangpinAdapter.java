package com.taobao.daogou.client.adpater;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.deque.LIFOLinkedBlockingDeque;
import com.taobao.daogou.client.MyActivity;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.util.Constants;
import com.taobao.daogou.client.util.InflaterHelper;
import com.taobao.daogou.client.view.DataLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.zip.Inflater;


/**
 * Created by jian on 14-8-9.
 */
public class GridForRemenshangpinAdapter extends MyBaseAdapter {


    public GridForRemenshangpinAdapter(Context c, List<JSONObject> d)
    {
        super(c, d);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null)
            vi = inflater.inflate(R.layout.grid_remenshangpin, null);

        ImageView image = (ImageView) vi.findViewById(R.id.image);
        TextView shop = (TextView) vi.findViewById(R.id.shop);
        TextView name = (TextView) vi.findViewById(R.id.name);
        TextView price = (TextView) vi.findViewById(R.id.price);
        LinearLayout click = (LinearLayout) vi.findViewById(R.id.click);
        final JSONObject jsonObject = data.get(i);
        try {
            imageLoader.displayImage(jsonObject.getString("itemPic"), image);
            shop.setText(jsonObject.getString("shopName"));
            name.setText(jsonObject.getString("itemName"));
            price.setText(jsonObject.getString("itemPrice"));
            final String shopId = jsonObject.getString("shopid");
            click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.ARGUMENT_ID, shopId);
                    ((MyActivity) context).onFragmentChangeWithBundle(Constants.FRAGMENT_ID_STORE, bundle);
                }
            });
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
