package com.qgnix.common.utils;

import android.content.res.Resources;

import com.qgnix.common.CommonAppContext;

/**
 * Created by cxf on 2017/10/10.
 * 获取string.xml中的字
 */

public class WordUtil {

    private static final String FONT_RED = "<font color='#FE0000'>%s</font>";

    private static final String FONT_BLANK = "<font color='#000000'>%s</font>";

    private static final String FONT_WRITE = "<font color='#FFFFFF'>%s</font>";

    private static final String FONT_ORANGE = "<font color='#FF9800'>%s</font>";

    private static final Resources sResources;

    static {
        sResources = CommonAppContext.sInstance.getResources();
    }

    public static String getString(int res) {
        return sResources.getString(res);
    }

    public static String getString(int res, Object... args) {
        return String.format(sResources.getString(res), args);
    }


    /*************************富文本处理****************************************************/
    /**
     * 红色字符
     *
     * @param resId
     * @return
     */
    public static String getRedHtmlStrFromRes(int resId) {
        return getRedHtmlStr(getString(resId));
    }

    /**
     * 红色字符
     *
     * @param data
     * @return
     */
    public static String getRedHtmlStr(int data) {
        return getRedHtmlStr(String.valueOf(data));
    }

    /**
     * 红色字符
     *
     * @param data
     * @return
     */
    public static String getRedHtmlStr(String data) {
        return String.format(FONT_RED, data);
    }

    /**
     * 黑色字符
     *
     * @param resId
     * @return
     */
    public static String getBlankHtmlStrFromRes(int resId) {
        return getBlankHtmlStr(getString(resId));
    }

    /**
     * 黑色字符
     *
     * @param data
     * @return
     */
    public static String getBlankHtmlStr(int data) {
        return getBlankHtmlStr(String.valueOf(data));
    }

    /**
     * 黑色字符
     *
     * @param data
     * @return
     */
    public static String getBlankHtmlStr(String data) {
        return String.format(FONT_BLANK, data);
    }


    /**
     * 白色字符
     *
     * @param resId
     * @return
     */
    public static String getWriteHtmlStrFromRes(int resId) {
        return getWriteHtmlStr(getString(resId));
    }

    /**
     * 白色字符
     *
     * @param data
     * @return
     */
    public static String getWriteHtmlStr(String data) {
        return String.format(FONT_WRITE, data);
    }

    /**
     * 橘色字符
     *
     * @param resId
     * @return
     */
    public static String getOrangeHtmlStrFromRes(int resId) {
        return getOrangeHtmlStr(getString(resId));
    }

    /**
     * 橘色字符
     *
     * @param data
     * @return
     */
    public static String getOrangeHtmlStr(String data) {
        return String.format(FONT_ORANGE, data);
    }
}
