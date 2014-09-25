package com.taobao.daogou.client.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taobao.daogou.client.MyActivity;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.adpater.GridForRemenshangpinAdapter;
import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;
import com.taobao.daogou.client.view.DataLoader;
import com.taobao.daogou.client.view.DetailDialog;
import com.taobao.daogou.client.view.GridViewWithPagerLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian on 14-8-9.
 * 热卖商品
 */
public class RemenshangpinFragment extends TitleFragment implements DataLoader {

    private GridViewWithPagerLoader mGrid;
    private GridForRemenshangpinAdapter mAdapter;
    private int mNnum = 12, mPage = 0;
    private com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
    private DetailDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(viewGroup != null)
            return viewGroup;
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_remenshangpin, null);
        mGrid = (GridViewWithPagerLoader) viewGroup.findViewById(R.id.grid);
        mTitle = (TextView) viewGroup.findViewById(R.id.title);

        List<JSONObject> data = new ArrayList<JSONObject>();
        mAdapter = new GridForRemenshangpinAdapter(getActivity(), data);
        mGrid.setAdapter(mAdapter);
        mGrid.setDataLoader(this);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                dialog.show(mAdapter.getBigPic(i), mAdapter.getItemName(i));
            }
        });

        dialog = new DetailDialog(getActivity(), R.style.ImageDialog);

        mTitle.setOnClickListener(this);

        mPage = 0;
        mNnum = 12;

        loadData();
        return viewGroup;
    }

    /*
    加载热卖商品
     */
    @Override
    public void loadData() {
        mPage++;
        String url = Constants.SERVER_IP + "guangjie/shopapi/getPopularItems?num=" + mNnum + "&page=" + mPage;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("itemList");
                            List<JSONObject> d = CommonHelper.getAdapterData(jsonArray);
                            for(int i=0; i<d.size(); i++) {
                                mAdapter.addItem(d.get(i));
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ;
                    }
                }
        );
        mNetworkHelper.add(jsonObjectRequest, getActivity());
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

}
