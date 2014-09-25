package com.taobao.daogou.client.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taobao.daogou.client.MyActivity;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;
import com.taobao.daogou.client.view.NoResultDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jian on 14-8-14.
 * 搜索页面
 */
public class DingweiFragment extends TitleFragment {

    private Button mSearch;
    private String mContent;
    private InputMethodManager mInputMethodManager;
    private Dialog mDialog;
    private AutoCompleteTextView mText;
    private String[] words;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        words = new String[] {"西溪印象城", "印象城"};
        mDialog = new NoResultDialog(getActivity(), R.style.MenuDialog, getString(R.string.hint_meiyoushangchang));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(viewGroup != null)
            return viewGroup;

        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_dingwei, null);
        mText = (AutoCompleteTextView) viewGroup.findViewById(R.id.text);
        mSearch = (Button) viewGroup.findViewById(R.id.search);
        mTitle = (TextView) viewGroup.findViewById(R.id.title);

        mText.setDropDownAnchor(R.id.main);
        mText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mContent = mText.getText().toString();
                getFragmentManager().popBackStack();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_search_hint, words);
        mText.setAdapter(adapter);
        mText.requestFocus();

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContent = mText.getText().toString();
                if(mContent.equalsIgnoreCase(words[0]) || mContent.equalsIgnoreCase(words[1]))
                    getFragmentManager().popBackStack();
                else
                    mDialog.show();
            }
        });


        mTitle.setOnClickListener(this);
        return viewGroup;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInputMethodManager.showSoftInput(mText, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void onDestroyView() {
        mInputMethodManager.hideSoftInputFromWindow(mText.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        super.onDestroyView();
    }
}
