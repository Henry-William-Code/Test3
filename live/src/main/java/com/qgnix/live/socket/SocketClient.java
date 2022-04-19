package com.qgnix.live.socket;


import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.utils.L;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by cxf on 2018/10/9.
 */

public class SocketClient {

    private static final String TAG = "socket";
    private Socket mSocket;
    private String mLiveUid;
    private String mStream;

    /**
     * 彩票id
     */
    private String mTicketId;
    /**
     * 直播socket handler
     */
    private SocketHandler mSocketHandler;

    /**
     * socket 数据类型，彩票信息dataType=2
     */
    private final int mDataType;

    public SocketClient(SocketMessageListener listener) {
        this.mDataType = 1;

        String socketUrl = CommonAppConfig.getInstance().getSocketUrl();
        if (TextUtils.isEmpty(socketUrl)) {
            L.e("===socket url 为空！");
            return;
        }
        try {
            IO.Options option = new IO.Options();
            option.forceNew = true;
            option.reconnection = true;
            option.reconnectionDelay = 2000;
            mSocket = IO.socket(socketUrl, option);
            mSocket.on(Socket.EVENT_CONNECT, mConnectListener);//连接成功
            mSocket.on(Socket.EVENT_DISCONNECT, mDisConnectListener);//断开连接
            mSocket.on(Socket.EVENT_CONNECT_ERROR, mErrorListener);//连接错误
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, mTimeOutListener);//连接超时
            mSocket.on(Socket.EVENT_RECONNECT, mReConnectListener);//重连
            mSocket.on(Constants.SOCKET_CONN, onConn);//连接socket消息
            mSocket.on(Constants.SOCKET_BROADCAST, onBroadcast);//接收服务器广播的具体业务逻辑相关的消息
            mSocket.on(Constants.SOCKET_CP_BROADCAST, onCpBroadcast);//接收服务器彩票广播的具体业务逻辑相关的消息
            //设置直播socket  handler
            this.mSocketHandler = new SocketHandler(listener, mDataType);
        } catch (Exception e) {
            L.e(TAG, "socket url 异常--->" + e.getMessage());
        }
    }


    public SocketClient(SocketCpMsgListener listener) {
        this.mDataType = 2;

        String socketUrl = CommonAppConfig.getInstance().getSocketUrl();
        if (TextUtils.isEmpty(socketUrl)) {
            L.e("===socket url 为空！");
            return;
        }
        try {
            IO.Options option = new IO.Options();
            option.forceNew = true;
            option.reconnection = true;
            option.reconnectionDelay = 2000;
            mSocket = IO.socket(socketUrl, option);
            mSocket.on(Socket.EVENT_CONNECT, mConnectListener);//连接成功
            mSocket.on(Socket.EVENT_DISCONNECT, mDisConnectListener);//断开连接
            mSocket.on(Socket.EVENT_CONNECT_ERROR, mErrorListener);//连接错误
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, mTimeOutListener);//连接超时
            mSocket.on(Socket.EVENT_RECONNECT, mReConnectListener);//重连
            mSocket.on(Constants.SOCKET_CONN, onConn);//连接socket消息
            mSocket.on(Constants.SOCKET_CP_BROADCAST, onCpBroadcast);//接收服务器彩票广播的具体业务逻辑相关的消息
            //设置直播socket  handler
            this.mSocketHandler = new SocketHandler(listener, mDataType);
        } catch (Exception e) {
            L.e(TAG, "socket url 异常--->" + e.getMessage());
        }
    }

    /**
     * 直播间链接socket
     *
     * @param liveUid
     * @param stream
     * @param ticketId
     */
    public void connect(String liveUid, String stream, String ticketId) {
        mLiveUid = liveUid;
        mStream = stream;
        mTicketId = ticketId;
        if (mSocket != null) {
            mSocket.connect();
        }
        if (mSocketHandler != null) {
            mSocketHandler.setLiveUid(liveUid);
        }
    }

    /**
     * 彩票详情链接socket
     *
     * @param ticketId
     */
    public void connect(String ticketId) {
        mTicketId = ticketId;
        if (mSocket != null) {
            mSocket.connect();
        }
    }

    /**
     * 断开链接
     */
    public void disConnect() {
        if (mSocket != null) {
            mSocket.close();
            mSocket.off();
        }
        if (mSocketHandler != null) {
            mSocketHandler.release();
        }
        mSocketHandler = null;
        mLiveUid = null;
        mStream = null;
    }

    /**
     * 向服务发送连接消息
     */
    private void conn() {
        org.json.JSONObject data = new org.json.JSONObject();
        try {
            data.put("uid", CommonAppConfig.getInstance().getUid());
            data.put("token", CommonAppConfig.getInstance().getToken());
            if (mDataType != 2) {
                data.put("liveuid", mLiveUid);
                data.put("stream", mStream);
                data.put("roomnum", mLiveUid);
            }
            data.put("ticketId", mTicketId);
            data.put("type", mDataType);
            mSocket.emit(Constants.SOCKET_CONN, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private Emitter.Listener mConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onConnect-->" + new Gson().toJson(args));
            conn();
        }
    };

    private Emitter.Listener mReConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--reConnect-->" + args);
            //conn();
        }
    };

    private Emitter.Listener mDisConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onDisconnect-->" + args);
            if (mSocketHandler != null) {
                mSocketHandler.sendEmptyMessage(Constants.SOCKET_WHAT_DISCONN);
            }
        }
    };
    private Emitter.Listener mErrorListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onConnectError-->" + Arrays.toString(args));
        }
    };

    private Emitter.Listener mTimeOutListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onConnectTimeOut-->" + args);
        }
    };

    private Emitter.Listener onConn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "==【onConn】-->" + Arrays.toString(args));
            if (mSocketHandler != null) {
                try {
                    String s = ((JSONArray) args[0]).getString(0);
                    L.e(TAG, "--onConn-->" + s);
                    Message msg = Message.obtain();
                    if (null != msg) {
                        msg.what = Constants.SOCKET_WHAT_CONN;
                        msg.obj = s.equals("ok");
                        mSocketHandler.sendMessage(msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Emitter.Listener onBroadcast = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocketHandler != null) {
                try {
                    JSONArray array = (JSONArray) args[0];
                    for (int i = 0; i < array.length(); i++) {
                        Message msg = Message.obtain();
                        msg.what = Constants.SOCKET_WHAT_BROADCAST;
                        msg.obj = array.getString(i);
                        mSocketHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };


    // 开奖时间
    private final Emitter.Listener onCpBroadcast = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocketHandler != null || args.length > 0) {
                try {
                    String res = args[0].toString();
                    Message msg = Message.obtain();
                    msg.what = Constants.SOCKET_CP_WHAT_BROADCAST;
                    msg.obj = res;
                    mSocketHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };


    public void send(SocketSendBean bean) {
        if (mSocket != null) {
            mSocket.emit(Constants.SOCKET_SEND, bean.create());
        }
    }
}
