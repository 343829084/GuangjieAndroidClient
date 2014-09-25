package com.taobao.daogou.client.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.taobao.daogou.client.R;

/*
* 没有搜索结果时的对话框
 */
public class NoResultDialog extends Dialog{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public NoResultDialog(Context context, int theme, String s) {
		super(context,theme);
		// TODO Auto-generated constructor stub
		setContentView(R.layout.dialog_noresult);

        TextView hint = (TextView) findViewById(R.id.hint);
        hint.setText(s);

        TextView sousuo = (TextView) findViewById(R.id.sousuo);
        sousuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
	}
	
	

}
