package com.qgnix.common.http;

import com.qgnix.common.CommonAppConfig;

/**
 * 接口名称
 */
public class ServiceName {

    /*================我的模块相关接口========================*/
    // 获取公告
    public static final String HOME_GET_INDEX_MSG = "Home.getIndexMsg_v2";
    // 直播tab
    public static final String HOME_GET_LIVEWCATE = "Home.getLivecate";
    /*================游戏模块相关接口========================*/
    // 游戏菜单
//    public static final String NG_CATEGORY = "Ng.Category_v2";
//    // 游戏列表
//    public static final String NG_GAME_CODE = "Ng.Gamecode_v2";
//    // 彩票游戏
//    public static final String HOME_GET_ADAS = "Home.getAdas_v2";
//    // 注册游戏
//    public static final String NG_REGISTER = "Ng.Register_v2";
//    // 登录游戏
//    public static final String NG_LOGIN = "Ng.Login_v2";
//    // 彩票帮助信息
//    public static final String TICKET_GET_HELP = "Ticket.Gethelp";
//    // 一键额度转换
//    public static final String NG_ALL_TRANS = "Ng.AllTrans_v2";
//    // 额度转换
//    public static final String NG_TRANS = "Ng.Trans_v2";
//    // 额度转换列表
//    public static final String NG_ALL_BALANCE= "Ng.AllBalance_v2";


     // 游戏菜单
    public static final String NG_CATEGORY = "ThirdGame.Category_v2";
    // 游戏列表
    public static final String NG_GAME_CODE = "ThirdGame.Gamecode_v2";
    // 彩票游戏
    public static final String HOME_GET_ADAS = "Home.getAdas_v2";
    // 注册游戏
    public static final String NG_REGISTER = "ThirdGame.Register_v2";
    // 登录游戏
    public static final String NG_LOGIN = "ThirdGame.Login_v2";
    // 彩票帮助信息
    public static final String TICKET_GET_HELP = "Ticket.Gethelp";
    // 一键额度转换
    public static final String NG_ALL_TRANS = "ThirdGame.AllTrans_v2";
    // 额度转换
    public static final String NG_TRANS = "ThirdGame.Trans_v2";
    // 额度转换列表
    public static final String NG_ALL_BALANCE= "ThirdGame.AllBalance_v2";

    /*================BC游戏模块相关接口========================*/
    // 彩票投注
    public static final String BC_TICKET_GET_ODDS_V2 = "Ticket.Getodds_v2";

    public static final String BC_TICKET_GET_QS = "Ticket.getqs";
    //彩种
    public static final String BC_TICKET_GET_TICKET = "Ticket.getTicket";
    public static final String BC_TICKET_GET_TICKET_AND_THIRD_GAME = "Ticket.getTicketAndThirdGame";
    /*================我的模块相关接口========================*/
    // banner
    public static final String HOME_GET_SLIDE = "Home.getSlide";
    // 热门直播
//    public static final String HOME_GET_HOT = "Home.getHot_v2";
    public static final String HOME_GET_HOT = "Home.getHot_v3";
    public static final String HOME_GET_G_LIVE_URL = "Home.GetGLiveURL";
    // 首页游戏
    public static final String HOME_GET_AD = "Home.getAd_v2";
    // 支付列表（暂时不用）
    public static final String USER_PAY_LIST = "User.getPaylist";
    // 线下充值账号获取接口 - 银行卡/支付宝 信息
    public static final String USER_ADMIN_BANK = "User.getAdminbank";
    // 线下充值
    public static final String CHARGE_CHARGE_OFFLINE = "Charge.ChargeOffline";
    // 提现页获取银行卡信息
    public static final String USER_GET_USER_ACCOUNT_LIST = "User.getUserAccountList";

    // 获取银行
    public static final String USER_BANK_LIST = "User.getBankList";
    // 获取币种
    public static final String USER_CURRENCY = "User.getCurrency";

    // 提现接口
    public static final String USER_SET_CASH = "User.SetCash";
    // 设置交易密码
    public static final String USER_UPDATE_WITHDRAWAL_PASS = "User.UpdateWithdrawal_Pass";
    // 重置交易密码
    public static final String USER_WITHDRAWAL_FIND_PASS = "User.UserWithdrawalFindPass";
    // 投注记录
    public static final String USER_GET_TOSS = "user.getToss";

    // 确认投注
    public static final String TICKET_GET_TZ = "Ticket.gettz";

    // 开奖历史
    public static final String OPEN_LOTTERY_API = "openLotteryApi";
    // 开奖历史明细
    public static final String OPEN_LOTTERY_HISTORY_API = "openLotteryHistoryApi";
    // 视频简介
    public static final String GET_LIVE_INTRODUCE = "Live.getLiveintroduce";


    /*================推广赚钱========================*/
    //我的分销
    public static final String AGENT_GET_AGENT_SUMMARY = "Agent.GetAgentSummary";
    public static final String AGENT_GET_SUB_MEMBER = "Agent.Getsubmember";
    public static final String AGENT_GET_SUB_MEMBER_NEW = "Agent.getSubMember";
    public static final String AGENT_GET_SUB_MEMBER_FIT_RECORD = "Agent.Getsubmemberfitrecord";
    // 赚钱
    public static final String USER_GET_SHARE_INFO = "User.getShareinfo";
    // 分享弹出框
    public static final String USER_GET_SHARE_INFO_BRIEF = "User.getShareinfoBrief";

    // 添加银行卡
    public static final String USER_SET_USER_BANK = "User.setUsersbank";
    // 充值配置
    public static final String CHARGE_GET_PAY = "Charge.LinearLayout";
    // 支付渠道信息
    public static final String CHARGE_GET_PAYS = "Charge.GetPays_v2";
    //获取验证码
    public static final String CHECK_CODE = CommonAppConfig.getCurHost() + "/api/public/?service=Login.captch&device_id=";

    /*================消息========================*/
    public static final String USER_MESSAGE_GET_COUNT_UNREAD = "UserMessage.CountUnread";
    public static final String USER_MESSAGE_GET_LIST = "UserMessage.List";
    public static final String USER_MESSAGE_DELETE = "UserMessage.Delete";//删除消息

}

