package com.qgnix.main.ixxo.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;



import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class UtilTools {
	
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
	public static int getRandomColor(){
		return colors[UtilTools.getRandom(colors.length-1)];
	}

}
