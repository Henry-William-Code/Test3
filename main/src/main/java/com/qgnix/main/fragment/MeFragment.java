package com.qgnix.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.HtmlConfig;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.event.CommonSuccessEvent;
import com.qgnix.common.fragment.BaseFragment;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.http.JsonBean;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.CopyUtils;
import com.qgnix.common.utils.SpUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.VersionUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.LiveConfig;
import com.qgnix.live.activity.BettingRecordActivity;
import com.qgnix.live.activity.LiveAnchorActivity;
import com.qgnix.live.activity.QutotaConversionActivity;
import com.qgnix.live.bean.LiveKsyConfigBean;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.main.R;
import com.qgnix.main.activity.DrawHistoryActivity;
import com.qgnix.main.activity.FansActivity;
import com.qgnix.main.activity.FollowActivity;
import com.qgnix.main.activity.LanSwitchActivity;
import com.qgnix.main.activity.MainActivity;
import com.qgnix.main.activity.MsgTypeListActivity;
import com.qgnix.main.activity.MyDistributionActivity;
import com.qgnix.main.activity.RechargeActivity;
import com.qgnix.main.activity.SettingActivity;
import com.qgnix.main.activity.UserInfoActivity;
import com.qgnix.main.activity.WalletTransferActivity;
import com.qgnix.main.activity.WebViewsActivity;
import com.qgnix.main.activity.WithdrawActivity;
import com.qgnix.main.bean.BannerBean;
import com.qgnix.main.bean.UpdateMsgUnReadEvent;
import com.qgnix.main.bean.WalletBean;
import com.qgnix.main.common.BannerJumpHelper;
import com.qgnix.main.dialog.CouponDialog;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.qgnix.main.utils.ReportEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.apache.commons.codec.binary.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ??????fragment
 */
public class MeFragment extends BaseFragment {

    /**
     * ????????????
     */
    private SmartRefreshLayout mRefreshLayout;

    /**
     * ??????
     */
    private ImageView mAvatar;
    /**
     * ??????
     */
    private TextView mTvNickname;
    /**
     * ??????
     */
    private TextView mTvAccount;

    /**
     * ????????????
     */
    private TextView mTvLevel;

    /**
     * ?????????
     */
    private TextView mTvAmount;

    /**
     * banner ??????
     */
//    private Banner mBanner;
    /**
     * ??????
     */
    private String mAccountNo = "";
    /**
     * banner??????
     */
//    private List<BannerBean> mBannerData;
    /**
     * ??????item
     */
    private LinearLayout mLlLive;

    private View mTvNotice;//????????????
    private int mUnReadCount;
    private ImageView tvWalletDown, tvWalletIn, tvWalletOut;
    private TextView tvDaily, tvMin, tvCoin;
    private WalletBean walletBean;

    public static MeFragment newInstance() {
        return new MeFragment();
    }


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initData( Bundle bundle) {

    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {

        // ??????????????????
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        // ??????
        mLlLive = view.findViewById(R.id.ll_live);

//        mBanner = view.findViewById(R.id.banner);
        mAvatar = view.findViewById(R.id.avatar);
        mTvNickname = view.findViewById(R.id.tv_nickname);
        mTvAccount = view.findViewById(R.id.tv_account);
        mTvLevel = view.findViewById(R.id.tv_level);

        mTvAmount = view.findViewById(R.id.tv_amount);

        mTvNotice = view.findViewById(R.id.tv_notice);

        tvCoin = view.findViewById(R.id.tv_coin);
        //????????????
        tvDaily = view.findViewById(R.id.tv_daily);
        //????????????
        tvMin = view.findViewById(R.id.tv_min);
        //????????????
        tvWalletIn = view.findViewById(R.id.tv_wallet_in);
        //????????????
        tvWalletOut = view.findViewById(R.id.tv_wallet_out);
        //????????????
        tvWalletDown = view.findViewById(R.id.tv_wallet_down);
        tvWalletDown.setOnClickListener(this);
        tvWalletOut.setOnClickListener(this);
        tvWalletIn.setOnClickListener(this);


        // ????????????
        view.findViewById(R.id.iv_msg).setOnClickListener(this);
        // ??????????????????
        view.findViewById(R.id.iv_edit_user_info).setOnClickListener(this);
        // ??????
        view.findViewById(R.id.iv_customer).setOnClickListener(this);
        // ????????????
        view.findViewById(R.id.iv_change_lan).setOnClickListener(this);

        // ????????????
        view.findViewById(R.id.ll_settings).setOnClickListener(this);

        //????????????
        view.findViewById(R.id.ll_my_fans).setOnClickListener(this);
        // ????????????
        view.findViewById(R.id.ll_my_follow).setOnClickListener(this);
        // ????????????
        view.findViewById(R.id.ll_draw_history).setOnClickListener(this);
        view.findViewById(R.id.btnCopy).setOnClickListener(this);
        // ??????
        view.findViewById(R.id.rela_chongzhi).setOnClickListener(this);
        // ??????
        view.findViewById(R.id.rela_tixian).setOnClickListener(this);
        // ??????
        view.findViewById(R.id.rela_zhuanqian).setOnClickListener(this);

        view.findViewById(R.id.rela_edu).setOnClickListener(this);
        //????????????
        view.findViewById(R.id.ll_betting_record).setOnClickListener(this);
        view.findViewById(R.id.ll_account_detail).setOnClickListener(this);
        //????????????
        view.findViewById(R.id.ll_help_center).setOnClickListener(this);
        // ??????
        view.findViewById(R.id.ll_customer_service).setOnClickListener(this);
        // ?????????
        view.findViewById(R.id.ll_customer_coupon).setOnClickListener(this);
        // ????????????
        view.findViewById(R.id.ll_live).setOnClickListener(this);
        // ????????????
        view.findViewById(R.id.ll_my_certification).setOnClickListener(this);
        view.findViewById(R.id.rela_userInfo).setOnClickListener(this);

        Activity activity = (MainActivity) getActivity();
        if (activity != null) {
            updateNoticeState(((MainActivity) activity).mUnReadCount);
        }
    }

    /**
     * ????????????????????????
     *
     * @param count
     */
    private void updateNoticeState(int count) {
        mTvNotice.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * ????????????????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenner(UpdateMsgUnReadEvent event) {
        updateNoticeState(event.getmUnReadCount());
    }

    @Override
    public void doBusiness() {
        if (null != mRefreshLayout) {
            mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(RefreshLayout refreshLayout) {
                    loadData();
                    getWalletInfo();
                }
            });
        }
        // banner
//        mBanner.setImageLoader(new ImageLoader() {
//            @Override
//            public void displayImage(Context context, Object path, ImageView imageView) {
//                ImgLoader.display(mContext, ((BannerBean) path).getSlide_pic(), imageView);
//            }
//        });
//
//        mBanner.setOnBannerListener(new OnBannerListener() {
//            @Override
//            public void OnBannerClick(int p) {
//                if (null == mBannerData || mBannerData.isEmpty()) {
//                    return;
//                }
//                BannerBean bannerBean = mBannerData.get(p);
//                new BannerJumpHelper().jump(MeFragment.this.mContext, bannerBean);
//            }
//        });

        loadData();
        //??????????????????
        getWalletInfo();
        //??????banner
//        getBannerData();
    }

    private void loadData() {
        CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if (null != mRefreshLayout) {
                    mRefreshLayout.finishRefresh();
                }
                showData(bean);
            }
        });
    }

    private void getWalletInfo() {
        MainHttpUtil.getWalletInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                try {
                    JSONObject obj = JSON.parseObject(info[0]);
                    Gson gson = new Gson();
                    walletBean = gson.fromJson(obj.toJSONString(), WalletBean.class);
                    tvDaily.setText("+" + walletBean.getDayRates() * 100 + "%");
                    tvMin.setText(WordUtil.getString(R.string.min_transfer_hint, walletBean.getMinTransInAmount()));
                    //???????????????0 ????????????????????????
                    tvCoin.setText(new BigDecimal(walletBean.getWalletBalance() + "").stripTrailingZeros().toPlainString());
                    mTvAmount.setText(new BigDecimal(walletBean.getCoin() + "").stripTrailingZeros().toPlainString());
                    

                    ImgLoader.displayWithError(mContext, walletBean.getAvatar(), mAvatar,R.mipmap.icon_avatar_placeholder2);
                    String niceName = walletBean.getUserNicename();
                    if (!TextUtils.isEmpty(niceName)) {
                        mTvNickname.setText(niceName);
                    }
                    mAccountNo = walletBean.getId() + "";
                    mTvAccount.setText(WordUtil.getString(R.string.account_no, mAccountNo));
                    mTvLevel.setText(WordUtil.getString(R.string.vip, walletBean.getLevelid()));
                    
                    reportRechage();
                } catch (Exception e) {
                    ToastUtil.show(e.getMessage());
                }

            }

            ;

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
            }
        });
    }

    //????????????
    private void reportRechage() {
        if(walletBean==null){
            return;
        }
        String walletBalance = SpUtil.getInstance().getStringValue("lastWalletBalance");
        if(!TextUtils.isEmpty(walletBalance)){
            float rechargeMoney = Float.parseFloat(walletBean.getWalletBalance())-Float.parseFloat(walletBalance);
            if(rechargeMoney>40){
                SpUtil.getInstance().setStringValue("lastWalletBalance",walletBean.getWalletBalance());
                Map<String,Float> map = new HashMap<>();
                map.put("purchase",rechargeMoney);
                ReportEvent.report(mContext,"iap_events",map);
            }
            String token = CommonAppConfig.getInstance().getToken();
            String rechargeToken = SpUtil.getInstance().getStringValue("rechargeToken");
            if(rechargeMoney>40 && TextUtils.isEmpty(rechargeToken)){
                SpUtil.getInstance().setStringValue("rechargeToken",token);
                Map<String,Object> map = new HashMap<>();
                map.put("value",true);
                ReportEvent.report(mContext,"iap_users",map);
            }
        }
        SpUtil.getInstance().setStringValue("lastWalletBalance",walletBean.getWalletBalance());
    }


    // ??????????????????
//    public void getBannerData() {
//        MainHttpUtil.getBannerInfo(new CommonCallback<String>() {
//            @Override
//            public void callback(String info) {
//                mBannerData = JSON.parseArray(info, BannerBean.class);
//                if (null != mBanner) {
//                    mBanner.setImages(mBannerData);
//                    mBanner.start();
//                }
//            }
//        });
//    }

    /**
     * ????????????
     *
     * @param u ????????????
     */
    private void showData(UserBean u) {
        if (null == u) {
            return;
        }
        /*ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
        String niceName = u.getUserNiceName();
        if (!TextUtils.isEmpty(niceName)) {
            mTvNickname.setText(niceName);
        }
        mAccountNo = u.getId();
        mTvAccount.setText(WordUtil.getString(R.string.account_no, mAccountNo));
        UserBean.ChargeLevel chargeLevel = u.getChargelevel();
        if (null != chargeLevel) {

            mTvLevel.setText(WordUtil.getString(R.string.vip, chargeLevel.getLevelid()));
        } else {
            mTvLevel.setText(WordUtil.getString(R.string.vip, 0));
        }
        mTvAmount.setText(u.getCoin());*/
        //??????????????????
        if (Integer.parseInt(u.getIszhubo()) == 1) {
            mLlLive.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onWidgetClick(View view) {
        super.onWidgetClick(view);
        int vId = view.getId();
        if (vId == R.id.iv_msg) {
            //??????????????????
            Intent intent = new Intent(mContext, MsgTypeListActivity.class);
            mContext.startActivity(intent);
        } else if (vId == R.id.iv_edit_user_info || vId == R.id.rela_userInfo) {
            //????????????
            Intent intent = new Intent(mContext, UserInfoActivity.class);
            mContext.startActivity(intent);
        } else if (vId == R.id.iv_customer || vId == R.id.ll_customer_service) {
            // ??????
            Intent intent = new Intent(mContext, WebViewsActivity.class);
            intent.putExtra("title", WordUtil.getString(R.string.customer_service));
            intent.putExtra("url", CommonAppConfig.getInstance().getConfig().getKefuUrl());
            mContext.startActivity(intent);
        } else if (vId == R.id.iv_change_lan) {
            // ????????????
            LanSwitchActivity.toForward(mContext);
        } else if (vId == R.id.ll_my_follow) {
            //????????????
            FollowActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
        } else if (vId == R.id.ll_my_fans) {
            //????????????
            FansActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
        } else if (vId == R.id.ll_settings) {
            // ??????
            mContext.startActivity(new Intent(mContext, SettingActivity.class));
        } else if (vId == R.id.rela_zhuanqian) {
            //????????????
            mContext.startActivity(new Intent(mContext, MyDistributionActivity.class));
        } else if (vId == R.id.ll_draw_history) {
            // ????????????
            mContext.startActivity(new Intent(mContext, DrawHistoryActivity.class));
        } else if (vId == R.id.btnCopy) {
            // ????????????
            CopyUtils.copy(mContext, mAccountNo);
        } else if (vId == R.id.rela_chongzhi) {
            //??????
            Intent intent = new Intent(mContext, RechargeActivity.class);
            startActivity(intent);
        } else if (vId == R.id.rela_tixian) {
            // ??????
            mContext.startActivity(new Intent(mContext, WithdrawActivity.class));
        } else if (vId == R.id.rela_edu) {
            // ????????????
            mContext.startActivity(new Intent(mContext, QutotaConversionActivity.class));
        } else if (vId == R.id.ll_account_detail) {
            //????????????
            WebViewsActivity.forward(mContext, HtmlConfig.ACCOUNT_DETAIL);
        } else if (vId == R.id.ll_help_center) {
            // ????????????
            WebViewsActivity.forward(mContext, HtmlConfig.HELP_CENTER,"Activity Center");
        } else if (vId == R.id.ll_betting_record) {
            // ????????????
            mContext.startActivity(new Intent(mContext, BettingRecordActivity.class));
        } else if (vId == R.id.ll_live) {
            // ????????????
            ((MainActivity) mContext).requestPermission(new Runnable() {
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
        } else if (vId == R.id.ll_my_certification) {
            // ????????????
            WebViewsActivity.forward(mContext, HtmlConfig.MY_CERTIFICATION);
        } else if (vId == R.id.ll_customer_coupon) {
            // ?????????
            new CouponDialog(mContext).show();
        } else if (vId == R.id.tv_wallet_in) {
            //????????????
            Intent intent = new Intent(mContext, WalletTransferActivity.class);
            intent.putExtra("transferIn", true);
            intent.putExtra(WalletTransferActivity.WALLET_INFO, walletBean);
            if (null != walletBean) {
                startActivityForResult(intent, WalletTransferActivity.WALLET_CODE);
            }
        } else if (vId == R.id.tv_wallet_out) {
            //????????????
            Intent intent = new Intent(mContext, WalletTransferActivity.class);
            intent.putExtra("transferIn", false);
            intent.putExtra(WalletTransferActivity.WALLET_INFO, walletBean);
            if (null != walletBean) {
                startActivityForResult(intent, WalletTransferActivity.WALLET_CODE);
            }
        } else if (vId == R.id.tv_wallet_down) {
            //??????
            final Uri uri = Uri.parse("https://wallet.honor.win");
            final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WalletTransferActivity.WALLET_CODE) {
            tvCoin.setText(data.getStringExtra("walletBalance"));
            mTvAmount.setText(data.getStringExtra("cherryBalance"));
            walletBean.setCoin(new Double(data.getStringExtra("cherryBalance") + ""));
            walletBean.setWalletBalance(data.getStringExtra("walletBalance"));
            //getWalletInfo();
        }
    }

    @Override
    public void onDestroy() {
        MainHttpUtil.cancel(CommonHttpConsts.GET_BASE_INFO);
        MainHttpUtil.cancel(MainHttpConsts.HOME_GET_SLIDE);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}