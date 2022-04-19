package com.qgnix.main.fragment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.qgnix.common.utils.DpUtil;
import com.qgnix.main.R;
import com.qgnix.main.interfaces.OnNavigatorClickListener;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * viewPager 指示器 适配器
 */
public class NavigatorAdapter extends CommonNavigatorAdapter {
    private final String[] mTitles;

    private final Context mContext;
    private final int[] mColors;
    /**
     * 点击回调
     */
    private OnNavigatorClickListener mOnNavigatorClickListener;

    public void setOnNavigatorClickListener(OnNavigatorClickListener onNavigatorClickListener) {
        this.mOnNavigatorClickListener = onNavigatorClickListener;
    }

    public NavigatorAdapter(Context mContext, String[] mTitles, int[] colors) {
        this.mContext = mContext;
        this.mTitles = mTitles;
        this.mColors = colors;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, mColors[0]));
        simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, mColors[1]));
        simplePagerTitleView.setText(mTitles[index]);
        simplePagerTitleView.setTextSize(18);
        simplePagerTitleView.getPaint().setFakeBoldText(true);
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnNavigatorClickListener.onNavigatorClick(index);
            }
        });
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setXOffset(DpUtil.dp2px(10));
        linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
        linePagerIndicator.setColors(ContextCompat.getColor(mContext, mColors[1]));
        return linePagerIndicator;
    }


}
