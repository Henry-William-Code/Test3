package com.qgnix.main.glide.engine;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.qgnix.main.glide.binding.inter.LifecycleListener;
import com.qgnix.main.glide.cache.ActiveCache;
import com.qgnix.main.glide.cache.MemoryCache;
import com.qgnix.main.glide.cache.disk.my.DiskBitmapCache;
import com.qgnix.main.glide.request.RequestOptions;
import com.qgnix.main.glide.resource.Key;
import com.qgnix.main.glide.resource.Value;
import com.qgnix.main.glide.resource.ValueCallback;
import com.qgnix.main.glide.util.Tool;
import com.qgnix.main.glide.work.ImageViewTarget;

public class Engine implements ValueCallback ,ResponseListener, LifecycleListener {
    private static final String TAG = "Engine";
    /**
     * 1、读缓存（1级，2级，3级）,读取逻辑
     * 2、分发给EngineJob
     */
    private Context glideContext;
    private String path;
    private String key;
    private ActiveCache activeCache; // 活动缓存
    private MemoryCache memoryCache; // 内存缓存
    private DiskBitmapCache diskLruCache; // 磁盘缓存
    private final int MEMORY_MAX_SIZE = 1024*1024*120;
    private static Engine engine;
    private int targetResId;

    public static Engine getInstance(){
        if(engine==null){
            engine = new Engine();
        }
        return engine;
    }

    private Engine(){
        if (activeCache == null) {
            activeCache = new ActiveCache(this); // 回调给外界，Value资源不再使用了 设置监听
        }
        if (memoryCache == null) {
            memoryCache = new MemoryCache(MEMORY_MAX_SIZE); // 内存缓存
        }
    }

    //磁盘缓存
    public void load(String path, Context context){
        this.path = path;
        this.glideContext = context;
        this.key = new Key(path).getKey();
        diskLruCache = DiskBitmapCache.getInstance(glideContext);
    }

    //磁盘缓存
    public void load(Uri uri, Context context){
        this.path = uri.getPath();
        this.glideContext = context;
        this.key = new Key(path).getKey();
        diskLruCache = DiskBitmapCache.getInstance(glideContext);
    }


    //磁盘缓存
    public void load(int resId, Context context){
        this.targetResId = resId;
    }


    //读取缓存方法
    public void into(final ImageViewTarget imageViewTarget, final RequestOptions requestOptions, int resId){
        Tool.assertMainThread();
        if(targetResId!=0){
            imageViewTarget.setImageResource(resId);
            targetResId = 0;
            return;
        }
        imageViewTarget.setImageResource(resId);
        /*imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //如果本地缓存有，就直接渲染
                Value value = cacheAction(imageView,requestOptions);//
                if(value!=null){
                    imageView.setImageBitmap(value.getmBitmap());
                }
            }
        },3000);*/
        Value value = cacheAction(imageViewTarget,requestOptions);//
        if(value!=null){
            imageViewTarget.setResource(value.getmBitmap());
        }

    }

    private Value cacheAction(ImageViewTarget imageViewTarget,RequestOptions requestOptions){
        //判断读取逻辑,1级缓存-活动缓存有，没有就找内存缓存，没有就去找磁盘缓存，找服务器
        Value value = activeCache.get(key);
        if(value!=null){
            //发现活动缓存有，就可以在这里设置进去
            memoryCache.put(key,value);
            activeCache.deleteActive(key);
            Log.w(TAG,"1111--我是来自于活动缓存的数据");

            return value;
        }
         value = memoryCache.get(key);
        if(value!=null){
            // 移动操作 剪切（内存--->活动）
            activeCache.put(key, value); // 把内存缓存中的元素，加入到活动缓存中...
            memoryCache.remove(key); // 移除内存缓存
            Log.w(TAG,"1111--我是来自于内存缓存的数据");
            return value;
        }
        value = diskLruCache.get(key);
        if(value!=null){
            Log.w(TAG,"1111--我是来自于磁盘缓存的数据");
            activeCache.put(key, value); // 把内存缓存中的元素，加入到活动缓存中...
            memoryCache.remove(key); // 移除内存缓存
            return value;
        }

        //服务器去找
        // TODO 第四步：真正去加载外部资源 数据模型LoadDat 去加载    HTTP / 本地io
        value = new EngineJob().loadResource(path, this, glideContext,imageViewTarget,requestOptions);
        if (value != null) {

            return value;
        }
        return null;  // 有意这样做的，为了后需好判断
    }


    //写缓存


    @Override
    public void valueNonUseListener(String key, Value value) {
// 加入到 内存缓存
        if (key != null && value != null) {
            memoryCache.put(key, value);
        }
    }

    @Override
    public void responseSuccess(String key, Value value) {
        Log.d(TAG, "saveCache: >>>>>>>>>>>>>>>>>>>>>>>>>> 加载外置资源成功后 ，保存到缓存中， key:" + key + " value:" + value);
        value.setKey(key);
        if (diskLruCache != null) {
            activeCache.put(key, value);  //这个无所谓 自由控制了
            diskLruCache.put(key, value); // 保存到磁盘缓存中....
        }
    }

    @Override
    public void responseException(Exception e) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "glide: Glide生命周期之 进行释放操作 缓存策略释放操作等, 释放 活动缓存的所有资源 >>>>>> ....");

        // 活动缓存.释放操作();
        if (activeCache != null) {
            activeCache.recycleActive();  // 活动缓存 给释放掉
        }
    }
}
