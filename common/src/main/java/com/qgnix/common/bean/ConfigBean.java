package com.qgnix.common.bean;

import android.content.Context;
import android.util.DisplayMetrics;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by cxf on 2017/8/5.
 */

public class ConfigBean {
    private String version;//Android apk安装包 版本号
    private String downloadApkUrl;//Android apk安装包 下载地址
    private String updateDes;//版本更新描述
    private int isForce;// 1 强制更新 非1 否
    private String liveWxShareUrl;//直播间微信分享地址
    private int videoAuditSwitch;//短视频审核是否开启
    private int videoCloudType;//短视频云储存类型 1七牛云 2腾讯云
    private String videoQiNiuHost;//短视频七牛云域名
    private String txCosAppId;//腾讯云存储appId
    private String txCosRegion;//腾讯云存储区域
    private String txCosBucketName;//腾讯云存储桶名字
    private String txCosVideoPath;//腾讯云存储视频文件夹
    private String txCosImagePath;//腾讯云存储图片文件夹
    private String coinName;//钻石名称
    private String votesName;//映票名称
    private String[] liveTimeCoin;//直播间计时收费规则
    private String[] loginType;//三方登录类型
    private String[][] liveType;//直播间开播类型
    private String[] shareType;//分享类型
    private List<LiveClassBean> liveClass;//直播分类
    private int maintainSwitch;//维护开关
    private String maintainTips;//维护提示
    private String beautyKey;//萌颜鉴权码
    private int beautyMeiBai;//萌颜美白数值
    private int beautyMoPi;//萌颜磨皮数值
    private int beautyBaoHe;//萌颜饱和数值
    private int beautyFenNen;//萌颜粉嫩数值
    private int beautyBigEye;//萌颜大眼数值
    private int beautyFace;//萌颜瘦脸数值
    private String mAdInfo;//引导页 广告信息
    private String kefuUrl;//客服地址
    private String chargekefuurl;//充值客服地址
    private int Activity_Switch = 0;//活动宣传页面显示
    private String googleStore;//谷歌store开关：1开 其他：关
    private String googleThirdGame;//第3方游戏开关：1开 其他：关
    private String googleVer;//第3方游戏开关：1开 其他：关
    /**
     * 短信验证码开关 1 true ,非1 false
     */
    private int sendCodeSwitch;
    /**
     * 图片地址
     */
    private String site;
    /**
     * 翻译地址
     */
    private String transserver;
    /**
     * 筹码
     */
    private String chips;
    /**
     * 版本号码
     */
    @JSONField(name = "ticket_version")
    private int ticketVersion;
    /**
     * 用户日志服务地址
     */
    @JSONField(name = "userlogserver")
    private String userlogserver;
    /**
     * ip
     */
    private String ip;
    /**
     * 新用户注册送彩金
     */
    @JSONField(name = "new_reg_bonus")
    private String newRegBonus;
    /**
     * socket 地址
     */
    @JSONField(name = "chatserver")
    private String chatServer;

    @JSONField(name = "apk_ver")
    public String getVersion() {
        return version;
    }

    @JSONField(name = "apk_ver")
    public void setVersion(String version) {
        this.version = version;
    }

    @JSONField(name = "apk_url")
    public String getDownloadApkUrl() {
        return downloadApkUrl;
    }

    @JSONField(name = "apk_url")
    public void setDownloadApkUrl(String downloadApkUrl) {
        this.downloadApkUrl = downloadApkUrl;
    }

    @JSONField(name = "apk_des")
    public String getUpdateDes() {
        return updateDes;
    }

    @JSONField(name = "apk_des")
    public void setUpdateDes(String updateDes) {
        this.updateDes = updateDes;
    }

    @JSONField(name = "apk_is_force")
    public int getIsForce() {
        return isForce;
    }

    @JSONField(name = "apk_is_force")
    public void setIsForce(int isForce) {
        this.isForce = isForce;
    }

    @JSONField(name = "wx_siteurl")
    public void setLiveWxShareUrl(String liveWxShareUrl) {
        this.liveWxShareUrl = liveWxShareUrl;
    }

    @JSONField(name = "wx_siteurl")
    public String getLiveWxShareUrl() {
        return liveWxShareUrl;
    }

    @JSONField(name = "name_coin")
    public String getCoinName() {
        return coinName;
    }

    @JSONField(name = "name_coin")
    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    @JSONField(name = "name_votes")
    public String getVotesName() {
        return votesName;
    }

    @JSONField(name = "name_votes")
    public void setVotesName(String votesName) {
        this.votesName = votesName;
    }

    @JSONField(name = "live_time_coin")
    public String[] getLiveTimeCoin() {
        return liveTimeCoin;
    }

    @JSONField(name = "live_time_coin")
    public void setLiveTimeCoin(String[] liveTimeCoin) {
        this.liveTimeCoin = liveTimeCoin;
    }

    @JSONField(name = "login_type")
    public String[] getLoginType() {
        return loginType;
    }

    @JSONField(name = "login_type")
    public void setLoginType(String[] loginType) {
        this.loginType = loginType;
    }

    @JSONField(name = "live_type")
    public String[][] getLiveType() {
        return liveType;
    }

    @JSONField(name = "live_type")
    public void setLiveType(String[][] liveType) {
        this.liveType = liveType;
    }

    @JSONField(name = "share_type")
    public String[] getShareType() {
        return shareType;
    }

    @JSONField(name = "share_type")
    public void setShareType(String[] shareType) {
        this.shareType = shareType;
    }

    @JSONField(name = "liveclass")
    public List<LiveClassBean> getLiveClass() {
        return liveClass;
    }

    @JSONField(name = "liveclass")
    public void setLiveClass(List<LiveClassBean> liveClass) {
        this.liveClass = liveClass;
    }

    @JSONField(name = "maintain_switch")
    public int getMaintainSwitch() {
        return maintainSwitch;
    }

    @JSONField(name = "maintain_switch")
    public void setMaintainSwitch(int maintainSwitch) {
        this.maintainSwitch = maintainSwitch;
    }

    @JSONField(name = "maintain_tips")
    public String getMaintainTips() {
        return maintainTips;
    }

    @JSONField(name = "maintain_tips")
    public void setMaintainTips(String maintainTips) {
        this.maintainTips = maintainTips;
    }

    @JSONField(name = "sprout_key")
    public String getBeautyKey() {
        return beautyKey;
    }

    @JSONField(name = "sprout_key")
    public void setBeautyKey(String beautyKey) {
        this.beautyKey = beautyKey;
    }

    @JSONField(name = "sprout_white")
    public int getBeautyMeiBai() {
        return beautyMeiBai;
    }

    @JSONField(name = "sprout_white")
    public void setBeautyMeiBai(int beautyMeiBai) {
        this.beautyMeiBai = beautyMeiBai;
    }

    @JSONField(name = "sprout_skin")
    public int getBeautyMoPi() {
        return beautyMoPi;
    }

    @JSONField(name = "sprout_skin")
    public void setBeautyMoPi(int beautyMoPi) {
        this.beautyMoPi = beautyMoPi;
    }

    @JSONField(name = "sprout_saturated")
    public int getBeautyBaoHe() {
        return beautyBaoHe;
    }

    @JSONField(name = "sprout_saturated")
    public void setBeautyBaoHe(int beautyBaoHe) {
        this.beautyBaoHe = beautyBaoHe;
    }

    @JSONField(name = "sprout_pink")
    public int getBeautyFenNen() {
        return beautyFenNen;
    }

    @JSONField(name = "sprout_pink")
    public void setBeautyFenNen(int beautyFenNen) {
        this.beautyFenNen = beautyFenNen;
    }

    @JSONField(name = "sprout_eye")
    public int getBeautyBigEye() {
        return beautyBigEye;
    }

    @JSONField(name = "sprout_eye")
    public void setBeautyBigEye(int beautyBigEye) {
        this.beautyBigEye = beautyBigEye;
    }

    @JSONField(name = "sprout_face")
    public int getBeautyFace() {
        return beautyFace;
    }

    @JSONField(name = "sprout_face")
    public void setBeautyFace(int beautyFace) {
        this.beautyFace = beautyFace;
    }


    public String[] getVideoShareTypes() {
        return shareType;
    }

    @JSONField(name = "video_audit_switch")
    public int getVideoAuditSwitch() {
        return videoAuditSwitch;
    }

    @JSONField(name = "video_audit_switch")
    public void setVideoAuditSwitch(int videoAuditSwitch) {
        this.videoAuditSwitch = videoAuditSwitch;
    }

    @JSONField(name = "cloudtype")
    public int getVideoCloudType() {
        return videoCloudType;
    }

    @JSONField(name = "cloudtype")
    public void setVideoCloudType(int videoCloudType) {
        this.videoCloudType = videoCloudType;
    }

    @JSONField(name = "qiniu_domain")
    public String getVideoQiNiuHost() {
        return videoQiNiuHost;
    }

    @JSONField(name = "qiniu_domain")
    public void setVideoQiNiuHost(String videoQiNiuHost) {
        this.videoQiNiuHost = videoQiNiuHost;
    }

    @JSONField(name = "txcloud_appid")
    public String getTxCosAppId() {
        return txCosAppId;
    }

    @JSONField(name = "txcloud_appid")
    public void setTxCosAppId(String txCosAppId) {
        this.txCosAppId = txCosAppId;
    }

    @JSONField(name = "txcloud_region")
    public String getTxCosRegion() {
        return txCosRegion;
    }

    @JSONField(name = "txcloud_region")
    public void setTxCosRegion(String txCosRegion) {
        this.txCosRegion = txCosRegion;
    }

    @JSONField(name = "txcloud_bucket")
    public String getTxCosBucketName() {
        return txCosBucketName;
    }

    @JSONField(name = "txcloud_bucket")
    public void setTxCosBucketName(String txCosBucketName) {
        this.txCosBucketName = txCosBucketName;
    }

    @JSONField(name = "kefu_url")
    public String getKefuUrl() {
        return kefuUrl;
    }

    @JSONField(name = "kefu_url")
    public void setKefuUrl(String kefuUrl) {
        this.kefuUrl = kefuUrl;
    }


    @JSONField(name = "charge_kefu_url")
    public String getChargeKefuUrl() {
        return chargekefuurl;
    }

    @JSONField(name = "charge_kefu_url")
    public void setChargeKefuUrl(String chargekefuurl) {
        this.chargekefuurl = chargekefuurl;
    }

    @JSONField(name = "txvideofolder")
    public String getTxCosVideoPath() {
        return txCosVideoPath;
    }

    @JSONField(name = "txvideofolder")
    public void setTxCosVideoPath(String txCosVideoPath) {
        this.txCosVideoPath = txCosVideoPath;
    }

    @JSONField(name = "tximgfolder")
    public String getTxCosImagePath() {
        return txCosImagePath;
    }

    @JSONField(name = "tximgfolder")
    public void setTxCosImagePath(String txCosImagePath) {
        this.txCosImagePath = txCosImagePath;
    }

    @JSONField(name = "guide")
    public String getAdInfo() {
        return mAdInfo;
    }

    @JSONField(name = "guide")
    public void setAdInfo(String adInfo) {
        mAdInfo = adInfo;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @JSONField(name = "sendcode_switch")
    public int getSendCodeSwitch() {
        return sendCodeSwitch;
    }

    @JSONField(name = "sendcode_switch")
    public void setSendCodeSwitch(int sendCodeSwitch) {
        this.sendCodeSwitch = sendCodeSwitch;
    }

    public String getTransserver() {
        return transserver;
    }

    public void setTransserver(String transserver) {
        this.transserver = transserver;
    }

    public String getChips() {
        return chips;
    }

    public void setChips(String chips) {
        this.chips = chips;
    }

    @JSONField(name = "activity_switch")
    public int getActivity_Switch() {
        return Activity_Switch;
    }

    @JSONField(name = "activity_switch")
    public void setActivity_Switch(int Activity_Switch) {
        this.Activity_Switch = Activity_Switch;
    }

    public int getTicketVersion() {
        return ticketVersion;
    }

    public void setTicketVersion(int ticketVersion) {
        this.ticketVersion = ticketVersion;
    }

    public String getUserlogserver() {
        return userlogserver;
    }

    public void setUserlogserver(String userlogserver) {
        this.userlogserver = userlogserver;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGoogleThirdGame() {
        return googleThirdGame;
    }

    public void setGoogleThirdGame(String googleThirdGame) {
        this.googleThirdGame = googleThirdGame;
    }

    public String getGoogleVer() {
        return googleVer;
    }

    public void setGoogleVer(String googleVer) {
        this.googleVer = googleVer;
    }

    public String getGoogleStore() {
        return googleStore;
    }

    public void setGoogleStore(String googleStore) {
        this.googleStore = googleStore;
    }

    public String getNewRegBonus() {
        return newRegBonus;
    }

    public void setNewRegBonus(String newRegBonus) {
        this.newRegBonus = newRegBonus;
    }

    public String getChatServer() {
        //chatServer =  "https://p.cherry1000.com,https://p.cherry8000.com";
        //服务端返回 逗号隔开的多个地址, 前端随机获取一个 起到负载均衡的目的
        if(chatServer!=null && chatServer.contains(",")){
            return chatServer.split(",")[new Random().nextInt(chatServer.split(",").length)];
        }else{
            return chatServer;
        }
    }

    public void setChatServer(String chatServer) {
        this.chatServer = chatServer;
    }







    //===========================code=================================//


    private static Random random = new Random();
    private static int current =0;
    private static int random_int =0;

    public static int getRandom(int length){


        while(current==random_int){
            random_int =  random.nextInt(length);
        }
        current = random_int;
        return current;
    }
    //随机整数
    public static Integer[] randomArr(int max,int randomSize)
    {

        try {
            if(max<randomSize){
                randomSize=max;
            }
            Random random = new Random();
            // Object[] values = new Object[randomSize];
            HashSet<Integer> hashSet = new LinkedHashSet<Integer>();

            // 生成随机数字并存入HashSet
            while(hashSet.size() < randomSize){
                hashSet.add(random.nextInt(max) + 1);
            }
            Integer[] its=new Integer[randomSize];
            Iterator<Integer> it=hashSet.iterator();
            int i=0;
            while(it.hasNext()){
                its[i]=it.next();
                i++;
            }
            return its;

        } catch (Exception e) {

        }
        return new Integer[]{} ;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static float getScreenDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * pxתdp
     * @param id
     * @param context
     * @return
     */
    public static int getDimenT(int id,Context context) {
        try {
            return (int) (id * getScreenDensity(context));
        } catch (Exception e) {
            return 0;
        }
    }
    public static void checkUpdate(final Context activity,final boolean flag) {

    }

    private  static  int[] colors = { };
    //===========================================================//
}
