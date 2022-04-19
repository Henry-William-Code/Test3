package com.qgnix.common.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qgnix.common.R;


/**
 * 主页tab的item
 */
@SuppressLint("ViewConstructor")
public class TabItemView extends LinearLayout {

    /**
     * 两个状态 未选中、选中
     */
    public static final  int NORMAL = 1;
    public static final  int SELECTED = 2;

    /**
     * Item 的标题
     */
    public final String mTitle;

    /**
     * 标题的两个状态的颜色 未选中、选中
     */
    public final int mColorTxtNormal;
    public final int mColorTxtSelected;

    /**
     * 两个图标的 资源 id ，未选中、选中
     */
    public final int mIconNormal;
    public final int mIconSelected;

    /**
     * tab标题
     */
    public TextView tvTabTitle,tvNotice;
    /**
     * tab图标
     */
    public ImageView ivTabIcon;

    private final Context mContext;

    /**
     * @param context   上下文
     * @param title     文本
     * @param colorTxts 字体颜色数组， 0 默认 ，1 选中
     * @param icons     图标集合 0 默认 ，1 选中
     */
    public TabItemView(Context context, String title, @ColorRes int[] colorTxts, @DrawableRes int[] icons) {
        this(context, title, colorTxts[0], colorTxts[1], icons[0], icons[1]);
    }

    public TabItemView(Context context, String title, @ColorRes int colorTxtNormal, @ColorRes int colorTxtSelected,
                       @DrawableRes int iconNormal, @DrawableRes int iconSelected) {
        super(context);
        this.mContext = context;
        this.mTitle = title;
        this.mColorTxtNormal = colorTxtNormal;
        this.mColorTxtSelected = colorTxtSelected;
        this.mIconNormal = iconNormal;
        this.mIconSelected = iconSelected;
        init();
    }

    /**
     * 初始化
     */

    private void init() {
        View view = LayoutInflater.from(super.getContext()).inflate(R.layout.item_botton_tab_layout, this);
        tvTabTitle =view.findViewById(R.id.tv_tab_title);
        ivTabIcon = view.findViewById(R.id.iv_tab_icon);
        tvNotice = view.findViewById(R.id.tv_notice);
        //fl_tab_root
        //ll_tab

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);

        tvTabTitle.setText(mTitle);

        setStatus(TabItemView.NORMAL);
    }

    /**
     * 设置提醒是否显示
     * @param isShow view.visible/gone
     */
    public void setNotice(int isShow){
        tvNotice.setVisibility(isShow);
    }


    /**
     * 设置状态
     */
    public void setStatus(int status) {

        int colorId = ContextCompat.getColor(mContext, status == SELECTED ? mColorTxtSelected : mColorTxtNormal);
        tvTabTitle.setTextColor(colorId);
        ivTabIcon.setImageResource(status == SELECTED ? mIconSelected : mIconNormal);
    }
}