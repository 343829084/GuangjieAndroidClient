package com.taobao.daogou.client.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.util.CommonHelper;

/**
 * Created by rd on 14-8-7.
 */
public class VoiceRecogFragment extends BaseFragment {
    TextView mTextview;
    Button mButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_voice_recog, null);

        mTextview = (TextView) viewGroup.findViewById(R.id.text);
        mButton = (Button) viewGroup.findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            boolean isRecoding = false;
            SpeechRecognizer speechRecognizer = null;
            @Override
            public void onClick(View view) {
                if (!isRecoding) {
                    isRecoding = true;
                    SpeechUtility.createUtility(getActivity(), SpeechConstant.APPID + "=53e1a147");
                    speechRecognizer = SpeechRecognizer.createRecognizer(getActivity(), null);
                    speechRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
                    speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_ch");
                    speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
                    int code = speechRecognizer.startListening(mRecoListener);
                    Log.v("voiceRecogCode", String.valueOf(code));
                } else {
                    isRecoding = false;
                    speechRecognizer.stopListening();
                }
            }
        });
        return viewGroup;
    }

    private RecognizerListener mRecoListener = new RecognizerListener() {
        // 识别结果回调接口(返回Json格式结果,用户可参见附录);
        // 一般情况下会通过onResults接口多次返回结果,完整的识别内容是多次结果的累加;
        //关于解析Json的代码可参见MscDemo中JsonParser类;
        //isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            results.getResultString();
            String result = CommonHelper.getRecogResult(results);
            mTextview.setText(result);
        }

        // 会话发生错误回调接口
        public void onError(SpeechError error) {
            Log.v("error", error.toString());
        }

        //开始录音
        public void onBeginOfSpeech() {
            Log.v("begin", "begin");
        }

        //音量值0~30
        public void onVolumeChanged(int volume) {
        }

        //结束录音
        public void onEndOfSpeech() {
        }

        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, String msg) {
        }
    };

}
