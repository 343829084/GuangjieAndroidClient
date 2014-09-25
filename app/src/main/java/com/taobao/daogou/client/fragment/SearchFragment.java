package com.taobao.daogou.client.fragment;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taobao.daogou.client.MyActivity;
import com.taobao.daogou.client.R;
import com.taobao.daogou.client.fragment.util.RouteHelper;
import com.taobao.daogou.client.network.NetworkHelper;
import com.taobao.daogou.client.util.CommonHelper;
import com.taobao.daogou.client.util.Constants;
import com.taobao.daogou.client.view.NoResultDialog;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jian on 14-8-14.
 * 搜索页面
 */
public class SearchFragment extends TitleFragment {

    private Button mSearch;
    private String mContent;
    private InputMethodManager mInputMethodManager;
    private Dialog mDialog;
    private AutoCompleteTextView mText;
    private boolean mState;
    private String[] words;
    private int i,j;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mDialog = new NoResultDialog(getActivity(), R.style.MenuDialog, getString(R.string.hint_meiyousousuojieguo));
        mState = true;

        final SharedPreferences settings = getActivity().getSharedPreferences(Constants.CONFIGURATION_FILE, Context.MODE_PRIVATE);

        Set<String> set = settings.getStringSet(Constants.CONFIGURATION_KEYWORD, null);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());
        final String str = formatter.format(curDate);
        String timestamp = settings.getString(Constants.CONFIGURATION_KEYWORD_TIMESTAMP, null);
        if(timestamp == null || (! timestamp.equalsIgnoreCase(str))) {
            words = new String[0];
            String url = Constants.SERVER_IP + "guangjie/shopapi/getAllShopNames";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("data");
                                List<JSONObject> d = CommonHelper.getAdapterData(jsonArray);
                                Set<String> s = new HashSet<String>();
                                for(int i=0; i<d.size(); i++) {
                                    s.add(d.get(i).getString("shopName"));
                                }
                                words = new String[s.size()];
                                s.toArray(words);

                                SharedPreferences.Editor editor = settings.edit();
                                editor.putStringSet(Constants.CONFIGURATION_KEYWORD, s);
                                editor.putString(Constants.CONFIGURATION_KEYWORD_TIMESTAMP, str);
                                editor.commit();


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
        else {
            words = new String[set.size()];
            set.toArray(words);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(viewGroup != null)
            return viewGroup;
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_search, null);
        mText = (AutoCompleteTextView) viewGroup.findViewById(R.id.text);
        mSearch = (Button) viewGroup.findViewById(R.id.search);
        mTitle = (TextView) viewGroup.findViewById(R.id.title);

      //  mText.setDropDownWidth();
        mText.setDropDownAnchor(R.id.main);
        mText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mContent = mText.getText().toString();
                if(mContent == null || mContent.equalsIgnoreCase("")) {

                    mDialog.show();
                    return;
                }
                String n = "config=cluster:daogou,hit:1,start:0,format:json&&query=\"" + mContent + "\" OR pos:\"" + mContent + "\" OR default:\"" + mContent + "\" OR tag:\"" + mContent + "\"";
                Search(n);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_search_hint, words);
        mText.setAdapter(adapter);
        mText.requestFocus();

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContent = mText.getText().toString();
                if(mContent == null || mContent.equalsIgnoreCase("")) {

                    mDialog.show();
                    return;
                }

        //        String n = "config=cluster:daogou,hit:2,start:0,format:json&&query=" + mContent + " OR pos:" + mContent + " OR default:" + mContent + " OR tag:" + mContent;
                String n = "config=cluster:daogou,hit:2,start:0,format:json&&query=\"" + mContent + "\" OR pos:\"" + mContent + "\" OR default:\"" + mContent + "\" OR tag:\"" + mContent + "\"";

                Search(n);
            }
        });


        mTitle.setOnClickListener(this);
        return viewGroup;
    }

    private void Search(String n) {
        mInputMethodManager.hideSoftInputFromWindow(mText.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        String url = "http://10.97.252.37:12345/";
        try {
            n = URLEncoder.encode(n, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url+n,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("hits");


                            if(jsonObject == null || jsonObject.getInt("totalHits") == 0)
                                NoSearchResult();
                            else {
                                JSONArray jsonArray = jsonObject.getJSONArray("hits");

                                ShowResult(CommonHelper.getAdapterData(jsonArray));
                            }
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
    搜索结果为空
     */
    private void NoSearchResult() {
        mDialog.show();
    }

    /*
    在新的Fragment中展示搜索结果
     */
    private void ShowResult(List<JSONObject> list)  {

        try {
            //只有一项结果，直接跳转至店铺详情
            if (list.size() == 1) {
                JSONObject jsonObject = list.get(0);
                String shopId = jsonObject.getJSONObject("summaryHit").getJSONObject("hitSummarySchema").getString("_id");
                Bundle bundle = new Bundle();
                bundle.putString(Constants.ARGUMENT_ID, shopId);
                ((MyActivity) getActivity()).onFragmentChangeWithBundle(Constants.FRAGMENT_ID_STORE, bundle);
            }
            //多项结果，列表展示
            else {
                Bundle bundle = new Bundle();
                bundle.putString("key", mContent);
                ((MyActivity) getActivity()).onFragmentChangeWithBundle(Constants.FRAGMENT_ID_SEARCHRESUTL, bundle);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
