package com.qgnix.main.dialog;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qgnix.common.HtmlConfig;
import com.qgnix.common.utils.WebViewUtils;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.main.R;

/**
 * 优惠券
 */
public class CouponDialog extends BaseCustomDialog {



    public CouponDialog(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_coupon_layout;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initView() {
        WebView wv = (WebView) findViewById(R.id.wv);
        wv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setUserAgentString("User-Agent:Android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wv.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        wv.loadUrl(WebViewUtils.handleWebUrlToken(HtmlConfig.COUPON));
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    public int showGravity() {
        return Gravity.CENTER;
    }

}



