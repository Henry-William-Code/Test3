package com.qgnix.live.news;

import com.qgnix.live.news.inter.Interceptor2;

import java.util.ArrayList;
import java.util.List;

public class OkHttpClient2 {
    boolean isCanceled;

    Dispatcher2 dispatcher;

    List<Interceptor2> myInterceptors=new ArrayList<>();

    //重试次数
    int recount;

    public boolean getCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public int getRecount() {
        return recount;
    }

    public void setRecount(int recount) {
        this.recount = recount;
    }

    public List<Interceptor2> getMyInterceptors() {
        return myInterceptors;
    }

    public OkHttpClient2(Builder builder) {
        dispatcher = builder.dispatcher;
        isCanceled = builder.isCanceled;
        recount = builder.recount;
        myInterceptors = builder.myInterceptors;
    }



    public final static class Builder{

        Dispatcher2 dispatcher;
        Cache2 cache;
        boolean isCanceled;
        List<Interceptor2> myInterceptors=new ArrayList<>();
        int recount = 3; // 重试次数

        public Builder() {
            dispatcher = new Dispatcher2();
        }

        public Builder dispatcher(Dispatcher2 dispatcher) {
            this.dispatcher = dispatcher;
            return this;
        }

        /** Sets the response cache to be used to read and write cached responses. */
        public Builder cache(Cache2 cache) {
            this.cache = cache;
            return this;
        }

        // 用户取消请求
        public Builder canceled() {
            isCanceled = true;
            return this;
        }

        public Builder setReCount(int recount) {
            this.recount = recount;
            return this;
        }

        public Builder addInterceptor(Interceptor2 interceptor2){
            myInterceptors.add(interceptor2);
            return this;
        }

        public OkHttpClient2 build() {
            return new OkHttpClient2(this);
        }
    }

    public Call2 newCall(Request2 request2) {
        // RealCall
        return new RealCall2(this, request2);
    }

    public Dispatcher2 dispatcher() {
        return dispatcher;
    }

}
