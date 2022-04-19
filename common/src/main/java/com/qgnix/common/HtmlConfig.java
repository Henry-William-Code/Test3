package com.qgnix.common;

/**
 * Created by cxf on 2018/10/15.
 */

public class HtmlConfig {

    //登录即代表同意服务和隐私条款
    public static final String LOGIN_PRIVCAY = CommonAppConfig.getCurHost() + "/index.php?g=portal&m=page&a=index&id=4";
    //直播间贡献榜
    public static final String LIVE_LIST = CommonAppConfig.getCurHost() + "/index.php?g=Appapi&m=contribute&a=index&uid=";
    //个人主页分享链接
    public static final String SHARE_HOME_PAGE = CommonAppConfig.getCurHost() + "/index.php?g=Appapi&m=home&a=index&touid=";
    //提现记录
    public static final String CASH_RECORD = CommonAppConfig.getCurHost() + "/index.php?g=Appapi&m=cash&a=index";
    //支付宝回调地址
    public static final String ALI_PAY_NOTIFY_URL = CommonAppConfig.getCurHost() + "/Appapi/Pay/notify_ali";
    //视频分享地址
    public static final String SHARE_VIDEO = CommonAppConfig.getCurHost() + "/index.php?g=appapi&m=video&a=index&videoid=";
    //直播间幸运礼物说明
    public static final String LUCK_GIFT_TIP = CommonAppConfig.getCurHost() + "/index.php?g=portal&m=page&a=index&id=26";
    //AwePay-Walaopay马来第三方支付
    public static final String WALAO_PAY_RUL = CommonAppConfig.getCurHost() + "/index.php?g=Appapi&m=Awepay";
    //Gspay马来第三方支付
    public static final String GS_PAY_RUL = CommonAppConfig.getCurHost() + "/index.php?g=Appapi&m=gspay";
    //Eeziepay马来第三方支付
    public static final String Eezie_PAY_RUL = CommonAppConfig.getCurHost() + "/index.php?g=Appapi&m=Eeziepay";
    // 我的认证
    public static final String MY_CERTIFICATION = CommonAppConfig.getCurHost() + "/index.php?g=Appapi&m=Auth&a=index";
    // 开奖记录
    public static final String OPEN_LOTTERY = CommonAppConfig.getCurHost() + "/index.php?g=Appapi&m=Page&a=openLottery";
    // 账户明细
    public static final String ACCOUNT_DETAIL = CommonAppConfig.getCurHost() + "/index.php?g=Appapi&m=Detail&a=index";
    // 帮助中心
    public static final String HELP_CENTER = CommonAppConfig.getCurHost() + "/index.php?g=portal&m=page&a=lists";
    //隐私协议
    public static final String PRIVACY_AGREEMENT = CommonAppConfig.getCurHost() + "/index.php?g=portal&m=page&a=index&id=3";
    // 关于我们
    public static final String ABOUT_US = CommonAppConfig.getCurHost() + "/index.php?g=portal&m=page&a=index&id=5";
    //绑定银行卡送币活动页面
    public static final String TIECARD = "/tiecardinfo.html?f=popup";
    //活动页面
    public static final String ACTIVITYINFO = "/activityinfo.html?f=popup";
    //主播工资提现
    public static final String ANCHOR_SALARY_WITHDRAW = CommonAppConfig.getCurHost() + "/index.php?g=Appapi&m=Auth&a=cash";
    // 优惠券
    public static final String  COUPON= CommonAppConfig.getCurHost() + "/index.php?g=Appapi&m=Coupon&a=Exchangecoupons";

}
