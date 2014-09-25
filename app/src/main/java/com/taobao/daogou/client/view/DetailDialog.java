package com.taobao.daogou.client.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.taobao.daogou.client.R;

/**
 * Created by jian on 14-8-16.
 * 显示商品图及信息
 */
public class DetailDialog extends Dialog {

    private ImageView mImage;
    private TextView mShop, mName;
    private ImageLoader mImageLoader;

    public DetailDialog(Context context, int theme) {
        super(context, theme);

        setContentView(R.layout.dialog_detail);
        mImage = (ImageView) findViewById(R.id.image);
        mName = (TextView) findViewById(R.id.name);
        mImageLoader = ImageLoader.getInstance();

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    public void show(String url, String name) {

        mImageLoader.displayImage(url, mImage);
        mName.setText(name);

        show();
    }
}
