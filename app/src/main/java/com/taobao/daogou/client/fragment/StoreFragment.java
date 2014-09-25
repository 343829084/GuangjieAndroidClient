package com.taobao.daogou.client.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.adpater.GridForDianpuremaiAdapter;
import com.taobao.daogou.client.fragment.util.FragmentRouter;
import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;
import com.taobao.daogou.client.view.DataLoader;
import com.taobao.daogou.client.view.DetailDialog;
import com.taobao.daogou.client.view.GridViewForScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian on 14-8-9.
 * 店铺详情
 */
public class StoreFragment extends MenuFrament implements DataLoader {

    private ImageView mLogo;
    private FrameLayout mFloor;
    private TextView mName, mPosition, mYouhui, mMiaoshu, mJieshao;
    private Button mGo;
    private String mID;
    private int mNum = 12, mPage = 0;
    private GridForDianpuremaiAdapter mAdapter;
    private GridViewForScrollView mGrid;
    private ImageLoader mImageLoader;
    private LinearLayout mHome;
    private TextView mLouceng;
    private DetailDialog dialog;
    private ScrollView mMain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new DetailDialog(getActivity(), R.style.ImageDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        mID = bundle.getString(Constants.ARGUMENT_ID);
        mImageLoader = ImageLoader.getInstance();

        if (viewGroup != null)
            return viewGroup;
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_store, null);
        mLogo = (ImageView) viewGroup.findViewById(R.id.logo);
        mName = (TextView) viewGroup.findViewById(R.id.name);
        mMiaoshu = (TextView) viewGroup.findViewById(R.id.miaoshu);
        mYouhui = (TextView) viewGroup.findViewById(R.id.youhui);
        mPosition = (TextView) viewGroup.findViewById(R.id.position);
        mGo = (Button) viewGroup.findViewById(R.id.go);
        mTitle = (TextView) viewGroup.findViewById(R.id.title);
        mFloor = (FrameLayout) viewGroup.findViewById(R.id.floor);
        mMenu = (ImageView) viewGroup.findViewById(R.id.menu);
        mLouceng = (TextView) viewGroup.findViewById(R.id.louceng);
        mGrid = (GridViewForScrollView) viewGroup.findViewById(R.id.grid);
        mMain = (ScrollView) viewGroup.findViewById(R.id.main);
        mMain.scrollTo(0, 0);
        mJieshao = (TextView) viewGroup.findViewById(R.id.jieshao);

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


        mMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mPopupWindow != null && mPopupWindow.isShowing())
                    mPopupWindow.dismiss();
                return false;
            }
        });


        mTitle.setOnClickListener(this);
        mMenu.setOnClickListener(this);

        mPage = 0;
        mNum = 12;
        loadBasicInfo();
        loadDianpuremai();
        return viewGroup;
    }

    /*
    加载店铺基本信息
     */
    private void loadBasicInfo() {
        String url = Constants.SERVER_IP + "guangjie/shopapi/getShopInfo?shopid=" + mID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (getActivity() == null) return;
                        try {
                            JSONObject jsonObject = response.getJSONObject("data");
                            mImageLoader.displayImage(jsonObject.getString("pic"), mLogo);
                            final String floor = jsonObject.getString("location").substring(0, 2);

                            StoreThumFragment fragment = new StoreThumFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("floor", floor);
                            bundle.putString("position", jsonObject.getString("location"));
                            fragment.setArguments(bundle);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(mFloor.getId(), fragment);
                            transaction.commit();
                            mLouceng.setText(floor);

                            mName.setText(jsonObject.getString("name"));
                            mPosition.setText("( " + jsonObject.getString("location") + " )");
                            mJieshao.setText(jsonObject.getString("desc"));

                            if (jsonObject.has("coupondesc")) {
                                String youhui = jsonObject.getString("coupondesc");
                                mYouhui.setText(youhui.substring(youhui.indexOf(":") + 1));
                                mMiaoshu.setText(jsonObject.getString("startTime") + "至" + jsonObject.getString("endTime"));
                            } else {
                                mYouhui.setText(R.string.hint_wuyouhui);
                                mMiaoshu.setText(R.string.hint_wuyouhui);
                            }

                            final String location = jsonObject.getString("location");
                            mFloor.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (mPopupWindow != null && mPopupWindow.isShowing())
                                        mPopupWindow.dismiss();
                                    Bundle intentBundle = new Bundle();
                                    intentBundle.putString("file", floor);
                                    intentBundle.putString("location", location);
                                    ((FragmentRouter) getActivity()).onFragmentChangeWithBundle(
                                            Constants.FRAGMENT_ID_STROE_MAP,
                                            intentBundle
                                    );
                                }
                            });

                            mGo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Bundle intentBundle = new Bundle();
                                    intentBundle.putString("file", floor);
                                    intentBundle.putString("location", location);
                                    ((FragmentRouter) getActivity()).onFragmentChangeWithBundle(
                                            Constants.FRAGMENT_ID_ROUTE,
                                            intentBundle
                                    );
                                }
                            });


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
    加载店铺热卖
     */
    private void loadDianpuremai() {
        List<JSONObject> data = new ArrayList<JSONObject>();
        mAdapter = new GridForDianpuremaiAdapter(getActivity(), data);
        mGrid.setAdapter(mAdapter);

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mPopupWindow != null && mPopupWindow.isShowing())
                    mPopupWindow.dismiss();

                if (getActivity() == null) return;

                // ImageView image = new ImageView(getActivity());
                // mImageLoader.displayImage(mAdapter.getBigPic(i), image);
                /*
                final Dialog dialog = new Dialog(getActivity(), R.style.ImageDialog);
                dialog.setContentView(image);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                */

                dialog.show(mAdapter.getBigPic(i), mAdapter.getItemName(i));
            }
        });

        loadData();
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

    @Override
    public void loadData() {
        mPage++;
        String url = Constants.SERVER_IP + "guangjie/shopapi/getPopularItemsByShopId?shopId=" + mID + "&num=" + mNum + "&page=" + mPage;
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
                            for (int i = 0; i < d.size(); i++) {
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

        mNetworkHelper.add(jsonObjectRequest, null);
    }
}
