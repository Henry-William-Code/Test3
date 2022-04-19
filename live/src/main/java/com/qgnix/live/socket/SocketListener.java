package com.qgnix.live.socket;

/**
 * socket 链接、断开链接监听
 */
public interface SocketListener {

    /**
     * 直播间  连接成功socket后调用
     */
    void onConnect(boolean successConn);

    /**
     * 直播间  自己的socket断开
     */
    void onDisConnect();

}
