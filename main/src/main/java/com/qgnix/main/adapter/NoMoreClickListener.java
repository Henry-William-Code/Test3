package com.qgnix.main.adapter;

import android.view.View;

import java.util.Calendar;

/**
 * @author sameal
 * @version 1.0
 * @date 2020/1/6 09:08
 */
public abstract class NoMoreClickListener implements View.OnClickListener {
    private int MIN_CLICK_DELAY_TIME = 2000;//多少秒点击一次 默认2.5秒
    private long lastClickTime = 0;

    public NoMoreClickListener() {
    }

    /**
     * 设置多少秒之内
     *
     * @param time
     */
    public NoMoreClickListener(int time) {
        this.MIN_CLICK_DELAY_TIME = time;
    }

    @Override
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onMoreClick(view);
        }
    }

    /**
     * 在N秒之内的 ==1 次点击回调次方法
     *
     * @param view
     */
    protected abstract void onMoreClick(View view);

}
