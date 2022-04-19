package com.qgnix.main.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.main.bean.BankCardBean;
import com.qgnix.main.bean.UserBankBean;
import com.qgnix.main.dialog.AnchorSalaryWithdrawDialog;
import com.qgnix.main.dialog.SelectBankCardDialog;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.qgnix.main.interfaces.OnSelectBankCardListener;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author sameal
 * @date 2020/7/16 21:38
 * 提现页
 */
public class WithdrawActivity extends AbsActivity implements View.OnClickListener {
    private TextView mTvRight;
    /**
     * 添加银行卡
     */
    private LinearLayout mLlAddBank;
    /**
     * 已选择银行卡信息
     */
    private RelativeLayout mRlBankInfo;
    /**
     * 银行名称
     */
    private TextView mTvBankName;
    /**
     * 银行卡号
     */
    private TextView mTvCardAccountNo;

    /**
     * 汇率
     */
    private TextView mTvExchangeRate;

    /**
     * 余额
     */
    private TextView mTvBalance;

    /**
     * 需打的流水为
     */
    private TextView mTvWageringRequirement;
    /**
     * 可提现目标金额
     */
    private TextView mTvTargetAmount;
    /**
     * 提现金额
     */
    private EditText mEtAmount;
    /**
     * 提现密码
     */
    private EditText mEtTransactionPassword;

    /**
     * 余额类型 0金币,1门票 2 返点
     */
    private int balanceType;
    /**
     * 银行卡Id
     */
    private String mCardAccountId;
    /**
     * 银行卡数据
     */
    private List<BankCardBean> mBankCards;
    /**
     * 选择银行卡弹框
     */
    private SelectBankCardDialog mSelectBankCardDialog;
    /**
     * 银行卡信息
     */
    private BankCardBean mCardBean;
    private Button mBtnSubmit;
    /**
     * 提现金额限制
     */
    private TextView mTvTip;
    /**
     * 是否设置过交易密码 0 表示未设置过 1 表示设置过
     */
    private int transactionStatus = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_withdraw;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.withdraw));

        mTvTip = findViewById(R.id.tv_tip);
        mLlAddBank = findViewById(R.id.ll_add_bank);
        //选择银行卡右箭头
        ImageView ivNext = findViewById(R.id.iv_arrow);
        mRlBankInfo = findViewById(R.id.rl_bank_info);

        mTvBankName = findViewById(R.id.tv_bank_name);
        mTvCardAccountNo = findViewById(R.id.tv_card_account_no);
        mTvExchangeRate = findViewById(R.id.tv_exchange_rate);
        mTvTargetAmount = findViewById(R.id.tv_target_amount);

        mEtAmount = findViewById(R.id.et_amount);
        mEtTransactionPassword = findViewById(R.id.et_transaction_password);

        mTvBalance = findViewById(R.id.tv_balance);
        mTvWageringRequirement = findViewById(R.id.tv_wagering_requirement);

        // 主播工资提现
        final Button btnAnchorWithdraw = findViewById(R.id.btn_anchor_withdraw);
        CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if ("1".equals(bean.getIszhubo())) {
                    btnAnchorWithdraw.setVisibility(View.VISIBLE);
                    btnAnchorWithdraw.setOnClickListener(WithdrawActivity.this);
                }
            }
        });
        balanceType = getIntent().getIntExtra("balanceType", 0);
        // 菜单新增银行卡/ 印度联系客服
        mTvRight = findViewById(R.id.tv_right);
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setOnClickListener(this);
        mLlAddBank.setOnClickListener(this);
        // 选择银行卡
        if (!isIndia()) {
            ivNext.setVisibility(View.VISIBLE);
            mRlBankInfo.setOnClickListener(this);
        }
        mBtnSubmit = findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);
        //重置交易密码
        findViewById(R.id.reset_transaction_password).setOnClickListener(this);

        mEtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handleTargetAmount(charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handleTargetAmount(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        getData();
    }

    /**
     * 处理目标金额
     *
     * @param etAmount
     */
    private void handleTargetAmount(CharSequence etAmount) {
        etAmount = etAmount.toString().trim();
        if (TextUtils.isEmpty(mCardAccountId)) {
            ToastUtil.show(R.string.Please_add_a_bank_card);
            return;
        }
        if (TextUtils.isEmpty(etAmount) || mCardBean == null) {
            resetTargetAmount();
        } else {
            BigDecimal temp = new BigDecimal(etAmount.toString());//, 2, BigDecimal.ROUND_HALF_UP
            String targetAmount = (temp.multiply(new BigDecimal(mCardBean.getRate())).setScale(2, BigDecimal.ROUND_HALF_UP)).toString();
            mTvTargetAmount.setText(mCardBean.getCurrency() + "：" + targetAmount);
        }
    }

    /**
     * 充值目标金额
     */
    private void resetTargetAmount() {
        if (null == mCardBean || TextUtils.isEmpty(mCardBean.getCurrency())) {
            mTvTargetAmount.setVisibility(View.GONE);
        }
        mTvTargetAmount.setText(mCardBean.getCurrency() + "：0");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            getData();
        }else  if (requestCode == SetTransactionPwdAct.RETURN_CODE) {
            this.transactionStatus = data.getIntExtra("setPwResult",0);
            if(this.transactionStatus==1){
                ((TextView)findViewById(R.id.reset_transaction_password)).setText(R.string.up_pay_pwd);
            }
        }
    }


    /**
     * 获取数据
     */
    private void getData() {
        MainHttpUtil.getCashAccountList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                UserBankBean userBankBean = JSON.parseObject(info[0], UserBankBean.class);
                handleDataToView(userBankBean);
            }
        });
    }

    /**
     * 处理数据
     *
     * @param userBankBean
     */
    private void handleDataToView(UserBankBean userBankBean) {
        //设置交易密码状态
        this.transactionStatus = userBankBean.getTransaction_pass_status();
        if(this.transactionStatus==1){
            ((TextView)findViewById(R.id.reset_transaction_password)).setText(R.string.up_pay_pwd);
        }
        mTvTip.setText(WordUtil.getString(R.string.minimum_withdrawal, userBankBean.getMin(), userBankBean.getMax()));
        // 银行卡信息
        mBankCards = userBankBean.getData();
        if (mBankCards != null && !mBankCards.isEmpty()) {
            mLlAddBank.setVisibility(View.GONE);
            mRlBankInfo.setVisibility(View.VISIBLE);
            mCardBean = mBankCards.get(0);
            mCardAccountId = mCardBean.getId();
            mTvBankName.setText(mCardBean.getBank_name());
            mTvCardAccountNo.setText(mCardBean.getBank_number());

            resetTargetAmount();
            handleTargetAmount(mEtAmount.getText().toString());
            //印度用户
            if (isIndia()) {
                // 客服
//                mTvRight.setText(R.string.customer_service);
                mTvRight.setVisibility(View.GONE);
                ImageView iv = findViewById(R.id.iv_customer);
                iv.setImageResource(R.mipmap.icon_main_customer2);
                iv.setVisibility(View.VISIBLE);
                iv.setOnClickListener(this);
            } else {
                //新增银行卡
                mTvRight.setVisibility(View.VISIBLE);
                mTvRight.setText(R.string.add_bank);
            }
//            if ("INR".equals(mCardBean.getCurrency())) {
//                mTvExchangeRate.setVisibility(View.GONE);
//
//            } else {
            // mTvExchangeRate.setVisibility(View.VISIBLE);
            mTvExchangeRate.setText(mCardBean.getRate() + " " + mCardBean.getCurrency() + "/COIN");//WordUtil.getString(R.string.exchange_rate, mCardBean.getCurrencyName(), mCardBean.getRate()));

            //      }
        } else {
            //新增银行卡
            mTvRight.setVisibility(View.VISIBLE);
            mTvRight.setText(R.string.add_bank);
        }
        //需打的流水为
        UserBankBean.GiveCoin giveCoin = userBankBean.getGive_coin();
        if (null != giveCoin) {
            String temp = giveCoin.getGive_coin();
            mTvWageringRequirement.setText(TextUtils.isEmpty(temp) ? "0" : temp);
        }

        // 余额
        UserBankBean.Balance balance = userBankBean.getBalance();
        if (null == balance || TextUtils.isEmpty(balance.getAllcoin())) {
            mTvBalance.setText("0.00");
        } else {
            mTvBalance.setText(balance.getAllcoin());
        }

    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.tv_right || vId == R.id.iv_customer) {
            if (isIndia()) {
                // 客服
                toCustomer();
            } else {
                //新增银行卡
                toAddBankCard();
            }
        } else if (vId == R.id.ll_add_bank) {
            toAddBankCard();
        } else if (vId == R.id.btn_submit) {
            mBtnSubmit.setClickable(false);
            // 提交
            submit();
        } else if (vId == R.id.rl_bank_info) {
            // 选择银行卡
            mSelectBankCardDialog = new SelectBankCardDialog(mContext, mBankCards, new OnSelectBankCardListener() {
                @Override
                public void onSelectBankCard(BankCardBean cardBean) {
                    mCardBean = cardBean;
                    mCardAccountId = cardBean.getId();
                    mTvBankName.setText(cardBean.getBank_name());
                    mTvCardAccountNo.setText(cardBean.getBank_number());
                    handleTargetAmount(mEtAmount.getText().toString());
                    mTvExchangeRate.setText(mCardBean.getRate() + " " + mCardBean.getCurrency() + "/COIN");
                }
            });
            mSelectBankCardDialog.show();
        } else if (vId == R.id.reset_transaction_password) {
            if (this.transactionStatus!=1) {
                //startActivity(new Intent(mContext, SetTransactionPwdAct.class));
                startActivityForResult(new Intent(mContext, SetTransactionPwdAct.class), SetTransactionPwdAct.RETURN_CODE);
            } else {
                toCustomer();
            }
        } else if (vId == R.id.btn_anchor_withdraw) {
            // 主播工资提现
            new AnchorSalaryWithdrawDialog(mContext).show();
        }
    }

    /**
     * 客服
     */
    private void toCustomer() {
        Intent intent = new Intent(mContext, WebViewsActivity.class);
        intent.putExtra("title", WordUtil.getString(R.string.customer_service));
        intent.putExtra("url", CommonAppConfig.getInstance().getConfig().getKefuUrl());
        mContext.startActivity(intent);
    }


    /**
     * 添加银行卡
     */
    private void toAddBankCard() {
        //添加银行卡
        Intent intent = new Intent(mActivity, AddBankActivity.class);
        intent.putExtra("callback", true);
        startActivityForResult(intent, 100);
    }

    /**
     * 申请提现--提现
     */
    private void submit() {
        if (TextUtils.isEmpty(mCardAccountId)) {
            ToastUtil.show(R.string.Please_add_a_bank_card);
            mBtnSubmit.setClickable(true);
            return;
        }
        String amount = mEtAmount.getText().toString().trim();

        if (TextUtils.isEmpty(amount)) {
            ToastUtil.show(R.string.Please_enter_the_withdrawal_amount);
            mBtnSubmit.setClickable(true);
            return;
        }
        BigDecimal amountBigDecimal = new BigDecimal(amount);
//        if (amountBigDecimal.compareTo(new BigDecimal("500")) < 0) {
//            ToastUtil.show(R.string.minimum_withdrawal_amount);
//            mBtnSubmit.setClickable(true);
//            return;
//        }

        // 可提现余额
        String effBalanceStr = mTvBalance.getText().toString();
        if (effBalanceStr.contains(",")) {
            effBalanceStr = effBalanceStr.replace(",", "");
        }
        if (effBalanceStr.contains("COIN")) {
            effBalanceStr = effBalanceStr.replace("COIN", "");
        }
        BigDecimal effBalance = new BigDecimal(effBalanceStr.trim());
        if (effBalance.compareTo(BigDecimal.ZERO) == 0) {
            ToastUtil.show(R.string.no_cash_withdrawal);
            mBtnSubmit.setClickable(true);
            return;
        }
        // 提现金额不能大于可提现金额
        if (amountBigDecimal.compareTo(effBalance) > 0) {
            ToastUtil.show(R.string.amount_greater_remaining_amount);
            mBtnSubmit.setClickable(true);
            return;
        }
        // 提现密码
        String transactionPassword = mEtTransactionPassword.getText().toString().trim();
        if (TextUtils.isEmpty(transactionPassword)) {
            ToastUtil.show(R.string.Please_enter_transaction_password);
            mBtnSubmit.setClickable(true);
            return;
        }
        MainHttpUtil.setUserCash(balanceType, transactionPassword, mCardAccountId, amount, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    ToastUtil.show(msg);
                    if (code == 1010) {
                        startActivityForResult(new Intent(mContext, SetTransactionPwdAct.class), SetTransactionPwdAct.RETURN_CODE);
                    }
                    return;
                }
                finish();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mBtnSubmit.setClickable(true);
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (null != mSelectBankCardDialog && mSelectBankCardDialog.isShowing()) {
            mSelectBankCardDialog.dismiss();
            mSelectBankCardDialog = null;
        }

        MainHttpUtil.cancel(MainHttpConsts.GET_USER_ACCOUNT_LIST);
        MainHttpUtil.cancel(MainHttpConsts.USER_SET_CASH);
        super.onDestroy();
    }


    /**
     * 是否印度账户
     *
     * @return
     */
    private boolean isIndia() {
        return "IN".equals(CommonAppConfig.getInstance().getCountry());
    }

}