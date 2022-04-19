package com.qgnix.main.http;

import android.util.Log;

import com.alibaba.android.arouter.utils.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.bean.VersionBean;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.http.HttpClient;
import com.qgnix.common.http.JsonBean;
import com.qgnix.common.http.ServiceName;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.MD5Util;
import com.qgnix.common.utils.SpUtil;
import com.qgnix.common.utils.StringUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.LiveConfig;

import java.io.File;
import java.util.Map;

import static com.qgnix.common.utils.DpUtil.SALT;

/**
 * Created by cxf on 2018/9/17.
 */

public class MainHttpUtil {

    public static final String DEVICE = "android";
    public static String cb = "";

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }

    /**
     * 手机号 密码登录
     */
    public static void login(String phoneNum, String pwd, String deviceId, HttpCallback callback) {
        HttpClient.getInstance().post("Login.userLogin", MainHttpConsts.LOGIN)
                .params("user_login", phoneNum)
                .params("user_pass", pwd)
                .params("deviceinfo", LiveConfig.getSystemParams())
                .params("device_id", deviceId)
                .params("v", "a_" + CommonAppConfig.getInstance().getVersion())
                .execute(callback);
    }

    /**
     * 请求签到奖励
     */
    public static void requestBonus(HttpCallback callback) {
        HttpClient.getInstance().get("User.Bonus", MainHttpConsts.REQUEST_BONUS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取签到奖励
     */
    public static void getBonus(String deviceId, HttpCallback callback) {
        HttpClient.getInstance().get("User.getBonus_v2", MainHttpConsts.GET_BONUS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("device_id", deviceId)
                .execute(callback);
    }

    /**
     * 用户手动取消绑定银行卡
     */
    public static void setCanceltieCard(HttpCallback callback) {
        HttpClient.getInstance().get("User.setCanceltiebank", MainHttpConsts.SET_CANCELTIEBANK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 用于用户首次登录设置分销关系
     */
    public static void setDistribut(String deviceId, String code, HttpCallback callback) {
        HttpClient.getInstance().get("User.setDistribut", MainHttpConsts.SET_DISTRIBUT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("code", code)
                .params("device_id", deviceId)
                .execute(callback);
    }

    /**
     * 首页
     */
    public static void getClassLive(Map<String, String> map, HttpCallback callback) {
        HttpClient.getInstance().get("Home.getClassLiveV3", MainHttpConsts.GET_LIVECATE)
                .params(map)
                .execute(callback);
    }


    //排行榜  收益榜
    public static void profitList(String type, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.profitList", MainHttpConsts.PROFIT_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("type", type)
                .params("p", p)
                .execute(callback);
    }

    //排行榜  贡献榜
    public static void consumeList(String type, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.consumeList", MainHttpConsts.CONSUME_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("type", type)
                .params("p", p)
                .execute(callback);

    }


    /**
     * 用户个人主页信息
     */
    public static void getUserHome(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("User.getUserHome", MainHttpConsts.GET_USER_HOME)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 获取游戏币余额
     *
     * @param callback
     */
    public static void getBalance(final HttpCallback callback, final String platType) {
        CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                HttpClient.getInstance().get("ThirdGame.AllBalance_v2", MainHttpConsts.HOME_GET_ADAS)
                        .params("uid", CommonAppConfig.getInstance().getUid())
                        .params("token", CommonAppConfig.getInstance().getToken())
                        .params("plat_type", platType)
                        .params("username", bean.getId())
                        .execute(callback);
            }
        });
    }


    /**
     * 搜索
     */
    public static void search(String key, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.search", MainHttpConsts.SEARCH)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("key", key)
                .params("p", p)
                .execute(callback);
    }



    /**
     * 搜索
     */
    public static void searchLiveRoom(String key, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.search", MainHttpConsts.SEARCH)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("key", key)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取对方的关注列表
     *
     * @param touid 对方的uid
     */
    public static void getFollowList(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("User.getFollowsList", MainHttpConsts.GET_FOLLOW_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取对方的粉丝列表
     *
     * @param touid 对方的uid
     */
    public static void getFansList(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("User.getFansList", MainHttpConsts.GET_FANS_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("p", p)
                .execute(callback);

    }

    /**
     * 上传头像，用post
     */
    public static void updateAvatar(File file, HttpCallback callback) {
        HttpClient.getInstance().post("User.updateAvatar", MainHttpConsts.UPDATE_AVATAR)
                .isMultipart(true)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("file", file)
                .execute(callback);
    }

    /**
     * 更新用户资料
     *
     * @param fields 用户资料 ,以json形式出现
     */
    public static void updateFields(String fields, HttpCallback callback) {
        HttpClient.getInstance().get("User.updateFields", MainHttpConsts.UPDATE_FIELDS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("fields", fields)
                .execute(callback);
    }

    /**
     * 获取 提现账户列表
     */
    public static void getCashAccountList(HttpCallback callback) {
        HttpClient.getInstance().get(ServiceName.USER_GET_USER_ACCOUNT_LIST, MainHttpConsts.GET_USER_ACCOUNT_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 分类直播
     */
    public static void getClassLive(int classId, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.getClassLiveV3", MainHttpConsts.GET_CLASS_LIVE)
                .params("liveclassid", classId)
                .params("iszbyy", 0)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取自己收到的主播印象列表
     */
    public static void getMyImpress(HttpCallback callback) {
        HttpClient.getInstance().get("User.GetMyLabel", MainHttpConsts.GET_MY_IMPRESS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 用于用户首次登录推荐
     */
    @Deprecated
    public static void getRecommend(HttpCallback callback) {
        HttpClient.getInstance().get("Home.getRecommend", MainHttpConsts.GET_RECOMMEND)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .execute(callback);
    }


    /**
     * 用于用户首次登录推荐,关注主播
     */
    @Deprecated
    public static void recommendFollow(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("Home.attentRecommend", MainHttpConsts.RECOMMEND_FOLLOW)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 获取验证码接口 注册用
     */
    public static void getRegisterCode(String mobile,String deviceId, HttpCallback callback) {
        String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
        if (!android.text.TextUtils.isEmpty(configString)) {
            JSONObject obj = JSON.parseObject(configString);
            MainHttpUtil.cb = obj.getString("cd");
        }
        //去掉加号跟服务端规则匹配
        mobile = mobile.startsWith("+") ? mobile.substring(1) : mobile;
        String sign = MD5Util.getMD5("mobile=" + mobile + "&" + MainHttpUtil.cb);
        HttpClient.getInstance().get("Login.getCode", MainHttpConsts.GET_REGISTER_CODE)
                .params("mobile", mobile)
                .params("sign", sign)
                .params("device_id", deviceId)
                .params("source", "android2")
                .execute(callback);
    }

    /**
     * 手机注册接口
     */
    public static void register(Map<String, String> params, HttpCallback callback) {
        HttpClient.getInstance().get("Login.userRegNew", MainHttpConsts.REGISTER)
                .params(params)
                .execute(callback);
    }

    /**
     * 找回密码
     */
    public static void findPwd(String user_login, String pass, String pass2, String code, String country, HttpCallback callback) {
        HttpClient.getInstance().get("Login.userFindPass", MainHttpConsts.FIND_PWD)
                .params("user_login", user_login)
                .params("user_pass", pass)
                .params("user_pass2", pass2)
                .params("code", code)
                .params("country", country)
                .execute(callback);
    }


    /**
     * 重置密码
     */
    public static void modifyPwd(String oldpass, String pass, String pass2, HttpCallback callback) {
        HttpClient.getInstance().get("User.updatePass", MainHttpConsts.MODIFY_PWD)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("oldpass", oldpass)
                .params("pass", pass)
                .params("pass2", pass2)
                .execute(callback);
    }


    /**
     * 获取验证码接口 找回密码用
     */
    public static void getFindPwdCode(String mobile, String deviceId,HttpCallback callback) {

        String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
        String cbKey = "";
        if (!android.text.TextUtils.isEmpty(configString)) {
            JSONObject obj = JSON.parseObject(configString);
            cbKey = obj.getString("cd");
            if(cbKey==null || !StringUtil.isNotBlank(cbKey)){
                cbKey="76576076c1f5f657b634e966c8836a06";
            }
        }


        //去掉加号跟服务端规则匹配
        mobile = mobile.startsWith("+") ? mobile.substring(1) : mobile;
        String sign = MD5Util.getMD5("mobile=" + mobile + "&" + cbKey);
        HttpClient.getInstance().get("Login.getForgetCode", MainHttpConsts.GET_FIND_PWD_CODE)
                .params("mobile", mobile)
                .params("source", "android2")
                .params("sign", sign)
                .params("device_id", deviceId)
                .execute(callback);
    }

    /**
     * 获取区号
     *
     * @param callback
     */

    public static void getPhoneRuleList(HttpCallback callback) {
        HttpClient.getInstance().get("Login.GetPhoneRules", MainHttpConsts.GetPhoneRules)
                .execute(callback);
    }


    /*================直播模块相关接口========================*/

    /**
     * 获取直播tab
     *
     * @param callback 回调
     */
    public static void getLivecate(HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.HOME_GET_LIVEWCATE, MainHttpConsts.MAIN_LIVE_VIEW_HOLDER)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(callback);
    }
    /*================游戏模块相关接口========================*/

    /**
     * 用于 游戏里需要查看帮助接口
     *
     * @param callback 回调
     */
    public static void getTicketHelp(String ticketId, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.TICKET_GET_HELP, MainHttpConsts.TICKET_GET_HELP)
                .params("ticketid", ticketId)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(callback);
    }


    /**
     * 获取游戏分类
     *
     * @param callback 回调
     */
    public static void getNgCategory(final CommonCallback<String> callback) {
        String category_v2 = SpUtil.getInstance().getStringValue(SpUtil.CATEGORY_V2, "");
        if (!TextUtils.isEmpty(category_v2)) {
            callback.callback(category_v2);
            return;
        }
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.NG_CATEGORY, MainHttpConsts.NG_CATEGORY)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code != 0 || info.length == 0) {
                            ToastUtil.show(msg);
                            return;
                        }
                        SpUtil.getInstance().setStringValue(SpUtil.CATEGORY_V2, info[0]);
                        callback.callback(info[0]);
                    }
                });
    }

    /**
     * 根据分类获取游戏信息
     *
     * @param callback 回调
     */
    public static void getNgGameCode(String platType, String gameType, String gameCode, String providerType, int p, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.NG_GAME_CODE, MainHttpConsts.NG_GAME_CODE)
                .params("plat_type", platType)
                .params("game_type", gameType)
                .params("game_code", gameCode)
                .params("provider_type", providerType)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("p", p)
                .params("pnum", 21)
                .execute(callback);
    }


    /**
     * 获取彩票游戏
     *
     * @param callback 回调
     */
    public static void getTicketAdas(String platType, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.HOME_GET_ADAS, MainHttpConsts.HOME_GET_ADAS)
                .params("plat_type", platType)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("country", config.getCountry())
                .execute(callback);
    }

    /**
     * 注册gn游戏
     *
     * @param callback 回调
     */
    public static void ngRegister(String gameCode, String gameType, String platType, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.NG_REGISTER, MainHttpConsts.NG_REGISTER)
                .params("plat_type", platType)
                .params("game_type", gameType)
                .params("game_code", gameCode)
                .params("username", config.getUid())
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(callback);
    }





    /*================我的模块相关接口========================*/


    /**
     * 获取银行卡信息
     *
     * @param callback 回调
     */
    public static void getBankInfo(String id, String currency, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.USER_ADMIN_BANK, MainHttpConsts.TOP_UP_ACT)
                .params("id", id)
                .params("currency", currency)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(callback);
    }

    /**
     * 线下充值
     *
     * @param type           type=1支付宝 2微信 3苹果支付 4wolaopay 21 线下支付宝 22线下微信 23线下苹果支付 24线下银行卡
     * @param money          充值金额
     * @param remittanceName 汇款姓名
     * @param remark         汇款备注
     * @param currency       币种
     * @param callback       回调
     */
    public static void chargeOffline(int type, String money, String remittanceName, String remark, String currency, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().post(ServiceName.CHARGE_CHARGE_OFFLINE, MainHttpConsts.TOP_UP_ACT)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("type", type)
                .params("money", money)
                .params("remitter", remittanceName)
                .params("remittanceNote", remark)
                .params("currency", currency)
                .execute(callback);
    }

    /**
     * banner数据
     */

    public static void getBannerInfo(final CommonCallback<String> callback) {
        String slide = SpUtil.getInstance().getStringValue(SpUtil.SLIDE, "");
        if (!TextUtils.isEmpty(slide)) {
            callback.callback(slide);
            return;
        }
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.HOME_GET_SLIDE, MainHttpConsts.HOME_GET_SLIDE)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("isAdult", CommonAppConfig.ADULT_MODE)
                .execute((new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code != 0 || info.length == 0) {
                            ToastUtil.show(msg);
                            return;
                        }
                        SpUtil.getInstance().setStringValue(SpUtil.SLIDE, info[0]);
                        callback.callback(info[0]);
                    }
                }));
    }

    /**
     * 获取 银行卡参数表
     */
    public static void getBankList(String currency, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.USER_BANK_LIST, MainHttpConsts.GET_BANK_LIST)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("currency", currency)
                .execute(callback);
    }

    /**
     * 获取货币类型
     */
    public static void getCurrency(HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.USER_CURRENCY, MainHttpConsts.USER_CURRENCY)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(callback);
    }

    /**
     * 提现接口
     *
     * @param balanceType    余额类型0金币,1门票,2分成提现
     * @param withdrawalpass 提现密码
     * @param accountId      账户ID
     * @param money          金额
     * @param callback       回调
     */
    public static void setUserCash(int balanceType, String withdrawalpass, String accountId, String money, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().post(ServiceName.USER_SET_CASH, MainHttpConsts.USER_SET_CASH)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("balance_type", balanceType)
                .params("withdrawalpass", withdrawalpass)
                .params("accountid", accountId)
                .params("cashvote", money)
                .execute(callback);

    }

    /**
     * 设置交易密码
     *
     * @param oldPass     初始为登录密码，修改时为提现密码
     * @param newPass     新密码
     * @param confirmPass 确认密码
     * @param callback    回调
     */
    public static void setTransactionPwd(String oldPass, String newPass, String confirmPass, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().post(ServiceName.USER_UPDATE_WITHDRAWAL_PASS, MainHttpConsts.SET_TRANSACTION_PWD_ACT)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("oldpass", oldPass)
                .params("pass", newPass)
                .params("pass2", confirmPass)
                .execute(callback);

    }

    /**
     * 重置交易密码
     *
     * @param loginPhone  手机账号
     * @param newPass     新密码
     * @param confirmPass 确认密码
     * @param checkCode   验证码
     * @param callback    回调
     */
    public static void resetTransactionPwd(String loginPhone, String newPass, String confirmPass, String checkCode, String country, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().post(ServiceName.USER_WITHDRAWAL_FIND_PASS, MainHttpConsts.RESET_TRANSACTION_PWD_ACT)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("user_login", loginPhone)
                .params("withdrawalpass", newPass)
                .params("withdrawalpass2", confirmPass)
                .params("code", checkCode)
                .params("country", country)
                .execute(callback);

    }
    /*================推广赚钱========================*/

    /**
     * 我的分销
     *
     * @param callback 回调
     */
    public static void getAgentSummary(HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.AGENT_GET_AGENT_SUMMARY, MainHttpConsts.AGENT_GET_AGENT_SUMMARY)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(callback);

    }

    /**
     * 我的下属信息
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param callback  回调
     */
    public static void getAgentSubMemberNew(int page, String startTime, String endTime, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.AGENT_GET_SUB_MEMBER_NEW, MainHttpConsts.AGENT_GET_SUB_MEMBER_NER)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("p", page)
                .params("startTime", startTime)
                .params("endTime", endTime)
                .execute(callback);

    }

    /**
     * 我的下线
     *
     * @param keywords 搜索关键字
     * @param p        页码
     * @param callback 回调
     */
    public static void getAgentSubMember(String keywords, int p, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.AGENT_GET_SUB_MEMBER, MainHttpConsts.AGENT_GET_SUB_MEMBER)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("suidlogin", keywords)
                .params("p", p)
                .execute(callback);

    }

    /**
     * 我的报表
     *
     * @param p        页码
     * @param callback 回调
     */
    public static void getAgentSubRecord(int p, String mtype, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.AGENT_GET_SUB_MEMBER_FIT_RECORD, MainHttpConsts.AGENT_GET_SUB_MEMBER_FIT_RECORD)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("p", p)
                .params("type", mtype)
                .execute(callback);

    }

    /**
     * 获取分享信息
     *
     * @param callback 回调
     */
    public static void getShareInfo(HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.USER_GET_SHARE_INFO, MainHttpConsts.USER_GET_SHARE_INFO)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(callback);

    }

    /**
     * 添加银行卡
     *
     * @param callback 回调
     */
    public static void addUserBank(String currency, String bank, String cardholdersName, String bankCardNo, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().post(ServiceName.USER_SET_USER_BANK, MainHttpConsts.USER_SET_USER_BANK)
                .params("currency", currency)
                .params("bank_name", bank)
                .params("bank_number", bankCardNo)
                .params("name", cardholdersName)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(callback);
    }

    /**
     * 获取充值配置信息
     *
     * @param callback 回调
     */
    @Deprecated
    public static void chargePay(HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.CHARGE_GET_PAY, MainHttpConsts.CHARGE_GET_PAY)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(callback);
    }

    /**
     * 获取充值渠道信息
     *
     * @param callback 回调
     */
    public static void rechargePay(HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.CHARGE_GET_PAYS, MainHttpConsts.CHARGE_GET_PAYS)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("country", config.getCountry())
                .execute(callback);
    }

    /**
     * 获取公告
     *
     * @param callback 回调
     */
    public static void getNotice(HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.HOME_GET_INDEX_MSG, MainHttpConsts.HOME_GET_INDEX_MSG)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(callback);
    }

    /**
     * 获取首页热门直播
     *
     * @param callback 回调
     */
    public static void getHotData(int p, long searchTime, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.HOME_GET_HOT, MainHttpConsts.HOME_GET_HOT)
                .params("p", p)
                .params("searchTime", searchTime)
                .params("pnum", 20)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("isAdult", CommonAppConfig.ADULT_MODE)
                .execute(callback);
    }

    /**
     * 获取首页游戏
     *
     * @param callback 回调
     */
    public static void getHomeAdData(HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.HOME_GET_AD, MainHttpConsts.HOME_GET_AD)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("country", "IN")
                .execute(callback);
    }

    /**
     * 获取用户未读消息数量
     */
    public static void getCountUnread(HttpCallback callback) {
        HttpClient.getInstance().get(ServiceName.USER_MESSAGE_GET_COUNT_UNREAD, MainHttpConsts.MESSAGE_GET_COUNT_UNREAD)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 读取一定条数用户消息, 并设置为已读, 消息按时间倒序排列
     */
    public static void getMsgList(int type, int oldestId, HttpCallback callback) {
        GetRequest getRequest = HttpClient.getInstance().get(ServiceName.USER_MESSAGE_GET_LIST, MainHttpConsts.MESSAGE_GET_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("type", type);
        if (oldestId > -1) {
            getRequest.params("oldestId", oldestId);
        }
        getRequest.execute(callback);
    }

    /**
     * 删除指定id的消息
     */
    public static void deleteMsg(int msgId, HttpCallback callback) {
        GetRequest getRequest = HttpClient.getInstance().get(ServiceName.USER_MESSAGE_DELETE, MainHttpConsts.MESSAGE_DELETE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("mid", msgId);
        getRequest.execute(callback);
    }

    /**
     * 获取钱包信息
     *
     * @param callback
     */
    public static void getWalletInfo(final HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        HttpClient.getInstance().get("", "/wallet/api/users/v1/" + uid, CommonHttpConsts.GET_BASE_INFO)
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 钱包转入金额
     *
     * @param callback
     */
    public static void walletTransferIn(String amount, final HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        HttpClient.getInstance().post("", "/wallet/api/users_wallet/v1/transfer_in?uid=" + uid + "&amount=" + amount,
                CommonHttpConsts.GET_BASE_INFO)
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 钱包转入金额
     *
     * @param payPsw
     * @param callback
     */
    public static void walletTransferOut(String amount, String payPsw, final HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String tokenId = CommonAppConfig.getInstance().getToken();
//        HttpClient.getInstance().post("", "/wallet/api/users/v1/transfer_out?uid="+uid+"&amount="+amount+"&token="+tokenId,
        HttpClient.getInstance().post("", "/wallet/api/users_wallet/v1/transfer_out",
                CommonHttpConsts.GET_BASE_INFO)
                .params("amount", amount)
                .params("payPassword", payPsw)
                .params("uid", uid)
                .params("token", tokenId)
                .execute(callback);
    }
}




