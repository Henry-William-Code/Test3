package com.qgnix.live.lottery;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.cache.CacheData;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ClickUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.LanSwitchUtil;
import com.qgnix.common.utils.RouteUtil;
import com.qgnix.common.utils.StringUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.live.dialog.SelectChipDialog;
import com.qgnix.live.interfaces.OnBettingCategoryDetailListener;
import com.qgnix.live.interfaces.OnSelectChipListener;
import com.qgnix.live.lottery.adapter.BettingCategoryDetailAdapter;
import com.qgnix.live.lottery.adapter.BettingTwoCategoryAdapter;
import com.qgnix.live.lottery.adapter.KjNumberAdapter;
import com.qgnix.live.lottery.entry.BalanceInfo;
import com.qgnix.live.lottery.entry.BettingCategoryBean;
import com.qgnix.live.lottery.entry.BettingDetailsBean;
import com.qgnix.live.lottery.entry.TicketData;
import com.qgnix.live.lottery.entry.TimeBean;
import com.qgnix.live.socket.SocketClient;
import com.qgnix.live.socket.SocketCpMsgListener;
import com.qgnix.live.utils.CpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ?????????????????????
 */
public class BettingSelectDialog extends BaseCustomDialog implements View.OnClickListener, SocketCpMsgListener {


    /**
     * ????????????
     */
    private RecyclerView recyclerView_one;

    /**
     * ????????????
     */
    private TabLayout mTl;
    /**
     * ????????????
     */
    private RecyclerView recyclerView_three;
    /**
     * ??????
     */
    private TextView tv_number;
    /**
     * ????????????
     */
    private TextView mTvBalance;
    private String mBalance;

    private final Context mContext;

    /**
     * ????????????
     */
    private TextView tv_kjNo;
    /**
     * ??????????????????
     */
    private TextView mTvCurrentEndTime;

    /**
     * ????????????
     */
    private static TicketData mTicketData;

    /**
     * ????????????
     */
    private final List<String> kjNumberList = new ArrayList<>();
    private KjNumberAdapter kjNumberAdapter;

    /**
     * ????????????
     */
    private List<BettingCategoryBean> mOneCategoryList = new ArrayList<>();
    /**
     * ??????adapter
     */
    private BettingTwoCategoryAdapter mTwoAdapter;
    /**
     * ????????????
     */
    private BettingCategoryDetailAdapter mChildSelectAdapter;

    /**
     * ??????????????????
     */
    private final List<BettingDetailsBean> mConfirmList = new ArrayList<>();

    // bc ?????????????????????
    private boolean isClosing;
    // ????????????

    /**
     * ??????
     */
    private int mMaxZs;

    /**
     * ??????tab
     */
    private BettingCategoryBean mTabBean;

    private SocketClient mSocketClient;

    //???????????????????????????????????????
    private CountDownTimer downTimer;
    private TextView mBetting;

    public BettingSelectDialog(Context context, TicketData mTicketData) {
        super(context);
        this.mContext = context;
        BettingSelectDialog.mTicketData = mTicketData;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_betting_select;
    }

    @Override
    public void initView() {

        mTl = findViewById(R.id.tl);
        recyclerView_one = findViewById(R.id.recyclerView_one);
        recyclerView_three = findViewById(R.id.recyclerView_three);

        mTvCurrentEndTime = findViewById(R.id.tv_current_end_time);

        // ????????????
        tv_number = findViewById(R.id.tv_number);
        tv_number.setText(CommonAppConfig.getInstance().getChips()[0]);
        // ?????????
        tv_number.setOnClickListener(this);
        // ??????
        mBetting = findViewById(R.id.betting);
        mBetting.setOnClickListener(this);
        // ????????????
        findViewById(R.id.iv_switch_ticket).setOnClickListener(this);
        //??????
        findViewById(R.id.tv_recharge).setOnClickListener(this);
        tv_kjNo = findViewById(R.id.tv_kjNo);  //???????????????

        // ????????????
        ImageView ivTicketImg = findViewById(R.id.iv_ticket_img);
        ImgLoader.display(mContext, mTicketData.getImage(), ivTicketImg);
        //????????????
        TextView tvTicketTitle = findViewById(R.id.tv_ticket_title);
        tvTicketTitle.setText(mTicketData.getTitle());

        // ??????socket
        mSocketClient = new SocketClient(this);
        mSocketClient.connect(mTicketData.getId());


        mTvBalance = findViewById(R.id.tv_balance);


        // tabLayout????????????
        mTl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                resetData();

                int position = tab.getPosition();
                mTabBean = mOneCategoryList.get(position);
                // ??????
                mMaxZs = mTabBean.getZs();
                //????????????
                if (mTabBean.getIsLast() == 1) {
                    // ????????????
                    toHandleLastChild(mTabBean.getItems());
                } else {
                    // ???????????????
                    toHandleNextCategory(mTabBean.getBet());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // TabLayout???????????????
        LinearLayout linearLayout = (LinearLayout) mTl.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(mContext, R.drawable.tab_layout_divider));
    }

    /**
     * ??????????????????
     *
     * @param data
     */
    private void toHandleLastChild(final List<BettingDetailsBean> data) {
        for (BettingDetailsBean bean : data) {
            bean.setSelect(false);
        }
        recyclerView_three.setLayoutManager(new GridLayoutManager(mContext, Math.min(data.size(), 6)));
        // ????????????
        mChildSelectAdapter = new BettingCategoryDetailAdapter(mContext, data);

        mChildSelectAdapter.setOnItemClickListener(new BettingCategoryDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

                BettingDetailsBean detailsBean = data.get(position);
                if (detailsBean.isSelect()) {
                    mConfirmList.remove(detailsBean);
                    detailsBean.setSelect(false);
                } else {
                    //  ??????
                    if (mMaxZs > 0 && mConfirmList.size() >= mMaxZs) {
                        ToastUtil.show(com.qgnix.live.R.string.can_only_choose_three, mMaxZs);
                        return;
                    }
                    mConfirmList.add(detailsBean);
                    detailsBean.setSelect(true);
                }
                mChildSelectAdapter.notifyDataSetChanged();
            }
        });
        recyclerView_three.setAdapter(mChildSelectAdapter);
    }


    /**
     * ?????????????????????
     *
     * @param netBet
     */
    private void toHandleNextCategory(List<BettingCategoryBean> netBet) {
        mTwoAdapter = new BettingTwoCategoryAdapter(mContext, netBet);
        mTwoAdapter.setIsLive(true);
        recyclerView_three.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView_three.setAdapter(mTwoAdapter);
        mTwoAdapter.setOnBettingCategoryDetailListener(new OnBettingCategoryDetailListener() {
            @Override
            public void onSelectedDataListener(BettingDetailsBean selectedData) {
                mConfirmList.add(selectedData);
            }

            @Override
            public void onUnSelectedDataListener(BettingDetailsBean detailsBean) {
                mConfirmList.remove(detailsBean);
            }
        });
    }

    @Override
    public void initData() {
        // ????????????
        getClassify();
        getBalance();
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_one.setLayoutManager(manager);
        kjNumberAdapter = new KjNumberAdapter(mContext, kjNumberList, mTicketData.getType());
        recyclerView_one.setAdapter(kjNumberAdapter);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public int showGravity() {
        return Gravity.BOTTOM;
    }

    private SelectChipDialog selectChipDialog;

    @Override
    public void onClick(View v) {
        if (!ClickUtil.canClick()) {
            return;
        }
        int vId = v.getId();
        if (vId == R.id.tv_number) {
            //????????????
            if (selectChipDialog == null) {
                selectChipDialog = new SelectChipDialog(mContext);
                selectChipDialog.setOnSelectChipListener(new OnSelectChipListener() {
                    @Override
                    public void onSelectChip(String amount) {
                        tv_number.setText(amount);
                    }
                });
            }
            selectChipDialog.show();
        } else if (vId == R.id.tv_recharge) {
            //????????????
            RouteUtil.forwardRecharge();
        } else if (vId == R.id.iv_switch_ticket) {
            //??????????????????
            new LotteryTypeDialog(mContext).show();
            dismiss();
        } else if (vId == R.id.betting) {
            mBetting.setClickable(false);
            // ??????
            toBetting();
        }
    }

    /**
     * ??????
     */
    private void toBetting() {
        if (isClosing) {
            ToastUtil.show(WordUtil.getString(R.string.Closeds));
            mBetting.setClickable(true);
        } else {
            if (null == mConfirmList || mConfirmList.isEmpty()) {
                ToastUtil.show(WordUtil.getString(R.string.Please_select_betting_information));
                mBetting.setClickable(true);
                return;
            }

            final List<BettingDetailsBean> tempDList = new ArrayList<>();

            //  ??????
            if (mMaxZs > 0) {
                if (mConfirmList.size() != mMaxZs) {
                    ToastUtil.show(R.string.must_choose_three, mMaxZs);
                    mBetting.setClickable(true);
                    return;
                }
                StringBuilder sbId = new StringBuilder();
                StringBuilder sbBId = new StringBuilder();
                StringBuilder sb = new StringBuilder();
                for (BettingDetailsBean bean : mConfirmList) {
                    sb.append(bean.getOdds_name()).append(",");
                    sbId.append(bean.getId()).append(",");
                    sbBId.append(bean.getB_id()).append("-");
                }
                BettingDetailsBean temp = new BettingDetailsBean();
                temp.setOdds(mConfirmList.get(0).getOdds());
                temp.setOdds_name(sb.deleteCharAt(sb.length() - 1).toString());
                temp.setId(sbId.deleteCharAt(sbId.length() - 1).toString());
                temp.setB_id(mTabBean.getId() + "|" + sbBId.deleteCharAt(sbBId.length() - 1).toString());
                temp.setChildTitle(mTabBean.getName());
                temp.setTicketName(mTicketData.getTitle());
                tempDList.add(temp);
            } else {
                for (BettingDetailsBean bean : mConfirmList) {
                    bean.setTicketName(mTicketData.getTitle());
                    // ??????
                    if (mTabBean.getIsLast() == 1) {
                        bean.setB_id(mTabBean.getId());
                        bean.setChildTitle(mTabBean.getName());
                    } else {
                        bean.setB_id(mTabBean.getId() + "|" + bean.getB_id());
                        if (TextUtils.isEmpty(mTabBean.getName())) {
                            bean.setChildTitle(bean.getChildTitle());
                        } else {
                            bean.setChildTitle(mTabBean.getName() + "-" + bean.getChildTitle());
                        }
                    }
                    tempDList.add(bean);
                }
            }
            new ConfirmBettingDialog(mContext, Integer.parseInt(tv_number.getText().toString().trim()), tempDList, new ConfirmBettingDialog.OnSuccessListener() {
                @Override
                public void onSuccess(int code, int totalPrice,String balance) {
                    mBetting.setClickable(true);
                    tempDList.clear();
                    resetData();
                    // ??????????????????
                    if (code == 200) {
                        if (TextUtils.isEmpty(balance)) {
                            float balanceValue = Float.parseFloat(mBalance);
                            mBalance = "" + (balanceValue - totalPrice);
                        } else {
                            mBalance = balance.contains(".") ? balance : balance + ".00";
                        }
                        mTvBalance.setText(mBalance);
//                        getBalance();
                    }
                }

                @Override
                public void onFail() {
                    mBetting.setClickable(true);
                    tempDList.clear();
                    resetData();
                }
            }).show();
        }
    }

    /**
     * ????????????
     */
    private void resetData() {
        //??????????????????
        mConfirmList.clear();

        if (null != mTwoAdapter) {
            mTwoAdapter.resetData();
        }
        if (null != mChildSelectAdapter) {
            mChildSelectAdapter.resetData();
        }
    }

    // ????????????
    public void getClassify() {
        String ticketOodsData = CacheData.getOodsData(mTicketData.getId());
        if (!TextUtils.isEmpty(ticketOodsData)) {
            L.e("===??????????????????===");
            handleData(ticketOodsData);
            return;
        }

        CommonHttpUtil.getBCTicketGetOddsV2(mTicketData.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                L.e("===??????????????????===");
                CacheData.setOodsData(info[0], mTicketData.getId());

                handleData(info[0]);


            }
        });
    }

    /**
     * ????????????
     *
     * @param data
     */
    private void handleData(String data) {
        mOneCategoryList = JSON.parseArray(data, BettingCategoryBean.class);
        int size = mOneCategoryList.size();
        if (size == 1) {
            mTl.setVisibility(View.GONE);
        } else {
            // ?????????????????????????????????????????????

            mTl.setTabMode(size > (LanSwitchUtil.isChina() ? 4 : 3) ? TabLayout.MODE_SCROLLABLE : TabLayout.MODE_FIXED);
            for (BettingCategoryBean bean : mOneCategoryList) {
                mTl.addTab(mTl.newTab().setText(bean.getName()));
            }
        }
        // ?????????????????????
        mTabBean = mOneCategoryList.get(0);
        mMaxZs = mTabBean.getZs();
        if (mTabBean.getIsLast() == 1) {
            // ????????????
            toHandleLastChild(mTabBean.getItems());
        } else {
            // ???????????????
            toHandleNextCategory(mTabBean.getBet());
        }
    }

    // ????????????
    private void getBalance() {

        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    ToastUtil.show(msg);
                    return;
                }
                if (info.length == 0) {
                    return;
                }
                BalanceInfo balanceInfo = new Gson().fromJson(info[0], BalanceInfo.class);
                mBalance = String.valueOf(balanceInfo.getCoin());
                mTvBalance.setText(mBalance);
            }

        });
    }


    /**
     * ???????????????
     */
    private String mCurrentDrawNo;

    /**
     * ??????????????????
     *
     * @param data
     */
    private void handleBCTicketRes(String data) {
        //?????????????????????????????????
        if (TextUtils.isEmpty(data)) {
            L.e("?????????????????????");
            return;
        }
        final TimeBean bean = JSON.parseObject(data, TimeBean.class);
        if (null == bean) {
            mTvCurrentEndTime.setText("-:-");
            return;
        }
        if (TextUtils.isEmpty(bean.getKj_no()) || TextUtils.isEmpty(bean.getLast_kj_number())) {
            mTvCurrentEndTime.setText("-:-");
            return;
        }

        //???????????? ????????????????????????
        if (null != mCurrentDrawNo && mCurrentDrawNo.equals(bean.getKj_no())) {
            return;
        }
        mCurrentDrawNo = bean.getKj_no();//????????????

        // ??????????????????
        int nowTime = bean.getNow_time();
        if (nowTime <= 2) {  //???????????????????????????????????? ?????????????????? ????????????
            Log.d("???????????????", "?????????????????? nowTime: " + nowTime);
            isClosing = true;
            mTvCurrentEndTime.setText(Html.fromHtml(getCurrentDrawTimeStr(WordUtil.getString(R.string.closing))));
        } else {
            isClosing = false;
            if (downTimer != null) {
                downTimer.cancel();
                downTimer = null;
            }

            downTimer = new CountDownTimer(nowTime * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //  L.e("====????????????????????????????????????===== ticket :" + bean.getT_id() + " time:" + StringUtil.stringForTime(millisUntilFinished / 1000));
                    if (!isClosing) {
                        mTvCurrentEndTime.setText(Html.fromHtml(getCurrentDrawTimeStr(StringUtil.stringForTime(millisUntilFinished / 1000))));
                    } else {
                        mTvCurrentEndTime.setText(Html.fromHtml(getCurrentDrawTimeStr(WordUtil.getString(R.string.closing))));
                    }
                }

                @Override
                public void onFinish() {
                    isClosing = true;
                    mTvCurrentEndTime.setText(Html.fromHtml(getCurrentDrawTimeStr(WordUtil.getString(R.string.closing))));
                }
            }.start();
        }

        //?????????????????????????????????
        if (TextUtils.isEmpty(bean.getLast_kj_number())) {
            return;
        }
        tv_kjNo.setText(WordUtil.getString(R.string.Last_draw, bean.getLast_kj_no()));
        String[] kjNumber = bean.getLast_kj_number().split(",");
        kjNumberList.clear();
        kjNumberList.addAll(CpUtils.formatDrawNum(mTicketData.getType(), kjNumber));
        kjNumberAdapter.notifyDataSetChanged();
    }

    /**
     * ?????????????????????????????????
     *
     * @param data
     * @return
     */
    private String getCurrentDrawTimeStr(String data) {
        return WordUtil.getWriteHtmlStrFromRes(R.string.current_period_end) + WordUtil.getOrangeHtmlStr(data);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (downTimer != null) {
            downTimer.cancel();
        }
    }

    @Override
    public void onLotteryInfo(String data) {
        handleBCTicketRes(data);
    }

    @Override
    public void onConnect(boolean successConn) {
        L.e("=====???????????????socket??????===" + successConn);
    }

    @Override
    public void onDisConnect() {
        L.e("=====???????????????socket????????????===");
    }
}
