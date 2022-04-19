package com.qgnix.common.utils;

/**
 * Created by cxf on 2018/9/29.
 */

public class ClickUtil {

    private static long sLastClickTime;

    public static boolean canClick() {
        long curTime = System.currentTimeMillis();
        if (curTime - sLastClickTime < 1500) {
            return false;
        }
        sLastClickTime = curTime;
        return true;
    }

    public static boolean canClick(int ms) {
        long curTime = System.currentTimeMillis();
        if (curTime - sLastClickTime < ms) {
            return false;
        }
        sLastClickTime = curTime;
        return true;
    }
}
