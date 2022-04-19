package com.qgnix.main.glide;

import android.app.Activity;
import android.content.Context;




import com.qgnix.main.glide.work.GlideBuilder;

public class Glide2 {
    /**
     * 步骤
     * 1、先画身体
     * 2、再找流程
     * 3、把原理搞清楚
     * 4、设计模式（建造者模式，工厂模式、门面模式）
     * 5、辅助类
     * 6、只写核心
     *
     * 细节
     *  生命周期的实现
     *  1、空fragment
     *  2、绑定生命周期，接口，
     *  3、适配androidX和FragmentActivity
     *
     */
    private static  volatile Glide2 glide;
    private final RequestManagerRetriever retriever;

    private Glide2(RequestManagerRetriever retriever) {
        this.retriever = retriever;
    }



    public static  Glide2 getInstance(RequestManagerRetriever retriever){
        if(glide==null){
            synchronized (Glide2.class){
                if(glide==null){
                    glide=new Glide2(retriever);
                }
            }
        }
        return glide;
    }

    public static RequestManager with(Activity activity){
        return getRetriever(activity).get(activity);
    }



    private static RequestManagerRetriever getRetriever( Context context) {
        return Glide2.get(context).getRequestManagerRetriever();
    }


    // Glide 是 new 出来的 -- > 转变
    public static Glide2 get(Context context) {
        return new GlideBuilder(context).build();
    }



    public RequestManagerRetriever getRequestManagerRetriever() {
        return retriever;
    }

}
