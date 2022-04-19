package com.qgnix.common.utils;

import android.text.TextUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Created by cxf on 2018/9/28.
 */

public class StringUtil {
    private static final DecimalFormat sDecimalFormat;
    private static final DecimalFormat sDecimalFormat2;
    private static final Pattern sIntPattern;
    private static final StringBuilder sStringBuilder;


    static {
        sDecimalFormat = new DecimalFormat("#.#");
        sDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        sDecimalFormat2 = new DecimalFormat("#.##");
        sDecimalFormat2.setRoundingMode(RoundingMode.DOWN);
        sIntPattern = Pattern.compile("^[-\\+]?[\\d]*$");
        sStringBuilder = new StringBuilder();
    }

    public static String format(double value) {
        return sDecimalFormat.format(value);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d) + "W";
    }


    /**
     * 把数字转化成多少万
     */
    public static String toWan2(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan3(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat2.format(num / 10000d) + "w";
    }

//    /**
//     * 判断字符串中是否包含中文
//     */
//    public static boolean isContainChinese(String str) {
//        Matcher m = sPattern.matcher(str);
//        if (m.find()) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 判断一个字符串是否是数字
     */
    public static boolean isInt(String str) {
        return sIntPattern.matcher(str).matches();
    }


    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        String s = "";
        if (hours > 0) {
            if (hours < 10) {
                s += "0" + hours + ":";
            } else {
                s += hours + ":";
            }
        }
        if (minutes > 0) {
            if (minutes < 10) {
                s += "0" + minutes + ":";
            } else {
                s += minutes + ":";
            }
        } else {
            s += "00" + ":";
        }
        if (seconds > 0) {
            if (seconds < 10) {
                s += "0" + seconds;
            } else {
                s += seconds;
            }
        } else {
            s += "00";
        }
        return s;
    }

    /**
     * 多个字符串拼接
     */
    public static String contact(String... args) {
        sStringBuilder.delete(0, sStringBuilder.length());
        for (String s : args) {
            sStringBuilder.append(s);
        }
        return sStringBuilder.toString();
    }

    /**
     * 把秒数转换为%d:%02d:%02d 格式
     *
     * @param timeSec
     * @return String
     */
    public static String stringForTime(Long timeSec) {
        Long totalSeconds = timeSec;
        Long seconds = totalSeconds % 60;
        Long minutes = totalSeconds / 60 % 60;
        Long hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%d:%02d:%02d", Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)) :
                String.format("%02d:%02d", Long.valueOf(minutes), Long.valueOf(seconds));
    }


    /**
     * 字符串非空 非"" 判断
     *
     * @param string
     * @return boolean
     */
    public static boolean isNotBlank(CharSequence string) {
        if (string == null) {
            return false;
        }
        for (int i = 0, n = string.length(); i < n; i++) {
            if (!Character.isWhitespace(string.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 去掉字符source中前后的flag字符
     *
     * @param source 原数据
     * @param flag   需去掉的字符
     * @return
     */
    public static String myTrim(String source, char flag) {
        if (TextUtils.isEmpty(source)) {
            return "";
        }
        int start = 0, end = source.length() - 1;

        while (start <= end && source.charAt(start) == flag) {
            start++;
        }
        while (start <= end && source.charAt(end) == flag) {
            end--;
        }
        return source.substring(start, end + 1);
    }
}
