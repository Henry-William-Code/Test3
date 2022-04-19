package com.qgnix.live.news.inter;

import android.text.TextUtils;

import com.qgnix.live.news.CacheTemp;
import com.qgnix.live.news.Chain2;
import com.qgnix.live.news.Request2;
import com.qgnix.live.news.Response2;
import com.qgnix.live.news.info.ResponseBody;

import java.io.IOException;

public class CacheInterceptor implements Interceptor2{
    @Override
    public Response2 doNext(Chain2 chain) throws IOException {
        Request2 request = chain.getRequest();

        //http 1.0 的版本只有pragma
        //Cache-Control 1.1版本有的
        //设置响应的缓存时间为60秒，即设置Cache-Control头，
        // 并移除pragma消息头，因为pragma也是控制缓存的一个消息头属性
        //关于Pragma:no-cache，跟Cache-Control: no-cache相同。
        // Pragma: no-cache兼容http 1.0 ，Cache-Control: no-cache是http 1.1提供的。
        // 因此，Pragma: no-cache可以应用到http 1.0 和http 1.1，
        // 而Cache-Control: no-cache只能应用于http 1.1.
        //一般用于访问量大的接口并且不会实时改变的接口，列表页，拼多多，60s，
        request = request.builder()
                .removeRequestHeader("pragma")
                .addRequestHeader("Cache-Control", "max-age=60")
                .build();
        String json = CacheTemp.cacheMap.get(request.getUrl());
        if(!TextUtils.isEmpty(json)){
            Response2 response2 = new Response2();
            ResponseBody body = new ResponseBody();
            body.setBodyString(json);
            response2.setBody(body);
            return response2;
        }
        //只有Get请求才能去拿缓存数据
        if(request.getRequestMethod().equals("GET")) {
            CacheTemp.isCache = false;
        }else{
            CacheTemp.isCache = false;
        }

        return chain.getResponse(request);
    }
}
