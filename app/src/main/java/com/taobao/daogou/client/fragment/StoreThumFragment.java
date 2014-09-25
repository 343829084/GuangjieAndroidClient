package com.taobao.daogou.client.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.fragment.util.MapHelper;
import com.taobao.daogou.client.fragment.util.MapTextStructure;
import com.taobao.daogou.client.network.NetworkHelper;
import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;

import org.apache.commons.lang3.concurrent.MultiBackgroundInitializer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by rd on 14-8-19.
 */
public class StoreThumFragment extends Fragment {
    private String mFileName, mPosition;
    private float posX, posY;
    private ImageView mImageView;
    private Bitmap mBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_store_thum, null);
        mImageView = (ImageView) viewGroup.findViewById(R.id.image);

        mFileName = getArguments().getString("floor");
        mPosition = getArguments().getString("position");

        try {
            mBitmap = BitmapFactory.decodeStream(
                    getActivity().getAssets().open("png/" + mFileName + ".svg_72.png"));

            /*
            Bitmap iconBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.iconfont_ico09);
            Bitmap newBitmap=Bitmap.createBitmap(mBitmap.getWidth(),mBitmap.getHeight(),Bitmap.Config.ARGB_8888);
            Canvas cv=new Canvas(newBitmap);
            cv.drawBitmap(mBitmap,0,0,null);
            Matrix matrix = new Matrix();

            matrix.setScale(0.2f,0.2f);
            matrix.postTranslate((float)(this.posX * 0.2), (float)(this.posY * 0.2));
            Paint mPaint = new Paint();
            cv.drawBitmap(iconBitmap,  matrix, mPaint);
            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();
            */
            mImageView.setImageBitmap(mBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Request mRequest = new JsonObjectRequest(
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

      //  NetworkHelper.getInstance(getActivity()).add(mRequest);

        return viewGroup;
    }

    private void initMapJson(JSONObject jsonObject) {
        List<MapTextStructure> mTextList = CommonHelper.parseMapJson(jsonObject);
        for (MapTextStructure structure : mTextList) {
            if (structure.icon.toLowerCase().startsWith(mPosition.toLowerCase())) {
                //这个店的位置
                this.posX = structure.X;
                this.posY = structure.Y;
                Log.v("pos", String.format("X:%f\tY:%f", posX, posY));


                /*
                Matrix matrix = new Matrix();
                float baseScale = Math.min(
                        (float) mImageView.getWidth() / mBitmap.getWidth(),
                        (float) mImageView.getHeight() / mBitmap.getHeight());
                matrix.setScale(baseScale, baseScale);

                Matrix matrix2 = new Matrix();
                matrix2.setTranslate(
                        -(posX - mImageView.getWidth() / 2),
                        -(posY - mImageView.getHeight() / 2)
                );
                matrix.postConcat(matrix2);
//                matrix.postScale(3, 3,
//                        mImageView.getWidth() / 2,
//                        mImageView.getHeight() / 2);
                CommonHelper.logMatrix("matrix", matrix);

//                matrix.setTranslate(
//                        this.posX,
//                        this.posY);
//                matrix.postScale(2, 2,
//                        this.posX,
//                        this.posY);
                */





                /*
                Matrix matrixMark = new Matrix();
                matrixMark.setTranslate(viewWidth * relX - 35 / 2, viewHeight * relY - 44 / 2);
                mMark.setImageMatrix(matrixMark);
                */
                /*
                Bitmap iconBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.iconfont_ico09);
                Bitmap newBitmap=Bitmap.createBitmap(mBitmap.getWidth(),mBitmap.getHeight(),Bitmap.Config.ARGB_8888);
                Canvas cv=new Canvas(newBitmap);
                cv.drawBitmap(mBitmap,0,0,null);
                cv.drawBitmap(iconBitmap,this.posX,this.posY,null);
                cv.save(Canvas.ALL_SAVE_FLAG);
                cv.restore();
                */

               // mBitmap = newBitmap;
                /*

                int bitmapWidth = mBitmap.getWidth();
                int bitmapHeight = mBitmap.getHeight();
                int viewWidth = mImageView.getWidth();
                int viewHeight = mImageView.getHeight();

                int descWidth = bitmapWidth / 4;
                int descHeight = bitmapHeight / 4;
                float left = (this.posX - descWidth) > 0 ? (this.posX - descWidth) : 0;
                float top = (this.posY - descHeight) > 0 ? (this.posY - descHeight) : 0;
                float right = (this.posX + descWidth) < bitmapWidth ? (this.posX + descWidth) : bitmapWidth;
                float down = (this.posY + descHeight) < bitmapHeight ? (this.posY + descHeight) : bitmapHeight;
                float relX = (this.posX - left) / (right - left);
                float relY = (this.posY - top) / (down - top);

                RectF small = new RectF((int) left, (int )top, (int) right, (int) down);
                RectF big = new RectF(0, 0, viewWidth, viewHeight);


                Matrix matrix = new Matrix();
                matrix.setRectToRect(small, big, Matrix.ScaleToFit.CENTER);
                mImageView.setScaleType(ImageView.ScaleType.MATRIX);
                mImageView.setImageMatrix(matrix);


                */
            }
        }
    }

    private String getJsonURL() {
        return String.format("%sstatic/map/%s.tmp.json",
                Constants.SERVER_IP, mFileName);
    }
}
