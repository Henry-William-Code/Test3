package com.qgnix.common.http;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.CommonAppContext;
import com.qgnix.common.utils.LanSwitchUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by cxf on 2018/9/17.
 */

public class HttpClient {

    private static final int TIMEOUT = 10000;
    private static HttpClient sInstance;
    private OkHttpClient mOkHttpClient;

    private HttpClient() {
    }

    public static HttpClient getInstance() {
        if (sInstance == null) {
            synchronized (HttpClient.class) {
                if (sInstance == null) {
                    sInstance = new HttpClient();
                }
            }
        }
        return sInstance;
    }

    public void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        builder.retryOnConnectionFailure(true);
//        Dispatcher dispatcher = new Dispatcher();
//        dispatcher.setMaxRequests(20000);
//        dispatcher.setMaxRequestsPerHost(10000);
//        builder.dispatcher(dispatcher);

        //输出HTTP请求 响应信息
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("http");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BASIC);
        builder.addInterceptor(loggingInterceptor);
        mOkHttpClient = builder.build();

        OkGo.getInstance().init(CommonAppContext.sInstance)
                .setOkHttpClient(mOkHttpClient)
                .setCacheMode(CacheMode.NO_CACHE)
                .setRetryCount(1);

    }

    public GetRequest<JsonBean> get(String serviceName, String tag) {
        return get("/api/public/?service=", serviceName, tag);
    }

    public GetRequest<JsonBean> get(String basePath, String serviceName, String tag) {
        return OkGo.<JsonBean>get(CommonAppConfig.getCurHost() + basePath + serviceName)
                .headers("Connection", "keep-alive")
                .headers("uid",CommonAppConfig.getInstance().getUid())
                .headers(LanSwitchUtil.LAN_KEY, LanSwitchUtil.getLanStr())
                .tag(tag);
    }

    public PostRequest<JsonBean> post(String serviceName, String tag) {
        return OkGo.<JsonBean>post(CommonAppConfig.getCurHost() + "/api/public/?service=" + serviceName)
                .headers("Connection", "keep-alive")
                .headers(LanSwitchUtil.LAN_KEY, LanSwitchUtil.getLanStr())
                .tag(tag);
    }

    public PostRequest<JsonBean> post(String basePath,String serviceName, String tag) {
        return OkGo.<JsonBean>post(CommonAppConfig.getCurHost() + serviceName)
                .headers("Connection", "keep-alive")
                .headers("uid",CommonAppConfig.getInstance().getUid())
                .headers(LanSwitchUtil.LAN_KEY, LanSwitchUtil.getLanStr())
                .tag(tag);
    }

    public GetRequest<String> getFullPath(String path, String tag) {
        return OkGo.<String>get(path)
                .headers("Connection", "keep-alive")
                .headers(LanSwitchUtil.LAN_KEY, LanSwitchUtil.getLanStr())
                .tag(tag);
    }

    public void cancel(String tag) {
        OkGo.cancelTag(mOkHttpClient, tag);
    }


}
