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
import com.taobao.daogou.client.adpater.ListForSearchResultAdapter;
import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;
import com.taobao.daogou.client.view.DataLoader;
import com.taobao.daogou.client.view.ListViewWithPageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian on 14-8-9.
 * 展示搜索结果
 */
public class SearchResultFragment extends TitleFragment implements DataLoader {

    private ListViewWithPageLoader mList;
    private ListForSearchResultAdapter mAdapter;
    private int mHit = 12, mStart = 0;
    private String mKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        mKey = bundle.getString("key");

        if(viewGroup != null)
            return viewGroup;
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_searchresult, null);
        mList = (ListViewWithPageLoader) viewGroup.findViewById(R.id.list);
        mTitle = (TextView) viewGroup.findViewById(R.id.title);
        mList.setDivider(null);


        List<JSONObject> data = new ArrayList<JSONObject>();
        mAdapter = new ListForSearchResultAdapter(getActivity(), data);
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

        loadData();
        return viewGroup;
    }



    @Override
    public void loadData() {

        String url = "http://10.97.252.37:12345/";
        String n = "config=cluster:daogou,hit:"+ mHit+ ",start:" + mStart+ ",format:json&&query=\"" + mKey + "\" OR pos:\"" + mKey + "\" OR default:\"" + mKey + "\" OR tag:\"" + mKey + "\"";
    //    String n = "config=cluster:daogou,hit:1,start:0,format:json&&query=\"" + mContent + "\" OR pos:\"" + mContent + "\" OR default:\"" + mContent + "\" OR tag:\"" + mContent + "\"";

        try {
            n = URLEncoder.encode(n, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url+n,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("hits");
                            JSONArray jsonArray = jsonObject.getJSONArray("hits");
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
        mStart += mHit;
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }


}
