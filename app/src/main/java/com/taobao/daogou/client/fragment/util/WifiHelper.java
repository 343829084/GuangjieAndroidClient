package com.taobao.daogou.client.fragment.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.taobao.daogou.client.network.NetworkHelper;
import com.taobao.daogou.client.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rd on 14-8-14.
 */
public class WifiHelper {
    public static WifiHelper mInstance;

    public static synchronized WifiHelper getInstance(Context context) {
        if (mInstance == null)
            mInstance = new WifiHelper(context);
        return mInstance;
    }

    private Context mContext;
    private WifiManager manager;
    private String mLocation = null;


    private WifiHelper(Context context) {
        this.mContext = context.getApplicationContext();
        this.manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    public String getLocation(final onWifiLocation onWifiLocation) {
        if (!manager.isWifiEnabled()) {
//            Toast.makeText(mContext, "正在启动Wifi网络定位", Toast.LENGTH_SHORT).show();
            manager.setWifiEnabled(true);
        }
        manager.startScan();
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mContext.unregisterReceiver(this);
                List<ScanResult> results = manager.getScanResults();
                getLocation(results, onWifiLocation);
            }
        };

        mContext.registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        return mLocation;
    }

    private void getLocation(List<ScanResult> mList,
                             final onWifiLocation onWifiLocation) {
        StringBuilder sb = new StringBuilder();
        for (ScanResult result : mList) {
            sb.append(result.BSSID).append("|").append(result.level).append("|");
        }
        final String list = sb.toString().substring(0, sb.length() - 1);

        final Request request = new StringRequest(
                Request.Method.POST,
                String.format("%sguangjie/locateapi/locate", Constants.SERVER_IP),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String string) {
                        try {
                            JSONObject jsonObject = new JSONObject(string);
                            String res = jsonObject.getString("data");
                            if (!res.equals("null")) {
                                mLocation = res;
                                onWifiLocation.onWifiLocation(res);
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mLocation = "L123";
                        onWifiLocation.onWifiLocation("L123");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onWifiLocation.onWifiLocation(null);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("wifiList", list);
                return map;
            }
        };

        NetworkHelper.getInstance(mContext).add(request);
    }

    public static interface onWifiLocation {
        public void onWifiLocation(String location);
    }
}
