package com.qgnix.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.HtmlConfig;
import com.qgnix.common.utils.WebViewUtils;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.main.R;

/**
 * 主播工资提现
 */
public class AnchorSalaryWithdrawDialog extends BaseCustomDialog {
    public AnchorSalaryWithdrawDialog(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_anchor_salary_withdraw_layout;
    }

    @Override
    public void initView() {
        WebView wv = findViewById(R.id.wv);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        wv.loadUrl(WebViewUtils.handleWebUrl(HtmlConfig.ANCHOR_SALARY_WITHDRAW + "&uid=" + CommonAppConfig.getInstance().getUid() + "&token=" + CommonAppConfig.getInstance().getToken()));
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public int showGravity() {
        return Gravity.CENTER;
    }
}
