package com.qgnix.live.news.info;

import java.io.InputStream;

public class ResponseBody {
    private InputStream inputStream;
    private String bodyString;
    private long contentLength;
    private byte[] bytes;
    public final InputStream byteStream() {
        return inputStream;
    }

    public String string(){
        return bodyString;
    }

    public String getBodyString() {
        return bodyString;
    }

    public ResponseBody setBodyString(String bodyString) {
        this.bodyString = bodyString;
        return this;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] bytes(){
        return bytes;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public long contentLength(){
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }
}
