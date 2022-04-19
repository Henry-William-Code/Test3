package com.qgnix.common.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.hjq.language.MultiLanguages;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.R;
import com.qgnix.common.bean.LanBean;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.OkHttp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Request;

/**
 * 切换语言帮助类
 *
 * @author czq
 * @date 2020-08-30
 */
public class LanSwitchUtil {
    public static final String TGA = "===SwitchLanguageUtils==";

    /**
     * 语言参数key
     */
    public static final String LAN_KEY = "lan";

    private static Application mContext;


    /**
     * 初始化
     *
     * @param application
     */
    public static void init(Application application) {
        mContext = application;
        MultiLanguages.init(application);
    }

    // 语言标识转语言编码
    public static String getLanStr() {
        return getLanBean(getCurLanCode()).getLan();
    }

    // 保存语言到本地
    public static void setLanToLocale(int lan) {
        setLanToLocale(getLanBean(lan));
    }

    // 保存语言到本地
    public static void setLanToLocale(LanBean lanBean) {
        // 当前语言保存本地
        CommonAppConfig.getInstance().setCurLan(lanBean.getNum());
        // 设置app语言
        MultiLanguages.setAppLanguage(mContext, LanSwitchUtil.getCurLan());
    }

    /**
     * 是否中文
     *
     * @return true 中文
     */
    public static boolean isChina() {
        Locale locale = MultiLanguages.getAppLanguage();
        return MultiLanguages.equalsCountry(locale, Locale.CHINA);
    }

    // 切换语言
    public static void changeLan(final Activity act, final LanBean lanBean, final Class<?> cls, boolean sIsLocal) {
        if (sIsLocal) {
            changeLocalLan(act, lanBean, cls);
            return;
        }
        // 设置数据库语言
        Map<String, String> map = new HashMap<>();
        map.put("uid", CommonAppConfig.getInstance().getUid());
        map.put("lan", String.valueOf(lanBean.getNum()));
        OkHttp.postAsync(CommonAppConfig.getCurHost() + "/api/public/?service=User.upUserlan", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                changeLocalLan(act, lanBean, cls);
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                L.e(TGA, "切换语言失败", e);
            }
        });
    }

    // 切换本地语言
    public static void changeLocalLan(final Activity act, LanBean lanBean, Class<?> cls) {
        // 当前语言保存本地
        setLanToLocale(lanBean);
        // 重新获取config数据
        CommonHttpUtil.getConfig(null);
        //重启应用
        restart(act, cls);

    }

    // 切换成功后重启
    private static void restart(final Activity act, final Class<?> cls) {
        // 1.使用recreate来重启Activity的效果差，有闪屏的缺陷
        //recreate();

        // 2.使用常规startActivity来重启Activity，有从左向右的切换动画，稍微比recreate的效果好一点，但是这种并不是最佳的效果
        //startActivity(new Intent(this, LanguageActivity.class));
        //finish();

        // 3.我们可以充分运用 Activity 跳转动画，在跳转的时候设置一个渐变的效果，相比前面的两种带来的体验是最佳的
        Intent intent = new Intent(act, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
        act.finish();
    }

    /**
     * 获取支持的语言
     *
     * @return
     */
    public static List<LanBean> getLan() {
        List<LanBean> beans = new ArrayList<>();
        // 非印度
     /*   if (!"IN".equalsIgnoreCase(CommonAppConfig.getInstance().getCountry())) {
            beans.add(new LanBean(0, "zh-cn", "中文", Locale.CHINA, R.mipmap.ic_china, 0 == getCurLanCode()));
        }*/
        beans.add(new LanBean(1, "en", "English", Locale.ENGLISH, R.mipmap.ic_english, 1 == getCurLanCode()));
        beans.add(new LanBean(2, "ms-my", "Bahasa Melayu", new Locale("ms", "MY"), R.mipmap.ic_malaysia, 2 == getCurLanCode()));
        beans.add(new LanBean(3, "hi-in", "हिंदी", new Locale("hi", "IN"), R.mipmap.ic_india, 3 == getCurLanCode()));
        return beans;
    }


    /**
     * 通过lan code 获取 Locale
     *
     * @param num
     * @return
     */
    public static LanBean getLanBean(int num) {
        List<LanBean> lanBeans = getLan();
        for (LanBean lanBean : lanBeans) {
            if (num == lanBean.getNum()) {
                return lanBean;
            }
        }
        // 默认英语
        return lanBeans.get(1);
    }

    /**
     * 获取默认语言code
     *
     * @return
     */
    public static int getCurLanCode() {
        return CommonAppConfig.getInstance().getCurLan();
    }

    /**
     * 获取当前语言
     *
     * @return
     */
    public static Locale getCurLan() {
        return getLanBean(getCurLanCode()).getLocale();
    }
}
