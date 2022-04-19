package com.qgnix.common.http;

import android.os.Handler;
import android.os.Looper;

import com.lzy.okgo.exception.HttpException;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.LanSwitchUtil;
import com.qgnix.common.utils.SpUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by fan on 2016/11/9.
 */
@Deprecated
public class OkHttp {
    public static final String TAG = "======OkHttp=======";

    /**
     * 静态实例
     */
    private static OkHttp sOkHttpManager;

    /**
     * okhttpclient实例
     */
    private OkHttpClient mClient;

    /**
     * 因为我们请求数据一般都是子线程中请求，在这里我们使用了handler
     */
    private Handler mHandler;

    //语言
    private String getMLanguage() {
        return LanSwitchUtil.getLanStr();
    }

    /**
     * 构造方法
     */
    private OkHttp() {
        mClient = new OkHttpClient();
        /**
         * 在这里直接设置连接超时.读取超时，写入超时
         */
        OkHttpClient.Builder builder = mClient.newBuilder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        mClient = builder.build();

        /**
         * 如果是用的3.0之前的版本  使用以下直接设置连接超时.读取超时，写入超时
         */
        //client.setConnectTimeout(10, TimeUnit.SECONDS);
        //client.setWriteTimeout(10, TimeUnit.SECONDS);
        //client.setReadTimeout(30, TimeUnit.SECONDS);

        /**
         * 初始化handler
         */
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 单例模式  获取OkHttp实例
     *
     * @return
     */
    public static OkHttp getInstance() {

        if (sOkHttpManager == null) {
            sOkHttpManager = new OkHttp();
        }
        return sOkHttpManager;
    }

    /**
     * 请求异常更新host
     * @param code
     * @param e
     */
    private void handleExceptionHost(int code,Exception e){
        L.e("请求失败--->code = " + code);
        if ((code == -1 && e instanceof UnknownHostException)||(code == 502 && e instanceof HttpException)) {
            //host异常, 修改请求host
            String curHost = SpUtil.getInstance().getStringValue(SpUtil.SERVER_HOST);
            for (String s : CommonAppConfig.SERVER_HOST) {
                if (!curHost.equals(s)) {
                    SpUtil.getInstance().setStringValue(SpUtil.SERVER_HOST, s);
                    break;
                }
            }
        }
    }

    /**
     * 分发失败的时候调用
     *
     * @param request
     * @param e
     * @param callBack
     */
    private void deliverDataFailure(final Request request, final IOException e, final DataCallBack callBack) {
        /**
         * 在这里使用异步处理
         */
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.requestFailure(request, e);
                }
            }
        });
    }

    /**
     * 分发成功的时候调用
     *
     * @param result
     * @param callBack
     */
    private void deliverDataSuccess(final String result, final DataCallBack callBack) {
        /**
         * 在这里使用异步线程处理
         */
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.requestSuccess(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 数据回调接口
     */
    public interface DataCallBack {

        void requestSuccess(String result) throws Exception;

        void requestFailure(Request request, IOException e);

    }

    //-------------------------提交表单--------------------------

    public static void postAsync(String url, Map<String, String> params, DataCallBack callBack) {
        getInstance().inner_postAsync(url, params, callBack);
    }

    private void inner_postAsync(String url, Map<String, String> params, final DataCallBack callBack) {
        L.e(TAG, "inner_postAsync-->url：" + url + "\n params：" + params);
        RequestBody requestBody = null;
        if (params == null) {
            params = new HashMap<>();
        }
        /**
         * 如果是3.0之前版本的，构建表单数据是下面的一句
         */
        //FormEncodingBuilder builder = new FormEncodingBuilder();

        /**
         * 3.0之后版本
         */
        FormBody.Builder builder = new FormBody.Builder();

        /**
         * 在这对添加的参数进行遍历，map遍历有四种方式，如果想要了解的可以网上查找
         */
        for (Map.Entry<String, String> map : params.entrySet()) {
            String key = map.getKey();
            String value = null;
            /**
             * 判断值是否是空的
             */
            if (map.getValue() == null) {
                value = "";
            } else {
                value = map.getValue();
            }
            /**
             * 把key和value添加到formbody中
             */
            builder.add(key, value);
        }
        requestBody = builder.build();
        //结果返回
        // 请求对象
        final Request request = new Request.Builder().url(url)
                .addHeader(LanSwitchUtil.LAN_KEY, getMLanguage())
                .post(requestBody)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                try {
                    handleExceptionHost(call.execute().code(),e);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                deliverDataFailure(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                deliverDataSuccess(result, callBack);
            }


        });
    }


    //-------------------------文件下载--------------------------
    public static void downloadAsync(String url, String desDir, DataCallBack callBack) {
        getInstance().inner_downloadAsync(url, desDir, callBack);
    }

    /**
     * 下载文件的内部逻辑处理类
     *
     * @param url      下载地址
     * @param desDir   目标地址
     * @param callBack
     */
    private void inner_downloadAsync(final String url, final String desDir, final DataCallBack callBack) {
        L.e(TAG, "inner_downloadAsync-->url：" + url + "\n desDir：" + desDir);
        final Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                try {
                    handleExceptionHost(call.execute().code(),e);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                deliverDataFailure(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                /**
                 * 在这里进行文件的下载处理
                 */
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    //文件名和目标地址
                    File file = new File(desDir, getFileName(url));
                    //把请求回来的response对象装换为字节流
                    inputStream = response.body().byteStream();
                    fileOutputStream = new FileOutputStream(file);
                    int len = 0;
                    byte[] bytes = new byte[2048];
                    //循环读取数据
                    while ((len = inputStream.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, len);
                    }
                    //关闭文件输出流
                    fileOutputStream.flush();
                    //调用分发数据成功的方法
                    deliverDataSuccess(file.getAbsolutePath(), callBack);
                } catch (IOException e) {
                    //如果失败，调用此方法
                    try {
                        handleExceptionHost(call.execute().code(),e);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    deliverDataFailure(request, e, callBack);
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }

                }
            }

        });
    }

    /**
     * 根据文件url获取文件的路径名字
     *
     * @param url
     * @return
     */
    private String getFileName(String url) {
        int separatorIndex = url.lastIndexOf("/");
        String path = (separatorIndex < 0) ? url : url.substring(separatorIndex + 1);
        return path;
    }


}
