package com.qgnix.live.news.inter;

import com.qgnix.live.http.HttpCodec;
import com.qgnix.live.news.CacheTemp;
import com.qgnix.live.news.Chain2;
import com.qgnix.live.news.Request2;
import com.qgnix.live.news.Response2;
import com.qgnix.live.news.SocketRequestServer;
import com.qgnix.live.news.info.ResponseBody;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class ConnectInterceptorHttps implements Interceptor2{
    @Override
    public Response2 doNext(Chain2 chain2) throws IOException {

        Response2 response2 = new Response2();
        try {
            SocketRequestServer srs = new SocketRequestServer();

            Request2 request2 = chain2.getRequest();


            //获得一个ssl上下文
            SSLContext sslContext = SSLContext.getInstance("TLS");

            //信任本机所有证书
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            //初始化证书
            trustManagerFactory.init((KeyStore) null);
            //信任证书设置
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            //证书管理器初始化
            sslContext.init(null, trustManagers, null);

            //创建socket
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            Socket socket = socketFactory.createSocket();
            socket.connect(new InetSocketAddress(srs.getHost(request2), srs.getPort(request2)));


            // todo 请求
            // output
            OutputStream os = socket.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
            String requestAll = srs.getRequestHeaderAll(request2);
            // Log.d(TAG, "requestAll:" + requestAll);
            System.out.println("requestAll:" + requestAll);
            bufferedWriter.write(requestAll); // 给服务器发送请求 --- 请求头信息 所有的
            bufferedWriter.flush(); // 真正的写出去...
            // todo 响应
            //final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            InputStream is = socket.getInputStream();

            HttpCodec httpCodec = new HttpCodec();
            //读一行  响应行
            String responseLine = httpCodec.readLine(is);
            System.out.println("响应行：" + responseLine);

            //读响应头
            Map<String, String> headers = httpCodec.readHeaders(is);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            if (headers.containsKey("Location")) {//Location就代表这个请求是重定向的
                request2.setRedictUrl(headers.get("Location"));
            }

            if (headers.get("Content-Type") != null && headers.get("Content-Type").contains("image/jpeg")) {
                //处理流
                long length = Long.valueOf(headers.get("Content-Length"));
                ResponseBody responseBody = new ResponseBody();
                byte[] bytes = httpCodec.readBytes(is, (int) length);
                responseBody.setInputStream(is);
                responseBody.setContentLength(length);
                responseBody.setBytes(bytes);
                response2.setBody(responseBody);

            } else if (headers.get("Content-Type") != null && headers.get("Content-Type").contains("text/html")) {
                String content = "";
                if(headers.get("Content-Length")!=null){
                    long length = Long.valueOf(headers.get("Content-Length"));
                    byte[] bytes = httpCodec.readBytes(is, (int) length);
                    content = new String(bytes);
                }else{
                    content = httpCodec.readChunked(is);
                }
                System.out.println("响应:" + content);
                if (request2.isCache()) {
                    CacheTemp.cacheMap.put(request2.getUrl(), content);
                }
                ResponseBody responseBody = new ResponseBody();
                responseBody.setBodyString(content.replaceAll("\\r\\n", ""));
                response2.setBody(responseBody);
            }
            //response2 = chain2.getResponse(request2); // 执行下一个拦截器（任务节点）
            is.close();
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        // response2.setBody("流程走通....");
        return response2;
    }
}
