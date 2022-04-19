package com.qgnix.main.utils;

import android.content.Context;

import com.appsflyer.AppsFlyerLib;
import com.qgnix.common.CommonAppConfig;

import java.util.HashMap;
import java.util.Map;

public class ReportEvent {
    public static void report(Context context,String eventStr,Map customDataMap){
        if(CommonAppConfig.getInstance().getSwitchStatu()) {
            AppsFlyerLib.getInstance().trackEvent(context, eventStr, customDataMap);
        }
    }
}
