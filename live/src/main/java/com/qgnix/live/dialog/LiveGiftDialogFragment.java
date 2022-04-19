package com.qgnix.live.dialog;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.bean.LiveGiftBean;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.cache.CacheData;
import com.qgnix.common.dialog.AbsDialogFragment;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.interfaces.OnItemClickListener;
import com.qgnix.common.utils.DpUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.RouteUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.R;
import com.qgnix.live.activity.LiveActivity;
import com.qgnix.live.adapter.LiveGiftCountAdapter;
import com.qgnix.live.adapter.LiveGiftPagerAdapter;
import com.qgnix.live.bean.LiveGuardInfo;
import com.qgnix.live.http.LiveHttpConsts;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.live.lottery.entry.BalanceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/12.
 * 送礼物的弹窗
 */

public class LiveGiftDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener<String>, LiveGiftPagerAdapter.ActionListener {

    private TextView mCoin;
    private TextView chognzhi;
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private View mLoading;
    private View mArrow;
    private View mBtnSend;
    private View mBtnSendGroup;
    private View mBtnSendLian;
    private TextView mBtnChooseCount;
    private PopupWindow mGiftCountPopupWindow;//选择分组数量的popupWindow
    private Drawable mDrawable1;
    private Drawable mDrawable2;
    private LiveGiftPagerAdapter mLiveGiftPagerAdapter;
    private LiveGiftBean mLiveGiftBean;
    private static final String DEFAULT_COUNT = "1";
    private String mCount = DEFAULT_COUNT;
    private String mLiveUid;
    private String mStream;
    private Handler mHandler;
    private int mLianCountDownCount;//连送倒计时的数字
    private TextView mLianText;
    private static final int WHAT_LIAN = 100;
    private boolean mShowLianBtn;//是否显示了连送按钮
    private LiveGuardInfo mLiveGuardInfo;

    private RadioButton ra_pt, ra_lw, ra_xy, ra_sh;
    private List<LiveGiftBean> giftLists = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_gift;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    public void setLiveGuardInfo(LiveGuardInfo liveGuardInfo) {
        mLiveGuardInfo = liveGuardInfo;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCoin = mRootView.findViewById(R.id.coin);
        chognzhi = mRootView.findViewById(R.id.chognzhi);
        mLoading = mRootView.findViewById(R.id.loading);
        mArrow = mRootView.findViewById(R.id.arrow);
        mBtnSend = mRootView.findViewById(R.id.btn_send);
        mBtnSendGroup = mRootView.findViewById(R.id.btn_send_group);
        mBtnSendLian = mRootView.findViewById(R.id.btn_send_lian);
        mBtnChooseCount = mRootView.findViewById(R.id.btn_choose);
        mLianText = mRootView.findViewById(R.id.lian_text);
        mDrawable1 = ContextCompat.getDrawable(mContext, R.drawable.bg_live_gift_send);
        mDrawable2 = ContextCompat.getDrawable(mContext, R.drawable.bg_live_gift_send_2);
        mViewPager = mRootView.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(5);

        ra_pt = mRootView.findViewById(R.id.ra_pt);
        ra_lw = mRootView.findViewById(R.id.ra_lw);
        ra_xy = mRootView.findViewById(R.id.ra_xy);
        ra_sh = mRootView.findViewById(R.id.ra_sh);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mRadioGroup != null) {
                    ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRadioGroup = mRootView.findViewById(R.id.radio_group);
        mBtnSend.setOnClickListener(this);
        mBtnSendLian.setOnClickListener(this);
        mBtnChooseCount.setOnClickListener(this);
        mCoin.setOnClickListener(this);
        chognzhi.setOnClickListener(this);
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_luck_gift_tip).setOnClickListener(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mLianCountDownCount--;
                if (mLianCountDownCount == 0) {
                    hideLianBtn();
                } else {
                    if (mLianText != null) {
                        mLianText.setText(mLianCountDownCount + "s");
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
                        }
                    }
                }
            }
        };
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLiveUid = bundle.getString(Constants.LIVE_UID);
            mStream = bundle.getString(Constants.LIVE_STREAM);
        }

        loadData("0");

        ra_pt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRadioGroup.removeAllViews();
                LiveGiftDialogFragment.this.loadData("0");
            }
        });

        ra_lw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRadioGroup.removeAllViews();
                LiveGiftDialogFragment.this.loadData("1");
            }
        });

        ra_xy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRadioGroup.removeAllViews();
                LiveGiftDialogFragment.this.loadData("3");
            }
        });

        ra_sh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRadioGroup.removeAllViews();
                LiveGiftDialogFragment.this.loadData("2");
            }
        });


        // 获取余额
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                BalanceInfo balanceInfo = new Gson().fromJson(info[0], BalanceInfo.class);
                mCoin.setText(String.valueOf(balanceInfo.getCoin()));
            }
        });
    }

    private void loadData(String mark) {
        String giftListData = CacheData.getGiftListData();
        if (!TextUtils.isEmpty(giftListData)) {
            L.e("===本地获取数据===");
            handleData(giftListData);
            return;
        }

        LiveHttpUtil.getGiftList(mark, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                JSONObject obj = JSON.parseObject(info[0]);
                String giftJson = obj.getString("giftlist");

                L.e("===网络获取数据===");
                CacheData.setGiftListData(giftJson);

                handleData(giftJson);
            }

            @Override
            public void onFinish() {
                if (mLoading != null) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * 处理数据
     *
     * @param data
     */
    private void handleData(String data) {
        List<LiveGiftBean> list = JSON.parseArray(data, LiveGiftBean.class);
        if (giftLists.size() != 0) {
            giftLists.clear();
        }
        giftLists.addAll(list);
        showGiftList(giftLists);
    }

    private void showGiftList(List<LiveGiftBean> list) {
        mLiveGiftPagerAdapter = new LiveGiftPagerAdapter(mContext, list);
        mLiveGiftPagerAdapter.setActionListener(this);
        mViewPager.setAdapter(mLiveGiftPagerAdapter);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0, size = mLiveGiftPagerAdapter.getCount(); i < size; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_gift_indicator, mRadioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            mRadioGroup.addView(radioButton);
        }
    }


    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mGiftCountPopupWindow != null) {
            mGiftCountPopupWindow.dismiss();
        }
        LiveHttpUtil.cancel(LiveHttpConsts.GET_GIFT_LIST);
        LiveHttpUtil.cancel(LiveHttpConsts.GET_COIN);
        LiveHttpUtil.cancel(LiveHttpConsts.SEND_GIFT);
        if (mLiveGiftPagerAdapter != null) {
            mLiveGiftPagerAdapter.release();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_send || i == R.id.btn_send_lian) {
            sendGift();

        } else if (i == R.id.btn_choose) {
            showGiftCount();

        } else if (i == R.id.btn_close) {
            dismiss();

        } else if (i == R.id.coin) {
            forwardMyCoin();

        } else if (i == R.id.btn_luck_gift_tip) {
            dismiss();
            ((LiveActivity) mContext).openLuckGiftTip();
        } else if (i == R.id.chognzhi) {
            forwardChongzhi();
        }
    }

    /**
     * 跳转到我的钻石
     */
    private void forwardMyCoin() {
        dismiss();
        //选择币种
        RouteUtil.forwardRecharge();
    }

    /**
     * 跳转到充值
     */
    private void forwardChongzhi() {
        dismiss();
        //选择币种
        RouteUtil.forwardRecharge();
    }

    /**
     * 显示分组数量
     */
    private void showGiftCount() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.view_gift_count, null);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true));
        LiveGiftCountAdapter adapter = new LiveGiftCountAdapter(mContext);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        mGiftCountPopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mGiftCountPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mGiftCountPopupWindow.setOutsideTouchable(true);
        mGiftCountPopupWindow.showAtLocation(mBtnChooseCount, Gravity.BOTTOM | Gravity.RIGHT, DpUtil.dp2px(70), DpUtil.dp2px(40));
    }

    /**
     * 隐藏分组数量
     */
    private void hideGiftCount() {
        if (mGiftCountPopupWindow != null) {
            mGiftCountPopupWindow.dismiss();
        }
    }


    @Override
    public void onItemClick(String bean, int position) {
        mCount = bean;
        mBtnChooseCount.setText(bean);
        hideGiftCount();
    }

    @Override
    public void onItemChecked(LiveGiftBean bean) {
        mLiveGiftBean = bean;
        hideLianBtn();
        mBtnSend.setEnabled(true);
        if (!DEFAULT_COUNT.equals(mCount)) {
            mCount = DEFAULT_COUNT;
            mBtnChooseCount.setText(DEFAULT_COUNT);
        }
        if (bean.getType() == LiveGiftBean.TYPE_DELUXE) {
            if (mBtnChooseCount != null && mBtnChooseCount.getVisibility() == View.VISIBLE) {
                mBtnChooseCount.setVisibility(View.INVISIBLE);
                mArrow.setVisibility(View.INVISIBLE);
                mBtnSend.setBackground(mDrawable2);
            }
        } else {
            if (mBtnChooseCount != null && mBtnChooseCount.getVisibility() != View.VISIBLE) {
                mBtnChooseCount.setVisibility(View.VISIBLE);
                mArrow.setVisibility(View.VISIBLE);
                mBtnSend.setBackground(mDrawable1);
            }
        }
    }

    /**
     * 赠送礼物
     */
    public void sendGift() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream) || mLiveGiftBean == null) {
            return;
        }
         /* if (mLiveGuardInfo != null) {
          if (mLiveGiftBean.getMark() == LiveGiftBean.MARK_GUARD && mLiveGuardInfo.getMyGuardType() < Constants.GUARD_TYPE_MONTH) {
                ToastUtil.show(R.string.guard_gift_tip);
                return;
            }

            if (mLiveGiftBean.getMark() == LiveGiftBean.MARK_GUARD && mLiveGuardInfo.getMyGuardType() != Constants.GUARD_TYPE_MONTH && mLiveGuardInfo.getMyGuardType() != Constants.GUARD_TYPE_YEAR) {
                ToastUtil.show(R.string.guard_gift_tip);
                return;
            }


        }*/
        SendGiftCallback callback = new SendGiftCallback(mLiveGiftBean);
        LiveHttpUtil.sendGift(mLiveUid, mStream, mLiveGiftBean.getId(), mCount, callback);
    }

    /**
     * 隐藏连送按钮
     */
    private void hideLianBtn() {
        mShowLianBtn = false;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() == View.VISIBLE) {
            mBtnSendLian.setVisibility(View.INVISIBLE);
        }
        if (mBtnSendGroup != null && mBtnSendGroup.getVisibility() != View.VISIBLE) {
            mBtnSendGroup.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示连送按钮
     */
    private void showLianBtn() {
        if (mLianText != null) {
            mLianText.setText("5s");
        }
        mLianCountDownCount = 5;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
        }
        if (mShowLianBtn) {
            return;
        }
        mShowLianBtn = true;
        if (mBtnSendGroup != null && mBtnSendGroup.getVisibility() == View.VISIBLE) {
            mBtnSendGroup.setVisibility(View.INVISIBLE);
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() != View.VISIBLE) {
            mBtnSendLian.setVisibility(View.VISIBLE);
        }
    }


    private class SendGiftCallback extends HttpCallback {

        private LiveGiftBean mGiftBean;

        public SendGiftCallback(LiveGiftBean giftBean) {
            mGiftBean = giftBean;
        }

        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
                        @Override
                        public void callback(UserBean u) {
                            if (u != null) {
                                u.setLevel(obj.getIntValue("level"));
                                u.setCoin(coin);
                            }
                        }
                    });
                    if (mCoin != null) {
                        mCoin.setText(coin);
                    }
                    if (mContext != null && mGiftBean != null) {
                        ((LiveActivity) mContext).onCoinChanged(coin);
                        ((LiveActivity) mContext).sendGiftMessage(mGiftBean, obj.getString("gifttoken"));
                    }
                    if (mLiveGiftBean.getType() == LiveGiftBean.TYPE_NORMAL) {
                        showLianBtn();
                    }
                }
            } else {
                hideLianBtn();
                ToastUtil.show(msg);
            }
        }
    }


}
