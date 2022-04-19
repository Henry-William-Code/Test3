package com.qgnix.live.news;

import com.qgnix.live.news.inter.Interceptor2;

import java.io.IOException;
import java.util.List;

public class ChainManager implements Chain2{

    private final List<Interceptor2> interceptors;
    private int index;
    private final Request2 request;
    private final RealCall2 call;

    public List<Interceptor2> getInterceptors() {
        return interceptors;
    }

    public int getIndex() {
        return index;
    }

    public RealCall2 getCall() {
        return call;
    }

    public ChainManager(List<Interceptor2> interceptors, int index, Request2 request, RealCall2 call) {
        this.interceptors = interceptors;
        this.index = index;
        this.request = request;
        this.call = call;
    }

    @Override
    public Request2 getRequest() {
        return request;
    }

    //okhttp的最终目的是不是就是拿Response
    @Override
    public Response2 getResponse(Request2 request2) throws IOException {
        //判断index++计数，不能大于size  也不能等于size
        if(index>=interceptors.size()){
            throw  new AssertionError();
        }

        if(interceptors.isEmpty()){
            throw new IOException("interceptors is empty");
        }
        //取出第一个 拦截器
        Interceptor2 interceptor2 = interceptors.get(index); // 0，1，2
        ChainManager manager = new ChainManager(interceptors,index+1,request,call);
        Response2 response2 = interceptor2.doNext(manager);
        return response2;
    }
}
