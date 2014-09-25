package com.taobao.daogou.client;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.taobao.daogou.client.fragment.CanyinyuleFragment;
import com.taobao.daogou.client.fragment.DingweiFragment;
import com.taobao.daogou.client.fragment.GengduofuwuFragment;
import com.taobao.daogou.client.fragment.HomeFragment;
import com.taobao.daogou.client.fragment.LoucengfenbuFragment;
import com.taobao.daogou.client.fragment.MainFragment;
import com.taobao.daogou.client.fragment.MapDetailFragment;
import com.taobao.daogou.client.fragment.PinpaileimuFragment;
import com.taobao.daogou.client.fragment.RemenshangpinFragment;
import com.taobao.daogou.client.fragment.RouteFragment;
import com.taobao.daogou.client.fragment.SearchFragment;
import com.taobao.daogou.client.fragment.SearchResultFragment;
import com.taobao.daogou.client.fragment.StoreFragment;
import com.taobao.daogou.client.fragment.StoreMapFragment;
import com.taobao.daogou.client.fragment.VoiceRecogFragment;
import com.taobao.daogou.client.fragment.util.FragmentRouter;
import com.taobao.daogou.client.fragment.TesedianpuFragment;
import com.taobao.daogou.client.util.Constants;




public class MyActivity extends BaseActivity implements FragmentRouter, Constants {
    FrameLayout mFragmentHolderLayout;
    FragmentManager mFragmentManager;
    private ImageView mDefault;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mFragmentHolderLayout = (FrameLayout) findViewById(R.id.main_activity_frame);
        mDefault = (ImageView) findViewById(R.id.main_default);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_alpha_default);
        animation.setAnimationListener(animationListener);
      //  animation.setFillEnabled(true);
      //  animation.setFillAfter(true);


        mFragmentManager = getFragmentManager();
        mDefault.startAnimation(animation);
     //   onFragmentChange(FRAGMENT_ID_HOME);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentChange(int fragment) {
        onFragmentChangeWithBundle(fragment, null);
    }

    @Override
    public void onFragmentChangeWithBundle(int fragmentID, Bundle bundle) {
        Fragment tmpFragment;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        switch (fragmentID) {
            case FRAGMENT_ID_MAIN:
                tmpFragment = new MainFragment();
                break;
            case FRAGMENT_ID_VOICE:
                tmpFragment = new VoiceRecogFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_MAP:
                tmpFragment = new MapDetailFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_HOME:
                tmpFragment = new HomeFragment();
                break;
            case FRAGMENT_ID_TESEDIANPU:
                tmpFragment = new TesedianpuFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_REMENSHANGPIN:
                tmpFragment = new RemenshangpinFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_CANYINYULE:
                tmpFragment = new CanyinyuleFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_LOUCENGFENBU:
                tmpFragment = new LoucengfenbuFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_STORE:
                tmpFragment = new StoreFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_PINPAILEIMU:
                tmpFragment = new PinpaileimuFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_SEARCH:
                tmpFragment = new SearchFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_SEARCHRESUTL:
                tmpFragment = new SearchResultFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_SERVICE:
                tmpFragment = new GengduofuwuFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_STROE_MAP:
                tmpFragment = new StoreMapFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_ROUTE:
                tmpFragment = new RouteFragment();
                transaction.addToBackStack(null);
                break;
            case FRAGMENT_ID_DINGWEI:
                tmpFragment = new DingweiFragment();
                transaction.addToBackStack(null);
                break;
            default:
                throw new RuntimeException("FragmentID not found");
        }
        if (fragmentID != FRAGMENT_ID_HOME) {
            transaction.setCustomAnimations(
                    R.animator.right_fade_in,
                    R.animator.left_fade_out,
                    R.animator.left_fade_in,
                    R.animator.right_fade_out);
        }
        if (bundle != null)
            tmpFragment.setArguments(bundle);

        transaction.replace(R.id.main_activity_frame, tmpFragment);
        transaction.commit();
    }

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mDefault.setVisibility(View.INVISIBLE);
            onFragmentChange(FRAGMENT_ID_HOME);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
}
