package com.taobao.daogou.client.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.taobao.daogou.client.R;

/**
 * Created by jian on 14-8-18.
 */
public class MenuFrament extends TitleFragment implements View.OnClickListener {

    protected PopupWindow mPopupWindow;
    protected ImageView mMenu;

    @Override
    public void onDestroyView() {
        if(mPopupWindow != null && mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu:
                if(mPopupWindow.isShowing())
                    mPopupWindow.dismiss();
                else
                    mPopupWindow.showAsDropDown(mMenu, (int) (mMenu.getWidth() - mPopupWindow.getWidth()), (int) (0));
                break;
            default:
                break;
        }

        super.onClick(view);
    }
}
