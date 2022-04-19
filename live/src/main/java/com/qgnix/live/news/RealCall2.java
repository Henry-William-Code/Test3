package com.qgnix.live.news;

import com.qgnix.live.news.inter.BridgeInterceptor;
import com.qgnix.live.news.inter.CacheInterceptor;
import com.qgnix.live.news.inter.ConnectInterceptor;
import com.qgnix.live.news.inter.Interceptor2;
import com.qgnix.live.news.inter.MyWorkInterceptor;
import com.qgnix.live.news.inter.RetryAndFollowInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RealCall2 implements Call2{
    private Request2 request2;
    private OkHttpClient2 okHttpClient2;
    private boolean executed;

    public Request2 getRequest2() {
        return request2;
    }

    public RealCall2(OkHttpClient2 okHttpClient2, Request2 request2) {
        this.okHttpClient2 = okHttpClient2;
        this.request2 = request2;
    }

    public OkHttpClient2 getOkHttpClient2() {
        return okHttpClient2;
    }



    @Override
    public void enqueue(Callback2 responseCallback) {
        // 不能被重复的执行 enqueue
        synchronized (this) {
            if (executed) {
                executed = true;
                throw new IllegalStateException("不能被重复的执行 enqueue Already Executed");
            }
        }

        okHttpClient2.dispatcher().enqueue(new AsyncCall2(responseCallback));
    }

    final class AsyncCall2 implements Runnable {

        private Callback2 callback2;

        public Request2 getRequest() {
            return RealCall2.this.request2;
        }

        public AsyncCall2(Callback2 callback2) {
            this.callback2 = callback2;
        }

        @Override
        public void run() {
            // 执行耗时操作
            boolean signalledCallback = false;
            try {
                //这里就是去搞事情去了
                Response2 response = getResponseWithInterceptorChain();
                // 如果用户取消了请求，回调给用户，说失败了
                if (okHttpClient2.getCanceled()) {
                    signalledCallback = true;
                    callback2.onFailure(RealCall2.this, new IOException("用户取消了 Canceled"));
                } else {
                    signalledCallback = true;
                    callback2.onResponse(RealCall2.this, response);
                }

            }catch (IOException e) {
                // 责任的划分
                if (signalledCallback) { // 如果等于true，回调给用户了，是用户操作的时候 报错
                    System.out.println("用户再使用过程中 出错了...");
                } else {
                    callback2.onFailure(RealCall2.this, new IOException("OKHTTP getResponseWithInterceptorChain 错误... e:" + e.toString()));
                }
            } finally {
                // 回收处理
                okHttpClient2.dispatcher().finished(this);
            }
        }

        private Response2 getResponseWithInterceptorChain() throws IOException {
//            Response2 response2 = new Response2();
//            response2.setBody("流程走通....");
//            return response2;
            //核心思想，责任链模式
            List<Interceptor2> interceptor2List = new ArrayList<>();
            interceptor2List.add(new BridgeInterceptor());//桥接拦截器
            interceptor2List.add(new CacheInterceptor());//缓存拦截器
            interceptor2List.add(new MyWorkInterceptor());//自定义拦截器
            interceptor2List.add(new RetryAndFollowInterceptor());
            //重试重定向拦截器，
            interceptor2List.add(new ConnectInterceptor());//网络拦截器
            //interceptor2List.add(new ConnectInterceptorHttps());//网络拦截器
            //如果说做了缓存，直接从缓存里面拿，缓存数据会在内存卡中保存一份
            //自定义拦截器
            if(okHttpClient2.getMyInterceptors()!=null && okHttpClient2.getMyInterceptors().size()>0){
                interceptor2List.addAll(okHttpClient2.getMyInterceptors());
            }

            ChainManager chainManager = new ChainManager(interceptor2List, 0, request2, RealCall2.this);
            return chainManager.getResponse(request2); //最终返回的Response
        }
    }
}
