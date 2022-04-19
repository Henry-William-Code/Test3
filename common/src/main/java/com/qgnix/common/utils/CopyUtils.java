package com.qgnix.common.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.qgnix.common.R;

/**
 * 复制相关
 */
public class CopyUtils {
    /**
     * 复制到剪贴板
     */
    public static void copy(Context ctx, String content) {
        ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        if (null == cm) {
            L.e("【copy  ClipboardManager is null】");
            return;
        }
        ClipData clipData = ClipData.newPlainText("text", content);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(WordUtil.getString(R.string.copy_success));
    }


    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    public static String getText(Context ctx) {
        ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        if (null == cm) {
            L.e("【copy  ClipboardManager is null】");
            return null;
        }
        if (!cm.hasPrimaryClip()) {
            return null;
        }
        ClipData clip = cm.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).coerceToText(ctx).toString();
        }
        return null;
    }
}
