package com.qgnix.main.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.cache.CacheData;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.ClickUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.RandomUtil;
import com.qgnix.common.utils.RouteUtil;
import com.qgnix.common.utils.StringUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.VibrateUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.activity.BettingRecordActivity;
import com.qgnix.live.dialog.SelectChipDialog;
import com.qgnix.live.dialog.TicketHelpDialog;
import com.qgnix.live.interfaces.OnBettingCategoryDetailListener;
import com.qgnix.live.interfaces.OnSelectChipListener;
import com.qgnix.live.lottery.ConfirmBettingDialog;
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
import com.qgnix.main.R;
import com.qgnix.main.glide.util.Utils;
import com.qgnix.main.http.MainHttpUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

/**
 * ??????????????????
 *
 * @author sameal
 * @date 2020/7/19 13:08
 */
public class BettingSelectActivity extends AbsActivity implements View.OnClickListener, SocketCpMsgListener {
    /**
     * ????????????
     */
    private TabLayout mTl;
    /**
     * ????????????
     */
    private TextView mTvRight;
    /**
     * ??????????????????
     */
    private TextView mTvCurrentDrawNo;
    /**
     * ??????????????????
     */
    private TextView mTvCurrentEndTime;
    /**
     * ??????????????????
     */
    private TextView mTvLastDrawNo;
    /**
     * ????????????
     */
    private RecyclerView mRvDrawNumber;
    /**
     * ????????????
     */
    private RecyclerView recyclerView_three;
    /**
     * ????????????
     */
    private TextView mTvBalance;
    private String mBalance;
    /**
     * ????????????
     */
    private TextView mTvBettingInfo;

    // bc ?????????????????????
    private boolean isClosing;

    /**
     * ????????????
     */
    private TextView mTvChipAmount;
    /**
     * ????????????
     */
    private RelativeLayout mRlDraw;

    /**
     * ????????????
     */
    private final List<String> kjNumberList = new ArrayList<>();
    private KjNumberAdapter kjNumberAdapter;


    /**
     * ??????????????????
     */
    private List<BettingCategoryBean> mOneCategoryList = new ArrayList<>();

    /**
     * ??????????????????
     */
    private final List<BettingDetailsBean> mConfirmList = new ArrayList<>();

    /**
     * ?????????????????????
     */
    private CountDownTimer mDownTimer;
    /**
     * ??????
     */
    private int mMaxZs;

    /**
     * ????????????
     */
    private TicketData mTicketData;
    private BettingTwoCategoryAdapter mTwoAdapter;
    private BettingCategoryDetailAdapter mChildSelectAdapter;
    /**
     * ??????tab
     */
    private BettingCategoryBean mTabBean;
    private TextView mBetting;
    private TextView mTvRecharge;
    private SocketClient mSocketClient;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_betting_select;
    }

    @Override
    protected void main() {
        super.main();
        initView();
        initData();
    }

    public void initView() {
        mTl = findViewById(R.id.tl);
        //????????????
        mTvRight = findViewById(R.id.tv_right);
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setText(R.string.more);
        mTvRight.setOnClickListener(this);

        mTvCurrentEndTime = findViewById(R.id.tv_current_end_time);
        mTvCurrentDrawNo = findViewById(R.id.tv_current_draw_no);
        mTvLastDrawNo = findViewById(R.id.tv_last_draw_no);

        mRvDrawNumber = findViewById(R.id.rv_draw_number);
        recyclerView_three = findViewById(R.id.recyclerView_three);

        //??????
        mBetting = findViewById(R.id.betting);
        mBetting.setOnClickListener(this);
        // ??????
        mTvRecharge = findViewById(R.id.tv_recharge);
        mTvRecharge.setOnClickListener(this);
        // ??????
        findViewById(R.id.tv_reset).setOnClickListener(this);
        // ??????
        findViewById(R.id.tv_machine_selection).setOnClickListener(this);

        //????????????
        mTvChipAmount = findViewById(R.id.tv_chip_amount);
        mTvChipAmount.setOnClickListener(this);
        // ????????????
        mRlDraw = findViewById(R.id.rl_draw);

        // ????????????
        String[] chips = CommonAppConfig.getInstance().getChips();
        if (chips.length > 0) {
            mTvChipAmount.setText(chips[0]);
        }

        mTvBalance = findViewById(R.id.tv_balance);
        mTvBettingInfo = findViewById(R.id.tv_betting_info);

        mTicketData = JSON.parseObject(getIntent().getStringExtra("data"), TicketData.class);
        setTitle(mTicketData.getTitle());
        // ??????socket
        mSocketClient = new SocketClient(this);
        mSocketClient.connect(mTicketData.getId());

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
                    //???????????????
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

        updateBettingInfo();

    }

    public void initData() {
        // ????????????
        getClassify();
        loadData();
        // ??????????????????
        mRvDrawNumber.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        kjNumberAdapter = new KjNumberAdapter(mContext, kjNumberList, mTicketData.getType());
        kjNumberAdapter.setTextColorBlank(true);
        kjNumberAdapter.setShow4DTag(true);
        mRvDrawNumber.setAdapter(kjNumberAdapter);
    }

    @Override
    public void onClick(View v) {
        if (!ClickUtil.canClick()) {
            return;
        }
        int id = v.getId();
        // ????????????
        if (id == R.id.tv_chip_amount) {
            toSelectChip();
        } else if (id == R.id.tv_right) {
            moreMenu();
        } else if (id == R.id.betting) {
            // ??????
            mBetting.setClickable(false);
            toBetting();
        } else if (id == R.id.tv_recharge) {
            //??????
            RouteUtil.forwardRecharge();

        } else if (id == R.id.tv_reset) {
            //??????
            resetData();
        } else if (id == R.id.tv_machine_selection) {
            //??????
            machineSelection();
        }
    }


    /**
     * ????????????
     */
    private void toSelectChip() {
        SelectChipDialog selectChipDialog = new SelectChipDialog(mContext);
        selectChipDialog.setOnSelectChipListener(new OnSelectChipListener() {
            @Override
            public void onSelectChip(String amount) {
                mTvChipAmount.setText(amount);
            }
        });
        selectChipDialog.show();
    }

    /**
     * ??????
     */
    private void machineSelection() {
        resetData();
        // ????????????
        VibrateUtil.vibrate(this, 1000);
        if (mTabBean.getIsLast() == 1) {
            List<BettingDetailsBean> items = mTabBean.getItems();
            int i = RandomUtil.nextInt(items.size());
            BettingDetailsBean detailsBean = items.get(i);
            detailsBean.setSelect(true);
            mConfirmList.add(detailsBean);
            // ??????????????????
            updateBettingInfo();
            if (null != mChildSelectAdapter) {
                mChildSelectAdapter.notifyDataSetChanged();
            }
        } else {
            List<BettingCategoryBean> beanBet = mTabBean.getBet();

            for (BettingCategoryBean categoryBean : beanBet) {
                List<BettingDetailsBean> items = categoryBean.getItems();
                int i = RandomUtil.nextInt(items.size());
                BettingDetailsBean detailsBean = items.get(i);
                detailsBean.setB_id(categoryBean.getId());
                detailsBean.setChildTitle(categoryBean.getName());
                detailsBean.setSelect(true);
                // ???????????????
                categoryBean.setSelectedCount(1);
                mConfirmList.add(detailsBean);
                // ??????????????????
                updateBettingInfo();
            }
            toHandleNextCategory(beanBet);
        }
    }

    /**
     * ????????????
     */
    private void moreMenu() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_betting_more, null, false);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(mTvRight, 0, 0);
        // ????????????
        view.findViewById(R.id.recharge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????????
                RouteUtil.forwardRecharge();
            }
        });
        // ????????????
        view.findViewById(R.id.withdraw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, WithdrawActivity.class));
            }
        });
        // ????????????
        view.findViewById(R.id.betting_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BettingRecordActivity.class);
                intent.putExtra("tId", mTicketData.getId());
                startActivity(intent);
            }
        });
        // ????????????
        view.findViewById(R.id.historical_lottery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ????????????
                Intent intent = new Intent(mContext, DrawHistoryDetailActivity.class);
                intent.putExtra("tId", mTicketData.getId());
                intent.putExtra("tType", mTicketData.getType());
                intent.putExtra("tName", mTicketData.getTitle());
                startActivity(intent);
            }
        });
        // ????????????
        view.findViewById(R.id.rules_of_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toTicketHelp();
            }
        });
    }


    private void toTicketHelp() {
        MainHttpUtil.getTicketHelp(mTicketData.getId(), new HttpCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                L.d("===getTicketHelp==", new Gson().toJson(info));
                if (code != 0) {
                    ToastUtil.show(msg);
                    return;
                }
                if (info.length == 0) {
                    return;
                }
                try {
                    JsonObject obj = new JsonParser().parse(info[0]).getAsJsonObject();
                    String content = obj.get("help_content") == null ? "" : obj.get("help_content").getAsString();
                    new TicketHelpDialog(mContext, mTicketData.getTitle(), content).builder().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * ??????
     */
    private void toBetting() {
        if (isClosing) {
            ToastUtil.show(WordUtil.getString(R.string.Closeds));
            mBetting.setClickable(true);
        } else {
            if (mConfirmList.isEmpty()) {
                ToastUtil.show(WordUtil.getString(R.string.Please_select_betting_information));
                mBetting.setClickable(true);
                return;
            }

            String etChipAmount = mTvChipAmount.getText().toString().trim();
            int chipAmount = TextUtils.isEmpty(etChipAmount) ? 0 : Integer.parseInt(etChipAmount);

            final List<BettingDetailsBean> tempDList = new ArrayList<>();
            //  ??????
            if (mMaxZs > 0) {
                if (mConfirmList.size() != mMaxZs) {
                    ToastUtil.show(com.qgnix.live.R.string.must_choose_three, mMaxZs);
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
            new ConfirmBettingDialog(mContext, chipAmount, tempDList, new ConfirmBettingDialog.OnSuccessListener() {
                @Override
                public void onSuccess(int code, int totalPrice, String balance) {
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
        // ??????????????????
        updateBettingInfo();
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
     */
    private void handleData(String data) {
        mOneCategoryList = JSON.parseArray(data, BettingCategoryBean.class);
        int size = mOneCategoryList.size();
        if (size == 1) {
            mTl.setVisibility(View.GONE);
        } else {
            // ?????????????????????????????????????????????
            mTl.setTabMode(size > 3 ? TabLayout.MODE_SCROLLABLE : TabLayout.MODE_FIXED);
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

    /**
     * ?????????????????????
     *
     * @param netBet
     */
    private void toHandleNextCategory(List<BettingCategoryBean> netBet) {
        mTwoAdapter = new BettingTwoCategoryAdapter(mContext, netBet);
        recyclerView_three.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView_three.setAdapter(mTwoAdapter);
        mTwoAdapter.setOnBettingCategoryDetailListener(new OnBettingCategoryDetailListener() {
            @Override
            public void onSelectedDataListener(BettingDetailsBean selectedData) {
                mConfirmList.add(selectedData);
                // ??????????????????
                updateBettingInfo();
            }

            @Override
            public void onUnSelectedDataListener(BettingDetailsBean detailsBean) {
                mConfirmList.remove(detailsBean);
                // ??????????????????
                updateBettingInfo();
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param data
     */
    private void toHandleLastChild(final List<BettingDetailsBean> data) {
        recyclerView_three.setLayoutManager(new GridLayoutManager(mContext, 6));
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
                        ToastUtil.show(R.string.can_only_choose_three, mMaxZs);
                        return;
                    }
                    mConfirmList.add(detailsBean);
                    detailsBean.setSelect(true);
                }
                mChildSelectAdapter.notifyDataSetChanged();
                // ??????????????????
                updateBettingInfo();


            }
        });
        recyclerView_three.setAdapter(mChildSelectAdapter);
    }

    /**
     * ??????????????????
     */
    private void updateBettingInfo() {
        int totalMoney = 0;
        int notes = 0;
        if (!mConfirmList.isEmpty()) {
            int chip = Integer.parseInt(mTvChipAmount.getText().toString().trim());
            if (mMaxZs > 0) {
                totalMoney = chip;
                notes = 1;
            } else {
                notes = mConfirmList.size();
                for (BettingDetailsBean bean : mConfirmList) {
                    totalMoney += chip;
                }
            }
        }

        String source = WordUtil.getBlankHtmlStrFromRes(com.qgnix.live.R.string.Total)
                + WordUtil.getRedHtmlStr(notes)
                + WordUtil.getBlankHtmlStrFromRes(com.qgnix.live.R.string.note)
                + WordUtil.getRedHtmlStr("  " + totalMoney)
                + WordUtil.getBlankHtmlStrFromRes(R.string.yuan);
        mTvBettingInfo.setText(Html.fromHtml(source));
    }

    // ????????????
    private void loadData() {
        //???????????????????????????????????? - ??????tab?????? BC
        //  getTime();
        getBalance();
    }

    /**
     * ????????????
     */
    private void getBalance() {
        CommonHttpUtil.getUserBrief(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if (bean != null) {
                    mBalance = bean.getCoin();
                    mTvBalance.setText(String.valueOf(mBalance));
                }
            }
        });
//        CommonHttpUtil.getBalance(new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                if (code != 0) {
//                    ToastUtil.show(msg);
//                    return;
//                }
//                if (info.length == 0) {
//                    return;
//                }
//                BalanceInfo balanceInfo = new Gson().fromJson(info[0], BalanceInfo.class);
//                mTvBalance.setText(String.valueOf(balanceInfo.getCoin()));
//            }
//
//        });
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
        if (TextUtils.isEmpty(data)) {
            L.e("?????????????????????");
            return;
        }
        //  L.e("====????????????????????????????????????????????????===== ticket json:" + jsonStr);
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

        if (mRlDraw.getVisibility() == View.GONE) {
            mRlDraw.setVisibility(View.VISIBLE);
        }


        if (TextUtils.isEmpty(bean.getKj_no())) {
            bean.setKj_no("-");
        }
        String currentDrawNo = WordUtil.getRedHtmlStr(mCurrentDrawNo) + WordUtil.getBlankHtmlStrFromRes(R.string.period) + WordUtil.getBlankHtmlStrFromRes(R.string.close);
        mTvCurrentDrawNo.setText(Html.fromHtml(currentDrawNo));


        //?????????????????????????????????
        if (TextUtils.isEmpty(bean.getLast_kj_number())) {
            String lastDrawNo = WordUtil.getRedHtmlStr("-") + WordUtil.getBlankHtmlStrFromRes(R.string.period) + WordUtil.getBlankHtmlStrFromRes(R.string.Draw);
            mTvLastDrawNo.setText(Html.fromHtml(lastDrawNo));
        } else {
            String lastDrawNo = WordUtil.getRedHtmlStr(bean.getLast_kj_no()) + WordUtil.getBlankHtmlStrFromRes(R.string.period) + WordUtil.getBlankHtmlStrFromRes(R.string.Draw);
            mTvLastDrawNo.setText(Html.fromHtml(lastDrawNo));
            String[] kjNumber = bean.getLast_kj_number().split(",");
            kjNumberList.clear();
            kjNumberList.addAll(CpUtils.formatDrawNum(mTicketData.getType(), kjNumber));
            kjNumberAdapter.notifyDataSetChanged();
        }

        // ??????????????????
        int nowTime = bean.getNow_time();
        if (nowTime <= 2) {
            Log.e("==???????????????????????????====", "????????????????????????????????????" + nowTime);
            isClosing = true;
            mTvCurrentEndTime.setText("-:-");
        } else {
            isClosing = false;
            if (mDownTimer != null) {
                mDownTimer.cancel();
                mDownTimer = null;
            }
            mDownTimer = new CountDownTimer(nowTime * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // L.e("====????????????????????????????????????===== ticket :" + bean.getT_id() + " time:" + StringUtil.stringForTime(millisUntilFinished / 1000));
                    if (!isClosing)
                        mTvCurrentEndTime.setText(StringUtil.stringForTime(millisUntilFinished / 1000));
                    else {
                        mTvCurrentEndTime.setText("-:-");
                    }
                }

                @Override
                public void onFinish() {
                    isClosing = true;
                    mTvCurrentEndTime.setText("-:-");
                    String currentDrawNo = WordUtil.getRedHtmlStr(bean.getKj_no()) + WordUtil.getBlankHtmlStrFromRes(R.string.period) + WordUtil.getBlankHtmlStrFromRes(R.string.closing);
                    mTvCurrentDrawNo.setText(Html.fromHtml(currentDrawNo));
                }
            };
            mDownTimer.start();
        }

    }

    @Override
    protected void onDestroy() {
        if (mDownTimer != null) {
            mDownTimer.cancel();
            mDownTimer = null;
        }

        //??????socket
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }

        CommonHttpUtil.cancel(CommonHttpConsts.BC_TICKET_GET_ODDS_V2);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_BALANCE);
        CommonHttpUtil.cancel(CommonHttpConsts.BC_TICKET_GET_QS);
        super.onDestroy();
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








    //=======================================lost code========================//













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




    // ????????????????????? Activity ??????????????????????????????????????????????????????You cannot start a load for a destroyed activity
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void assertNotDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }

    //===============================================================//
}