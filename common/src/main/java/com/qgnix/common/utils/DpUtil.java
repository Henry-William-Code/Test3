package com.qgnix.common.utils;

import android.content.Context;

import com.qgnix.common.CommonAppContext;

/**
 * Created by cxf on 2017/8/9.
 * dp转px工具类
 */

public class DpUtil {
    public static final String SALT = "76576076c1f5f657b634e966c8836a06";
    private static float scale;

    static {
        scale = CommonAppContext.sInstance.getResources().getDisplayMetrics().density;
    }

    public static int dp2px(int dpVal) {
        return (int) (scale * dpVal + 0.5f);
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
