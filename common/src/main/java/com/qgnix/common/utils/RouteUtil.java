package com.qgnix.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.qgnix.common.Constants;

/**
 * Created by cxf on 2019/2/25.
 */

public class RouteUtil {
    //Intent隐式启动 action
    public static final String PATH_LOGIN_INVALID = "/main/LoginInvalidActivity";
    public static final String PATH_COIN = "/main/MyCoinActivity";
    //充值
    public static final String RECHARGE_PATH = "/main/RechargeActivity";
    public static final String LIST = "/main/MainListActivity";
    public static final String BROWSER_ACTIVITY = "/main/BrowserActivity";

    /**
     * 登录过期
     */
    public static void forwardLoginInvalid(String tip) {
        ARouter.getInstance().build(PATH_LOGIN_INVALID)
                .withString(Constants.TIP, tip)
                .navigation();
    }

    /**
     * 跳转到充值页面
     */
    public static void forwardMyCoin(Context context) {
        ARouter.getInstance().build(PATH_COIN).navigation();
    }

    /**
     * 充值
     */
    public static void forwardRecharge() {
        ARouter.getInstance().build(RECHARGE_PATH).navigation();
    }

    public static void toGame(Fragment fragment, String url, String platType, String coin, String thridCoin) {
        Postcard postcard = ARouter.getInstance().build(BROWSER_ACTIVITY)
                .withString(Constants.PARAM_URL, url)
                .withString(Constants.PARAM_TITLE, "title")
                .withInt(Constants.PARAM_MODE, 0)
                .withString(Constants.PARAM_PLAT, platType)
                .withString(Constants.PARAM_BALANCE, coin)
                .withString(Constants.PARAM_TRANSFER, thridCoin);
        if (fragment == null) {
            postcard.navigation();
        } else {
            LogisticsCenter.completion(postcard);
            Intent intent = new Intent(fragment.getActivity(), postcard.getDestination());
            intent.putExtras(postcard.getExtras());
            fragment.startActivityForResult(intent, 100);
        }
    }

}
