package com.qgnix.main.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.KeyboardUtils;
import com.qgnix.common.utils.RouteUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.adapter.RechargeAdapter;
import com.qgnix.main.adapter.RechargeAmountAdapter;
import com.qgnix.main.bean.RechargeAmountBean;
import com.qgnix.main.bean.RechargePaysBean;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.qgnix.main.utils.ReportEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sameal
 * @date 2020/7/17 21:17
 * 充值
 */
@Route(path = RouteUtil.RECHARGE_PATH)
public class RechargeActivity extends AbsActivity {
    /**
     * 金额
     */
    private EditText mEtAmount;
    /**
     * 金币
     */
    private TextView mTvCoin;
    /**
     * 公告
     */
    private TextView mTvNotice;
    /**
     * 线下支付
     */
    private TextView mTvOffline;
    private TextView mTvCrypto;
    /**
     * 汇率
     */
    private String mRate;
    /**
     * 金额
     */
    private final List<RechargeAmountBean> mAmounts = new ArrayList<>();
    private RechargeAmountAdapter mAmountAdapter;
    /**
     * 充值渠道（线上）
     */
    private RechargeAdapter mAdapter;

    private final List<RechargePaysBean.PayChannelsBean> mRechargeChannels = new ArrayList<>();

    /**
     * 线下充值渠道
     */
    private List<RechargePaysBean.PayChannelsBean> mBeanOffline;
    private RechargeAdapter mOfflineAdapter;

    private List<RechargePaysBean.PayChannelsBean> mBeanCrypto;
    private RechargeAdapter mCryptoAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recharge;
    }

    @Override
    protected void main() {
        super.main();
        TextView tvTitle = findViewById(R.id.titleView);
        tvTitle.setText(R.string.payment);

        mTvNotice = findViewById(R.id.tv_notice);
        mTvOffline = findViewById(R.id.tv_offline);
        mTvCrypto = findViewById(R.id.tv_crypto);
        // 金额
        mEtAmount = findViewById(R.id.et_amount);
        // 点击获取焦点
        mEtAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtAmount.setFocusable(true);
                mEtAmount.setFocusableInTouchMode(true);
            }
        });

        //回车事件关闭键盘
        mEtAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    KeyboardUtils.hideSoftInput(mActivity);
                    return true;
                }
                return false;
            }
        });

        mEtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                toRate();
                if (!TextUtils.isEmpty(s)) {
                    for (RechargeAmountBean amountBean : mAmounts) {
                        amountBean.setSelect(s.toString().equals(amountBean.getAmount()));
                    }
                    mAmountAdapter.notifyDataSetChanged();
                }
            }
        });
        // 金币
        mTvCoin = findViewById(R.id.tv_coin);
        mTvCoin.setText(WordUtil.getString(R.string.recharging_coins, "0"));
        // 充值金额
        RecyclerView rvAmount = findViewById(R.id.rv_amount);
        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        rvAmount.setLayoutManager(manager);
        mAmountAdapter = new RechargeAmountAdapter(mContext, mAmounts);
        mAmountAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                for (RechargeAmountBean bean : mAmounts) {
                    bean.setSelect(false);
                }
                RechargeAmountBean bean = mAmounts.get(position);
                mEtAmount.setText(bean.getAmount());
                mEtAmount.setSelection(bean.getAmount().length());
                toRate();
                bean.setSelect(true);
                mAmountAdapter.notifyDataSetChanged();
            }
        });
        rvAmount.setAdapter(mAmountAdapter);


        // 充值渠道(线上)
        RecyclerView rvOnlineChannels = findViewById(R.id.rv_online_channels);
        rvOnlineChannels.setLayoutManager(new LinearLayoutManager(mContext));

        mAdapter = new RechargeAdapter(mContext, mRechargeChannels);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                final RechargePaysBean.PayChannelsBean bean = mRechargeChannels.get(position);
                handleItemClick(bean, 1);
            }
        });
        rvOnlineChannels.setAdapter(mAdapter);


        //充值客服
        findViewById(R.id.recharge_customer_service).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Map<String,Object> reportMap = new HashMap<>();
                reportMap.put("value",true);
                ReportEvent.report(RechargeActivity.this,"iap_users",reportMap);

                Intent intent = new Intent(mContext, WebViewsActivity.class);
                intent.putExtra("title", WordUtil.getString(R.string.customer_service));
                intent.putExtra("url", CommonAppConfig.getInstance().getConfig().getChargeKefuUrl());
                mContext.startActivity(intent);
            }
        });

        //充值信息
        getRechargePays();

        if(CommonAppConfig.getInstance().getSwitchStatu()) {

            findViewById(R.id.recharge_customer_service).setVisibility(View.GONE);
            findViewById(R.id.tv_online).setVisibility(View.GONE);
            findViewById(R.id.rv_online_channels).setVisibility(View.GONE);
            findViewById(R.id.tv_offline).setVisibility(View.GONE);
            findViewById(R.id.rv_offline_channels).setVisibility(View.GONE);
            findViewById(R.id.rv_crypto_channels).setVisibility(View.GONE);
            mTvCrypto.setVisibility(View.GONE);
            findViewById(R.id.recharge_customer_google).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.recharge_customer_service).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_online).setVisibility(View.VISIBLE);
            findViewById(R.id.rv_online_channels).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_offline).setVisibility(View.VISIBLE);
            findViewById(R.id.rv_offline_channels).setVisibility(View.VISIBLE);
            findViewById(R.id.rv_crypto_channels).setVisibility(View.VISIBLE);
            mTvCrypto.setVisibility(View.VISIBLE);
            findViewById(R.id.recharge_customer_google).setVisibility(View.GONE);
        }
    }

    /**
     * @param bean
     * @param flag 1 线上 0 线下
     */
    private void handleItemClick(final RechargePaysBean.PayChannelsBean bean, int flag) {
        // 线下渠道
        if (null != mBeanOffline && !mBeanOffline.isEmpty()) {
            for (RechargePaysBean.PayChannelsBean payChannelsBean : mBeanOffline) {
                payChannelsBean.setSelect(false);
            }
        }
        if (null != mBeanCrypto && !mBeanCrypto.isEmpty()) {
            for (RechargePaysBean.PayChannelsBean payChannelsBean : mBeanCrypto) {
                payChannelsBean.setSelect(false);
            }
        }
        // 线上渠道
        for (RechargePaysBean.PayChannelsBean channel : mRechargeChannels) {
            channel.setSelect(false);
        }
        bean.setSelect(true);
        if (null != mOfflineAdapter) {
            mOfflineAdapter.notifyDataSetChanged();
        }
        if (null != mCryptoAdapter) {
            mCryptoAdapter.notifyDataSetChanged();
        }
        mAdapter.notifyDataSetChanged();

        String amount = mEtAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amount) || Integer.parseInt(amount) < bean.getMin()) {
            ToastUtil.show(WordUtil.getString(R.string.min_limit, bean.getMin()));
            return;
        }
        if (Integer.parseInt(amount) > bean.getMax()) {
            ToastUtil.show(WordUtil.getString(R.string.max_limit, bean.getMax()));
            return;
        }
        switch (flag) {
            case 0:
                // 线下充值
                Intent intent = new Intent(mContext, OfflineRechargeActivity.class);
                intent.putExtra("id", bean.getUrl());
                intent.putExtra("title", bean.getChannelName());
                intent.putExtra("currency", bean.getCurrency());
                intent.putExtra("amount", amount);
                startActivity(intent);
                break;
            case 1:
            case 2:
                if (bean.getUrl().startsWith("/")) {
                    WebViewsActivity.forward(mContext, CommonAppConfig.getCurHost() + bean.getUrl() + "&currency=" + bean.getCurrency() + "&money=" + amount, bean.getChannelName());
                }
                break;
        }
    }

    /**
     * 支付渠道
     */
    private void getRechargePays() {
        MainHttpUtil.rechargePay(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                RechargePaysBean bean = JSON.parseObject(info[0], RechargePaysBean.class);
                // 金币
                TextView mTvAmount = findViewById(R.id.tv_amount);
                String s = WordUtil.getString(R.string.amount, bean.getOnline().get(0).getCurrency());
                mTvAmount.setText(s);
                // 公告
                mTvNotice.setText(bean.getDeclare());

                mRate = bean.getRate();
                handleRechargePrice(bean.getAmounts());

                List<RechargePaysBean.PayChannelsBean> beanOnline = bean.getOnline();
                beanOnline.get(0).setSelect(true);
                mRechargeChannels.clear();
                mRechargeChannels.addAll(beanOnline);
                mAdapter.notifyDataSetChanged();

                // 线下充值渠道
                mBeanOffline = bean.getOffline();
                if(mBeanOffline==null ||mBeanOffline.size()==0){
                    findViewById(R.id.tv_offline).setVisibility(View.GONE);
                    findViewById(R.id.rv_offline_channels).setVisibility(View.GONE);
                }
                handleOffline(mBeanOffline);

                mBeanCrypto = bean.getCrypto();
                if(mBeanCrypto==null ||mBeanCrypto.size()==0){
                    findViewById(R.id.rv_crypto_channels).setVisibility(View.GONE);
                    mTvCrypto.setVisibility(View.GONE);
                }
                handleCrypto(mBeanCrypto);
            }
        });
    }

    /**
     * 线下充值
     *
     * @param beanOffline
     */
    private void handleOffline(final List<RechargePaysBean.PayChannelsBean> beanOffline) {
        if (null == beanOffline || beanOffline.isEmpty()) {
            return;
        }
        if(CommonAppConfig.getInstance().getSwitchStatu()) {
            return;
        }
        mTvOffline.setVisibility(View.VISIBLE);

        // 充值渠道(线下)
        RecyclerView rvOfflineChannels = findViewById(R.id.rv_offline_channels);
        rvOfflineChannels.setVisibility(View.VISIBLE);
        rvOfflineChannels.setLayoutManager(new LinearLayoutManager(mContext));


        mOfflineAdapter = new RechargeAdapter(mContext, beanOffline);
        mOfflineAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                final RechargePaysBean.PayChannelsBean bean = beanOffline.get(position);
                handleItemClick(bean, 0);
            }
        });
        rvOfflineChannels.setAdapter(mOfflineAdapter);

    }

    /**
     * crypto
     *
     * @param beanCrypto
     */
    private void handleCrypto(final List<RechargePaysBean.PayChannelsBean> beanCrypto) {
        if (null == beanCrypto || beanCrypto.isEmpty()) {
            return;
        }


        mTvCrypto.setVisibility(View.VISIBLE);

        // 充值渠道(Crypto)
        RecyclerView rvCryptoChannels = findViewById(R.id.rv_crypto_channels);
        rvCryptoChannels.setVisibility(View.VISIBLE);
        rvCryptoChannels.setLayoutManager(new LinearLayoutManager(mContext));
        if(CommonAppConfig.getInstance().getSwitchStatu()) {
            mTvCrypto.setVisibility(View.GONE);
            rvCryptoChannels.setVisibility(View.GONE);
        }
        mCryptoAdapter = new RechargeAdapter(mContext, beanCrypto);
        mCryptoAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                final RechargePaysBean.PayChannelsBean bean = beanCrypto.get(position);
                handleItemClick(bean, 2);
            }
        });
        rvCryptoChannels.setAdapter(mCryptoAdapter);
    }

    /**
     * 充值金额
     *
     * @param data
     */
    private void handleRechargePrice(String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }
        mAmounts.clear();
        String[] amounts = data.split(",");
        if (null == amounts || amounts.length == 0) {
            return;
        }
        RechargeAmountBean bean;
        for (String amount : amounts) {
            bean = new RechargeAmountBean();
            bean.setAmount(amount);
            bean.setSelect(false);
            mAmounts.add(bean);
        }
        String amount = mAmounts.get(0).getAmount();
        mEtAmount.setText(amount);
        //将光标移至文字末尾
        mEtAmount.setSelection(amount.length());
        toRate();
        mAmounts.get(0).setSelect(true);
        mAmountAdapter.notifyDataSetChanged();
    }

    /**
     * 汇率转换
     */
    private void toRate() {
        String amount = mEtAmount.getText().toString();
        String coin = "0";
        if (!TextUtils.isEmpty(amount)) {
            BigDecimal temp = new BigDecimal(amount.trim());
            coin = temp.multiply(new BigDecimal(mRate)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        }

        mTvCoin.setText(WordUtil.getString(R.string.recharging_coins, coin));
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.CHARGE_GET_PAYS);
        super.onDestroy();
    }
}