package com.taobao.daogou.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.taobao.daogou.client.R;

/**
 * Created by jian on 14-8-15.
 * 更多服务
 */
public class GengduofuwuFragment extends TitleFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(viewGroup != null)
            return viewGroup;
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_gengduofuwu, null);

        mTitle = (TextView) viewGroup.findViewById(R.id.title);
        mTitle.setOnClickListener(this);

        return viewGroup;
    }
}
