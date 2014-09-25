package com.taobao.daogou.client.fragment;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.taobao.daogou.client.R;
import com.taobao.daogou.client.fragment.BaseFragment;
import com.taobao.daogou.client.network.NetworkHelper;

/**
 * Created by jian on 14-8-11.
 * 带有标题栏的Fragment
 */
public class TitleFragment extends BaseFragment implements View.OnClickListener {

    protected TextView mTitle;
    protected NetworkHelper mNetworkHelper;

    public TitleFragment() {
        mNetworkHelper = NetworkHelper.getInstance(getActivity());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title:
                getFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }
}
