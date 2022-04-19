package com.qgnix.phonelive;

import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.faceunity.FURenderer;
import com.hjq.language.MultiLanguages;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;
import com.qgnix.common.CommonAppContext;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.LanSwitchUtil;
import com.qgnix.phonelive.activity.CrashHandler;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.rtmp.TXLiveBase;

import java.util.Map;


/**
 * Created by cxf on 2017/8/3.
 */
public class AppContext extends CommonAppContext {

    public static AppContext sInstance;

    private static final String AF_DEV_KEY = "6E8suMFSBQPptBjQDBv83Q";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        // 默认语言处理
        MultiLanguages.setAppLanguage(this, LanSwitchUtil.getCurLan());

        CrashReport.initCrashReport(getApplicationContext(), "8698fde209", BuildConfig.ENV_TYPE == 2);

        //腾讯云鉴权url
        //String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/8e650851ffee02085f98f0ab831aae19/TXLiveSDK.licence";

        //String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/5f6b37a457161dd1016611d26da2272a/TXLiveSDK.licence";
        //String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/3d7da7ab7229f26db053afbb48b71ba0/TXLiveSDK.licence";
        String ugcLicenceUrl = "https://license.vod2.myqcloud.com/license/v2/1257433614_1/v_cube.license";
        //腾讯云鉴权key
        // String ugcKey = "3acb0f1c09a36b75c8cd39537ed0d855";

        //String ugcKey = "dd54c6bf5b901e1d2dbdb75e70c7cd32";
        //String ugcKey = "6389d1d2f20d78e295b85c28e3785859";
        String ugcKey = "f1c2dbacb4be627eea1c4c76b7e0a8df";
        TXLiveBase.getInstance().setLicence(this, ugcLicenceUrl, ugcKey);
        L.setDeBug(BuildConfig.DEBUG);
        //初始化极光推送
        //初始化极光IM
        // ImMessageUtil.getInstance().init();

        //初始化 ARouter
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
        //美颜
        FURenderer.initFURenderer(this);

        Hawk.init(this)
                .setEncryption(new NoEncryption())
                .build();
        initAppsFlyer();
    }

    //集成appsFlyer
    private void initAppsFlyer() {
        AppsFlyerConversionListener conversionDataListener =
                new AppsFlyerConversionListener() {
                    @Override
                    public void onConversionDataSuccess(Map<String, Object> map) {

                    }

                    @Override
                    public void onConversionDataFail(String s) {

                    }

                    @Override
                    public void onAppOpenAttribution(Map<String, String> map) {
                    }
                    @Override
                    public void onAttributionFailure(String s) {
                    }
                };
        AppsFlyerLib.getInstance().init(AF_DEV_KEY,conversionDataListener,getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(this);
    }

}
