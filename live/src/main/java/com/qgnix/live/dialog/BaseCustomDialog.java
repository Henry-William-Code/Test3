package com.qgnix.live.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

import com.qgnix.live.R;


// 自定义dialog
public abstract class BaseCustomDialog extends Dialog {

    public BaseCustomDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉白色背景
        setContentView(getLayoutId());//这行一定要写在前面
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(isCancelable());//点击外部不可dismiss
        Window window = this.getWindow();
        window.setGravity(showGravity());
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        initView();
        initData();
    }

    // 绑定布局
    public abstract int getLayoutId();

    // 绑定视图
    public abstract void initView();

    // 初始化数据
    public abstract void initData();

    // 是否可以点击
    public abstract boolean isCancelable();

    // 显示位置
    public abstract int showGravity();

}
