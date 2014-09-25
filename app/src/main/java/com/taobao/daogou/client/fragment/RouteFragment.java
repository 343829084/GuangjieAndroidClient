package com.taobao.daogou.client.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.fragment.util.PathStructure;
import com.taobao.daogou.client.fragment.util.RouteCrossLevelStructure;
import com.taobao.daogou.client.fragment.util.RouteHelper;
import com.taobao.daogou.client.fragment.util.RouteLayout;
import com.taobao.daogou.client.fragment.util.RoutePosStructure;
import com.taobao.daogou.client.fragment.util.WifiHelper;
import com.taobao.daogou.client.network.NetworkHelper;
import com.taobao.daogou.client.util.Constants;

import org.json.JSONObject;

import java.util.List;

/**
 */
public class RouteFragment extends MenuFrament implements WifiHelper.onWifiLocation {
    String storeLocation;
    RouteLayout mRouteLayout;
    LinearLayout mHome;
    TextView mPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_route, null);
        mRouteLayout = (RouteLayout) viewGroup.findViewById(R.id.route_layout);
        mTitle = (TextView) viewGroup.findViewById(R.id.title);
        mMenu = (ImageView) viewGroup.findViewById(R.id.menu);
        mPath = (TextView) viewGroup.findViewById(R.id.path);

        View view = inflater.inflate(R.layout.popupwindow_menu, null);
        mHome = (LinearLayout) view.findViewById(R.id.home);
        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mPopupWindow.dismiss();

            }
        });
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setOutsideTouchable(true);

        mTitle.setOnClickListener(this);
        mMenu.setOnClickListener(this);

        storeLocation = getArguments().getString("location");
        WifiHelper.getInstance(getActivity()).getLocation(this);

        return viewGroup;
    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getActivity(), "正在定位与计算路线中，请稍候", Toast.LENGTH_LONG).show();
    }

    private List<RoutePosStructure> routeList;
    private Integer mLevels[];
    private PathStructure mPathStructure;

    @Override
    public void onWifiLocation(final String location) {
        if (getActivity() == null) return;
        Request request = new JsonObjectRequest(
                String.format("%sguangjie/locateapi/getRoute?from=%s&to=%s", Constants.SERVER_IP, location, storeLocation),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("route", response.toString());
                        if (getActivity() == null) return;
                        routeList = RouteHelper.getRouteList(response);
                        mLevels = RouteHelper.getDiffLevel(routeList);
                        for (int i = 0; i != mLevels.length; ++i) {
                            FrameLayout frameLayout = new FrameLayout(getActivity());
                            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            frameLayout.setLayoutParams(params);
                            frameLayout.setId(Constants.VIEW_ID_ROUTE_FRAME + i);
                            mRouteLayout.addView(frameLayout);
                        }

                        for (int i = 0; i != mLevels.length; ++i) {
                            MapFragment mapFragment = new MapFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("file", RouteHelper.levelToName(mLevels[i]));
                            mapFragment.setArguments(bundle);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(Constants.VIEW_ID_ROUTE_FRAME + i, mapFragment, RouteHelper.levelToName(mLevels[i]));
                            transaction.commit();
                        }

                        mRouteLayout.onRoute(routeList);

                        mPathStructure = RouteHelper.getPath(response);
                        Spanned text = Html.fromHtml(mPathStructure.html);
                        mPath.setText(text);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        NetworkHelper.getInstance(getActivity()).add(request);
    }


}
