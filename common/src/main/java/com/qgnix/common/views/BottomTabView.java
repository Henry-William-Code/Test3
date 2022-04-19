package com.qgnix.common.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

/***
 * 主页底部tab
 */
public class BottomTabView extends LinearLayout {

    /**
     * 记录最新的选择位置
     */
    private int lastPosition = -1;

    /**
     * 所有 TabItem 的集合
     */
    private List<TabItemView> tabItemViews;

    public BottomTabView(Context context) {
        super(context);
    }

    public BottomTabView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }


    public BottomTabView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置 Tab Item View
     */
    public void setTabItemViews(List<TabItemView> tabItemViews) {
        setTabItemViews(tabItemViews, null);
    }

    /**
     * 设置 Tab Item View
     *
     * @param tabItemViews view
     * @param centerView   中间的view
     */
    public void setTabItemViews(List<TabItemView> tabItemViews, View centerView) {

        if (this.tabItemViews != null) {
            throw new RuntimeException("不能重复设置！");
        }

        if (tabItemViews == null || tabItemViews.size() < 2) {
            throw new RuntimeException("TabItemView 的数量必须大于2！");
        }

        this.tabItemViews = tabItemViews;
        for (int i = 0; i < tabItemViews.size(); i++) {

            if (centerView != null && i == tabItemViews.size() / 2) {
                this.addView(centerView);
            }

            final TabItemView tabItemView = tabItemViews.get(i);

            this.addView(tabItemView);

            final int finalI = i;

            tabItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (finalI == lastPosition) {
                        // 第二次点击
                        if (onSecondSelectListener != null) {
                            onSecondSelectListener.onSecondSelect(finalI);
                        }
                        return;
                    }

                    updatePosition(finalI);

                    if (onTabItemSelectListener != null) {
                        onTabItemSelectListener.onTabItemSelect(finalI);
                    }
                }
            });
        }

        // 默认状态选择第一个
        updatePosition(0);
    }

    /**
     * 更新被选中 Tab Item 的状态
     * 恢复上一个 Tab Item 的状态
     */
    public void updatePosition(int position) {
        if (lastPosition != position) {
            if (tabItemViews != null && tabItemViews.size() != 0) {
                tabItemViews.get(position).setStatus(TabItemView.SELECTED);
                if (lastPosition != -1) {
                    tabItemViews.get(lastPosition).setStatus(TabItemView.NORMAL);
                }
                lastPosition = position;
            } else {
                throw new RuntimeException("please setTabItemViews !");
            }
        }
    }

    /**
     * 重置选中状态
     */
    public void resetStatus() {
        for (TabItemView itemView : tabItemViews) {
            itemView.setStatus(TabItemView.NORMAL);
        }
    }

    OnTabItemSelectListener onTabItemSelectListener;
    OnSecondSelectListener onSecondSelectListener;

    public void setOnTabItemSelectListener(OnTabItemSelectListener onTabItemSelectListener) {
        this.onTabItemSelectListener = onTabItemSelectListener;
    }

    public void setOnSecondSelectListener(OnSecondSelectListener onSecondSelectListener) {
        this.onSecondSelectListener = onSecondSelectListener;
    }

    /**
     * 第二次被选择的监听器
     */
    public interface OnSecondSelectListener {
        void onSecondSelect(int position);
    }

    /**
     * 第一次被选择的监听器
     */
    public interface OnTabItemSelectListener {
        void onTabItemSelect(int position);
    }

    public void setNoticeState(int isShow){
        tabItemViews.get(tabItemViews.size()-1).setNotice(isShow);
    }

}
