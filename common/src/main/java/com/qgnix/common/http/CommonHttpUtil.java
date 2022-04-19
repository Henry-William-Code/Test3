package com.qgnix.common.http;

import android.util.Log;

import com.alibaba.android.arouter.utils.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.R;
import com.qgnix.common.activity.ErrorActivity;
import com.qgnix.common.bean.ConfigBean;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.bean.VersionBean;
import com.qgnix.common.event.FollowEvent;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.SpUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/9/17.
 */

public class CommonHttpUtil {


    /**
     * 初始化
     */
    public static void init() {
        HttpClient.getInstance().init();
    }

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }

    /**
     * 获取version
     */
    public static void getVersion(final CommonCallback<VersionBean> commonCallback) {
        HttpClient.getInstance().get("Home.version", CommonHttpConsts.GET_VERSION)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            String netDataVersion = info[0];
                            String localDataVersion = SpUtil.getInstance().getStringValue(SpUtil.VERSION, "{}");
                            if ("[]".equals(netDataVersion) || netDataVersion.equals(localDataVersion)) {
                                if (commonCallback != null) {
                                    commonCallback.callback(null);
                                }
                                return;
                            }
                            VersionBean netBean = JSON.parseObject(netDataVersion, VersionBean.class);
                            VersionBean localBean = JSON.parseObject(localDataVersion, VersionBean.class);
                            if (netBean.getConfig() != localBean.getConfig()) {
                                SpUtil.getInstance().removeValue(SpUtil.CONFIG);
                                SpUtil.getInstance().removeValue(SpUtil.SLIDE);
                            }
                            if (netBean.getTicket() != localBean.getTicket()) {
                                SpUtil.getInstance().removeValue(SpUtil.TICKET);
                                SpUtil.getInstance().removeValue(SpUtil.CATEGORY_V2);
                            }
                            SpUtil.getInstance().setStringValue(SpUtil.VERSION, netDataVersion);
                        }
                        if (commonCallback != null) {
                            commonCallback.callback(null);
                        }
                    }

                    @Override
                    public void onError() {
                        if (commonCallback != null) {
                            commonCallback.callback(null);
                        }
                    }
                });
    }

    /**
     * 获取config
     */
    public static void getConfig(final CommonCallback<ConfigBean> commonCallback) {
        String config = SpUtil.getInstance().getStringValue(SpUtil.CONFIG, "");
        if (!TextUtils.isEmpty(config)) {
            ConfigBean bean = initCommonAppConfig(config);
            if (commonCallback != null) {
                commonCallback.callback(bean);
            }
            return;
        }
        HttpClient.getInstance().get("Home.getConfig", CommonHttpConsts.GET_CONFIG)
                .params("source","native")
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code != 0 || info.length == 0) {
                            ToastUtil.show(msg);
                            return;
                        }
                        try {
                            SpUtil.getInstance().setStringValue(SpUtil.CONFIG, info[0]);
                            ConfigBean bean = initCommonAppConfig(info[0]);
                            if (commonCallback != null) {
                                commonCallback.callback(bean);
                            }
                        } catch (Exception e) {
                            String error = "info[0]:" + info[0] + "\n\n\n" + "Exception:" + e.getClass() + "---message--->" + e.getMessage();
                            ErrorActivity.forward("GetConfig接口返回数据异常", error);
                        }
                    }

                    @Override
                    public void onError() {
                        if (commonCallback != null) {
                            commonCallback.callback(null);
                        }
                    }
                });
    }

    private static ConfigBean initCommonAppConfig(String info) {
        JSONObject obj = JSON.parseObject(info);
        ConfigBean bean = JSON.toJavaObject(obj, ConfigBean.class);
        CommonAppConfig.getInstance().setConfig(bean);
        CommonAppConfig.getInstance().setLevel(obj.getString("level"));
        CommonAppConfig.getInstance().setAnchorLevel(obj.getString("levelanchor"));
        return bean;
    }

    /**
     * 关注别人 或 取消对别人的关注的接口
     */
    public static void setAttention(String touid, CommonCallback<Integer> callback) {
        setAttention(CommonHttpConsts.SET_ATTENTION, touid, callback);
    }

    /**
     * 关注别人 或 取消对别人的关注的接口
     */
    public static void setAttention(String tag, final String touid, final CommonCallback<Integer> callback) {
        if (touid.equals(CommonAppConfig.getInstance().getUid())) {
            ToastUtil.show(WordUtil.getString(R.string.cannot_follow_self));
            return;
        }
        HttpClient.getInstance().get("User.setAttent", tag)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            int isAttention = JSON.parseObject(info[0]).getIntValue("isattent");//1是 关注  0是未关注
                            EventBus.getDefault().post(new FollowEvent(touid, isAttention));
                            if (callback != null) {
                                callback.callback(isAttention);
                            }
                        }
                    }
                });
    }


    /**
     * 用支付宝充值 的时候在服务端生成订单号
     *
     * @param money    RMB价格
     * @param changeid 要购买的钻石的id
     * @param coin     要购买的钻石的数量
     * @param callback
     */
    public static void getAliOrder(String money, String changeid, String coin, HttpCallback callback) {
        HttpClient.getInstance().get("Charge.getAliOrder", CommonHttpConsts.GET_ALI_ORDER)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("money", money)
                .params("changeid", changeid)
                .params("coin", coin)
                .execute(callback);
    }


    //不做任何操作的HttpCallback
    public static final HttpCallback NO_CALLBACK = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {

        }
    };



    /*================BC彩票相关接口========================*/

    /**
     * 充值页面，我的余额
     */
    public static void getBalance(HttpCallback callback) {
        HttpClient.getInstance().get("User.getBalance", CommonHttpConsts.GET_BALANCE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取BC彩票分类
     *
     * @param ticketId 彩票id
     * @param callback 回调
     */
    public static void getBCTicketGetOddsV2(String ticketId, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.BC_TICKET_GET_ODDS_V2, CommonHttpConsts.BC_TICKET_GET_ODDS_V2)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("ticketid", ticketId)
                .execute(callback);
    }

    /**
     * 获取BC彩票结果
     *
     * @param ticketId 彩票id
     * @param callback 回调
     */
    @Deprecated
    public static void getBCTicketGetQs(String ticketId, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.BC_TICKET_GET_QS, CommonHttpConsts.BC_TICKET_GET_QS)
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .params("ticketid", ticketId)
                .params("v", System.currentTimeMillis())
                .execute(callback);
    }

    /**
     * 获取BC彩票
     *
     * @param callback 回调
     */
    public static void getBCTicket(HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        config.getUserBean(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                String country = "1".equals(bean.getIszhubo()) ? "IN" : config.getCountry();
                HttpClient.getInstance().get(ServiceName.BC_TICKET_GET_TICKET_AND_THIRD_GAME, CommonHttpConsts.BC_TICKET_GET_TICKET)
                        .params("uid", config.getUid())
                        .params("token", config.getToken())
                        .params("country", country)
                        .execute(callback);
            }
        });
    }


    /**
     * 手机注册接口
     */
    public static void saveUserLog(String action, int type, String deviceId, String relation) {
        String userLogService = CommonAppConfig.getInstance().getUserLogService();
        HttpClient.getInstance().getFullPath(userLogService + "/user_log", CommonHttpConsts.SAVE_USER_LOG)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("ip", CommonAppConfig.getInstance().getIp())
                .params("action", action)
                .params("type", type)
                .params("device_id", deviceId)
                .params("relation", relation)
                .execute(new Callback<String>() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {

                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        L.e("===上报成功===");
                    }

                    @Override
                    public void onCacheSuccess(Response<String> response) {

                    }

                    @Override
                    public void onError(Response<String> response) {
                        L.e("===上报失败===");
                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void uploadProgress(Progress progress) {

                    }

                    @Override
                    public void downloadProgress(Progress progress) {

                    }

                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        return null;
                    }
                });
    }

    /**
     * 获取用户信息
     */
    public static void getBaseInfo(String uid, String token, final CommonCallback<UserBean> commonCallback) {
        HttpClient.getInstance().get("User.getBaseInfo", CommonHttpConsts.GET_BASE_INFO)
                .params("uid", uid)
                .params("token", token)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            UserBean bean = JSON.toJavaObject(obj, UserBean.class);
                            Log.d("UserBean", "onSuccess: " + new Gson().toJson(bean));
                            CommonAppConfig.getInstance().setUserBean(bean);
                            CommonAppConfig.getInstance().inviteCodePopupCode = bean.getInvite_state();
                            CommonAppConfig.getInstance().setBlankStatus(bean.getTie_card_state());
                            //设置国家码
                            CommonAppConfig.getInstance().setCountry(bean.getCountry());
                            SpUtil.getInstance().setStringValue(SpUtil.USER_INFO, info[0]);
                            if (commonCallback != null) {
                                commonCallback.callback(bean);
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        if (commonCallback != null) {
                            commonCallback.callback(null);
                        }
                    }
                });
    }

    public static void getUserBrief(final CommonCallback<UserBean> commonCallback) {
        HttpClient.getInstance().get("User.GetUserBrief", CommonHttpConsts.GET_USER_BRIEF)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            UserBean bean = JSON.toJavaObject(obj, UserBean.class);
                            Log.d("getUserBrief", "onSuccess: " + new Gson().toJson(bean));
                            if (commonCallback != null) {
                                commonCallback.callback(bean);
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        if (commonCallback != null) {
                            commonCallback.callback(null);
                        }
                    }
                });
    }


    /**
     * 获取用户信息
     */
    public static void getBaseInfo(CommonCallback<UserBean> commonCallback) {
        getBaseInfo(CommonAppConfig.getInstance().getUid(),
                CommonAppConfig.getInstance().getToken(),
                commonCallback);
    }

    /**
     * 登录gn游戏
     *
     * @param callback 回调
     */
    public static void ngLogin(String platType, String gameType, String gameCode, HttpCallback callback) {
        CommonAppConfig config = CommonAppConfig.getInstance();
        HttpClient.getInstance().get(ServiceName.NG_LOGIN, CommonHttpConsts.NG_LOGIN)
                .params("plat_type", platType)
                .params("game_type", gameType)
                .params("game_code", gameCode)
                .params("is_mobile_url", 1)
                .params("username", config.getUid())
                .params("uid", config.getUid())
                .params("token", config.getToken())
                .execute(callback);
    }


}




