package com.qgnix.main.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.qgnix.common.fragment.BaseFragment;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.main.interfaces.OnNavigatorClickListener;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

public class SportFragment extends BaseFragment{
    //---------------------------------------- 视图控件相关开始位置 ----------------------------------

    /**
     * 指示器
     */
    private MagicIndicator mMagicIndicator;


    //---------------------------------------- 视图控件相关结束位置 ----------------------------------
    //---------------------------------------- 其它变量相关开始位置 ----------------------------------

    private FragmentManager mFragmentManager;


    /**
     * tab
     */
    final String[] mTitles = {WordUtil.getString(R.string.sport_title_in_progress), WordUtil.getString(R.string.sport_title_schedule)};


    //---------------------------------------- 其它变量相关结束位置 ----------------------------------


    public static SportFragment newInstance() {
        return new SportFragment();
    }

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_sport;
}

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        // 指示器
        mMagicIndicator = view.findViewById(R.id.magic_indicator);
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
                setCurrentFragment(getFragments().get(index));

            }
        });
        // 指示器
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(adapter);
        mMagicIndicator.setNavigator(commonNavigator);

        mFragmentManager = getChildFragmentManager();
        setCurrentFragment(getFragments().get(0));
    }


    /**
     * 获取fragments
     *
     * @return list集合
     */
    private List<BaseFragment> getFragments() {
        final List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(SportProgressFragment.newInstance());
        fragments.add(SportScheduleFragment.newInstance());
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
}
