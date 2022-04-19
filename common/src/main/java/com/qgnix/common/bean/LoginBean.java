package com.qgnix.common.bean;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;

public class LoginBean {


    /**
     * statusCode : 01
     * data : https://gci.ng-live.com/forwardGame.do?params=TrkpAKYFnYFQ8roawlfDjZn+b0NE2w6gUJGrnaTeW0fCPYgGX50RwkzoqERnz1jhWc4ZDA9D1epCGCYqFoRTmReVMfUsR1Ta72xFCPlRDCr67/Wg1EwdHuflQP1Zc9ADzxyeCAWQ4kPW1JkTpFSeEYrclZfhMwK+eG21F9TBtmNuaWkQPcU960/YunAIBGE8u8DAtJS3yYo9E9O20fSnUSbD/+rDt4QN87BHW5CFqtr6a9jXL7c2NrkB/rf8FIrZV73v1rSh8pAApzGz/qk9uaAzQMAEL3I5N5fEsLgrVcw=&key=43ca92affe778fd298978b5721dc8c0b
     * message : 成功
     */

    private String statusCode;
    private String data;
    private String message;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
