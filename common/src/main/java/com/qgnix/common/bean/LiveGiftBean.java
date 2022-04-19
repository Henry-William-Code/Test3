package com.qgnix.common.bean;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;

/**
 * Created by cxf on 2018/10/12.
 */

public class LiveGiftBean {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_DELUXE = 1;
    public static final int MARK_NORMAL = 0;
    public static final int MARK_HOT = 1;
    public static final int MARK_GUARD = 2;
    public static final int MARK_LUCK = 3;

    private int id;
    private int type;//0 普通礼物 1是豪华礼物
    private int mark;// 0 普通  1热门  2守护 3幸运
    private String name;
    private String price;
    private String icon;
    private boolean checked;
    private int page;
    private View mView;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    @JSONField(name = "giftname")
    public String getName() {
        return name;
    }

    @JSONField(name = "giftname")
    public void setName(String name) {
        this.name = name;
    }

    @JSONField(name = "needcoin")
    public String getPrice() {
        return price;
    }

    @JSONField(name = "needcoin")
    public void setPrice(String price) {
        this.price = price;
    }

    @JSONField(name = "gifticon")
    public String getIcon() {
        return icon;
    }

    @JSONField(name = "gifticon")
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
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
