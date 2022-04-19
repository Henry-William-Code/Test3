package com.qgnix.main.glide.resource;

import android.graphics.Bitmap;

public class Value {
    //https://img1.baidu.com/it/u=3694053003,1912698683&fm=26&fmt=auto&gp=0.jpg
    private String key;//Lrucache,它无法识别特殊字符串,通过算将路径转成key
    private Bitmap mBitmap;
    private ValueCallback callback; // 监听回调

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public ValueCallback getCallback() {
        return callback;
    }

    public void setCallback(ValueCallback callback) {
        this.callback = callback;
    }

    /**
     * 回收  回调 释放 Value本身   【通过 活动缓存 把当前Value 移动到  LRU内存缓存】
     */
    public void recycle() {
        if(callback != null) {
            // 证明我们的Value没有使用（管理回收）
            // 告诉外界，回调接口
            callback.valueNonUseListener(key, this); // 活动缓存管理监听【Value不在使用了】
        }
    }
}
