package com.qgnix.live.news.inter;

import com.qgnix.live.news.Chain2;
import com.qgnix.live.news.Response2;

import java.io.IOException;

public interface Interceptor2 {

    Response2 doNext(Chain2 chain2) throws IOException;
    /**
     * 1、桥接拦截器  --》 封装请求头
     * 2、缓存拦截器
     * 3、重定向或重试拦截器
     * 4、自定义的拦截器
     * 5、网络拦截器
     * 最终目标去访问网络
     */

}

