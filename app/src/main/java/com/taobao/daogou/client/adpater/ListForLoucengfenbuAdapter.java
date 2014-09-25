package com.taobao.daogou.client.adpater;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.fragment.util.MyFilterDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jian on 14-8-11.
 */
public class ListForLoucengfenbuAdapter extends MyBaseAdapter {
    private int mPosition = -1;

    public ListForLoucengfenbuAdapter(Context c, List<JSONObject> d) {
        super(c, d);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View vi = null;
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            vi = inflater.inflate(R.layout.list_loucengfenbu, null);
            viewHolder.imageView = (ImageView) vi.findViewById(R.id.image);
            viewHolder.textView = (TextView) vi.findViewById(R.id.floor_text);
            vi.setTag(viewHolder);
        } else {
            vi = view;
            viewHolder = (ViewHolder) vi.getTag();
        }

        try {
            imageLoader.displayImage(
                    getFileName(data.get(i).getString("floor")),
                    viewHolder.imageView);
            viewHolder.textView.setText(data.get(i).getString("floor"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (i == mPosition)
            vi.setActivated(true);
        else
            vi.setActivated(false);

        return vi;
    }

    public void setLocationPos(int pos) {
        this.mPosition = pos;
        notifyDataSetChanged();
    }

    private String getFileName(String file) {
        return "assets://png/" + file + ".svg_36.png";
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
