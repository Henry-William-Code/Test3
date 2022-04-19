package com.qgnix.main.glide.cache;

import android.os.Build;
import android.util.LruCache;

import com.qgnix.main.glide.resource.Value;

/**
 * 2、内存缓存
 */
public class MemoryCache extends LruCache<String, Value> {

    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    /**
     * bitmap大小
     * @param key
     * @param value
     * @return
     */
    @Override
    protected int sizeOf(String key, Value value) {
        int sdkInt = Build.VERSION.SDK_INT;

        if(sdkInt>= Build.VERSION_CODES.KITKAT){
            return value.getmBitmap().getAllocationByteCount();
        }
        return value.getmBitmap().getByteCount();
    }
}
