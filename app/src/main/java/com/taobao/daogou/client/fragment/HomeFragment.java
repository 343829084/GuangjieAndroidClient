package com.taobao.daogou.client.fragment;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taobao.daogou.client.MyActivity;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.adpater.ListForPinpaihuodongAdapter;
import com.taobao.daogou.client.network.NetworkHelper;
import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;
import com.taobao.daogou.client.view.DataLoader;
import com.taobao.daogou.client.view.ListViewForScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by jian on 14-8-7.
 * 首页
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, DataLoader, AMapLocationListener {

    private FrameLayout mTesedianpu, mRemenshangpin, mCanyinyule, mLoucengbuju, mPinpaileimu, mQitafuwu;
    private ListViewForScrollView mList;
    private ListForPinpaihuodongAdapter mAdapter;
    private NetworkHelper mNetworkHelper;
    private int mPage=0, mNum=12;
    private LinearLayout mSearch;
    private LocationManagerProxy mLocationManagerProxy;
    private ScrollView mMain;
    private TextView mDingwei, mShangshang;
    private boolean mState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mState = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mNetworkHelper = NetworkHelper.getInstance(getActivity());

        if(viewGroup != null)
            return viewGroup;
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, null);
        mTesedianpu = (FrameLayout) viewGroup.findViewById(R.id.tesedianpu);
        mRemenshangpin = (FrameLayout) viewGroup.findViewById(R.id.remenshangpin);
        mCanyinyule = (FrameLayout) viewGroup.findViewById(R.id.yulexiuxian);
        mLoucengbuju = (FrameLayout) viewGroup.findViewById(R.id.loucengfenbu);
        mPinpaileimu = (FrameLayout) viewGroup.findViewById(R.id.pinpaileimu);
        mQitafuwu = (FrameLayout) viewGroup.findViewById(R.id.qitafuwu);
        mSearch = (LinearLayout) viewGroup.findViewById(R.id.search);
        mList = (ListViewForScrollView) viewGroup.findViewById(R.id.list);
        mMain = (ScrollView) viewGroup.findViewById(R.id.main);
        mDingwei = (TextView) viewGroup.findViewById(R.id.dingwei);
        mShangshang = (TextView) viewGroup.findViewById(R.id.shangchang);
        mList.setDivider(null);

        List<JSONObject> data = new ArrayList<JSONObject>();
        mAdapter = new ListForPinpaihuodongAdapter(getActivity(), data);
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

        mRemenshangpin.setOnClickListener(this);
        mTesedianpu.setOnClickListener(this);
        mCanyinyule.setOnClickListener(this);
        mLoucengbuju.setOnClickListener(this);
        mPinpaileimu.setOnClickListener(this);
        mQitafuwu.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mShangshang.setOnClickListener(this);

        mPage = 0;
        mNum = 12;
        loadData();


        /*
        if(mState == true) {
            mState = false;
            Thread thread = new Thread(runnable);
            thread.start();
        }
        else {
            mDingwei.setVisibility(View.INVISIBLE);
            mMain.setVisibility(View.VISIBLE);
            mShangshang.setVisibility(View.VISIBLE);
        }
        */

        mDingwei.setVisibility(View.INVISIBLE);
        mMain.setVisibility(View.VISIBLE);
        mShangshang.setVisibility(View.VISIBLE);

        return viewGroup;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tesedianpu:
                ((MyActivity) getActivity()).onFragmentChange(Constants.FRAGMENT_ID_TESEDIANPU);
                break;
            case R.id.remenshangpin:
                ((MyActivity) getActivity()).onFragmentChange(Constants.FRAGMENT_ID_REMENSHANGPIN);
                break;
            case R.id.yulexiuxian:
                ((MyActivity) getActivity()).onFragmentChange(Constants.FRAGMENT_ID_CANYINYULE);
                break;
            case R.id.loucengfenbu:
                ((MyActivity) getActivity()).onFragmentChange(Constants.FRAGMENT_ID_LOUCENGFENBU);
                break;
            case R.id.pinpaileimu:
                ((MyActivity) getActivity()).onFragmentChange(Constants.FRAGMENT_ID_PINPAILEIMU);
                break;
            case R.id.qitafuwu:
                ((MyActivity) getActivity()).onFragmentChange(Constants.FRAGMENT_ID_SERVICE);
                break;
            case R.id.search:
                ((MyActivity) getActivity()).onFragmentChange(Constants.FRAGMENT_ID_SEARCH);
                break;
            case R.id.shangchang:
                ((MyActivity) getActivity()).onFragmentChange(Constants.FRAGMENT_ID_DINGWEI);
                break;
            default:
                break;
        }
    }

    /*
    加载品牌活动
     */
    @Override
    public void loadData() {

        mPage++;
        String url = Constants.SERVER_IP + "guangjie/couponapi/getCouponList?num=" + mNum + "&page=" + mPage;

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

    private void getMyLocation() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 0, this);
        mLocationManagerProxy.setGpsEnable(false);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation != null && aMapLocation.getAMapException().getErrorCode() ==0) {
            String province = aMapLocation.getProvince();
            String city = aMapLocation.getCity();
            String address = aMapLocation.getAddress();

            //String city = aMapLocation.getCity();
            Bundle bundle = aMapLocation.getExtras();
            String desc = bundle.getString("desc");
            Toast.makeText(getActivity(), desc, Toast.LENGTH_LONG).show();
        }

        if(mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destroy();
        }
        mLocationManagerProxy = null;


    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(Constants.DINGWEI_TIME);
                        mDingwei.setVisibility(View.INVISIBLE);
                        mMain.setVisibility(View.VISIBLE);
                        mShangshang.setVisibility(View.VISIBLE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /*
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(mState == true) {
            mState = false;
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
    */



}

