package com.qgnix.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.R;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.utils.DpUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.WebViewUtils;
import com.qgnix.live.utils.MyPicJavaScript;

/**
 * Created by cxf on 2018/9/25.
 */

public class WebViewTestActivity extends AbsActivity {

    private WebView mWebView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview_test;
    }

    @Override
    protected void main() {
        mWebView = findViewById(R.id.webView_test);
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.e("H5-------->" + url);
                if (url.startsWith(Constants.COPY_PREFIX)) {
                    String content = url.substring(Constants.COPY_PREFIX.length());
                    if (!TextUtils.isEmpty(content)) {
                        WebViewUtils.copy(mContext,content);
                    }
                } else {
                    view.loadUrl(WebViewUtils.handleWebUrl(url));
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setTitle(view.getTitle());
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {

                } else {

                }
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUserAgentString("User-Agent:Android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.loadUrl("https://55funwith.com/livetv/0/cherry/123/195584/9b62792b5e67670b49fd63b7926bab09/42.48.198.136/1632387843?=");

        // js 方法监听
        mWebView.addJavascriptInterface(new MyPicJavaScript(getApplicationContext()), "Android"); // JS回调
    }



//    protected boolean canGoBack() {
//        return mWebView != null && mWebView.canGoBack();
//    }

//    @Override
//    public void onBackPressed() {
//        if (isNeedExitActivity()) {
//            finish();
//        } else {
//            if (canGoBack()) {
//                mWebView.goBack();
//            } else {
//                finish();
//            }
//        }
//    }



    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.destroy();
        }
        super.onDestroy();
    }


}
