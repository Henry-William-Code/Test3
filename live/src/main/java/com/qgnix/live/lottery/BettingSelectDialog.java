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
 * 直播间选择投注
 */
public class BettingSelectDialog extends BaseCustomDialog implements View.OnClickListener, SocketCpMsgListener {


    /**
     * 开奖号码
     */
    private RecyclerView recyclerView_one;

    /**
     * 一级分类
     */
    private TabLayout mTl;
    /**
     * 投注号码
     */
    private RecyclerView recyclerView_three;
    /**
     * 投注
     */
    private TextView tv_number;
    /**
     * 账户余额
     */
    private TextView mTvBalance;
    private String mBalance;

    private final Context mContext;

    /**
     * 开奖号码
     */
    private TextView tv_kjNo;
    /**
     * 本期截止时间
     */
    private TextView mTvCurrentEndTime;

    /**
     * 彩票信息
     */
    private static TicketData mTicketData;

    /**
     * 开奖号码
     */
    private final List<String> kjNumberList = new ArrayList<>();
    private KjNumberAdapter kjNumberAdapter;

    /**
     * 一级分类
     */
    private List<BettingCategoryBean> mOneCategoryList = new ArrayList<>();
    /**
     * 二级adapter
     */
    private BettingTwoCategoryAdapter mTwoAdapter;
    /**
     * 投注明细
     */
    private BettingCategoryDetailAdapter mChildSelectAdapter;

    /**
     * 确认投注信息
     */
    private final List<BettingDetailsBean> mConfirmList = new ArrayList<>();

    // bc 游戏是否封盘中
    private boolean isClosing;
    // 默认倍数

    /**
     * 注数
     */
    private int mMaxZs;

    /**
     * 一级tab
     */
    private BettingCategoryBean mTabBean;

    private SocketClient mSocketClient;

    //直播间下方游戏按钮的计时器
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

        // 投注筹码
        tv_number = findViewById(R.id.tv_number);
        tv_number.setText(CommonAppConfig.getInstance().getChips()[0]);
        // 初始值
        tv_number.setOnClickListener(this);
        // 投注
        mBetting = findViewById(R.id.betting);
        mBetting.setOnClickListener(this);
        // 切换彩票
        findViewById(R.id.iv_switch_ticket).setOnClickListener(this);
        //充值
        findViewById(R.id.tv_recharge).setOnClickListener(this);
        tv_kjNo = findViewById(R.id.tv_kjNo);  //上期开奖号

        // 彩票信息
        ImageView ivTicketImg = findViewById(R.id.iv_ticket_img);
        ImgLoader.display(mContext, mTicketData.getImage(), ivTicketImg);
        //彩票名称
        TextView tvTicketTitle = findViewById(R.id.tv_ticket_title);
        tvTicketTitle.setText(mTicketData.getTitle());

        // 链接socket
        mSocketClient = new SocketClient(this);
        mSocketClient.connect(mTicketData.getId());


        mTvBalance = findViewById(R.id.tv_balance);


        // tabLayout监听事件
        mTl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                resetData();

                int position = tab.getPosition();
                mTabBean = mOneCategoryList.get(position);
                // 注数
                mMaxZs = mTabBean.getZs();
                //最后一级
                if (mTabBean.getIsLast() == 1) {
                    // 投注明细
                    toHandleLastChild(mTabBean.getItems());
                } else {
                    // 下一级分类
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


        // TabLayout中间分界线
        LinearLayout linearLayout = (LinearLayout) mTl.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(mContext, R.drawable.tab_layout_divider));
    }

    /**
     * 处理投注明细
     *
     * @param data
     */
    private void toHandleLastChild(final List<BettingDetailsBean> data) {
        for (BettingDetailsBean bean : data) {
            bean.setSelect(false);
        }
        recyclerView_three.setLayoutManager(new GridLayoutManager(mContext, Math.min(data.size(), 6)));
        // 投注明细
        mChildSelectAdapter = new BettingCategoryDetailAdapter(mContext, data);

        mChildSelectAdapter.setOnItemClickListener(new BettingCategoryDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

                BettingDetailsBean detailsBean = data.get(position);
                if (detailsBean.isSelect()) {
                    mConfirmList.remove(detailsBean);
                    detailsBean.setSelect(false);
                } else {
                    //  注数
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
     * 处理下一级分类
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
        // 获取分类
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
            //选择筹码
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
            //选择币种
            RouteUtil.forwardRecharge();
        } else if (vId == R.id.iv_switch_ticket) {
            //切换彩票信息
            new LotteryTypeDialog(mContext).show();
            dismiss();
        } else if (vId == R.id.betting) {
            mBetting.setClickable(false);
            // 投注
            toBetting();
        }
    }

    /**
     * 投注
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

            //  注数
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
                    // 一级
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
                    // 更新账户余额
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
     * 重置数据
     */
    private void resetData() {
        //清空选中数量
        mConfirmList.clear();

        if (null != mTwoAdapter) {
            mTwoAdapter.resetData();
        }
        if (null != mChildSelectAdapter) {
            mChildSelectAdapter.resetData();
        }
    }

    // 获取分类
    public void getClassify() {
        String ticketOodsData = CacheData.getOodsData(mTicketData.getId());
        if (!TextUtils.isEmpty(ticketOodsData)) {
            L.e("===本地获取数据===");
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
                L.e("===网络获取数据===");
                CacheData.setOodsData(info[0], mTicketData.getId());

                handleData(info[0]);


            }
        });
    }

    /**
     * 处理数据
     *
     * @param data
     */
    private void handleData(String data) {
        mOneCategoryList = JSON.parseArray(data, BettingCategoryBean.class);
        int size = mOneCategoryList.size();
        if (size == 1) {
            mTl.setVisibility(View.GONE);
        } else {
            // 大于三个可以滚动，小于三个固定

            mTl.setTabMode(size > (LanSwitchUtil.isChina() ? 4 : 3) ? TabLayout.MODE_SCROLLABLE : TabLayout.MODE_FIXED);
            for (BettingCategoryBean bean : mOneCategoryList) {
                mTl.addTab(mTl.newTab().setText(bean.getName()));
            }
        }
        // 默认选中第一个
        mTabBean = mOneCategoryList.get(0);
        mMaxZs = mTabBean.getZs();
        if (mTabBean.getIsLast() == 1) {
            // 投注明细
            toHandleLastChild(mTabBean.getItems());
        } else {
            // 下一级分类
            toHandleNextCategory(mTabBean.getBet());
        }
    }

    // 获取余额
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
     * 当前开奖号
     */
    private String mCurrentDrawNo;

    /**
     * 处理开奖信息
     *
     * @param data
     */
    private void handleBCTicketRes(String data) {
        //直播间下方游戏中的心跳
        if (TextUtils.isEmpty(data)) {
            L.e("开奖数据为空！");
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

        //初始状态 或者新的一期来临
        if (null != mCurrentDrawNo && mCurrentDrawNo.equals(bean.getKj_no())) {
            return;
        }
        mCurrentDrawNo = bean.getKj_no();//本期期号

        // 开奖截止时间
        int nowTime = bean.getNow_time();
        if (nowTime <= 2) {  //拉取的数据如果还剩下三秒 显示为封盘中 不让投注
            Log.d("数据有问题", "获取投注期数 nowTime: " + nowTime);
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
                    //  L.e("====直播间下方游戏按钮倒计时===== ticket :" + bean.getT_id() + " time:" + StringUtil.stringForTime(millisUntilFinished / 1000));
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

        //设置上期期号及开奖结果
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
     * 获取当前开奖时间字符串
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
        L.e("=====游戏详情，socket连接===" + successConn);
    }

    @Override
    public void onDisConnect() {
        L.e("=====游戏详情，socket断开连接===");
    }
}
