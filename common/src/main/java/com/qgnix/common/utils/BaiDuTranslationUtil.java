package com.qgnix.common.utils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.qgnix.common.bean.TranslateBean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

/**
 * 百度翻译帮助类
 */
public class BaiDuTranslationUtil {

    /**
     * 翻译Url
     */
    // private static final String BASE_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";
    public static final String HOST = "https://fanyi-api.baidu.com";
    private static final String BASE_URL = "/api/trans/vip/translate";
    /**
     * appId
     */
    private static final String APP_ID = "20201025000598359";
    /**
     * 密钥
     */
    private static final String APP_SECRET = "2rKbAAhFt3fBsZlW1sqf";


    /**
     * 获取签名信息
     * appid+q+salt+密钥
     *
     * @param q    待翻译字符串
     * @param salt 随机数
     * @return 签名信息
     */
    private static String getSign(String q, String salt) throws UnsupportedEncodingException {
        String md5Str = MD5Util.getMD5(APP_ID + q + salt + APP_SECRET);
        return URLEncoder.encode(md5Str, "UTF-8");
    }

    /**
     * 获取翻译结果
     *
     * @param q 待翻译字符串
     * @return 翻译后的结果
     */
    public static void getTranslationData(String translateHost, String q, String to, Callback<TranslateBean> callback) throws UnsupportedEncodingException {
        String salt = String.valueOf(new Random(10).nextInt());
        String sign = getSign(q, salt);
        if (to.contains("-")) {
            to = to.split("-")[0];
        }
        OkGo.<TranslateBean>get(translateHost+BASE_URL)
                .params("q", q)
                .params("from", "auto")
                .params("to", to)
                .params("appid", APP_ID)
                .params("salt", salt)
                .params("sign", sign)
                .execute(callback);
    }


}
