package com.qgnix.common;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qgnix.common.bean.ConfigBean;
import com.qgnix.common.bean.LevelBean;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.SpUtil;
import com.qgnix.common.utils.WordUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by cxf on 2017/8/4.
 */

public class CommonAppConfig {
    // 本地推广邀请码
    public static final String LOCAL_INVITATION_CODE = getMetaDataString("INVITE_CODE");
    //域名
    public static final String[] SERVER_HOST = getMetaDataString("SERVER_HOST").split(",");
    //外部sd卡
    public static final String DCMI_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    //内部存储 /data/data/<application package>/files目录
    public static final String INNER_PATH = CommonAppContext.sInstance.getFilesDir().getAbsolutePath();
    //文件夹名字
    private static final String DIR_NAME = "qgnix";
    //下载音乐的时候保存的路径
    public static final String MUSIC_PATH = DCMI_PATH + "/" + DIR_NAME + "/music/";
    //拍照时图片保存路径
    public static final String CAMERA_IMAGE_PATH = DCMI_PATH + "/" + DIR_NAME + "/camera/";

    public static final String GIF_PATH = INNER_PATH + "/gif/";

    //是否使用游戏
    public static final boolean GAME_ENABLE = true;
    //是否上下滑动切换直播间
    public static final boolean LIVE_ROOM_SCROLL = true;
    //直播sdk类型是否由后台控制的
    public static final boolean LIVE_SDK_CHANGED = true;
    //使用指定的直播sdk类型
    public static final int LIVE_SDK_USED = Constants.LIVE_SDK_KSY;

    // 模式 1 成人 0 非成人
    public static int ADULT_MODE = 0;

    private static CommonAppConfig sInstance;

    private CommonAppConfig() {

    }

    public static CommonAppConfig getInstance() {
        if (sInstance == null) {
            synchronized (CommonAppConfig.class) {
                if (sInstance == null) {
                    sInstance = new CommonAppConfig();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取当前域名
     *
     * @return 当前域名
     */
    public static String getCurHost() {
        return SpUtil.getInstance().getStringValue(SpUtil.SERVER_HOST, SERVER_HOST[0]);
//        return "http://dev.douzhide.com";http://69.172.93.174//开发环境
//        return  "http://69.172.93.174";//预发布环境
    }

    private String mUid;
    private String mToken;
    private ConfigBean mConfig;
    private double mLng;
    private double mLat;
    private String mProvince;//省
    private String mCity;//市
    private String mDistrict;//区
    private UserBean mUserBean;
    private String mVersion;
    private boolean mLaunched;//App是否启动了
    private SparseArray<LevelBean> mLevelMap;
    private SparseArray<LevelBean> mAnchorLevelMap;
    private boolean mFrontGround;
    private int mAppIconRes;
    private String mAppName;

    public Integer inviteCodePopupCode = 1; //登录后是否弹出 邀请码输入框 默认否  0表示无邀请码  1 表示邀请码对的 2表示邀请码错误 提示输入正确的邀请码 3 表示用户忽略邀请码(表明邀请码没填或者填错了)

    public String getUid() {
        if (TextUtils.isEmpty(mUid)) {
            String[] uidAndToken = SpUtil.getInstance()
                    .getMultiStringValue(SpUtil.UID, SpUtil.TOKEN);
            if (uidAndToken != null) {
                if (!TextUtils.isEmpty(uidAndToken[0]) && !TextUtils.isEmpty(uidAndToken[1])) {
                    mUid = uidAndToken[0];
                    mToken = uidAndToken[1];
                }
            } else {
                return "-1";
            }
        }
        return mUid;
    }

    public String getToken() {
        return mToken;
    }

    public String getCoinName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getCoinName();
        }
        return Constants.DIAMONDS;
    }

    public String getVotesName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getVotesName();
        }
        return Constants.VOTES;
    }

    public ConfigBean getConfig() {
        if (mConfig == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                mConfig = JSON.parseObject(configString, ConfigBean.class);
            }
        }
        return mConfig;
    }

    public void getConfig(CommonCallback<ConfigBean> callback) {
        if (callback == null) {
            return;
        }
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            callback.callback(configBean);
        } else {
            CommonHttpUtil.getConfig(callback);
        }
    }

    public void setConfig(ConfigBean config) {
        mConfig = config;
    }

    /**
     * 获取图片地址host
     */
    public String getImgHost() {
        if (null == mConfig) {
            return null;
        }
        return mConfig.getSite();
    }

    /**
     * 获取验证码开关
     */
    public boolean sendCodeSwitch() {
        if (null == mConfig) {
            return false;
        }
        return mConfig.getSendCodeSwitch() == 1;
    }

    /**
     * 获取翻译地址
     */
    public String getTransserver() {
        if (null == mConfig) {
            return "";
        }
        return mConfig.getTransserver();
    }

    /**
     * 获取筹码数据
     */
    public String[] getChips() {
        if (null == mConfig) {
            return new String[0];
        }
        return mConfig.getChips().split(",");
    }

    /**
     * 获取彩票版本号
     *
     * @return
     */
    public int getTicketVersion() {
        if (null == mConfig) {
            return 1;
        }
        return mConfig.getTicketVersion();
    }

    /**
     * 用户日志服务地址
     *
     * @return
     */
    public String getUserLogService() {
        if (null == mConfig) {
            return "";
        }
        return mConfig.getUserlogserver();
    }

    /**
     * ip
     *
     * @return
     */
    public String getIp() {
        if (null == mConfig) {
            return "";
        }
        return mConfig.getIp();
    }

    /**
     * socket地址
     *
     * @return
     */
    public String getSocketUrl() {
        if (null == mConfig) {
            return "";
        }
        return mConfig.getChatServer() + "?cid=" + CommonAppConfig.getInstance().getUid();
    }

    /**
     * 经度
     */
    public double getLng() {
        if (mLng == 0) {
            String lng = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_LNG);
            if (!TextUtils.isEmpty(lng)) {
                try {
                    mLng = Double.parseDouble(lng);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mLng;
    }

    /**
     * 纬度
     */
    public double getLat() {
        if (mLat == 0) {
            String lat = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_LAT);
            if (!TextUtils.isEmpty(lat)) {
                try {
                    mLat = Double.parseDouble(lat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mLat;
    }

    /**
     * 省
     */
    public String getProvince() {
        if (TextUtils.isEmpty(mProvince)) {
            mProvince = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_PROVINCE);
        }
        return mProvince == null ? "" : mProvince;
    }

    /**
     * 市
     */
    public String getCity() {
        if (TextUtils.isEmpty(mCity)) {
            mCity = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_CITY);
        }
        return mCity == null ? "" : mCity;
    }

    public void setUserBean(UserBean bean) {
        mUserBean = bean;
    }

    //    public UserBean getUserBean() {
//        if (mUserBean == null) {
//            String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
//            if (!TextUtils.isEmpty(userBeanJson)) {
//                mUserBean = JSON.parseObject(userBeanJson, UserBean.class);
//            }
//        }
//        return mUserBean;
//    }
    public void getUserBean(CommonCallback<UserBean> callback) {
        if (mUserBean == null) {
            String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
            if (!TextUtils.isEmpty(userBeanJson)) {
                mUserBean = JSON.parseObject(userBeanJson, UserBean.class);
                callback.callback(mUserBean);
            } else {
                CommonHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                    @Override
                    public void callback(UserBean bean) {
                        callback.callback(bean);
                    }
                });
            }
        } else {
            callback.callback(mUserBean);
        }
    }

    public void clearUserInfo() {
        //清除用户信息
        SpUtil.getInstance().removeValue(SpUtil.USER_INFO);
        //清除绑定银行卡状态信息
        SpUtil.getInstance().removeValue(SpUtil.BANK_STATUS);
        setUserBean(null);
    }

    /**
     * 是否是主播
     *
     * @return true 主播 false 普通用户
     */
//    public boolean isAnchor() {
//        UserBean userBean = getUserBean();
//        if (null == userBean) {
//            return false;
//        }
//        return "1".equals(userBean.getIszhubo());
//    }

    /**
     * 设置登录信息
     */
    public void setLoginInfo(String uid, String token, boolean save) {
        L.e("登录成功", "uid------>" + uid);
        L.e("登录成功", "token------>" + token);
        mUid = uid;
        mToken = token;
        if (save) {
            Map<String, String> map = new HashMap<>();
            map.put(SpUtil.UID, uid);
            map.put(SpUtil.TOKEN, token);
            SpUtil.getInstance().setMultiStringValue(map);
        }
    }

    /**
     * 设置游戏开关
     */
    public void setSwitchPlay(String googleThirdGame) {
        L.e("config", "开关是否打开------>" + googleThirdGame);
        SpUtil.getInstance().setGoogleGame(googleThirdGame);
    }

    /**
     * 设置送币
     */
    public void setCoinAward(String coinAward) {
        SpUtil.getInstance().setStringValue(SpUtil.TIE_CARD_REWARD,coinAward);
    }

    /**
     * 设置送币
     */
    public String getCoinAward() {
        return SpUtil.getInstance().getStringValue(SpUtil.TIE_CARD_REWARD,"0");
    }

    /**
     * 设置游戏开关
     */
    public boolean getSwitchPlay() {

        return  SpUtil.getInstance().getGoogleThirdGame();
    }

    /**
     * 设置审核开关
     */
    public void setSwitchStatu(String googleStore) {
        L.e("config", "开关是否打开------>" + googleStore);
        SpUtil.getInstance().setGoogleStore(googleStore);
    }


    /**
     * 存储服务器版本号
     */
    public void setServerVersion(String version) {
        L.e("config", "服务器版本号------>" + version);
        SpUtil.getInstance().setGoogleVersion(version);
    }


    /**
     * 获取审核开关
     */
    public boolean getSwitchStatu() {
        L.e("config", "开关是否打开------>" + SpUtil.getInstance().getGoogleStore());
       return  SpUtil.getInstance().getGoogleStore();
    }

    /**
     * 清除登录信息
     */
    public void clearLoginInfo() {
        mUid = null;
        mToken = null;
        SpUtil.getInstance().clear();
    }


    /**
     * 清除定位信息
     */
    public void clearLocationInfo() {
        mLng = 0;
        mLat = 0;
        mProvince = null;
        mCity = null;
        mDistrict = null;
        SpUtil.getInstance().removeValue(
                SpUtil.LOCATION_LNG,
                SpUtil.LOCATION_LAT,
                SpUtil.LOCATION_PROVINCE,
                SpUtil.LOCATION_CITY,
                SpUtil.LOCATION_DISTRICT);

    }


    /**
     * 获取版本号
     */
    public String getVersion() {
        if (TextUtils.isEmpty(mVersion)) {
            try {
                PackageManager manager = CommonAppContext.sInstance.getPackageManager();
                PackageInfo info = manager.getPackageInfo(CommonAppContext.sInstance.getPackageName(), 0);
                mVersion = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mVersion;
    }

    /**
     * 获取App名称
     */
    public String getAppName() {
        if (TextUtils.isEmpty(mAppName)) {
            int res = CommonAppContext.sInstance.getResources().getIdentifier("app_name", "string", "com.qgnixhonor.sport");
            mAppName = WordUtil.getString(res);
        }
        return mAppName;
    }


    /**
     * 获取App图标的资源id
     */
    public int getAppIconRes() {
        if (mAppIconRes == 0) {
            mAppIconRes = CommonAppContext.sInstance.getResources().getIdentifier("ic_launcher", "mipmap", "com.qgnixhonor.sport");
        }
        return mAppIconRes;
    }


    private static String getMetaDataString(String key) {
        String res = null;
        try {
            ApplicationInfo appInfo = CommonAppContext.sInstance.getPackageManager().getApplicationInfo(CommonAppContext.sInstance.getPackageName(), PackageManager.GET_META_DATA);
            res = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     * 保存用户等级信息
     */
    public void setLevel(String levelJson) {
        if (TextUtils.isEmpty(levelJson)) {
            return;
        }
        List<LevelBean> list = JSON.parseArray(levelJson, LevelBean.class);
        if (list == null || list.size() == 0) {
            return;
        }
        if (mLevelMap == null) {
            mLevelMap = new SparseArray<>();
        }
        mLevelMap.clear();
        for (LevelBean bean : list) {
            mLevelMap.put(bean.getLevel(), bean);
        }
    }

    /**
     * 保存主播等级信息
     */
    public void setAnchorLevel(String anchorLevelJson) {
        if (TextUtils.isEmpty(anchorLevelJson)) {
            return;
        }
        List<LevelBean> list = JSON.parseArray(anchorLevelJson, LevelBean.class);
        if (list == null || list.size() == 0) {
            return;
        }
        if (mAnchorLevelMap == null) {
            mAnchorLevelMap = new SparseArray<>();
        }
        mAnchorLevelMap.clear();
        for (LevelBean bean : list) {
            mAnchorLevelMap.put(bean.getLevel(), bean);
        }
    }

    /**
     * 获取用户等级
     */
    public LevelBean getLevel(int level) {
        if (mLevelMap == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                JSONObject obj = JSON.parseObject(configString);
                setLevel(obj.getString("level"));
            }
        }
        int size = mLevelMap.size();
        if (mLevelMap == null || size == 0) {
            return null;
        }
        return mLevelMap.get(level);
    }

    /**
     * 获取主播等级
     */
    public LevelBean getAnchorLevel(int level) {
        if (mAnchorLevelMap == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                JSONObject obj = JSON.parseObject(configString);
                setAnchorLevel(obj.getString("levelanchor"));
            }
        }
        int size = mAnchorLevelMap.size();
        if (mAnchorLevelMap == null || size == 0) {
            return null;
        }
        return mAnchorLevelMap.get(level);
    }


    /**
     * 判断某APP是否安装
     */
    public static boolean isAppExist(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            PackageManager manager = CommonAppContext.sInstance.getPackageManager();
            List<PackageInfo> list = manager.getInstalledPackages(0);
            for (PackageInfo info : list) {
                if (packageName.equalsIgnoreCase(info.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isLaunched() {
        return mLaunched;
    }

    public void setLaunched(boolean launched) {
        mLaunched = launched;
    }

    //app是否在前台
    public boolean isFrontGround() {
        return mFrontGround;
    }

    //app是否在前台
    public void setFrontGround(boolean frontGround) {
        mFrontGround = frontGround;
    }

    public String getUuid() {
        String uuid = "";
        for (int i = 0; i < 32; i++) {
            //注意replaceAll前面的是正则表达式
            uuid = UUID.randomUUID().toString().replaceAll("-", "");
        }
        return uuid;
    }


    /**
     * 设置当前语言
     *
     * @param lan
     */
    public void setCurLan(int lan) {
        SpUtil.getInstance().setIntValue(SpUtil.CUR_LAN, lan);
    }


    /**
     * 获取当前语言
     */
    public int getCurLan() {
        return SpUtil.getInstance().getIntValue(SpUtil.CUR_LAN, 1);
    }

    /**
     * 获取deviceId
     *
     * @param ctx 上下午
     * @return deviceId
     */
    public String getDeviceId(Context ctx) {
        return Settings.System.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 是否模拟器
     *
     * @param ctx 上下文
     * @return true 模拟器 false 手机
     */
    public boolean isEmulator(Context ctx) {
        IntentFilter intentFilter = new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatusIntent = ctx.registerReceiver(null, intentFilter);
        int voltage = batteryStatusIntent.getIntExtra("voltage", 99999);
        int temperature = batteryStatusIntent.getIntExtra("temperature", 99999);
        return ((voltage == 0) && (temperature == 0)) || ((voltage == 10000) && (temperature == 0));
    }

    /**
     * 设置绑定银行卡状态
     *
     * @param status 0 初始状态未绑定  1 表示绑定成功,且送马币成功 2表示绑定了错误的银行卡 3 表示用户忽略绑定要求 4 关闭该功能 - 登录接口返回该字段给前端判断处理'
     */
    public void setBlankStatus(int status) {
        SpUtil.getInstance().setIntValue(SpUtil.BANK_STATUS, status);
    }

    /**
     * 登录后是否弹出   0 初始状态未绑定  1 表示绑定成功,且送马币成功 2表示绑定了错误的银行卡 3 表示用户忽略绑定要求 4 关闭该功能 - 登录接口返回该字段给前端判断处理'
     */
    public int getBankStatus() {
        return SpUtil.getInstance().getIntValue(SpUtil.BANK_STATUS, 0);
    }


    /**
     * 设置国家码
     *
     * @param country
     */
    public void setCountry(String country) {
        SpUtil.getInstance().setStringValue(SpUtil.ACCOUNT_COUNTRY, country);
    }

    /**
     * 获取国家码
     */
    public String getCountry() {
        return SpUtil.getInstance().getStringValue(SpUtil.ACCOUNT_COUNTRY);
    }

    /**
     * 获取国家码
     */
    public String getNewRegBonus() {
        return getConfig().getNewRegBonus();
    }
}
