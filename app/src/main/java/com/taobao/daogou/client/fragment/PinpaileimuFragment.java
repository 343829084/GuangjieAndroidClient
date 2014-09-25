package com.taobao.daogou.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taobao.daogou.client.MyActivity;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.adpater.GridForPinpaileimuAdapter;
import com.taobao.daogou.client.adpater.ListForPinpaileimuAdapter;
import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;
import com.taobao.daogou.client.view.DataLoader;
import com.taobao.daogou.client.view.GridViewWithPagerLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian on 14-8-14.
 * 品牌类目
 */
public class PinpaileimuFragment extends TitleFragment implements DataLoader {

    private ListView mList;
    private GridViewWithPagerLoader mGrid;
    private ListForPinpaileimuAdapter mAdapter;
    private GridForPinpaileimuAdapter mGridForPinpaileimuAdapter;
    private int mNum = 12, mPage = 0;
    private String mID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(viewGroup != null)
            return viewGroup;
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_pinpaileimu, null);
        mTitle = (TextView) viewGroup.findViewById(R.id.title);
        mList = (ListView) viewGroup.findViewById(R.id.list);
        mGrid = (GridViewWithPagerLoader) viewGroup.findViewById(R.id.grid);
        mList.setDivider(null);

        List<JSONObject> data = new ArrayList<JSONObject>();
        mAdapter = new ListForPinpaileimuAdapter(getActivity(), data);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long ItemId) {
                mAdapter.setActive(position);
                mAdapter.notifyDataSetChanged();
                loadInitData(position);
            }
        });


        mTitle.setOnClickListener(this);

        loadCategory();
        return viewGroup;
    }

    /*
    加载类目
     */
    private void loadCategory() {
        String url = Constants.SERVER_IP + "guangjie/shopapi/getBandCategory";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("categoryList");
                            List<JSONObject> d = CommonHelper.getAdapterData(jsonArray);
                            for(int i=0; i<d.size(); i++) {
                                mAdapter.addItem(d.get(i));
                            }
                            mAdapter.notifyDataSetChanged();
                            loadInitData(0);
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

    /*
    初始化当前类目的首页
     */
    public void loadInitData(int position) {
        mID = mAdapter.getTypeId(position);
        mNum = 12;
        mPage = 0;
        List<JSONObject> data = new ArrayList<JSONObject>();
        mGridForPinpaileimuAdapter = new GridForPinpaileimuAdapter(getActivity(), data);
        mGrid.setAdapter(mGridForPinpaileimuAdapter);
        mGrid.setDataLoader(this);

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long ItemId) {
                if (getActivity() == null) return;
                String shopId = mGridForPinpaileimuAdapter.getShopId(position);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.ARGUMENT_ID, shopId);
                ((MyActivity) getActivity()).onFragmentChangeWithBundle(Constants.FRAGMENT_ID_STORE, bundle);
            }
        });

        loadData();
    }


    /*
    加载当前类目的品牌
     */
    @Override
    public void loadData() {
        mPage++;
        String url = Constants.SERVER_IP + "/guangjie/shopapi/getShopsByCategory?categoryId=" + mID + "&num=" + mNum + "&page=" + mPage;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("shopList");
                            List<JSONObject> d = CommonHelper.getAdapterData(jsonArray);
                            for(int i=0; i<d.size(); i++) {
                                mGridForPinpaileimuAdapter.addItem(d.get(i));
                            }
                            mGridForPinpaileimuAdapter.notifyDataSetChanged();
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
        return mGridForPinpaileimuAdapter.getCount();
    }
}
