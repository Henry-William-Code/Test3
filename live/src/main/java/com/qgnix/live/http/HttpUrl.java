package com.qgnix.live.http;


import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author
 * @date 2021/4/16
 */

public class HttpUrl {


    String protocol;
    String host;
    String file;
    int port;

    /**
     * scheme://host:port/path?query#fragment
     *
     * @param path
     * @throws MalformedURLException
     */
    public HttpUrl(String path) throws MalformedURLException {
        URL url = new URL(path);
        host = url.getHost();
        file = url.getFile();
        file = (file == null || file.length() == 0) ? "/" : file;
        protocol = url.getProtocol();
        port = url.getPort();
        port = port == -1 ? url.getDefaultPort() : port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getFile() {
        return file;
    }

    public int getPort() {
        return port;
    }
}
