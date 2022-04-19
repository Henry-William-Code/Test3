package com.qgnix.common.utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.qgnix.common.CommonAppContext;
import com.qgnix.common.R;

/**
 * Created by cxf on 2017/8/3.
 */

public class ToastUtil {

    private static Toast sToast;
    private static long sLastTime;
    private static String sLastString;

    static {
        sToast = makeToast();
    }

    private static Toast makeToast() {
        Toast toast = new Toast(CommonAppContext.sInstance);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = LayoutInflater.from(CommonAppContext.sInstance).inflate(R.layout.view_toast, null);
        toast.setView(view);
        return toast;
    }


    public static void show(int res) {
        show(WordUtil.getString(res));
    }

    public static void show(int res, Object... args) {
        show(WordUtil.getString(res, args));
    }

    public static void show(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        long curTime = System.currentTimeMillis();
        if (curTime - sLastTime > 2000) {
            sLastTime = curTime;
            sLastString = s;
            sToast.setText(s);
            sToast.show();
        } else {
            if (!s.equals(sLastString)) {
                sLastTime = curTime;
                sLastString = s;
                sToast = makeToast();
                sToast.setText(s);
                sToast.show();
            }
        }

    }


    /**
     * 成功toast
     *
     * @param context 上下文
     */
    public static void showSuccess(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_betting_success, null);
        ImageView ivSuccess = view.findViewById(R.id.iv_success);
        // 通过逐帧动画的资源文件获得AnimationDrawable示例
        AnimationDrawable drawable = (AnimationDrawable) context.getResources().getDrawable(R.drawable.anim_betting_success);
        // 把AnimationDrawable设置为ImageView的背景
        ivSuccess.setBackground(drawable);
        //开始动画
        drawable.start();
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

}
