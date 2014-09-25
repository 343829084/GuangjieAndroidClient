package com.taobao.daogou.client.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/*
* 适配ScrollView的GridView
*/
public class GridViewForScrollView extends GridView{

	public GridViewForScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public GridViewForScrollView(Context context, AttributeSet attrs) {   
		super(context, attrs);   
	}   
	public GridViewForScrollView(Context context, AttributeSet attrs, int defStyle) {   
	    super(context, attrs, defStyle);   
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST); 
		super.onMeasure(widthMeasureSpec, expandSpec);
	} 
}
