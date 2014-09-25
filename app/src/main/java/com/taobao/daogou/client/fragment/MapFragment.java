package com.taobao.daogou.client.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.fragment.util.MapHelper;
import com.taobao.daogou.client.fragment.util.MapTextStructure;
import com.taobao.daogou.client.fragment.util.MyMapFramelayout;
import com.taobao.daogou.client.fragment.util.MyMapImageView;
import com.taobao.daogou.client.fragment.util.RouteHelper;
import com.taobao.daogou.client.fragment.util.RoutePosStructure;
import com.taobao.daogou.client.fragment.util.WifiHelper;
import com.taobao.daogou.client.fragment.util.mapPoint;
import com.taobao.daogou.client.fragment.util.onDisplayMatrixChangeListener;
import com.taobao.daogou.client.fragment.util.onLogoLoaded;
import com.taobao.daogou.client.fragment.util.onUserSelectionListener;
import com.taobao.daogou.client.network.NetworkHelper;
import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;
import com.taobao.daogou.client.util.onOrientationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rd on 14-8-7.
 */
public class MapFragment extends TitleFragment implements
        Constants,
        onDisplayMatrixChangeListener,
        onLogoLoaded,
        onUserSelectionListener,
        WifiHelper.onWifiLocation,
        mapPoint,
        onOrientationListener {

    MyMapFramelayout mTouchLayout;
    MyMapImageView mImageView;
    String mFileName;
    Handler mHandler = new Handler();
    Request mRequest;
    TextView mLouceng;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_map, null);
        mTouchLayout = (MyMapFramelayout) viewGroup.findViewById(R.id.fragment_map_touch);
        mImageView = (MyMapImageView) viewGroup.findViewById(R.id.fragment_map_image);
        mLouceng = (TextView) viewGroup.findViewById(R.id.louceng);
        mLouceng.setText(getArguments().getString("file"));

        mImageView.init();
        mTouchLayout.init();
        mTouchLayout.addOnDisplayMatrixChangeListsner(mImageView);
        mTouchLayout.addOnDisplayMatrixChangeListsner(this);
        mTouchLayout.addOnDisplayMatrixCheckListener(mImageView);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(
                    getActivity().getAssets().open(getCurrentFileName()));
            mImageView.setBitmapWithScale(bitmap, mFileCurrentScale);
        } catch (IOException e) {
            e.printStackTrace();
        }

        NetworkHelper.getInstance(getActivity()).add(mRequest, this);
        return viewGroup;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        init();
    }

    private void init() {
        Bundle bundle = getArguments();
        mFileName = bundle.getString("file");

        mRequest = new JsonObjectRequest(
                getJsonURL(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (getActivity() == null) return;
                        CommonHelper
                                .getPreference(getActivity())
                                .edit()
                                .putString(Constants.PREFERENCE_MAP_JSON + mFileName, response.toString())
                                .commit();
                        initMapJson(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (getActivity() == null) return;
                        try {
                            JSONObject jsonObject = new JSONObject(
                                    CommonHelper.getPreference(getActivity())
                                            .getString(Constants.PREFERENCE_MAP_JSON + mFileName, null)
                            );
                            initMapJson(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    private void initMapJson(JSONObject jsonObject) {
        List<MapTextStructure> mTextList = CommonHelper.parseMapJson(jsonObject);
        mImageView.setTextList(mTextList);
        mImageView.onWifiLocation(mLocation);

        String icons[] = new String[mTextList.size()];
        for (int i = 0; i != mTextList.size(); ++i)
            icons[i] = mTextList.get(i).icon;
        MapHelper.getInstance(getActivity()).getLogos(icons, MapFragment.this, new Handler());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        NetworkHelper.getInstance(getActivity()).cancle(this);
    }

    private String getJsonURL() {
        return String.format("%sstatic/map/%s.tmp.json",
                Constants.SERVER_IP, mFileName);
    }

    /**
     * 根据文件名和缩放比例获取asset的文件名
     *
     * @return
     */
    private String getCurrentFileName() {
        return String.format("png/%s.svg_%d.png", mFileName, mFileCurrentScale * 90);
    }

    private int mFileCurrentScale = 1; //当前文件的缩放比例
    private boolean isLoadingMap = false; //是否正在加载图片

    @Override
    public void onDisplayMatrixChange(Matrix displayMatrix) {
        float scale = CommonHelper.getValues(displayMatrix, Matrix.MSCALE_X);
        if (scale > mFileCurrentScale * 2 && !isLoadingMap) {
            //需要重新加载图片
            isLoadingMap = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mFileCurrentScale *= 2;
                    final Bitmap bitmap;
                    try {
                        bitmap = BitmapFactory.decodeStream(
                                getActivity().getAssets().open(getCurrentFileName())
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                        isLoadingMap = false;
                        return;
                    }
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mImageView.setBitmapWithScale(bitmap, mFileCurrentScale);
//                            isLoadingMap = false;
//                        }
//                    });
                }
            }).start();
        }

        if (scale * 1.2 < mFileCurrentScale && !isLoadingMap && mFileCurrentScale != 1) {
            isLoadingMap = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mFileCurrentScale /= 2;
                    final Bitmap bitmap;
                    try {
                        bitmap = BitmapFactory.decodeStream(
                                getActivity().getAssets().open(getCurrentFileName())
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                        isLoadingMap = false;
                        return;
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mImageView.setBitmapWithScale(bitmap, mFileCurrentScale);
                            isLoadingMap = false;
                        }
                    });

                }
            }).start();
        }
    }

    @Override
    public void onLogoLoaded(Map<String, Bitmap> logos) {
        mImageView.setLogoMap(logos);
    }


    @Override
    public void onUserSelection(Map<String, Boolean> selection) {
        Log.v("selection", selection.toString());
        mImageView.onUserSelection(selection);
    }

    private String mLocation = null;
    private List<RoutePosStructure> mRouteList = null;

    @Override
    public void onWifiLocation(String location) {
        mLocation = location;
        if (mImageView != null)
            mImageView.onWifiLocation(location);
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageView.onWifiLocation(mLocation);
        mImageView.onOrientation(mOrientation);
    }

    @Override
    public void mapPoint(float x, float y, float[] dst) {
        if (mImageView != null)
            mImageView.mapPoint(x, y, dst);
    }

    float mOrientation = Float.NaN;
    @Override
    public void onOrientation(float value) {
        this.mOrientation = value;
        if (mImageView != null)
            mImageView.onOrientation(mOrientation);
    }
}
