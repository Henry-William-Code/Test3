package com.qgnix.common.utils;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * 振动util
 */
public class VibrateUtil {

    /**
     * 震动milliseconds毫秒
     *
     * @param activity
     * @param milliseconds
     */
    public static void vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        if (vib.hasVibrator()) {
            vib.vibrate(milliseconds);
        }

    }

    /**
     * 取消震动
     *
     * @param activity
     */
    public static void vibrateCancel(final Activity activity) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        if (vib.hasVibrator()) {
            vib.cancel();
        }
    }
}
