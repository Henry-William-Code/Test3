package com.qgnix.live.socket;

/**
 * socket 开奖信息监听
 */
public interface SocketCpMsgListener extends SocketListener{
    /**
     * 直播间  开奖信息
     *
     * @param data 开奖数据
     */
    void onLotteryInfo(String data);

}
