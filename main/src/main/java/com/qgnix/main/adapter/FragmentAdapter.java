package com.qgnix.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * 时间：2019-07-23 10:13<br>
 * 作者：<br>
 * 描述：订单列表中viewpager适配器
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    /**
     * fragment列表
     */
    private List<Fragment> fragmentList;

    /**
     * 标题组
     */
    private String[] tabListTitle;

    public FragmentAdapter(FragmentManager manager, List<Fragment> fragmentList, String[] tabListTitle) {
        super(manager);
        this.fragmentList = fragmentList;
        this.tabListTitle = tabListTitle;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return null == fragmentList ? 0 : fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabListTitle[position];
    }



    // 动态设置我们标题的方法
    public void setPageTitle(int position, String title) {
        if (position >= 0 && position < tabListTitle.length) {
            tabListTitle[position] = title;
            notifyDataSetChanged();
        }
    }
}
