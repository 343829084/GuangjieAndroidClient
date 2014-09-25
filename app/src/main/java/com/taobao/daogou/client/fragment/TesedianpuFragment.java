package com.taobao.daogou.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taobao.daogou.client.MyActivity;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.adpater.ListForDianpuAdapter;
import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;
import com.taobao.daogou.client.view.DataLoader;
import com.taobao.daogou.client.view.ListViewWithPageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian on 14-8-9.
 * 特色店铺
 */
public class TesedianpuFragment extends TitleFragment implements DataLoader {

    private ListViewWithPageLoader mList;
    private ListForDianpuAdapter mAdapter;
    private int mNum = 12, mPage = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(viewGroup != null)
            return viewGroup;
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_tesedianpu, null);
        mList = (ListViewWithPageLoader) viewGroup.findViewById(R.id.list);
        mTitle = (TextView) viewGroup.findViewById(R.id.title);
        mList.setDivider(null);


        List<JSONObject> data = new ArrayList<JSONObject>();
        mAdapter = new ListForDianpuAdapter(getActivity(), data);
        mList.setAdapter(mAdapter);
        mList.setDataLoader(this);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long ItemId) {
                String shopId = mAdapter.getShopId(position);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.ARGUMENT_ID, shopId);
                ((MyActivity) getActivity()).onFragmentChangeWithBundle(Constants.FRAGMENT_ID_STORE, bundle);
            }
        });

        mTitle.setOnClickListener(this);
        mPage = 0;
        mNum = 12;
        loadData();

        return viewGroup;
    }

    /*
    加载特色店铺
     */
    @Override
    public void loadData() {

        mPage++;
        String url = Constants.SERVER_IP + "guangjie/shopapi/getSpecialShopList?num=" + mNum + "&page=" + mPage;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("couponList");
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
