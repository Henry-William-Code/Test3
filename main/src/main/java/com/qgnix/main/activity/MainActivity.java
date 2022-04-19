package com.qgnix.main.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.HtmlConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.bean.ConfigBean;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.fragment.BaseFragment;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.DialogUtil;
import com.qgnix.common.utils.DpUtil;
import com.qgnix.common.utils.ProcessResultUtil;
import com.qgnix.common.utils.SpUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.VersionUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.common.views.BottomTabView;
import com.qgnix.common.views.TabItemView;
import com.qgnix.live.LiveConfig;
import com.qgnix.live.activity.LiveAnchorActivity;
import com.qgnix.live.bean.LiveBean;
import com.qgnix.live.bean.LiveKsyConfigBean;
import com.qgnix.live.dialog.TopUpDialogFragment;
import com.qgnix.live.http.LiveHttpConsts;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.live.utils.LiveStorge;
import com.qgnix.main.R;
import com.qgnix.main.bean.BonusBean;
import com.qgnix.main.bean.MsgUnReadNumBean;
import com.qgnix.main.bean.NoticeEntry;
import com.qgnix.main.bean.UpdateMsgUnReadEvent;
import com.qgnix.main.dialog.ActivityInfoDialog;
import com.qgnix.main.dialog.SignDialog;
import com.qgnix.main.dialog.TieCardDialog;
import com.qgnix.main.event.MoreGame;
import com.qgnix.main.fragment.GameFragment;
import com.qgnix.main.fragment.HomeFragment;
import com.qgnix.main.fragment.LiveFragment;
import com.qgnix.main.fragment.MeFragment;
import com.qgnix.main.fragment.SportFragment;
import com.qgnix.main.glide.util.Utils;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.qgnix.main.presenter.CheckLivePresenter;
import com.qgnix.main.utils.ReportEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

public class MainActivity extends AbsActivity {
    /**
     * tab字体颜色
     */
    private static final int[] TAB_TXT_COLORS = {R.color.global3, R.color.global2};
    /**
     * 添加view的容器
     */
    private FrameLayout mFlContainer;
    /**
     * 底部tab
     */
    protected BottomTabView mBottomTabView;

    private FragmentManager mFragmentManager;


    private ProcessResultUtil mProcessResultUtil;
    private CheckLivePresenter mCheckLivePresenter;
    private long mLastClickBackTime;//上次点击back键的时间
    /**
     * 银行卡dialog
     */
    private TieCardDialog mTieCardDialog;
    /**
     * 活动页面显示
     */
    private ActivityInfoDialog mActivityInfoDialog;

    public int mUnReadCount = 0;//未读消息数量

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if("register".equals(getIntent().getStringExtra("from"))){
            RegistBankActivity.newIntent(this);
        }
        EventBus.getDefault().register(this);
        reportUseDay();
    }

    //埋点上报
    private void reportUseDay() {
        long currentMillis = System.currentTimeMillis();
        long  firstDayLong = SpUtil.getInstance().getLongValue("firstDay",0L);
        if(firstDayLong==0L) {
            SpUtil.getInstance().setLongtValue("firstDay", currentMillis);
        }
        firstDayLong = SpUtil.getInstance().getLongValue("firstDay", 0L);
        long time = currentMillis-firstDayLong;
        Map<String,Object> map = new HashMap<>();
        map.put("value",true);

        if(time>30*24*60*60*1000 && SpUtil.getInstance().getLongValue("day30_login",0L)==0L){
            SpUtil.getInstance().setLongtValue("day30_login", currentMillis);
            ReportEvent.report(this,"day30_login",map);
        }else if(time>14*24*60*60*1000&& SpUtil.getInstance().getLongValue("day14_login",0L)==0L){
            SpUtil.getInstance().setLongtValue("day14_login", currentMillis);
            ReportEvent.report(this,"day14_login",map);
        }else if(time>7*24*60*60*1000&& SpUtil.getInstance().getLongValue("day7_login",0L)==0L){
            SpUtil.getInstance().setLongtValue("day7_login", currentMillis);
            ReportEvent.report(this,"day7_login",map);
        }else if(time>6*24*60*60*1000&& SpUtil.getInstance().getLongValue("day6_login",0L)==0L){
            SpUtil.getInstance().setLongtValue("day6_login", currentMillis);
            ReportEvent.report(this,"day6_login",map);
        }else if(time>5*24*60*60*1000&& SpUtil.getInstance().getLongValue("day5_login",0L)==0L){
            SpUtil.getInstance().setLongtValue("day5_login", currentMillis);
            ReportEvent.report(this,"day5_login",map);
        }else if(time>4*24*60*60*1000&& SpUtil.getInstance().getLongValue("day4_login",0L)==0L){
            SpUtil.getInstance().setLongtValue("day4_login", currentMillis);
            ReportEvent.report(this,"day4_login",map);
        }else if(time>3*24*60*60*1000&& SpUtil.getInstance().getLongValue("day3_login",0L)==0L){
            SpUtil.getInstance().setLongtValue("day3_login", currentMillis);
            ReportEvent.report(this,"day3_login",map);
        }else if(time>2*24*60*60*1000&& SpUtil.getInstance().getLongValue("day2_login",0L)==0L){
            SpUtil.getInstance().setLongtValue("day2_login", currentMillis);
            ReportEvent.report(this,"day2_login",map);
        }else if(time>1*24*60*60*1000&& SpUtil.getInstance().getLongValue("day1_login",0L)==0L){
            SpUtil.getInstance().setLongtValue("day1_login", currentMillis);
            ReportEvent.report(this,"day1_login",map);
        }

        String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
        if (!TextUtils.isEmpty(userBeanJson)) {
            UserBean mUserBean = JSON.parseObject(userBeanJson, UserBean.class);
            if(TextUtils.isEmpty(mUserBean.getId())&&!SpUtil.getInstance().getBooleanValue("report_tourist")){
                SpUtil.getInstance().setBooleanValue("report_tourist", true);
                ReportEvent.report(this,"guest_login",map);
            }
        }else{
            if(!SpUtil.getInstance().getBooleanValue("report_tourist")){
                SpUtil.getInstance().setBooleanValue("report_tourist", true);
                ReportEvent.report(this,"guest_login",map);
            }
        }

    }

    @Override
    protected void main() {
        CommonAppConfig.getInstance().setLaunched(true);
        mFlContainer = findViewById(R.id.fl_container);
        mBottomTabView = findViewById(R.id.bottom_tab_view);
        mBottomTabView.setTabItemViews(getTabViews(), getCenterView());

        mFragmentManager = getSupportFragmentManager();
        final List<BaseFragment> fragments = getFragments();
        setCurrentFragment(fragments.get(0));
        mBottomTabView.setOnTabItemSelectListener(new BottomTabView.OnTabItemSelectListener() {
            @Override
            public void onTabItemSelect(int position) {
                setCurrentFragment(fragments.get(position));
            }
        });
        mProcessResultUtil = new ProcessResultUtil(this);
        checkVersion();
        showInvitationCode();
        requestBonus();
        showTieCard();
        showactivity_switch();
        getRechargeNotice();
        requestCheckMsgUnreadCount();
        setStatusBarColor(R.color.color0);

    }

    /**
     * 充值公告
     */
    private void getRechargeNotice() {
        // 获取公告
        MainHttpUtil.getNotice(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    ToastUtil.show(msg);
                    return;
                }
                List<NoticeEntry> notice = JSON.parseArray(info[0], NoticeEntry.class);
                //显示充值公告
                if (null != notice && !notice.isEmpty()) {
                    NoticeEntry noticeEntry = notice.get(0);
                    TopUpDialogFragment mDialogFragment = new TopUpDialogFragment(mContext, noticeEntry.getTitle(), noticeEntry.getContent()).builder();
                    mDialogFragment.show();
                }

            }
        });
    }

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (configBean.getMaintainSwitch() == 1) {//开启维护
                        DialogUtil.showSimpleTipDialog(mContext, WordUtil.getString(R.string.main_maintain_notice), configBean.getMaintainTips());
                    }
                    // true 取消提示，false 提示
                    boolean isCancelVersion = SpUtil.getInstance().getBooleanValue(SpUtil.IS_CANCEL_VERSION + configBean.getVersion());
                    // 没有取消提示，并且有新版本
                    if (!isCancelVersion && !VersionUtil.isLatest(configBean.getVersion())) {
                        VersionUtil.showDialog2(mContext, configBean);
                    }
                }
            }
        });
    }

    /**
     * 填写邀请码
     */
    private void showInvitationCode() {
        if (CommonAppConfig.getInstance().inviteCodePopupCode == 0 || CommonAppConfig.getInstance().inviteCodePopupCode == 2) {
            String title = WordUtil.getString(R.string.main_input_invatation_code);
            if (CommonAppConfig.getInstance().inviteCodePopupCode == 2) {
                title = WordUtil.getString(R.string.main_input_invatation_code_correct);
            }
            DialogUtil.showSimpleInputDialog(mContext, title, new DialogUtil.SimpleCallback2() {
                @Override
                public void onConfirmClick(final Dialog dialog, final String content) {
                    if (TextUtils.isEmpty(content)) {
                        ToastUtil.show(R.string.main_input_invatation_code_correct);
                        return;
                    }
                    MainHttpUtil.setDistribut(CommonAppConfig.getInstance().getDeviceId(mContext), content, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                CommonAppConfig.getInstance().clearUserInfo();
                                if (null != dialog) {
                                    dialog.dismiss();
                                }
                            }
                        }
                    });
                }

                @Override
                public void onCancelClick() {
                    CommonAppConfig.getInstance().inviteCodePopupCode = 1; //点击取消按钮就暂时不提示
                    MainHttpUtil.setDistribut(CommonAppConfig.getInstance().getDeviceId(mContext), "", new HttpCallback() {  //发送空的 表示取消 验证码 输入邀请
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                CommonAppConfig.getInstance().clearUserInfo();
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 签到奖励
     */
    private void requestBonus() {
        MainHttpUtil.requestBonus(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (obj.getIntValue("bonus_switch") == 0) {
                        return;
                    }
                    int day = obj.getIntValue("bonus_day");
                    if (day <= 0) {
                        return;
                    }
                    List<BonusBean> list = JSON.parseArray(obj.getString("bonus_list"), BonusBean.class);
                    //签到
                    SignDialog dialog = new SignDialog(mContext);
                    dialog.show();
                    dialog.setData(list, day, obj.getString("count_day"));
                }
            }
        });
    }

    /**
     * 请求未读消息数量
     */
    private void requestCheckMsgUnreadCount() {
        MainHttpUtil.getCountUnread(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                int count = 0;
                List<MsgUnReadNumBean> lists = JSON.parseArray(info[0], MsgUnReadNumBean.class);
                if (null == lists || lists.isEmpty()) {
                    return;
                }
                for (int i = 0; i < lists.size(); i++) {
                    MsgUnReadNumBean bean = lists.get(i);
                    count += bean.getC();
                }
                updateNoticeState(count);
            }

            @Override
            public void onFinish() {
                super.onFinish();

            }
        });
    }

    /**
     * 更新红点提示状态
     *
     * @param count
     */
    private void updateNoticeState(int count) {
        mUnReadCount = count;
        mBottomTabView.setNoticeState(mUnReadCount > 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * 监听未读数量变更
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenner(UpdateMsgUnReadEvent event) {
        updateNoticeState(event.getmUnReadCount());
    }

    /**
     * 判断是否绑定过银行卡，如果没有，则显示绑定银行卡活动
     */
    private void showTieCard() {
        if (CommonAppConfig.getInstance().getBankStatus() == 0 || CommonAppConfig.getInstance().getBankStatus() == 2) {
            mTieCardDialog = new TieCardDialog(mContext);
            mTieCardDialog.show();
        }
    }

    /**
     * 判断是否需要显示活动开关，打开关联H5页面
     */
    private void showactivity_switch() {
        boolean passLnterval = true; //是否过了间隔（2小时）
        long time = Long.parseLong(SpUtil.getInstance().getStringValue(SpUtil.LAST_SHOW_ACTIVITY_TIME, "0"));
        if (time > 0) {
            Date theDate = new Date(time);
            theDate.setHours(theDate.getHours() + 2);
            passLnterval = new Date().getTime() > theDate.getTime();
        }
        if (!passLnterval) {//还没过时间间隔
            return;
        }
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (configBean.getActivity_Switch() == 1) {//开启活动
                        mActivityInfoDialog = new ActivityInfoDialog(mContext);
                        mActivityInfoDialog.show();
                        SpUtil.getInstance().setStringValue(SpUtil.LAST_SHOW_ACTIVITY_TIME, "" + new Date().getTime());
                    }

                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        if (null != mTieCardDialog && mTieCardDialog.isShowing()) {
            mTieCardDialog.dismiss();
            mTieCardDialog = null;
        }

        EventBus.getDefault().unregister(this);
        LiveHttpUtil.cancel(LiveHttpConsts.GET_LIVE_SDK);
        MainHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        MainHttpUtil.cancel(MainHttpConsts.REQUEST_BONUS);
        MainHttpUtil.cancel(MainHttpConsts.GET_BONUS);
        MainHttpUtil.cancel(MainHttpConsts.SET_DISTRIBUT);
        MainHttpUtil.cancel(MainHttpConsts.MAIN_LIVE_VIEW_HOLDER);
        if (mCheckLivePresenter != null) {
            mCheckLivePresenter.cancel();
        }

        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        CommonAppConfig.getInstance().setLaunched(false);
        LiveStorge.getInstance().clear();


        super.onDestroy();
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void forwardFromRegister(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("from","register");
        context.startActivity(intent);
    }

    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, String key, int position) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        mCheckLivePresenter.watchLive(liveBean, key, position);
    }

    public void watchLive(List<LiveBean> listList, LiveBean liveBean, String key, int position) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        mCheckLivePresenter.watchLive(listList, liveBean, key, position);
    }


    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastClickBackTime > 2000) {
            mLastClickBackTime = curTime;
            ToastUtil.show(R.string.main_click_next_exit);
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data) {
            ToastUtil.show(data.getStringExtra("sname"));
        }
        //建议添加逻辑判断（使请求码与结果码配对，确保返回结果是请求码所请求的）
        //   tv1.setText(data.getStringExtra("select"));
    }

    @Subscribe
    public void onMoreGame(MoreGame moreGame) {
        mBottomTabView.updatePosition(2);
        setCurrentFragment(getFragments().get(2));
    }

    protected View getCenterView() {
        View centerView = LayoutInflater.from(mContext).inflate(R.layout.tab_main_center_view, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DpUtil.dp2px(mContext, 60), DpUtil.dp2px(mContext, 60));
        layoutParams.bottomMargin = DpUtil.dp2px(mContext, 8);
        centerView.setLayoutParams(layoutParams);
        centerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
                    @Override
                    public void callback(UserBean bean) {
                        String iszhubo = bean.getIszhubo();
                        if ("1".equalsIgnoreCase(iszhubo)) {
                            // 主播
                            // 开启直播
                            requestPermission(new Runnable() {
                                @Override
                                public void run() {
                                    if (!CommonAppConfig.LIVE_SDK_CHANGED) {
                                        LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, LiveConfig.getDefaultKsyConfig());
                                        return;
                                    }
                                    LiveHttpUtil.getLiveSdk(new HttpCallback() {
                                        @Override
                                        public void onSuccess(int code, String msg, String[] info) {
                                            if (code == 0 && info.length > 0) {
                                                try {
                                                    JSONObject obj = JSON.parseObject(info[0]);
                                                    LiveAnchorActivity.forward(mContext, obj.getIntValue("live_sdk"), JSON.parseObject(obj.getString("android"), LiveKsyConfigBean.class));
                                                } catch (Exception e) {
                                                    LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, LiveConfig.getDefaultKsyConfig());
                                                }
                                            }
                                        }
                                    });
                                }
                            });

                        } else {
                            // 申请主播
                            WebViewsActivity.forward(mContext, HtmlConfig.MY_CERTIFICATION);
                        }
                    }
                });
            }
        });
        return centerView;
    }

    private void setCurrentFragment(BaseFragment fragment) {
        if (null != mFragmentManager) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_container, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            throw new NullPointerException("==mFragmentManager为空=");
        }
    }


    /**
     * 获取fragments
     *
     * @return list集合
     */
    private List<BaseFragment> getFragments() {
        final List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(SportFragment.newInstance());
        //fragments.add(HomeFragment.newInstance());
        fragments.add(LiveFragment.newInstance());
        fragments.add(GameFragment.newInstance());
        fragments.add(MeFragment.newInstance());
        return fragments;
    }

    /**
     * 获取tabItem集合
     *
     * @return list
     */
    protected List<TabItemView> getTabViews() {
        List<TabItemView> itemViews = new ArrayList<>();
        TabItemView itemView0 = new TabItemView(mContext, WordUtil.getString(R.string.menu_sport), TAB_TXT_COLORS, new int[]{R.mipmap.icon_main_home_0, R.mipmap.icon_main_home_1});
        //TabItemView itemView0 = new TabItemView(mContext, WordUtil.getString(R.string.menu_home), TAB_TXT_COLORS, new int[]{R.mipmap.icon_main_home_0, R.mipmap.icon_main_home_1});
        TabItemView itemView1 = new TabItemView(mContext, WordUtil.getString(R.string.menu_find), TAB_TXT_COLORS, new int[]{R.mipmap.icon_main_near_0, R.mipmap.icon_main_near_1});
        TabItemView itemView2 = new TabItemView(mContext, WordUtil.getString(R.string.menu_game), TAB_TXT_COLORS, new int[]{R.mipmap.icon_main_list_0, R.mipmap.icon_main_list_1});
        TabItemView itemView3 = new TabItemView(mContext, WordUtil.getString(R.string.menu_me), TAB_TXT_COLORS, new int[]{R.mipmap.icon_main_me_0, R.mipmap.icon_main_me_1});
        itemViews.add(itemView0);
        itemViews.add(itemView1);
        itemViews.add(itemView2);
        itemViews.add(itemView3);
        return itemViews;
    }






    //=======================================code========================//













    private static final int HASH_MULTIPLIER = 31;
    private static final int HASH_ACCUMULATOR = 17;
    private static final char[] HEX_CHAR_ARRAY = "0123456789abcdef".toCharArray();
    // 32 bytes from sha-256 -> 64 hex chars.
    private static final char[] SHA_256_CHARS = new char[64];

    /** Returns the hex string of the given byte array representing a SHA256 hash. */

    public static String sha256BytesToHex(byte[] bytes) {
        synchronized (SHA_256_CHARS) {
            return bytesToHex(bytes, SHA_256_CHARS);
        }
    }

    // Taken from:
    // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    // /9655275#9655275
    @SuppressWarnings("PMD.UseVarargs")

    private static String bytesToHex(byte[] bytes, char[] hexChars) {
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_CHAR_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_CHAR_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Returns the allocated byte size of the given bitmap.
     *
     * @see #getBitmapByteSize(Bitmap)
     * @deprecated Use {@link #getBitmapByteSize(Bitmap)} instead. Scheduled to be
     *     removed in Glide 4.0.
     */
    @Deprecated
    public static int getSize(Bitmap bitmap) {
        return getBitmapByteSize(bitmap);
    }

    /** Returns the in memory size of the given {@link Bitmap} in bytes. */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getBitmapByteSize(Bitmap bitmap) {
        // The return value of getAllocationByteCount silently changes for recycled bitmaps from the
        // internal buffer size to row bytes * height. To avoid random inconsistencies in caches, we
        // instead assert here.
        if (bitmap.isRecycled()) {
            throw new IllegalStateException(
                    "Cannot obtain size for recycled Bitmap: "
                            + bitmap
                            + "["
                            + bitmap.getWidth()
                            + "x"
                            + bitmap.getHeight()
                            + "] "
                            + bitmap.getConfig());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Workaround for KitKat initial release NPE in Bitmap, fixed in MR1. See issue #148.
            try {
                return bitmap.getAllocationByteCount();
            } catch (
                    @SuppressWarnings("PMD.AvoidCatchingNPE")
                            NullPointerException e) {
                // Do nothing.
            }
        }
        return bitmap.getHeight() * bitmap.getRowBytes();
    }

    /**
     * Returns the in memory size of {@link Bitmap} with the given width, height, and
     * {@link Bitmap.Config}.
     */
    public static int getBitmapByteSize(int width, int height,  Bitmap.Config config) {
        return width * height * getBytesPerPixel(config);
    }

    private static int getBytesPerPixel( Bitmap.Config config) {
        // A bitmap by decoding a GIF has null "config" in certain environments.
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        int bytesPerPixel;
        switch (config) {
            case ALPHA_8:
                bytesPerPixel = 1;
                break;
            case RGB_565:
            case ARGB_4444:
                bytesPerPixel = 2;
                break;
            case RGBA_F16:
                bytesPerPixel = 8;
                break;
            case ARGB_8888:
            default:
                bytesPerPixel = 4;
                break;
        }
        return bytesPerPixel;
    }

    /** Returns true if width and height are both > 0 and/or equal to {@link Target#SIZE_ORIGINAL}. */
    /*public static boolean isValidDimensions(int width, int height) {
        return isValidDimension(width) && isValidDimension(height);
    }*/

    /*private static boolean isValidDimension(int dimen) {
        return dimen > 0 || dimen == Target.SIZE_ORIGINAL;
    }*/

    /**
     * Throws an {@link IllegalArgumentException} if called on a thread other than the main
     * thread.
     */
    public static void assertMainThread() {
        if (!isOnMainThread()) {
            throw new IllegalArgumentException("You must call this method on the main thread");
        }
    }

    /** Throws an {@link IllegalArgumentException} if called on the main thread. */
    public static void assertBackgroundThread() {
        if (!isOnBackgroundThread()) {
            throw new IllegalArgumentException("You must call this method on a background thread");
        }
    }

    /** Returns {@code true} if called on the main thread, {@code false} otherwise. */
    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /** Returns {@code true} if called on a background thread, {@code false} otherwise. */
    public static boolean isOnBackgroundThread() {
        return !isOnMainThread();
    }

    /** Creates a {@link Queue} of the given size using Glide's preferred implementation. */

    public static <T> Queue<T> createQueue(int size) {
        return new ArrayDeque<>(size);
    }

    /**
     * Returns a copy of the given list that is safe to iterate over and perform actions that may
     * modify the original list.
     *
     * <p>See #303, #375, #322, #2262.
     */

    @SuppressWarnings("UseBulkOperation")
    public static <T> List<T> getSnapshot(Collection<T> other) {
        // toArray creates a new ArrayList internally and does not guarantee that the values it contains
        // are non-null. Collections.addAll in ArrayList uses toArray internally and therefore also
        // doesn't guarantee that entries are non-null. WeakHashMap's iterator does avoid returning null
        // and is therefore safe to use. See #322, #2262.
        List<T> result = new ArrayList<>(other.size());
        for (T item : other) {
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Null-safe equivalent of {@code a.equals(b)}.
     *
     * @see java.util.Objects#equals
     */
    public static boolean bothNullOrEqual( Object a,  Object b) {
        return a == null ? b == null : a.equals(b);
    }

    /*public static boolean bothModelsNullEquivalentOrEquals( Object a,  Object b) {
        if (a == null) {
            return b == null;
        }
        if (a instanceof Model) {
            return ((Model) a).isEquivalentTo(b);
        }
        return a.equals(b);
    }*/

    public static int hashCode(int value) {
        return hashCode(value, HASH_ACCUMULATOR);
    }

    public static int hashCode(int value, int accumulator) {
        return accumulator * HASH_MULTIPLIER + value;
    }

    public static int hashCode(float value) {
        return hashCode(value, HASH_ACCUMULATOR);
    }

    public static int hashCode(float value, int accumulator) {
        return hashCode(Float.floatToIntBits(value), accumulator);
    }

    public static int hashCode( Object object, int accumulator) {
        return hashCode(object == null ? 0 : object.hashCode(), accumulator);
    }

    public static int hashCode(boolean value, int accumulator) {
        return hashCode(value ? 1 : 0, accumulator);
    }

    public static int hashCode(boolean value) {
        return hashCode(value, HASH_ACCUMULATOR);
    }

    /**
     * Return whether wifi is connected.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isWifiConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) Utils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI;
    }




    // 传入进来的形参 Activity 释放被销毁了，如果销毁了，就抛出异常You cannot start a load for a destroyed activity
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void assertNotDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }

    //===============================================================//
}
