package com.qgnix.main.glide.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import com.qgnix.main.glide.request.RequestOptions;
import com.qgnix.main.glide.resource.Key;
import com.qgnix.main.glide.resource.Value;
import com.qgnix.main.glide.util.MainThreadExecutor;
import com.qgnix.main.glide.util.ThreadPoolManager;
import com.qgnix.main.glide.work.ImageViewTarget;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;

public class EngineJob implements Runnable {
    private String path;
    private ResponseListener responseListener;
    private Context context;

    private ImageViewTarget imageViewTarget;
    private RequestOptions requestOptions;
    public Value loadResource(String path, ResponseListener responseListener, Context context, ImageViewTarget imageViewTarget, RequestOptions requestOptions){
        //执行线程，线程池
        this.path = path;
        this.responseListener = responseListener;
        this.context = context;
        this.imageViewTarget = imageViewTarget;
        this.requestOptions = requestOptions;
        Uri uri = Uri.parse(path);
        if("HTTP".equalsIgnoreCase(uri.getScheme())||"HTTPS".equalsIgnoreCase(uri.getScheme())){
            ThreadPoolManager.getInstance().execute(this);
        }else{
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Value value = new Value();
            value.setmBitmap(bitmap);
            String key = new Key(path).getKey();
            responseListener.responseSuccess(key,value);
            imageViewTarget.setResource(bitmap);
        }
        return null;
    }
    @Override
    public void run() {
        //这里就去做接口请求，由子线程直接转为主线程
        final Bitmap bitmap = getImageBitmap(path);
        Executor executor = new MainThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Value value = new Value();
                value.setmBitmap(bitmap);
                String key = new Key(path).getKey();
                responseListener.responseSuccess(key,value);
                Log.w("EngineJob","1111--我是来自于服务器获取的数据");
                imageViewTarget.setResource(bitmap);
            }
        });

    }


    private Bitmap getImageBitmap(String url){
        Bitmap bitmap=null;
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL imageUrl = new URL(url);
            //要用到HttpURLConnection
            conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
             is = conn.getInputStream();
            //Bitmap工厂类，流转化成Bitmap

            bitmap = BitmapFactory.decodeStream(is);
            Matrix matrix = new Matrix();
            bitmap= Bitmap.createBitmap(bitmap,0,0,requestOptions.getImageWidth(),requestOptions.getImageHeight(),matrix,true);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null) {
                conn.disconnect();
            }
        }
        return bitmap;
    }
}
