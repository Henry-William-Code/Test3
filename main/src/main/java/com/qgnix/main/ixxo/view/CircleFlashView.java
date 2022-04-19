/**
*author:Ray
*date:2015-1-12
*TODO
**/

package com.qgnix.main.ixxo.view;

 
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;



public class CircleFlashView {
	//private static View v=null;
	/** 
     * 得到自定义的progressDialog 
     * @param context
     * @return 
     */  
	@SuppressLint("InflateParams")
    public static View createCircleFlashView(Context context ) {  
 
        	LayoutInflater inflater = LayoutInflater.from(context);  
        	View v = inflater.inflate(0, null);// 得到加载view
            AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);  
            alphaAnimation1.setDuration(300);  
            alphaAnimation1.setRepeatCount(Animation.INFINITE);  
            alphaAnimation1.setRepeatMode(Animation.REVERSE);  
            v.setAnimation(alphaAnimation1);  
            alphaAnimation1.start();  
        
        return v;  
  
    }  
}
