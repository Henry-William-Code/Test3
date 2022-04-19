package com.qgnix.live.news.inter;

import android.util.Log;

import com.qgnix.live.news.Chain2;
import com.qgnix.live.news.Request2;
import com.qgnix.live.news.Response2;
import com.qgnix.live.news.info.ResponseBody;

import java.io.IOException;

public class MyWorkInterceptor implements Interceptor2 {
    private final String TAG = MyWorkInterceptor.class.getSimpleName();

    @Override
    public Response2 doNext(Chain2 chain2) throws IOException {
        Request2 request2 = chain2.getRequest();
       if(request2.getUrl().contains("baidu.com")){
           Response2 response2 = new Response2();
           response2.setBody(new ResponseBody().setBodyString(""));
           Log.e(TAG,"我是百度，不可正常通行");
           return response2;
       }else{
           Log.e(TAG,"我不是百度，可正常通行");
       }
        return chain2.getResponse(request2);
    }
}
