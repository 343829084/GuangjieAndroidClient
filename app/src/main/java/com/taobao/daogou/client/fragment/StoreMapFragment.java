package com.taobao.daogou.client.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.taobao.daogou.client.R;

/**
 * Created by rd on 14-8-15.
 */
public class StoreMapFragment extends MenuFrament {
    private MapFragment mMapFragment;
    Handler handler = new Handler();
    LinearLayout mHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_map_store, null);
        mTitle = (TextView) viewGroup.findViewById(R.id.title);
        mMenu = (ImageView) viewGroup.findViewById(R.id.menu);

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
        return viewGroup;
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        mMapFragment = new MapFragment();
        mMapFragment.setArguments(getArguments());
        transaction.replace(R.id.fragment_map_store_frame, mMapFragment);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        final String location = getArguments().getString("location");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMapFragment.onWifiLocation(location);
            }
        }, 1200);
    }
}
