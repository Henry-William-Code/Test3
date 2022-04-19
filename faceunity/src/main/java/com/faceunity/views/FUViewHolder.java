package com.faceunity.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.faceunity.FURenderer;
import com.faceunity.R;
import com.faceunity.control.BeautyControlView;
import com.faceunity.interfaces.BeautyEffectListener;
import com.faceunity.interfaces.BeautyViewHolder;
import com.qgnix.common.views.AbsViewHolder;

/**
 * Created by cxf on 2018/6/22.
 * faceunity 美颜 UI相关
 */

public class FUViewHolder extends AbsViewHolder implements BeautyViewHolder {

    private VisibleListener mVisibleListener;
    private boolean mShowed;


    public FUViewHolder(Context context, ViewGroup parentView, FURenderer fuRenderer) {
        super(context, parentView);
        BeautyControlView mBeautyControlView = (BeautyControlView) findViewById(R.id.fu_beauty_control);
        mBeautyControlView.setOnFUControlListener(fuRenderer);
        mBeautyControlView.iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_faceunity;
    }

    @Override
    public void init() {
    }


    @Override
    public void setEffectListener(BeautyEffectListener effectListener) {

    }

    @Override
    public void show() {
        if (mVisibleListener != null) {
            mVisibleListener.onVisibleChanged(true);
        }
        if (mParentView != null && mContentView != null) {
            ViewParent parent = mContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentView);
            }
            mParentView.addView(mContentView);
        }
        mShowed = true;
    }

    @Override
    public void hide() {
        removeFromParent();
        if (mVisibleListener != null) {
            mVisibleListener.onVisibleChanged(false);
        }
        mShowed = false;
    }


    @Override
    public boolean isShowed() {
        return mShowed;
    }

    @Override
    public void release() {
        mVisibleListener = null;
    }

    @Override
    public void setVisibleListener(VisibleListener visibleListener) {
        mVisibleListener = visibleListener;
    }


}
