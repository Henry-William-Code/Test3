package com.qgnix.common.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.qgnix.common.CommonAppConfig;

/**
 * @author chengzhiqiang
 * webview 帮助类
 */
public class WebViewUtils {

    private WebViewUtils() {

    }

    /**
     * 处理webview url
     *
     * @param url 原始url
     * @return 处理后的url
     */
    public static String handleWebUrl(String url) {
        if (!url.contains(LanSwitchUtil.LAN_KEY + "=")) {
            if (url.contains("?")) {
                url = url + "&" + LanSwitchUtil.LAN_KEY + "=" + LanSwitchUtil.getLanStr();
            } else {
                url = url + "?" + LanSwitchUtil.LAN_KEY + "=" + LanSwitchUtil.getLanStr();
            }
        }
        L.e("H5--handle--->" + url);
        return url;
    }

    /**
     * 处理webview url 带token
     *
     * @param url 原始url
     * @return 处理后的url
     */
    public static String handleWebUrlToken(String url) {
        if (!url.contains(LanSwitchUtil.LAN_KEY + "=")) {
            if (url.contains("?")) {
                //forward请求h5页面, 没办法通过header设置lan变量就通过url设置lan变量
                url += "&uid=" + CommonAppConfig.getInstance().getUid() + "&token=" + CommonAppConfig.getInstance().getToken() + "&" + LanSwitchUtil.LAN_KEY + "=" + LanSwitchUtil.getLanStr();
            } else {
                url = url + "?uid=" + CommonAppConfig.getInstance().getUid() + "&token=" + CommonAppConfig.getInstance().getToken() + "&" + LanSwitchUtil.LAN_KEY + "=" + LanSwitchUtil.getLanStr();
            }
        }
        L.e("H5--handle--->" + url);
        return url;
    }


    /**
     * 复制到剪贴板
     */
    public static void copy(Context ctx, String content) {
        ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(ctx.getString(com.qgnix.common.R.string.copy_success));
    }
}
