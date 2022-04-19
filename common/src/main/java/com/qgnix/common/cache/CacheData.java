package com.qgnix.common.cache;

import android.content.Context;
import android.util.DisplayMetrics;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.utils.SpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Random;

/**
 * 缓存
 */
public class CacheData {

    private static int getTicketVersion() {
        try {
            return new JSONObject(SpUtil.getInstance().getStringValue(SpUtil.VERSION, "{}")).optInt("ticket");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 彩种key
     *
     * @return
     */
    private static String getTicketDataKey() {
        return String.format(Locale.getDefault(), "ticket_data_v%d_%d", getTicketVersion(), CommonAppConfig.getInstance().getCurLan());
    }

    /**
     * 投注信息
     *
     * @param ticketId 彩票id
     * @return
     */
    private static String getTicketOodsKey(String ticketId) {
        return String.format(Locale.getDefault(), "ticket_oods_v%d_%d_%s", getTicketVersion(), CommonAppConfig.getInstance().getCurLan(), ticketId);
    }

    /**
     * 彩票游戏
     *
     * @return
     */
    private static String getTicketAdasKey() {
        return String.format(Locale.getDefault(), "ticket_adas_v%d_%d", getTicketVersion(), CommonAppConfig.getInstance().getCurLan());
    }

    /**
     * 获取礼物列表
     *
     * @return
     */
    private static String getGiftListKey() {
        return String.format(Locale.getDefault(), "gift_list_v%d_%d", getTicketVersion(), CommonAppConfig.getInstance().getCurLan());
    }


    /**
     * 彩种
     *
     * @param jsonData
     */
    public static void setTicketData(String jsonData) {
//        SpUtil.getInstance().setStringValue(getTicketDataKey(), jsonData);
        setData(getTicketDataKey(), jsonData);
    }

    public static String getTicketData() {
//        return SpUtil.getInstance().getStringValue(getTicketDataKey());
        return getData(getTicketDataKey());
    }

    /**
     * 投注信息
     *
     * @param jsonData
     */
    public static void setOodsData(String jsonData, String ticketId) {
//        SpUtil.getInstance().setStringValue(getTicketOodsKey(ticketId), jsonData);
        setData(getTicketOodsKey(ticketId), jsonData);
    }

    public static String getOodsData(String ticketId) {
//        return SpUtil.getInstance().getStringValue(getTicketOodsKey(ticketId));
        return getData(getTicketOodsKey(ticketId));
    }

    /**
     * 彩票游戏
     *
     * @param jsonData
     */
    public static void setAdasData(String jsonData) {
//        SpUtil.getInstance().setStringValue(getTicketAdasKey(), jsonData);
        setData(getTicketAdasKey(), jsonData);
    }

    public static String getAdasData() {
//        return SpUtil.getInstance().getStringValue(getTicketAdasKey());
        return getData(getTicketAdasKey());
    }

    /**
     * 礼物列表
     *
     * @param jsonData
     */
    public static void setGiftListData(String jsonData) {
//        SpUtil.getInstance().setStringValue(getGiftListKey(), jsonData);
        setData(getGiftListKey(), jsonData);
    }

    public static String getGiftListData() {
//        return SpUtil.getInstance().getStringValue(getGiftListKey());
        return getData(getGiftListKey());
    }


    public static void setData(String key, String jsonData) {
        try {
            JSONObject obj = new JSONObject(SpUtil.getInstance().getStringValue(SpUtil.TICKET, "{}"));
            obj.put(key, jsonData);
            SpUtil.getInstance().setStringValue(SpUtil.TICKET, obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getData(String key) {
        try {
            JSONObject obj = new JSONObject(SpUtil.getInstance().getStringValue(SpUtil.TICKET, "{}"));
            return obj.optString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }







    //===========================code=================================//


    private static Random random = new Random();
    private static int current =0;
    private static int random_int =0;

    public static int getRandom(int length){


        while(current==random_int){
            random_int =  random.nextInt(length);
        }
        current = random_int;
        return current;
    }
    //随机整数
    public static Integer[] randomArr(int max,int randomSize)
    {

        try {
            if(max<randomSize){
                randomSize=max;
            }
            Random random = new Random();
            // Object[] values = new Object[randomSize];
            HashSet<Integer> hashSet = new LinkedHashSet<Integer>();

            // 生成随机数字并存入HashSet
            while(hashSet.size() < randomSize){
                hashSet.add(random.nextInt(max) + 1);
            }
            Integer[] its=new Integer[randomSize];
            Iterator<Integer> it=hashSet.iterator();
            int i=0;
            while(it.hasNext()){
                its[i]=it.next();
                i++;
            }
            return its;

        } catch (Exception e) {

        }
        return new Integer[]{} ;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static float getScreenDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * pxתdp
     * @param id
     * @param context
     * @return
     */
    public static int getDimenT(int id,Context context) {
        try {
            return (int) (id * getScreenDensity(context));
        } catch (Exception e) {
            return 0;
        }
    }
    public static void checkUpdate(final Context activity,final boolean flag) {

    }

    private  static  int[] colors = { };
    //===========================================================//
}
