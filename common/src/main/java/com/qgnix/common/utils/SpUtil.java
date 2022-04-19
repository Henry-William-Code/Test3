package com.qgnix.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.qgnix.common.BuildConfig;
import com.qgnix.common.CommonAppContext;

import java.util.Map;

/**
 * Created by cxf on 2018/9/17.
 * SharedPreferences 封装
 */

public class SpUtil {

    private static SpUtil sInstance;
    private SharedPreferences mSharedPreferences;

    public static final String UID = "uid";
    public static final String TOKEN = "token";
    public static final String USER_INFO = "userInfo";
    public static final String CONFIG = "config";
    public static final String CDKEY = "cd";
    public static final String VERSION = "version";
    public static final String SLIDE = "slide";
    public static final String TICKET = "ticket";
    public static final String CATEGORY_V2 = "category_v2";
    public static final String HAS_SYSTEM_MSG = "hasSystemMsg";
    public static final String LOCATION_LNG = "locationLng";
    public static final String LOCATION_LAT = "locationLat";
    public static final String LOCATION_PROVINCE = "locationProvince";
    public static final String LOCATION_CITY = "locationCity";
    public static final String LOCATION_DISTRICT = "locationDistrict";
    public static final String LAST_SHOW_ACTIVITY_TIME = "last_show_activity_time";
    public static final String GOOGLE_STORE = "googleStore";
    public static final String GOOGLE_VERSION = "googleVersion";
    public static final String GOOGLE_THIRD_PLAY = "googleThirdPlay";
    public static final String TIE_CARD_REWARD = "tie_card_reward";

    /**********czq 新增*******************/
    // 语言当前选中
    public static final String CUR_LAN = "cur_lan";
    //  绑定银行卡状态
    public static final String BANK_STATUS = "bank_status";
    // 账户国家码
    public static final String ACCOUNT_COUNTRY = "account_country";
    // 是否取消了当前版本升级
    public static final String IS_CANCEL_VERSION = "cancel_version_";
    // 请求host
    public static final String SERVER_HOST = "SERVER_HOST";

    private SpUtil() {
        mSharedPreferences = CommonAppContext.sInstance.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
    }

    public static SpUtil getInstance() {
        if (sInstance == null) {
            synchronized (SpUtil.class) {
                if (sInstance == null) {
                    sInstance = new SpUtil();
                }
            }
        }
        return sInstance;
    }

    /**
     * 保存一个字符串
     */
    public void setStringValue(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 获取一个字符串
     */
    public String getStringValue(String key) {
        return getStringValue(key, "");
    }

    /**
     * 获取一个字符串
     */
    public String getStringValue(String key, String defVal) {
        return mSharedPreferences.getString(key, defVal);
    }

    /**
     * 保存多个字符串
     */
    public void setMultiStringValue(Map<String, String> pairs) {
        if (pairs == null || pairs.size() == 0) {
            return;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (Map.Entry<String, String> entry : pairs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                editor.putString(key, value);
            }
        }
        editor.apply();
    }
    /**
     * 保存多个字符串
     */
    public void setGoogleStore(String googleStore) {

        SharedPreferences.Editor editor = mSharedPreferences.edit();

            if (!TextUtils.isEmpty(googleStore)) {
                editor.putString(GOOGLE_STORE, googleStore);
            }

        editor.apply();
    }

    /**
     * 保存单个字符串
     */
    public void setGoogleVersion(String version) {

        SharedPreferences.Editor editor = mSharedPreferences.edit();

            if (!TextUtils.isEmpty(version)) {
                editor.putString(GOOGLE_VERSION, version);
            }

        editor.apply();
    }
    /**
     * 保存1个字符串
     */
    public void setGoogleGame(String googleStore) {

        SharedPreferences.Editor editor = mSharedPreferences.edit();

            if (!TextUtils.isEmpty(googleStore)) {
                editor.putString(GOOGLE_THIRD_PLAY, googleStore);
            }

        editor.apply();
    }

    /**
     * 保存多个字符串
     */
    public boolean getGoogleStore() {
        return "1".equals(mSharedPreferences.getString(SpUtil.GOOGLE_STORE,"")) && "1".equals(BuildConfig.googleStore) || ("1".equals(BuildConfig.googleStore) && VersionUtil.compareVersion(mSharedPreferences.getString(SpUtil.GOOGLE_VERSION,"1.0"),VersionUtil.getVersion())<=0);
    }
    /**
     * 保存多个字符串
     */
    public boolean getGoogleThirdGame() {
        return "1".equals(mSharedPreferences.getString(SpUtil.GOOGLE_THIRD_PLAY,"")) && "1".equals(BuildConfig.googleStore) || ("1".equals(BuildConfig.googleStore) && VersionUtil.compareVersion(mSharedPreferences.getString(SpUtil.GOOGLE_VERSION,"1.0"),VersionUtil.getVersion())<=0);
    }


    /**
     * 获取多个字符串
     */
    public String[] getMultiStringValue(String... keys) {
        if (keys == null || keys.length == 0) {
            return null;
        }
        int length = keys.length;
        String[] result = new String[length];
        for (int i = 0; i < length; i++) {
            String temp = "";
            if (!TextUtils.isEmpty(keys[i])) {
                temp = mSharedPreferences.getString(keys[i], "");
            }
            result[i] = temp;
        }
        return result;
    }


    /**
     * 保存一个布尔值
     */
    public void setBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 获取一个布尔值
     */
    public boolean getBooleanValue(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    /**
     * 保存多个布尔值
     */
    public void setMultiBooleanValue(Map<String, Boolean> pairs) {
        if (pairs == null || pairs.size() == 0) {
            return;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (Map.Entry<String, Boolean> entry : pairs.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            if (!TextUtils.isEmpty(key)) {
                editor.putBoolean(key, value);
            }
        }
        editor.apply();
    }

    /**
     * 获取多个布尔值
     */
    public boolean[] getMultiBooleanValue(String[] keys) {
        if (keys == null || keys.length == 0) {
            return null;
        }
        int length = keys.length;
        boolean[] result = new boolean[length];
        for (int i = 0; i < length; i++) {
            boolean temp = false;
            if (!TextUtils.isEmpty(keys[i])) {
                temp = mSharedPreferences.getBoolean(keys[i], false);
            }
            result[i] = temp;
        }
        return result;
    }


    /**
     * 保存一个整形
     */
    public void setIntValue(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 保存一个整形
     */
    public void setLongtValue(String key, long value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * 获取一个Long
     */
    public long getLongValue(String key, long defVal) {
        return mSharedPreferences.getLong(key, defVal);
    }


    /**
     * 获取一个整形
     */
    public int getIntValue(String key, int defVal) {
        return mSharedPreferences.getInt(key, defVal);
    }

    public void removeValue(String... keys) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        editor.apply();
    }

    /**
     * 清空本地数据
     */
    public void clear() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
