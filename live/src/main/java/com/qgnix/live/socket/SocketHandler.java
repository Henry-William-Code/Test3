package com.qgnix.live.socket;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.bean.FollowUpBean;
import com.qgnix.live.bean.LiveBuyGuardMsgBean;
import com.qgnix.live.bean.LiveChatBean;
import com.qgnix.live.bean.LiveDanMuBean;
import com.qgnix.live.bean.LiveEnterRoomBean;
import com.qgnix.live.bean.LiveGiftPrizePoolWinBean;
import com.qgnix.live.bean.LiveLuckGiftWinBean;
import com.qgnix.live.bean.LiveReceiveGiftBean;
import com.qgnix.live.bean.LiveUserGiftBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播相关socket handler
 */
public class SocketHandler extends Handler {

    private SocketMessageListener mListener;
    private SocketCpMsgListener mCpListener;
    private String mLiveUid;
    private CpMsgFilterRepeatHelper cpMsgFilterRepeatHelper = new CpMsgFilterRepeatHelper(50);//50应该就够用了 , 减少内存占用

    /**
     * socket 数据类型，彩票信息dataType=2
     */
    private final int mDataType;


    public SocketHandler(SocketMessageListener listener, int dataType) {
        mListener = new WeakReference<>(listener).get();
        mDataType = dataType;
    }

    public SocketHandler(SocketCpMsgListener listener, int dataType) {
        mCpListener = new WeakReference<>(listener).get();
        mDataType = dataType;
    }

    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.SOCKET_WHAT_CONN:
                if (mDataType == 2) {
                    // 彩票
                    mCpListener.onConnect(null == msg.obj ? false : (Boolean) msg.obj);
                } else {
                    if (null != mListener) {
                        mListener.onConnect(null == msg.obj ? false : (Boolean) msg.obj);
                    }
                }
                break;
            case Constants.SOCKET_WHAT_BROADCAST:
                if (mDataType != 2) {
                    processBroadcast((String) msg.obj);
                }
                break;
            case Constants.SOCKET_CP_WHAT_BROADCAST:
                //处理开奖消息
                processCpBroadcast(msg.obj.toString());
                break;
            case Constants.SOCKET_WHAT_DISCONN:
                if (mDataType == 2) {
                    // 彩票
                    mCpListener.onDisConnect();
                } else {
                    if (null != mListener) {
                        mListener.onDisConnect();
                    }
                }
                break;
            default:
                break;
        }
    }


    //处理直播间的广播
    private void processCpBroadcast(String socketMsg) {
        L.e("收到 cp直播间的 socket--->" + socketMsg);
        JSONObject jObj = JSON.parseObject(socketMsg);

        String jStr = jObj.getString("info");
        if (jStr != null) {
            //处理开奖消息
            if (mDataType == 2) {
                // 彩票
                mCpListener.onLotteryInfo(jStr);
            } else {
                if (null != mListener) {
                    mListener.onLotteryInfo(jStr);
                }
            }

        } else {
            if (mDataType == 2 || null == mListener) {
                return;
            }
            SocketReceiveBean received = JSON.parseObject(socketMsg, SocketReceiveBean.class);
            JSONObject map = received.getMsg().getJSONObject(0);

            if(cpMsgFilterRepeatHelper.filterRepeat(map)){
                return;
            }

            if (Constants.SOCKET_SEND_MSG.equals(map.getString("_method_"))) {//系统消息
                String msgtype = map.getString("msgtype");
                if ("3".equals(msgtype)) {
                    // 跟投
                    FollowUpBean bean = JSON.parseObject(map.getString("ct"), FollowUpBean.class);
                    LiveChatBean chatBean = new LiveChatBean();
                    chatBean.setType(LiveChatBean.FOLLOW_UP);
                    chatBean.setFollowUpBean(bean);
                    mListener.onFollowUp(chatBean);
                } else if ("4".equals(msgtype)) {
                    //中奖
                    List<String> list = JSON.parseArray(map.getString("ct"), String.class);
                    if (null != list && !list.isEmpty()) {
                        List<LiveChatBean> beanList = new ArrayList<>();
                        LiveChatBean chatBean;
                        for (String s : list) {
                            chatBean = new LiveChatBean();
                            chatBean.setType(LiveChatBean.WINNING);
                            chatBean.setContent(s);
                            beanList.add(chatBean);
                        }

                        mListener.onWinning(beanList);
                    }
                }
            }
        }
    }


    //处理直播间的广播
    private void processBroadcast(String socketMsg) {
        L.e("收到socket--->" + socketMsg);
        if (null == mListener) {
            return;
        }
        if (Constants.SOCKET_STOP_PLAY.equals(socketMsg)) {
            mListener.onSuperCloseLive();//超管关闭房间
            return;
        }
        if (Constants.SOCKET_STOP_PLAY_AFTER_EXAM.equals(socketMsg)) {
            mListener.onSuperCloseLive(WordUtil.getString(R.string.live_steam_issue));//服务端因为直播流问题关闭房间
            return;
        }

        SocketReceiveBean received = JSON.parseObject(socketMsg, SocketReceiveBean.class);
        JSONObject map = received.getMsg().getJSONObject(0);
        switch (map.getString("_method_")) {

            case Constants.SOCKET_SYSTEM://系统消息
                systemChatMessage(map.getString("ct"));
                break;
            case Constants.SOCKET_KICK://踢人
                systemChatMessage(map.getString("ct"));
                mListener.onKick(map.getString("touid"));
                break;
            case Constants.SOCKET_SHUT_UP://禁言
                String ct = map.getString("ct");
                systemChatMessage(ct);
                mListener.onShutUp(map.getString("touid"), ct);
                break;
            case Constants.SOCKET_SEND_MSG://文字消息，点亮，用户进房间，这种混乱的设计是因为服务器端逻辑就是这样设计的,客户端无法自行修改
                String msgtype = map.getString("msgtype");
                if ("2".equals(msgtype)) {//发言，点亮
                    if ("409002".equals(received.getRetcode())) {
                        ToastUtil.show(R.string.live_you_are_shut);
                        return;
                    }
                    if(cpMsgFilterRepeatHelper.filterRepeat(map)){
                        return;
                    }
                    LiveChatBean chatBean = new LiveChatBean();
                    chatBean.setId(map.getString("uid"));
                    chatBean.setUserNiceName(map.getString("uname"));
                    chatBean.setLevel(map.getIntValue("level"));
                    chatBean.setAnchor(map.getIntValue("isAnchor") == 1);
                    chatBean.setManager(map.getIntValue("usertype") == Constants.SOCKET_USER_TYPE_ADMIN);
                    chatBean.setContent(map.getString("ct"));
                    int heart = map.getIntValue("heart");
                    chatBean.setHeart(heart);
                    chatBean.setLiangName(map.getString("liangname"));
                    chatBean.setVipType(map.getIntValue("vip_type"));
                    chatBean.setGuardType(map.getIntValue("guard_type"));
                    mListener.onChat(chatBean);
                } else if ("0".equals(msgtype)) {//用户进入房间
                    JSONObject obj = JSON.parseObject(map.getString("ct"));
                    LiveUserGiftBean u = JSON.toJavaObject(obj, LiveUserGiftBean.class);
                    UserBean.Vip vip = new UserBean.Vip();
                    int vipType = obj.getIntValue("vip_type");
                    vip.setType(vipType);
                    u.setVip(vip);
                    UserBean.Car car = new UserBean.Car();
                    car.setId(obj.getIntValue("car_id"));
                    car.setSwf(obj.getString("car_swf"));
                    car.setSwftime(obj.getFloatValue("car_swftime"));
                    car.setWords(obj.getString("car_words"));
                    u.setCar(car);
                    UserBean.Liang liang = new UserBean.Liang();
                    String liangName = obj.getString("liangname");
                    liang.setName(liangName);
                    u.setLiang(liang);
                    LiveChatBean chatBean = new LiveChatBean();
                    chatBean.setType(LiveChatBean.ENTER_ROOM);
                    chatBean.setId(u.getId());
                    chatBean.setUserNiceName(u.getUserNiceName());
                    chatBean.setLevel(u.getLevel());
                    chatBean.setVipType(vipType);
                    chatBean.setLiangName(liangName);
                    chatBean.setManager(obj.getIntValue("usertype") == Constants.SOCKET_USER_TYPE_ADMIN);
                    chatBean.setContent(WordUtil.getString(R.string.live_enter_room));
                    chatBean.setGuardType(obj.getIntValue("guard_type"));
                    mListener.onEnterRoom(new LiveEnterRoomBean(u, chatBean));
                } else if ("5".equals(msgtype)) {
                    int isTran = map.getIntValue("ct");
                    L.e("==翻译开关：" + isTran);
                    mListener.onTranslate(map.getIntValue("ct"));
                }
                break;
            case Constants.SOCKET_SEND_GIFT://送礼物
                LiveReceiveGiftBean receiveGiftBean = JSON.parseObject(map.getString("ct"), LiveReceiveGiftBean.class);
                receiveGiftBean.setAvatar(map.getString("uhead"));
                receiveGiftBean.setUserNiceName(map.getString("uname"));
                LiveChatBean chatBean = new LiveChatBean();
                chatBean.setUserNiceName(receiveGiftBean.getUserNiceName());
                chatBean.setLevel(receiveGiftBean.getLevel());
                chatBean.setId(map.getString("uid"));
                chatBean.setLiangName(map.getString("liangname"));
                chatBean.setVipType(map.getIntValue("vip_type"));
                chatBean.setType(LiveChatBean.GIFT);
                chatBean.setContent(WordUtil.getString(R.string.live_send_gift_1) + receiveGiftBean.getGiftCount() + WordUtil.getString(R.string.live_send_gift_2) + receiveGiftBean.getGiftName());
                receiveGiftBean.setLiveChatBean(chatBean);
                if (map.getIntValue("ifpk") == 1) {
                    if (!TextUtils.isEmpty(mLiveUid)) {
                        if (mLiveUid.equals(map.getString("roomnum"))) {
                            mListener.onSendGift(receiveGiftBean);
                            mListener.onSendGiftPk(map.getLongValue("pktotal1"), map.getLongValue("pktotal2"));
                        } else {
                            mListener.onSendGiftPk(map.getLongValue("pktotal2"), map.getLongValue("pktotal1"));
                        }
                    }
                } else {
                    mListener.onSendGift(receiveGiftBean);
                }

                break;
            case Constants.SOCKET_SEND_BARRAGE://发弹幕
                LiveDanMuBean liveDanMuBean = JSON.parseObject(map.getString("ct"), LiveDanMuBean.class);
                liveDanMuBean.setAvatar(map.getString("uhead"));
                liveDanMuBean.setUserNiceName(map.getString("uname"));
                mListener.onSendDanMu(liveDanMuBean);
                break;
            case Constants.SOCKET_LEAVE_ROOM://离开房间
                UserBean u = JSON.parseObject(map.getString("ct"), UserBean.class);
                mListener.onLeaveRoom(u);
                break;
            case Constants.SOCKET_LIVE_END://主播关闭直播
                mListener.onLiveEnd();
                break;
            case Constants.SOCKET_CHANGE_LIVE://主播切换计时收费类型
                mListener.onChangeTimeCharge(map.getIntValue("type_val"));
                break;
            case Constants.SOCKET_UPDATE_VOTES://门票或计时收费时候更新主播的映票数
                mListener.onUpdateVotes(map.getString("uid"), map.getString("votes"), map.getIntValue("isfirst"));
                break;
            case Constants.SOCKET_FAKE_FANS://僵尸粉
                JSONObject obj = map.getJSONObject("ct");
                String s = obj.getJSONObject("data").getJSONArray("info").getJSONObject(0).getString("list");
                L.e("僵尸粉--->" + s);
                List<LiveUserGiftBean> list = JSON.parseArray(s, LiveUserGiftBean.class);
                mListener.addFakeFans(list);
                break;
            case Constants.SOCKET_SET_ADMIN://设置或取消管理员
                systemChatMessage(map.getString("ct"));
                mListener.onSetAdmin(map.getString("touid"), map.getIntValue("action"));
                break;
            case Constants.SOCKET_BUY_GUARD://购买守护
                LiveBuyGuardMsgBean buyGuardMsgBean = new LiveBuyGuardMsgBean();
                buyGuardMsgBean.setUid(map.getString("uid"));
                buyGuardMsgBean.setUserName(map.getString("uname"));
                buyGuardMsgBean.setVotes(map.getString("votestotal"));
                buyGuardMsgBean.setGuardNum(map.getIntValue("guard_nums"));
                buyGuardMsgBean.setGuardType(map.getIntValue("guard_type"));
                mListener.onBuyGuard(buyGuardMsgBean);
                break;
            case Constants.SOCKET_LINK_MIC://连麦
                processLinkMic(map);
                break;
            case Constants.SOCKET_LINK_MIC_ANCHOR://主播连麦
                processLinkMicAnchor(map);
                break;
            case Constants.SOCKET_LINK_MIC_PK://主播PK
                processAnchorLinkMicPk(map);
                break;
            case Constants.SOCKET_RED_PACK://红包消息
                String uid = map.getString("uid");
                if (TextUtils.isEmpty(uid)) {
                    return;
                }
                LiveChatBean liveChatBean = new LiveChatBean();
                liveChatBean.setType(LiveChatBean.RED_PACK);
                liveChatBean.setId(uid);
                String name = uid.equals(mLiveUid) ? WordUtil.getString(R.string.live_anchor) : map.getString("uname");
                liveChatBean.setContent(name + map.getString("ct"));
                mListener.onRedPack(liveChatBean);
                break;

            case Constants.SOCKET_LUCK_WIN://幸运礼物中奖
                mListener.onLuckGiftWin(map.toJavaObject(LiveLuckGiftWinBean.class));
                break;

            case Constants.SOCKET_PRIZE_POOL_WIN://奖池中奖
                mListener.onPrizePoolWin(map.toJavaObject(LiveGiftPrizePoolWinBean.class));
                break;
            case Constants.SOCKET_PRIZE_POOL_UP://奖池升级
                mListener.onPrizePoolUp(map.getString("uplevel"));
                break;
            //游戏socket
            case Constants.SOCKET_GAME_ZJH://游戏 智勇三张
                if (CommonAppConfig.GAME_ENABLE) {
                    mListener.onGameZjh(map);
                }
                break;
            case Constants.SOCKET_GAME_HD://游戏 海盗船长
                if (CommonAppConfig.GAME_ENABLE) {
                    mListener.onGameHd(map);
                }
                break;
            case Constants.SOCKET_GAME_ZP://游戏 幸运转盘
                if (CommonAppConfig.GAME_ENABLE) {
                    mListener.onGameZp(map);
                }
                break;
            case Constants.SOCKET_GAME_NZ://游戏 开心牛仔
                if (CommonAppConfig.GAME_ENABLE) {
                    mListener.onGameNz(map);
                }
                break;
            case Constants.SOCKET_GAME_EBB://游戏 二八贝
                if (CommonAppConfig.GAME_ENABLE) {
                    mListener.onGameEbb(map);
                }
                break;
            case Constants.SOCKET_USER_LIST_NOT:
                //用户列表消息
                handleUserList(map.getJSONObject("ct"));
                break;
            default:
                break;
        }
    }


    /**
     * 接收到系统消息，显示在聊天栏中
     */
    private void systemChatMessage(String content) {
        LiveChatBean bean = new LiveChatBean();
        bean.setContent(content);
        bean.setType(LiveChatBean.SYSTEM);
        mListener.onChat(bean);
    }

    /**
     * 处理观众与主播连麦逻辑
     */
    private void processLinkMic(JSONObject map) {
        if (null == mListener) {
            return;
        }
        int action = map.getIntValue("action");
        switch (action) {
            case 1://主播收到观众连麦的申请
                UserBean u = new UserBean();
                u.setId(map.getString("uid"));
                u.setUserNiceName(map.getString("uname"));
                u.setAvatar(map.getString("uhead"));
                u.setSex(map.getIntValue("sex"));
                u.setLevel(map.getIntValue("level"));
                mListener.onAudienceApplyLinkMic(u);
                break;
            case 2://观众收到主播同意连麦的消息
                if (map.getString("touid").equals(CommonAppConfig.getInstance().getUid())) {
                    mListener.onAnchorAcceptLinkMic();
                }
                break;
            case 3://观众收到主播拒绝连麦的消息
                if (map.getString("touid").equals(CommonAppConfig.getInstance().getUid())) {
                    mListener.onAnchorRefuseLinkMic();
                }
                break;
            case 4://所有人收到连麦观众发过来的流地址
                String uid = map.getString("uid");
                if (!TextUtils.isEmpty(uid) && !uid.equals(CommonAppConfig.getInstance().getUid())) {
                    mListener.onAudienceSendLinkMicUrl(uid, map.getString("uname"), map.getString("playurl"));
                }
                break;
            case 5://连麦观众自己断开连麦
                mListener.onAudienceCloseLinkMic(map.getString("uid"), map.getString("uname"));
                break;
            case 6://主播断开已连麦观众的连麦
                mListener.onAnchorCloseLinkMic(map.getString("touid"), map.getString("uname"));
                break;
            case 7://已申请连麦的观众收到主播繁忙的消息
                if (map.getString("touid").equals(CommonAppConfig.getInstance().getUid())) {
                    mListener.onAnchorBusy();
                }
                break;
            case 8://已申请连麦的观众收到主播无响应的消息
                if (map.getString("touid").equals(CommonAppConfig.getInstance().getUid())) {
                    mListener.onAnchorNotResponse();
                }
                break;
            case 9://所有人收到已连麦的观众退出直播间消息
                mListener.onAudienceLinkMicExitRoom(map.getString("touid"));
                break;
        }
    }

    /**
     * 处理主播与主播连麦逻辑
     *
     * @param map
     */
    private void processLinkMicAnchor(JSONObject map) {
        int action = map.getIntValue("action");
        switch (action) {
            case 1://收到其他主播连麦的邀请的回调
                UserBean u = new UserBean();
                u.setId(map.getString("uid"));
                u.setUserNiceName(map.getString("uname"));
                u.setAvatar(map.getString("uhead"));
                u.setSex(map.getIntValue("sex"));
                u.setLevel(map.getIntValue("level"));
                u.setLevelAnchor(map.getIntValue("level_anchor"));
                mListener.onLinkMicAnchorApply(u, map.getString("stream"));
                break;
            case 3://对方主播拒绝连麦的回调
                mListener.onLinkMicAnchorRefuse();
                break;
            case 4://所有人收到对方主播的播流地址的回调
                mListener.onLinkMicAnchorPlayUrl(map.getString("pkuid"), map.getString("pkpull"));
                break;
            case 5://断开连麦的回调
                mListener.onLinkMicAnchorClose();
                break;
            case 7://对方主播正在忙的回调
                mListener.onLinkMicAnchorBusy();
                break;
            case 8://对方主播无响应的回调
                mListener.onLinkMicAnchorNotResponse();
                break;
            case 9://对方主播正在游戏
                mListener.onlinkMicPlayGaming();
                break;
        }
    }

    /**
     * 处理主播与主播PK逻辑
     *
     * @param map
     */
    private void processAnchorLinkMicPk(JSONObject map) {
        int action = map.getIntValue("action");
        switch (action) {
            case 1://收到对方主播PK回调
                UserBean u = new UserBean();
                u.setId(map.getString("uid"));
                u.setUserNiceName(map.getString("uname"));
                u.setAvatar(map.getString("uhead"));
                u.setSex(map.getIntValue("sex"));
                u.setLevel(map.getIntValue("level"));
                u.setLevelAnchor(map.getIntValue("level_anchor"));
                mListener.onLinkMicPkApply(u, map.getString("stream"));
                break;
            case 3://对方主播拒绝PK的回调
                mListener.onLinkMicPkRefuse();
                break;
            case 4://所有人收到PK开始址的回调
                mListener.onLinkMicPkStart(map.getString("pkuid"));
                break;
            case 5://PK时候断开连麦的回调
                mListener.onLinkMicPkClose();
                break;
            case 7://对方主播正在忙的回调
                mListener.onLinkMicPkBusy();
                break;
            case 8://对方主播无响应的回调
                mListener.onLinkMicPkNotResponse();
                break;
            case 9://pk结束的回调
                mListener.onLinkMicPkEnd(map.getString("win_uid"));
                break;
        }
    }

    public void release() {
        mListener = null;
    }


    /**
     * 用户列表消息
     *
     * @param obj
     */
    private void handleUserList(JSONObject obj) {
        L.e("==用户列表消息===" + obj.getString("userlist"));
        List<LiveUserGiftBean> datas = JSON.parseArray(obj.getString("userlist"), LiveUserGiftBean.class);
        mListener.onUserList(datas);
    }
}
