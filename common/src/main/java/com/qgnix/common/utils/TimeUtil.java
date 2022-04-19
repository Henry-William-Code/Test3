package com.qgnix.common.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间：2019-08-23 14:29 <br>
 * 作者：<br>
 * 描述：时间处理工具类
 */
public class TimeUtil {


    /**
     * 根据时间值获取时间格式
     *
     * @param format
     * @param time
     * @return
     */
    public static String getFormatTime(String format, Date time) {
        return getFormatTime(format, time.getTime());
    }

    /**
     * 根据毫秒值获取时间格式
     *
     * @param format
     * @param time
     * @return
     */
    public static String getFormatTime(String format, String time) {
        try {
            return getFormatTime(format, Long.parseLong(time));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据毫秒值获取时间格式
     *
     * @param format
     * @param time
     * @return
     */
    public static String getFormatTime(String format, long time) {
        return new SimpleDateFormat(format).format(new Date(time));
    }

    /**
     * 是否包含当前时间
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isContainsTime(String startTime, String endTime) {
        try {
            if (Long.parseLong(startTime) < System.currentTimeMillis() && System.currentTimeMillis() < Long.parseLong(endTime)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 是否大于结束时间
     *
     * @param endTime
     * @param endTime
     * @return
     */
    public static boolean isAfterEndTime(String endTime) {
        if(TextUtils.isEmpty(endTime)) {
            return true;
        }

        try {
            if (System.currentTimeMillis() < Long.parseLong(endTime)) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }


    /**
     * 文本字符串转为时间类型
     * @param format
     * @param dateText
     * @return
     */
    public static Date stringSwitchDate(String format, String dateText) {
        try{
            return new SimpleDateFormat(format).parse(dateText);
        }catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }

    }



//    public static long getCurrentNetworkTime() {
//        NTPUDPClient timeClient = new NTPUDPClient();
//        InetAddress inetAddress = InetAddress.getByName("time-a.nist.gov");
//        TimeInfo timeInfo = timeClient.getTime(inetAddress);
//        //long localDeviceTime = timeInfo.getReturnTime();
//        long serverTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
//
//        Date time = new Date(serverTime);
//        Log.d(TAG, "Time from " + TIME_SERVER + ": " + time);
//        return serverTime;
//    }
}