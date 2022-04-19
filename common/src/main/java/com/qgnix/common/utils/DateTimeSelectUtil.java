package com.qgnix.common.utils;


import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import org.jaaksi.pickerview.picker.TimePicker;
import org.jaaksi.pickerview.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeSelectUtil {


    /**
     * 格式化时间
     */
    public static final String TIME_FORMAT_1 = "yyyy年MM月dd日";

    /**
     * 接口回调
     */
    private TimePicker.OnTimeSelectListener listener;

    public DateTimeSelectUtil(TimePicker.OnTimeSelectListener listener) {
        this.listener = listener;
    }


    /**
     * 选择时间
     * @param context
     * @param type
     * @param timeFormat
     */
    public void selectTime(Context context, int type, String timeFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.CHINA);
        TimePicker mTimePicker = new TimePicker.Builder(context, TimePicker.TYPE_DATE, listener)
                // 设置不包含超出的结束时间<=
                .setContainsEndDate(true)
                // 设置时间间隔为30分钟
                // .setTimeMinuteOffset(30).setRangDate(startTime, endTime)
                .setSelectedDate(new Date().getTime())
                .setFormatter(new TimePicker.DefaultFormatter() {
                    @Override
                    public CharSequence format(TimePicker picker, int type, int position, long value) {
                        //if (type == TimePicker.TYPE_MIXED_DATE) {
                        //    CharSequence text;
                        //    int dayOffset = DateUtil.getDayOffset(value, System.currentTimeMillis());
                        //    if (dayOffset == 0) {
                        //        text = "今天";
                        //    } else if (dayOffset == 1) {
                        //        text = "明天";
                        //    } else {
                        //        text = sdf.format(value);
                        //    }
                        //    return text;
                        //}
                        return super.format(picker, type, position, value);
                    }
                }).create();
        Dialog pickerDialog = mTimePicker.getPickerDialog();
        pickerDialog.setCanceledOnTouchOutside(true);
        mTimePicker.getTopBar().getTitleView().setText("请选择日期");
        mTimePicker.setTag(type);
        mTimePicker.show();
    }



}
