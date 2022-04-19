package com.qgnix.main.glide.cache.disk.my;

import com.qgnix.main.glide.resource.Value;

public interface BitmapCache {
    /**
     * 对path 加密后的字符串 sha算法加密
     *
     * @param key
     * @param value
     */
    void put(String key, Value value);
    Value get(String key);
    void remove(String key);
}
