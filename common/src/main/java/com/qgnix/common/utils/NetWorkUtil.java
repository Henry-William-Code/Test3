package com.qgnix.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.support.annotation.RequiresPermission;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
/**
 * 网络相关utils
 */
public class NetWorkUtil {

    private static long lastTotalRxBytes = 0;
    private static long lastTimeStamp = 0;
    /**
     * 获取网速
     */
    public static long getNetSpeed() {
        long nowTotalRxBytes = TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
        long nowTimeStamp = System.currentTimeMillis();
        long temp = nowTimeStamp - lastTimeStamp;
        if (temp<=0){
            return 0;
        }
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / temp);//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        return speed;
    }

    /**
     * 获取网络信息
     * @param ctx 上下文
     * @return NetworkInfo
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static NetworkInfo getActiveNetworkInfo(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return null;
        return cm.getActiveNetworkInfo();
    }


    /**
     * 网络是否连接
     * @param ctx 上下文
     * @return
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isConnected(Context ctx) {
        NetworkInfo info = getActiveNetworkInfo(ctx);
        return info != null && info.isConnected();
    }

}
