package com.qgnix.main.dialog;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.HtmlConfig;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.LanSwitchUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.main.R;
import com.qgnix.main.activity.AddBankActivity;
import com.qgnix.main.http.MainHttpUtil;

/**
 * Created by cxf on 2020/10/8.
 * 绑定银行卡活动页面弹出框
 */

public class TieCardDialog extends BaseCustomDialog implements View.OnClickListener {

    private final Context mContext;


    public TieCardDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_tie_card_layout;
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
        //后台用了静态页面,提高性能 所以这里直接选择好语言 直接访问对应的静态页面 ,
        wv.loadUrl(CommonAppConfig.getCurHost() + "/banner/" + LanSwitchUtil.getLanStr() + HtmlConfig.TIECARD);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        // js 方法监听
        wv.addJavascriptInterface(this, "webapp"); // JS回调
        findViewById(R.id.btn_close).setOnClickListener(this);
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


    /**
     * 取消绑定银行卡，修改激活银行卡状态
     */
    private void cancelbank() {
        MainHttpUtil.setCanceltieCard(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show(msg);
                dismiss();
            }
        });
    }


    //银行卡绑定广告页中的去绑定银行卡按钮
    @JavascriptInterface
    public void addBank() {
        AddBankActivity.newIntent(mContext);
        dismiss();
    }

    //银行卡绑定广告页中的不再提醒绑定银行卡按钮
    @JavascriptInterface
    public void neverRemind() {
        //取消 代码
        MainHttpUtil.setCanceltieCard(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                CommonAppConfig.getInstance().setBlankStatus(3);
                dismiss();
            }
        });
    }

    //银行卡绑定广告页中的关闭本窗口
    @JavascriptInterface
    public void closeApp() {
        dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_close) {
            dismiss();
        }
    }
}



