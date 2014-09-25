package com.taobao.daogou.client.fragment;

import android.content.BroadcastReceiver;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.taobao.daogou.client.R;
import com.taobao.daogou.client.adpater.ListForLoucengfenbuAdapter;
import com.taobao.daogou.client.fragment.util.FragmentRouter;
import com.taobao.daogou.client.fragment.util.WifiHelper;
import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jian on 14-8-11.
 */
public class LoucengfenbuFragment extends TitleFragment implements WifiHelper.onWifiLocation {

    private ListView mList;
    private ListForLoucengfenbuAdapter mAdapter;
    String mLocation = null;
    static String floorName[] = {
        "B1", "BM", "L1", "L2", "L3", "L4"
    };

    static {
        ArrayUtils.reverse(floorName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_loucengfenbu, null);

        mTitle = (TextView) viewGroup.findViewById(R.id.title);
        mList = (ListView) viewGroup.findViewById(R.id.list);

        JSONArray jsonArray = new JSONArray();
        try
        {
            for (String floor : floorName) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("floor", floor);
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<JSONObject> data = CommonHelper.getAdapterData(jsonArray);
        mAdapter = new ListForLoucengfenbuAdapter(getActivity(), data);
        mList.setAdapter(mAdapter);
        mTitle.setOnClickListener(this);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("file", mAdapter.getData(i).getString("floor"));
                    ((FragmentRouter) getActivity()).onFragmentChangeWithBundle(
                            Constants.FRAGMENT_ID_MAP, bundle);
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Unknown floor");
                }
            }
        });

        mLocation = WifiHelper.getInstance(getActivity()).getLocation(this);
        setLocation(mLocation);

        return viewGroup;
    }

    @Override
    public void onWifiLocation(String location) {
        if (getActivity() == null) return;
        Log.v("location", location == null ? "未知" : location);
        if (location == null) {
            Toast.makeText(getActivity(), "定位失败", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mLocation == null) {
            mLocation = location;
            Toast.makeText(getActivity(), String.format(
                            "基于Wifi定位于楼层 %s", mLocation.substring(0, 2)),
                    Toast.LENGTH_LONG
            ).show();
        }
        setLocation(location);
    }

    private void setLocation(String location) {
        if (location == null) return;
        String floor = location.substring(0, 2);
        int pos = ArrayUtils.indexOf(floorName, floor);
        mAdapter.setLocationPos(pos);
    }
}
