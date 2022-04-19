package com.qgnix.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.fragment.BaseFragment;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.BuildConfig;
import com.qgnix.main.R;
import com.qgnix.main.activity.LanSwitchActivity;
import com.qgnix.main.activity.MainListActivity;
import com.qgnix.main.activity.WebViewsActivity;
import com.qgnix.main.dialog.SwitchModeDialog;
import com.qgnix.main.interfaces.OnNavigatorClickListener;
import com.qgnix.main.interfaces.OnSwitchModelListener;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页fragment
 */
public class HomeFragment extends BaseFragment {

    /**
     * tab
     */
    final String[] mTitles = {WordUtil.getString(R.string.popular), WordUtil.getString(R.string.kj)};
    /**
     * 推广
     */
    private ImageView mIvPromote;
    /**
     * 指示器
     */
    private MagicIndicator mMagicIndicator;

    private FragmentManager mFragmentManager;

    private TextView tv_center;
    private String[] tv_center_texts = {"HONOR0.WIN", "HONOR1.WIN", "HONOR2.WIN", "HONOR3.WIN", "HONOR4.WIN", "HONOR5.WIN", "HONOR6.WIN", "HONOR7.WIN", "HONOR8.WIN", "HONOR9.WIN"};
    private int currIndex = 0;
    private Handler handler;
    private Runnable task;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public void initData( Bundle bundle) {
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        // 指示器
        mMagicIndicator = view.findViewById(R.id.magic_indicator);
        tv_center = view.findViewById(R.id.tv_center);
        //客服
        view.findViewById(R.id.iv_customer_services).setOnClickListener(this);
        //切换语言
        view.findViewById(R.id.iv_change_lan).setOnClickListener(this);
        //排行
        view.findViewById(R.id.iv_ranking).setOnClickListener(this);
        // 切换模式
        mIvPromote = view.findViewById(R.id.iv_promote);
        if(CommonAppConfig.getInstance().getSwitchStatu()){
            mIvPromote.setVisibility(View.GONE);
        }
        mIvPromote.setOnClickListener(this);
    }

    @Override
    public void doBusiness() {


        //指示器适配器
        NavigatorAdapter adapter = new NavigatorAdapter(mContext, mTitles, new int[]{R.color.color1, R.color.color2});
        adapter.setOnNavigatorClickListener(new OnNavigatorClickListener() {
            @Override
            public void onNavigatorClick(int index) {
                mMagicIndicator.onPageSelected(index);
                mMagicIndicator.onPageScrolled(index, 0.0F, 0);

                mIvPromote.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
                if(CommonAppConfig.getInstance().getSwitchStatu()){
                    mIvPromote.setVisibility(View.GONE);
                }
                setCurrentFragment(getFragments().get(index));

            }
        });
        // 指示器
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(adapter);
        mMagicIndicator.setNavigator(commonNavigator);

        mFragmentManager = getChildFragmentManager();
        setCurrentFragment(getFragments().get(0));

        task = new Runnable() {
            @Override
            public void run() {
                if (currIndex > 9) {
                    currIndex = 0;
                }
                tv_center.setText(tv_center_texts[currIndex++]);
                handler.postDelayed(task, 1000);
                System.out.println(""+tv_center.getText());
            }
        };
        handler = new Handler();
        handler.post(task);
    }


    @Override
    public void onWidgetClick(View view) {
        super.onWidgetClick(view);
        int vId = view.getId();
        if (vId == R.id.iv_customer_services) {
            //客服中心
            Intent intent = new Intent(mContext, WebViewsActivity.class);
            intent.putExtra("title", WordUtil.getString(R.string.customer_service));
            intent.putExtra("url", CommonAppConfig.getInstance().getConfig().getKefuUrl());
            mContext.startActivity(intent);
        } else if (vId == R.id.iv_change_lan) {
            //切换语言
            LanSwitchActivity.toForward(mContext);
        } else if (vId == R.id.iv_ranking) {
            //排行
            MainListActivity.forward(mContext);
        } else if (vId == R.id.iv_promote) {
            // 切换模式
            new SwitchModeDialog(mContext, new OnSwitchModelListener() {
                @Override
                public void success() {
                    setCurrentFragment(getFragments().get(0));
                }
            }).show();
        }
    }

    /**
     * 获取fragments
     *
     * @return list集合
     */
    private List<BaseFragment> getFragments() {
        final List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(HomeHotFragment.newInstance());
        fragments.add(HomeDrawFragment.newInstance());
        return fragments;
    }

    private void setCurrentFragment(BaseFragment fragment) {
        if (null != mFragmentManager) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_container, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            throw new NullPointerException("==mFragmentManager为空=");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            handler.removeCallbacks(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}