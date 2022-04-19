package com.qgnix.main.glide.work;

import android.content.Context;

import com.qgnix.main.glide.Glide2;
import com.qgnix.main.glide.RequestManagerRetriever;

/**
 * 有很多参数
 * 无限次的添加
 * ....
 *
 * Build --->  结果
 */
public class GlideBuilder {

    public GlideBuilder(Context context) {}

    // 创建Glide
    public Glide2 build() {
        RequestManagerRetriever requestManagerRetriever = new RequestManagerRetriever();
        Glide2 glide = Glide2.getInstance(requestManagerRetriever); // 实例化 Glide 仅此而已
        return glide;
    }
}
