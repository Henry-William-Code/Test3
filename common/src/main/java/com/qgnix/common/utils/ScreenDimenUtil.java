package com.qgnix.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.qgnix.common.CommonAppContext;

import java.lang.reflect.Field;

/**
 * Created by cxf on 2017/10/30.
 * 获取屏幕尺寸
 */

public class ScreenDimenUtil {

    private Resources mResources;
    private int mStatusBarHeight;//状态栏高度
    private int mContentHeight;
    private int mScreenWdith;
    private int mScreenHeight;


    private static ScreenDimenUtil sInstance;

    private ScreenDimenUtil() {
        mResources = CommonAppContext.sInstance.getResources();
        DisplayMetrics dm = mResources.getDisplayMetrics();
        mScreenWdith = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        //网上找的办法，使用反射在DecoderView未绘制出来之前计算状态栏的高度
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            mStatusBarHeight = mResources.getDimensionPixelSize(x);
            mContentHeight = mScreenHeight - mStatusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ScreenDimenUtil getInstance() {
        if (sInstance == null) {
            synchronized (ScreenDimenUtil.class) {
                if (sInstance == null) {
                    sInstance = new ScreenDimenUtil();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取屏幕的宽度
     *
     * @return
     */
    public int getScreenWdith() {
        return mScreenWdith;
    }

    /**
     * 获取屏幕的高度
     *
     * @return
     */
    public int getScreenHeight() {
        return mScreenHeight;
    }

    /**
     * 获取ContentView的高度
     *
     * @return
     */
    public int getContentHeight() {
        return mContentHeight;
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        return mStatusBarHeight;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
