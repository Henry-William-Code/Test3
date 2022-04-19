package com.qgnix.common.views;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * baseview接口
 */
public interface IBaseView extends View.OnClickListener {
    /**
     * 初始化数据，需要注意该方法在setContentView之前执行
     *
     * @param bundle 传递过来的bundle
     */
    void initData( final Bundle bundle);

    /**
     * 绑定布局
     *
     * @return 返回布局id
     */
    @LayoutRes
    int setLayoutId();

    /**
     * 初始化view
     *
     * @param savedInstanceState 实例状态
     * @param view               根view
     */
    void initView(final Bundle savedInstanceState, final View view);

    /**
     * 处理业务操作
     */
    void doBusiness();

    /**
     * 视图的点击事件
     *
     * @param view
     */
    void onWidgetClick(final View view);
}
