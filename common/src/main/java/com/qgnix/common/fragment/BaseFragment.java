package com.qgnix.common.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qgnix.common.utils.ClickUtil;
import com.qgnix.common.views.IBaseView;


/**
 * 基类fragment 基于v4.app
 */
public abstract class BaseFragment extends Fragment implements IBaseView {
    //private static final String TAG = "===" + BaseFragment.class.getSimpleName();

    protected final String TAG = "===" + getClass().getSimpleName();

    /**
     * 当前Activity渲染的视图View
     */
    protected View contentView;
    protected Context mContext;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "===onCreate===");
        mContext = getContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        Log.i(TAG, "===onCreateView===");
        setRetainInstance(true);
        contentView = inflater.inflate(setLayoutId(), container, false);
        initView(savedInstanceState, contentView);
        doBusiness();
        return contentView;
    }

    /**
     * onCreateView是创建的时候调用，onViewCreated是在onCreateView后被触发的事件，
     * 前后关系就是fragment中的onCreateView和onViewCreated的区别和联系，
     * 且onStart运行时间位于onViewCreated之后
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "===onViewCreated===");
        initData(getArguments());
    }


    @Override
    public void onClick(View v) {
        // 防止快速点击
        if (ClickUtil.canClick()) {
            onWidgetClick(v);
        }

    }

    @Override
    public void onWidgetClick(View view) {

    }

}