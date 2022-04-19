package com.qgnix.live.news;

import com.qgnix.live.news.info.ResponseBody;

// 响应的result信息
public class Response2 {

    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    private ResponseBody body;


    public ResponseBody getBody() {
        return body;
    }
    public ResponseBody body() {
        return body;
    }

    public void setBody(ResponseBody body) {
        this.body = body;
    }




}



