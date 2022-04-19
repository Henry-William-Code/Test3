package com.qgnix.live.utils;

import android.text.TextUtils;

import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.bean.CpTypeEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 彩票utils
 */
public class CpUtils {
    /**
     * 六合彩红色
     * #FF2600
     */
    private static final String LHC_RED = "0,1,2,7,8,12,13,18,19,23,24,29,30,34,35,40,45,46";

    private static final List<String> LHC_RED_LIST;
    /**
     * 六合彩蓝色
     * #17E2E5
     */
    private static final String LHC_BLUE = "3,4,9,10,14,15,20,25,26,31,36,37,41,42,47,48";
    private static final List<String> LHC_BLUE_LIST;
    /**
     * 六合彩绿色
     * #07BF00
     */
    public static final String LHC_GREEN = "5,6,11,16,17,21,22,27,28,32,33,38,39,43,44,49";

    static {
        LHC_RED_LIST = Arrays.asList(LHC_RED.split(","));
        LHC_BLUE_LIST = Arrays.asList(LHC_BLUE.split(","));
    }

    /**
     * 通过开奖号获取六合彩背景色
     *
     * @param no 开奖号
     * @return 颜色
     */
    private static String getLhcBgColor(String no) {
        if (LHC_RED_LIST.contains(no)) {
            return "#FF2600";
        } else if (LHC_BLUE_LIST.contains(no)) {
            return "#17E2E5";
        } else {
            return "#07BF00";
        }
    }

    /**
     * 通过开奖号获取pk10背景色
     *
     * @param no 开奖号
     * @return
     */
    private static String getPK100BgColor(String no) {
        switch (no) {
            case "1":
                return "#E6DE00";
            case "2":
                return "#0092DD";
            case "3":
                return "#4B4B4B";
            case "4":
                return "#FF7600";
            case "5":
                return "#17E2E5";
            case "6":
                return "#5234FF";
            case "7":
                return "#BFBFBF";
            case "8":
                return "#FF2600";
            case "9":
                return "#780B00";
            case "10":
            default:
                return "#07BF00";
        }

    }

    /**
     * 通过开奖号获取幸运色背景色
     *
     * @param no 开奖号
     * @return 颜色
     */
    private static String getLCBgColor(String no) {
        switch (no) {
            // 绿色
            case "1":
            case "3":
            case "7":
            case "9":
                return "#008000";
            // 红色
            case "2":
            case "4":
            case "6":
            case "8":
                return "#FF0000";
            default:
                // 0 或 5 紫色
                return "#8A2BE2";
        }
    }

    /**
     * 通过开奖号获取背景色
     *
     * @param cpType 彩票类型
     * @param no     开奖号码
     * @return 颜色
     */
    public static String getDrawNoBgColor(String cpType, String no) {
        if (CpTypeEnum.LHC.name().equals(cpType)) {
            return getLhcBgColor(no);
        } else if (CpTypeEnum.PK10.name().equals(cpType)) {
            return getPK100BgColor(no);
        } else if (CpTypeEnum.LC.name().equals(cpType)) {
            return getLCBgColor(no);
        } else {
            // 红色
            return "#FF2600";
        }
    }


    /**
     * 格式化开奖号，个位数补0
     *
     * @param no 原始开奖号
     * @return
     */
    public static String formatZeroDrawNum(String cpType, String no) {
        if (TextUtils.isEmpty(no)) {
            return "";
        }
        // 六合彩 赛车类
        if (CpTypeEnum.LHC.name().equals(cpType) || CpTypeEnum.PK10.name().equals(cpType)) {
            if (no.length() <= 1) {
                return "0" + no;
            }
        }
        return no;
    }

    /**
     * 格式化开奖号 补+ 、=
     *
     * @param nos 原始开奖号
     * @return
     */
    public static List<String> formatDrawNum(String cpType, String[] nos) {
        List<String> temp = new ArrayList<>();

        int len = nos.length;
        // 幸运28 快三
        if (CpTypeEnum.EB.name().equals(cpType)) {
            for (int i = 0; i < len; i++) {
                temp.add(nos[i]);
                if (i == len - 1) {
                    break;
                }
                if (i == len - 2) {
                    temp.add("=");
                } else {
                    temp.add("+");
                }
            }
        } else if (CpTypeEnum.KS.name().equals(cpType)) {
            int count = 0;
            String no;
            for (int i = 0; i < nos.length; i++) {
                no = nos[i];
                temp.add(no);
                count += Integer.parseInt(no);
                if (i == len - 1) {
                    break;
                }
                temp.add("+");
            }
            temp.add("=");
            // temp.add(String.valueOf(count));
            String s = String.valueOf(count);
            if (count % 2 == 0) {
                s += "  "+WordUtil.getString(R.string.even);
            } else {
                s +="  "+WordUtil.getString(R.string.odd);
            }
            if (count > 10) {
                s += "  "+WordUtil.getString(R.string.large);
            } else {
                s +="  "+ WordUtil.getString(R.string.small);
            }
            temp.add(s);
        } else {
            temp.addAll(Arrays.asList(nos));
        }

        if (CpTypeEnum.LHC.name().equals(cpType)) {
            temp.add(len - 1, "+");
        }
        return temp;
    }

    /**
     * 通过彩票类型获取rv的行数
     *
     * @param cpType 彩票类型
     * @return
     */
    public static int getSpanCount(String cpType) {
        if (CpTypeEnum.EB.name().equals(cpType)) {
            return 7;
        }
        if (CpTypeEnum.LHC.name().equals(cpType)) {
            return 4;
        } else {
            return 5;
        }
    }
}
