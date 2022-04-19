package com.qgnix.live.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.webkit.JavascriptInterface;

import com.qgnix.common.utils.ImgUtils;
import com.qgnix.common.utils.QRCodeUtils;
import com.qgnix.common.utils.ToastUtil;



// 调用js 方法处理
public class MyPicJavaScript {

    private Context mContext;
    private ClipboardManager cm;
    private ClipData mClipData;

    private Bitmap bitmap;

    public MyPicJavaScript(Context context) {
        this.mContext = context;
    }

    // 我的收益
    @JavascriptInterface
    public void tixian() {
        Intent intent=new Intent("com.qgnix.main.activity.MyProfitActivity");
        mContext.startActivity(intent);
    }

    // 复制链接到粘贴板
    @JavascriptInterface
    public void clipurl(String url) {
        //获取剪贴板管理器：
        cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        mClipData = ClipData.newPlainText("Label", url);
        cm.setPrimaryClip(mClipData);
        ToastUtil.show("链接已复制");
    }

    @JavascriptInterface
    public void savepic(String url) {
        bitmap = QRCodeUtils.createQRCodeBitmap(url, 400, 400, "UTF-8", "H", "1", Color.BLACK, Color.WHITE);
        ImgUtils.saveImageToGallery(mContext, bitmap);
    }
}
