package com.qgnix.live.news.inter;

import android.text.TextUtils;

import com.qgnix.live.news.Chain2;
import com.qgnix.live.news.ChainManager;
import com.qgnix.live.news.OkHttpClient2;
import com.qgnix.live.news.RealCall2;
import com.qgnix.live.news.Request2;
import com.qgnix.live.news.Response2;

import java.io.IOException;

public class RetryAndFollowInterceptor implements Interceptor2 {

    @Override
    public Response2 doNext(Chain2 chain2) throws IOException {
        //1、要从网络拦截器下手，去拿Response
        System.out.println("我是重试重定向拦截器，执行了");


        ChainManager chainManager = (ChainManager) chain2;

        RealCall2 realCall2 = chainManager.getCall();
        OkHttpClient2 client2 = realCall2.getOkHttpClient2();

        IOException ioException = null;

        // 重试次数
        if (client2.getRecount() != 0) {
            for (int i = 0; i < client2.getRecount(); i++) { // 3
                try {
                    // Log.d(TAG, "我是重试拦截器，我要Return Response2了");
                    System.out.println("我是重试拦截器，我要Return Response2了");
                    // 如果没有异常，循环就结束了
                    Response2 response2 = chain2.getResponse(chainManager.getRequest()); // 执行下一个拦截器（任务节点）
                    Request2 request2 = chainManager.getRequest();//这里是拿到网络拦截器的网络返回的结果
                    //如果说RedictUrl有数据，就代表是重定向
                    if(!TextUtils.isEmpty(request2.getRedictUrl())){
                        //就把url替换的
                        request2.setUrl(request2.getRedictUrl());
                        return chain2.getResponse(request2);
                    }
                    return response2;
                } catch (IOException e) {
                    // e.printStackTrace();
                    ioException = e;
                }

            }
        }
        // return null;
        throw ioException;
    }
}
