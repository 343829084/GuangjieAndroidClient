package com.taobao.daogou.client.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.taobao.daogou.client.MyActivity;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.util.Constants;

/**
 * 主 Fragment
 */
public class MainFragment extends BaseFragment implements View.OnClickListener, Constants {

    Button mVoiceButton, mMapButton, mRecommendButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_main, null);

        mVoiceButton = (Button) viewGroup.findViewById(R.id.voice_button);
        mMapButton = (Button) viewGroup.findViewById(R.id.map_button);
        mRecommendButton = (Button) viewGroup.findViewById(R.id.recommend_button);

        mVoiceButton.setOnClickListener(this);
        mMapButton.setOnClickListener(this);
        mRecommendButton.setOnClickListener(this);

        return viewGroup;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.voice_button:
                ((MyActivity) getActivity()).onFragmentChange(Constants.FRAGMENT_ID_VOICE);
                break;
            case R.id.map_button:
                ((MyActivity) getActivity()).onFragmentChange(Constants.FRAGMENT_ID_MAP);
                break;
            case R.id.recommend_button:
                ((MyActivity) getActivity()).onFragmentChange(Constants.FRAGMENT_ID_HOME);
                break;
        }
    }
}
