package com.qgnix.live.news;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;

public class Dispatcher2 {
    private int maxRequests = 64;//同时访问任务，不同域名最大限制64个
    private int maxRequestsPerHost = 5;//同时访问同一个域名服务器，最大限制5个
    //真正执行者是call（包工头），call然后交给拦截器去执行具体任务
    private Deque<RealCall2.AsyncCall2> runningAsyncCalls = new ArrayDeque<>();
    //等待的队列
    private Deque<RealCall2.AsyncCall2> readyAsyncCalls = new ArrayDeque<>();

    //同步方案

    //异步方案
    public void enqueue(RealCall2.AsyncCall2 call){
        //小于64个，同一域名请求小于5个
        if(runningAsyncCalls.size()<maxRequests && runningCallsForHost(call)<maxRequestsPerHost){
            runningAsyncCalls.add(call);
            //创建一个线程，从线程池找
            executorService().execute(call);
        }else{
            readyAsyncCalls.add(call);
        }

    }

    //计算当前域名有没有超过5个
    private int runningCallsForHost(RealCall2.AsyncCall2 call) {
        int count = 0;
        if(runningAsyncCalls.isEmpty()){
            return 0;
        }
        SocketRequestServer srs = new SocketRequestServer();
        for(RealCall2.AsyncCall2 runningAsyncCall:runningAsyncCalls){
            if(srs.getHost(runningAsyncCall.getRequest()).equals(call.getRequest())){
                count++;
            }
        }
        return count;
    }

    //在线程池中获取线程
    public ExecutorService executorService(){
        ExecutorService service = ThreadPoolManager.getInstance().getExecutor();
        return service;
    }



    /*
      //1个okhttp请求结束
     * 1.移除运行完成的任务
    * 2.把等待队列里面所有的任务取出来【执行】  AsyncCall2.run finished
    * @param call2
    */
    public void finished(RealCall2.AsyncCall2 call2){
        runningAsyncCalls.remove(call2);
        //如果准备中的任务为空，直接返回
        if(readyAsyncCalls.isEmpty()){
            return;
        }
        for(RealCall2.AsyncCall2 readyAsyncCall:readyAsyncCalls){
            readyAsyncCalls.remove(readyAsyncCall);
            runningAsyncCalls.add(readyAsyncCall);
            //开始执行任务
            executorService().execute(readyAsyncCall);
        }
    }
}
