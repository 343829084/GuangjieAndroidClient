package com.taobao.daogou.client.fragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.taobao.daogou.client.R;
import com.taobao.daogou.client.fragment.util.FragmentRouter;
import com.taobao.daogou.client.fragment.util.WifiHelper;
import com.taobao.daogou.client.util.Constants;
import com.taobao.daogou.client.util.OrientationHelper;
import com.taobao.daogou.client.util.onOrientationListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rd on 14-8-14.
 */
public class MapDetailFragment extends MenuFrament
        implements View.OnClickListener, WifiHelper.onWifiLocation, onOrientationListener {
    Button mButton[] = new Button[8];
    ImageView mGetLoctionView;
    Handler handler = new Handler();
    LinearLayout mHome;
    boolean isLocating = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_map_detail, null);
        mTitle = (TextView) viewGroup.findViewById(R.id.title);
        mGetLoctionView = (ImageView) viewGroup.findViewById(R.id.fragment_map_detail_get_location);
        mMenu = (ImageView) viewGroup.findViewById(R.id.menu);

        LinearLayout linearLayout = (LinearLayout) viewGroup.findViewById(R.id.map_detail_button_group1);
        for (int i = 0; i != linearLayout.getChildCount(); ++i)
            mButton[i] = (Button) linearLayout.getChildAt(i);

        linearLayout = (LinearLayout) viewGroup.findViewById(R.id.map_detail_button_group2);
        for (int i = 0; i != linearLayout.getChildCount(); ++i)
            mButton[i + 4] = (Button) linearLayout.getChildAt(i);
        for (Button button : mButton)
            button.setOnClickListener(onSelectionButtonClickListener);

        mTitle.setOnClickListener(this);
        mGetLoctionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLocating) {
                    isLocating = true;
                    WifiHelper.getInstance(getActivity()).getLocation(MapDetailFragment.this);
                }
            }
        });

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

        mMenu.setOnClickListener(this);

        return viewGroup;
    }

    MapFragment mMapFragment;
    @Override
    public void onStart() {
        super.onStart();
        FragmentManager manager = getFragmentManager();
        mMapFragment = new MapFragment();
        mMapFragment.setArguments(getArguments());
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.map_detail_map, mMapFragment);
        transaction.commit();

        OrientationHelper.getInstance(getActivity()).addListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        OrientationHelper.getInstance(getActivity()).removeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WifiHelper.getInstance(getActivity())
                        .getLocation(mMapFragment);
            }
        }, 800);
    }

    private Map<String, Boolean> userSelectionMap = new HashMap<String, Boolean>();
    private View.OnClickListener onSelectionButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String name = ((Button) view).getText().toString();
            view.setActivated(!view.isActivated());
            if (userSelectionMap.get(name) == null) {
                userSelectionMap.put(name, true);
            } else {
                userSelectionMap.put(name, !userSelectionMap.get(name));
            }
            mMapFragment.onUserSelection(userSelectionMap);
        }
    };

    @Override
    public void onWifiLocation(final String location) {
        isLocating = false;
        if (location.toLowerCase().startsWith(getArguments().getString("file").toLowerCase())) {
            mMapFragment.onWifiLocation(location);
        } else {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("是否切换楼层")
                    .setMessage(String.format("系统定位到 %s 楼层", location.substring(0, 2)))
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Bundle bundle = new Bundle();
                            bundle.putString("file", location.substring(0, 2));
                            FragmentManager manager = getFragmentManager();
                            manager.popBackStack();
                            ((FragmentRouter) getActivity())
                                    .onFragmentChangeWithBundle(Constants.FRAGMENT_ID_MAP, bundle);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    @Override
    public void onOrientation(float value) {
        mMapFragment.onOrientation(value);
    }
}
