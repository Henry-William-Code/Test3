package com.qgnix.live.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.request.PostRequest;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.http.HttpClient;
import com.qgnix.common.http.JsonBean;
import com.qgnix.common.http.ServiceName;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.MD5Util;
import com.qgnix.common.utils.SpUtil;
import com.qgnix.common.utils.StringUtil;
import com.qgnix.live.BuildConfig;
import com.qgnix.live.LiveConfig;

import java.io.File;

import static com.qgnix.common.utils.DpUtil.SALT;

/**
 * Created by cxf on 2019/3/21.
 */

public class LiveHttpUtil {


    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }

    /**
     * 当直播间是门票收费，计时收费或切换成计时收费的时候，观众请求这个接口
     *
     * @param liveUid 主播的uid
     * @param stream  主播的stream
     */
    public static void roomCharge(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.roomCharge", LiveHttpConsts.ROOM_CHARGE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("liveuid", liveUid)
                .execute(callback);

    }

    /**
     * 当直播间是计时收费的时候，观众每隔一分钟请求这个接口进行扣费
     *
     * @param liveUid 主播的uid
     * @param stream  主播的stream
     */
    public static void timeCharge(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.timeCharge", LiveHttpConsts.TIME_CHARGE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("liveuid", liveUid)
                .execute(callback);
    }


    /**
     * 获取用户余额
     */
    public static void getCoin(HttpCallback callback) {
        HttpClient.getInstance().get("Live.getCoin", LiveHttpConsts.GET_COIN)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取用户的直播记录
     *
     * @param touid 对方的uid
     */
    @Deprecated
    public static void getLiveRecord(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("User.getLiverecord", LiveHttpConsts.GET_LIVE_RECORD)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取直播回放url
     *
     * @param recordId 视频的id
     */
    public static void getAliCdnRecord(String recordId, HttpCallback callback) {
        HttpClient.getInstance().get("User.getAliCdnRecord", LiveHttpConsts.GET_ALI_CDN_RECORD)
                .params("id", recordId)
                .execute(callback);
    }


    /**
     * 获取主播印象列表
     */
    public static void getAllImpress(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("User.GetUserLabel", LiveHttpConsts.GET_ALL_IMPRESS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 给主播设置印象
     */
    public static void setImpress(String touid, String ImpressIDs, HttpCallback callback) {
        HttpClient.getInstance().get("User.setUserLabel", LiveHttpConsts.SET_IMPRESS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("labels", ImpressIDs)
                .execute(callback);
    }


    /**
     * 获取当前直播间的管理员列表
     */
    public static void getAdminList(String liveUid, HttpCallback callback) {
        HttpClient.getInstance().get("Live.getAdminList", LiveHttpConsts.GET_ADMIN_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .execute(callback);
    }

    /**
     * 主播设置或取消直播间的管理员
     */
    public static void setAdmin(String liveUid, String touid, HttpCallback callback) {
        HttpClient.getInstance().get("Live.setAdmin", LiveHttpConsts.SET_ADMIN)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 获取直播间的禁言列表
     */
    public static void getLiveShutUpList(String liveUid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Livemanage.getShutList", LiveHttpConsts.GET_LIVE_SHUT_UP_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 直播间解除禁言
     */
    public static void liveCancelShutUp(String liveUid, String toUid, HttpCallback callback) {
        HttpClient.getInstance().get("Livemanage.cancelShut", LiveHttpConsts.LIVE_CANCEL_SHUT_UP)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("touid", toUid)
                .execute(callback);
    }

    /**
     * 获取直播间的拉黑列表
     */
    public static void getLiveBlackList(String liveUid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Livemanage.getKickList", LiveHttpConsts.GET_LIVE_BLACK_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 直播间解除拉黑
     */
    public static void liveCancelBlack(String liveUid, String toUid, HttpCallback callback) {
        HttpClient.getInstance().get("Livemanage.cancelKick", LiveHttpConsts.LIVE_CANCEL_BLACK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("touid", toUid)
                .execute(callback);
    }


    /**
     * 直播结束后，获取直播收益，观看人数，时长等信息
     */
    public static void getLiveEndInfo(String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.stopInfo", LiveHttpConsts.GET_LIVE_END_INFO)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 获取直播间举报内容列表
     */
    public static void getLiveReportList(HttpCallback callback) {
        HttpClient.getInstance().get("Live.getReportClass", LiveHttpConsts.GET_LIVE_REPORT_LIST)
                .execute(callback);
    }

    /**
     * 举报用户
     */
    public static void setReport(String touid, String content, HttpCallback callback) {
        HttpClient.getInstance().get("Live.setReport", LiveHttpConsts.SET_REPORT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("content", content)
                .execute(callback);
    }

    /**
     * 直播间点击聊天列表和头像出现的弹窗
     */
    public static void getLiveUser(String touid, String liveUid, HttpCallback callback) {
        HttpClient.getInstance().get("Live.getPop", LiveHttpConsts.GET_LIVE_USER)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("liveuid", liveUid)
                .execute(callback);
    }

    /**
     * 主播或管理员踢人
     */
    public static void kicking(String liveUid, String touid, HttpCallback callback) {
        HttpClient.getInstance().get("Live.kicking", LiveHttpConsts.KICKING)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("touid", touid)
                .execute(callback);
    }


    /**
     * 主播或管理员禁言
     */
    public static void setShutUp(String liveUid, String stream, int type, String touid, HttpCallback callback) {
        HttpClient.getInstance().get("Live.setShutUp", LiveHttpConsts.SET_SHUT_UP)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("type", type)
                .params("touid", touid)
                .execute(callback);
    }


    /**
     * 超管关闭直播间或禁用账户
     *
     * @param type 0表示关闭当前直播 1表示禁播，2表示封禁账号
     */
    public static void superCloseRoom(String liveUid, int type, HttpCallback callback) {
        HttpClient.getInstance().get("Live.superStopRoom", LiveHttpConsts.SUPER_CLOSE_ROOM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("type", type)
                .execute(callback);
    }


    /**
     * 守护商品类型列表
     */
    public static void getGuardBuyList(HttpCallback callback) {
        HttpClient.getInstance().get("Guard.getList", LiveHttpConsts.GET_GUARD_BUY_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 购买守护接口
     */
    public static void buyGuard(String liveUid, String stream, int guardId, HttpCallback callback) {
        HttpClient.getInstance().get("Guard.BuyGuard", LiveHttpConsts.BUY_GUARD)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("guardid", guardId)
                .execute(callback);
    }


    /**
     * 查看主播的守护列表
     */
    public static void getGuardList(String liveUid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Guard.GetGuardList", LiveHttpConsts.GET_GUARD_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 观众跟主播连麦时，获取自己的流地址
     */
    public static void getLinkMicStream(HttpCallback callback) {
        HttpClient.getInstance().get("Linkmic.RequestLVBAddrForLinkMic", LiveHttpConsts.GET_LINK_MIC_STREAM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .execute(callback);
    }

    /**
     * 主播连麦成功后，要把这些信息提交给服务器
     *
     * @param touid    连麦用户ID
     * @param pull_url 连麦用户播流地址
     */
    public static void linkMicShowVideo(String touid, String pull_url) {
        HttpClient.getInstance().get("Live.showVideo", LiveHttpConsts.LINK_MIC_SHOW_VIDEO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("pull_url", pull_url)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {

                    }
                });
    }


    /**
     * 主播设置是否允许观众发起连麦
     */
    public static void setLinkMicEnable(boolean linkMicEnable, HttpCallback callback) {
        HttpClient.getInstance().get("Linkmic.setMic", LiveHttpConsts.SET_LINK_MIC_ENABLE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("ismic", linkMicEnable ? 1 : 0)
                .execute(callback);
    }


    /**
     * 观众检查主播是否允许连麦
     */
    public static void checkLinkMicEnable(String liveUid, HttpCallback callback) {
        HttpClient.getInstance().get("Linkmic.isMic", LiveHttpConsts.CHECK_LINK_MIC_ENABLE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("liveuid", liveUid)
                .execute(callback);
    }

    /**
     * 连麦pk检查对方主播在线状态
     */
    public static void livePkCheckLive(String liveUid, String stream, String uidStream, HttpCallback callback) {
        HttpClient.getInstance().get("Livepk.checkLive", LiveHttpConsts.LIVE_PK_CHECK_LIVE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("uid_stream", uidStream)
                .execute(callback);
    }

    /**
     * 直播间发红包
     */
    public static void sendRedPack(String stream, String coin, String count, String title, int type, int sendType, HttpCallback callback) {
        HttpClient.getInstance().get("Red.SendRed", LiveHttpConsts.SEND_RED_PACK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("coin", coin)
                .params("nums", count)
                .params("des", title)
                .params("type", type)
                .params("type_grant", sendType)
                .execute(callback);
    }

    /**
     * 获取直播间红包列表
     */
    public static void getRedPackList(String stream, HttpCallback callback) {
        String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
        String cbKey = "";
        if (!TextUtils.isEmpty(configString)) {
            JSONObject obj = JSON.parseObject(configString);
             cbKey = obj.getString("cd");
            if(cbKey==null || !StringUtil.isNotBlank(cbKey)){
                cbKey="76576076c1f5f657b634e966c8836a06";
            }
        }

        String sign = MD5Util.getMD5("stream=" + stream + "&" + cbKey);
        HttpClient.getInstance().get("Red.GetRedList", LiveHttpConsts.GET_RED_PACK_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 直播间抢红包
     */
    public static void robRedPack(String stream, int redPackId, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
        String cbKey = "";
        if (!TextUtils.isEmpty(configString)) {
            JSONObject obj = JSON.parseObject(configString);
            cbKey = obj.getString("cd");
            if(cbKey==null || !StringUtil.isNotBlank(cbKey)){
                cbKey="76576076c1f5f657b634e966c8836a06";
            }
        }
        String sign = MD5Util.getMD5("redid=" + redPackId + "&stream=" + stream + "&uid=" + uid + "&" + cbKey);
        HttpClient.getInstance().get("Red.RobRed", LiveHttpConsts.ROB_RED_PACK)
                .params("uid", uid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("redid", redPackId)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 直播间红包领取详情
     */
    public static void getRedPackResult(String stream, int redPackId, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();

        String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
        String cbKey = "";
        if (!TextUtils.isEmpty(configString)) {
            JSONObject obj = JSON.parseObject(configString);
            cbKey = obj.getString("cd");
            if(cbKey==null || !StringUtil.isNotBlank(cbKey)){
                cbKey="76576076c1f5f657b634e966c8836a06";
            }
        }

        String sign = MD5Util.getMD5("redid=" + redPackId + "&stream=" + stream + "&" + cbKey);
        HttpClient.getInstance().get("Red.GetRedRobList", LiveHttpConsts.GET_RED_PACK_RESULT)
                .params("uid", uid)
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("redid", redPackId)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 发送弹幕
     */
    public static void sendDanmu(String content, String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.sendBarrage", LiveHttpConsts.SEND_DANMU)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("giftid", "1")
                .params("giftcount", "1")
                .params("content", content)
                .execute(callback);
    }

    /**
     * 检查直播间状态，是否收费 是否有密码等
     *
     * @param liveUid 主播的uid
     * @param stream  主播的stream
     */
    public static void checkLive(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.checkLive", LiveHttpConsts.CHECK_LIVE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("apk_ver", BuildConfig.VERSION_NAME)
                .execute(callback);
    }


    /**
     * 观众进入直播间
     */
    public static void enterRoom(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.enterRoom", LiveHttpConsts.ENTER_ROOM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("city", CommonAppConfig.getInstance().getCity())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("apk_ver", BuildConfig.VERSION_NAME)
                .params("v", 2)
                .execute(callback);
    }


    /**
     * 获取礼物列表，同时会返回剩余的钱
     */
    public static void getGiftList(String mark, HttpCallback callback) {
        HttpClient.getInstance().get("Live.getGiftList", LiveHttpConsts.GET_GIFT_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("mark", mark)
                .execute(callback);
    }

    /**
     * 观众给主播送礼物
     */
    public static void sendGift(String liveUid, String stream, int giftId, String giftCount, HttpCallback callback) {
        HttpClient.getInstance().get("Live.sendGift", LiveHttpConsts.SEND_GIFT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .params("giftid", giftId)
                .params("giftcount", giftCount)
                .execute(callback);
    }

    /**
     * 连麦pk搜索主播
     */
    public static void livePkSearchAnchor(String key, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Livepk.Search", LiveHttpConsts.LIVE_PK_SEARCH_ANCHOR)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("key", key)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 获取主播连麦pk列表
     */
    public static void getLivePkList(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Livepk.GetLiveList", LiveHttpConsts.GET_LIVE_PK_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 主播添加背景音乐时，搜索歌曲
     *
     * @param key      关键字
     * @param callback
     */
    public static void searchMusic(String key, HttpCallback callback) {
        HttpClient.getInstance().get("Livemusic.searchMusic", LiveHttpConsts.SEARCH_MUSIC)
                .params("key", key)
                .execute(callback);
    }

    /**
     * 获取歌曲的地址 和歌词的地址
     */
    public static void getMusicUrl(String musicId, HttpCallback callback) {
        HttpClient.getInstance().get("Livemusic.getDownurl", LiveHttpConsts.GET_MUSIC_URL)
                .params("audio_id", musicId)
                .execute(callback);
    }


    /**
     * 主播开播
     *
     * @param title    直播标题
     * @param type     直播类型 普通 密码 收费等
     * @param typeVal  密码 价格等
     * @param file     封面图片文件
     * @param callback
     */
    public static void createRoom(String title, int liveClassId, int type, int typeVal, String ticket_id, File file, HttpCallback callback) {
        CommonAppConfig appConfig = CommonAppConfig.getInstance();
        appConfig.getUserBean(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean u) {
                if (u == null) {
                    return;
                }
                PostRequest<JsonBean> request = HttpClient.getInstance().post("Live.createRoom", LiveHttpConsts.CREATE_ROOM)
                        .params("uid", appConfig.getUid())
                        .params("token", appConfig.getToken())
                        .params("user_nicename", u.getUserNiceName())
                        .params("avatar", u.getAvatar())
                        .params("avatar_thumb", u.getAvatarThumb())
                        .params("city", appConfig.getCity())
                        .params("province", appConfig.getProvince())
                        .params("lat", appConfig.getLat())
                        .params("lng", appConfig.getLng())
                        .params("title", title)
                        .params("liveclassid", liveClassId)
                        .params("type", type)
                        .params("type_val", typeVal)
                        .params("iszbyy", 0)//视频直播是 0  语音直播是 1
                        .params("ticket_id", ticket_id)
                        .params("deviceinfo", LiveConfig.getSystemParams())
                        .params("v", 2);
                if (file != null) {
                    request.params("file", file);
                }
                request.execute(callback);
            }
        });
    }

    /**
     * 修改直播状态
     */
    public static void changeLive(String stream) {
        HttpClient.getInstance().get("Live.changeLive", LiveHttpConsts.CHANGE_LIVE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("stream", stream)
                .params("status", "1")
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        L.e("开播---changeLive---->" + info[0]);
                    }
                });
    }

    /**
     * 主播结束直播
     */
    public static void stopLive(String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Live.stopRoom", LiveHttpConsts.STOP_LIVE)
                .params("stream", stream)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 主播开播前获取sdk类型  0金山  1腾讯
     */
    public static void getLiveSdk(HttpCallback callback) {
        HttpClient.getInstance().get("Live.getSDK", LiveHttpConsts.GET_LIVE_SDK)
                .execute(callback);
    }


    /**
     * 腾讯sdk 跟主播连麦时，获取主播的低延时流
     */
    public static void getTxLinkMicAccUrl(String originStreamUrl, HttpCallback callback) {
        HttpClient.getInstance().get("Linkmic.RequestPlayUrlWithSignForLinkMic", LiveHttpConsts.GET_TX_LINK_MIC_ACC_URL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("originStreamUrl", originStreamUrl)
                .execute(callback);
    }


    /**
     * 连麦时候 主播混流
     */
    public static void linkMicTxMixStream(String mergeparams) {
        HttpClient.getInstance().get("Linkmic.MergeVideoStream", LiveHttpConsts.LINK_MIC_TX_MIX_STREAM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("mergeparams", mergeparams)
                .execute(CommonHttpUtil.NO_CALLBACK);
    }


    /**
     * 我是哪些直播间的管理员，返回这些直播间列表
     */
    @Deprecated
    public static void getMyAdminRoomList(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Livemanage.GetRoomList", LiveHttpConsts.GET_MY_ADMIN_ROOM_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }


    /**
     * 获取直播间奖池等级
     */
    public static void getLiveGiftPrizePool(String liveUid, String stream, HttpCallback callback) {
        HttpClient.getInstance().get("Jackpot.GetJackpot", LiveHttpConsts.GET_LIVE_GIFT_PRIZE_POOL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", liveUid)
                .params("stream", stream)
                .execute(callback);
    }

    /**
     * 根据主播id获取游戏
     *
     * @param liveUid  主播ID，可选
     * @param callback 回调
     */
    public static void getTik(String liveUid, HttpCallback callback) {
        HttpClient.getInstance().get("Home.getTik", LiveHttpConsts.GET_TIK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("cid", liveUid)
                .execute(callback);
    }


    /**
     * 投注记录
     *
     * @param p        页数
     * @param callback 回调
     */
    public static void getBettingRecord(String tId, int p, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.USER_GET_TOSS, LiveHttpConsts.USER_GET_TOSS)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("p", p)
                .params("t_id", tId)
                .execute(callback);

    }

    /**
     * 确认投注
     *
     * @param tz       参数
     * @param callback 回调
     */
    public static void saveTicketZt(String tz, HttpCallback callback) {
        L.e("===确认投注==", tz);
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().post(ServiceName.TICKET_GET_TZ, LiveHttpConsts.TICKET_GET_TZ)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("tz", tz)
                .params("time", System.currentTimeMillis() / 1000) // 时间传秒
                .execute(callback);

    }


    /**
     * 开奖历史
     *
     * @param p        页数
     * @param callback 回调
     */
    public static void getOpenLotteryHistory(int p, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient httpClient = HttpClient.getInstance();
        httpClient.get("/index.php?g=Appapi&m=Page&a=", ServiceName.OPEN_LOTTERY_API, LiveHttpConsts.OPEN_LOTTERY_API)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("country", config.getCountry())
                .params("p", p)
                .params("pnum", 20)
                .execute(callback);

    }

    /**
     * 开奖历史明细
     *
     * @param p        页数
     * @param tId      彩票ID
     * @param callback 回调
     */
    public static void getOpenLotteryHistoryDetail(int p, String tId, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient httpClient = HttpClient.getInstance();
        httpClient.get("/index.php?g=Appapi&m=Page&a=", ServiceName.OPEN_LOTTERY_HISTORY_API, LiveHttpConsts.OPEN_LOTTERY_HISTORY_API)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("t_id", tId)
                .params("p", p)
                .params("pnum", 20)
                .execute(callback);

    }

    /**
     * 额度转换
     *
     * @param callback 回调
     */
    public static void nGAllTrans(HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient httpClient = HttpClient.getInstance();
        httpClient.post(ServiceName.NG_ALL_TRANS, LiveHttpConsts.NG_ALL_TRANS)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("username", config.getUid())
                .execute(callback);

    }

    /**
     * 额度转换
     *
     * @param callback 回调
     */
    public static void nGetTrans(String amount,String platType,int type,HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient httpClient = HttpClient.getInstance();
        httpClient.post(ServiceName.NG_TRANS, LiveHttpConsts.NG_TRANS)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("username", config.getUid())
                .params("plat_type",platType)
                .params("money", amount)
                .params("status", String.valueOf(type))
                .params("client_transfer_id", config.getUid())
                .execute(callback);

    }

    /**
     * 额度转换列表
     *
     * @param callback 回调
     */
    public static void nGAllBalance(HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient httpClient = HttpClient.getInstance();
        httpClient.get(ServiceName.NG_ALL_BALANCE, LiveHttpConsts.NG_ALL_BALANCE)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("username", config.getUid())
                .execute(callback);

    }


    /**
     * 获取视频介绍
     */
    public static void getVideoIntroduction(String liveId, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient httpClient = HttpClient.getInstance();
        httpClient.get(ServiceName.GET_LIVE_INTRODUCE, LiveHttpConsts.GET_LIVE_INTRODUCE)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("liveuid", liveId)
                .execute(callback);

    }

    /**
     * 获取分享信息
     *
     * @param callback 回调
     */
    public static void getShareInfoBrief(HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.USER_GET_SHARE_INFO_BRIEF, ServiceName.USER_GET_SHARE_INFO_BRIEF)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(callback);
    }
    /**
     *  获取GLive直播H5页面地址
     *
     * @param callback 回调
     */
    public static void getGLiveURL(String channel, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.HOME_GET_G_LIVE_URL, LiveHttpConsts.GET_G_LIVE_URL)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("channel", channel)
                .execute(callback);
    }
}
