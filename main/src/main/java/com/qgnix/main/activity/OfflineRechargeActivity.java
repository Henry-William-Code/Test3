package com.qgnix.main.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.CopyUtils;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.main.bean.BankCardBean;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;

/**
 * @author sameal
 * @date 2020/7/17 21:17
 * 线下充值
 */
public class OfflineRechargeActivity extends AbsActivity {
    /**
     * 银行卡号
     */
    private TextView mTvBackCardNumber;
    /**
     * 收款人姓名
     */
    private TextView mTvPayeeName;

    /**
     * 开户银行
     */
    private TextView mTvBackAccount;

    /**
     * 汇款姓名
     */
    private EditText mEtRemittanceName;
    /**
     * 汇款留言
     */
    private EditText mEtRemittanceRemark;
    /**
     * 币种
     */
    private String mCurrency;
    /**
     * 金额
     */
    private String mAmount;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_offline_recharge;
    }

    @Override
    protected void main() {
        super.main();
        TextView tvTitle = findViewById(R.id.titleView);


        TextView tvAmount = findViewById(R.id.tv_amount);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        tvTitle.setText(title);


        /**
         * 获取银行卡ID
         */
        String id = intent.getStringExtra("id");
        mCurrency = intent.getStringExtra("currency");
        mAmount = intent.getStringExtra("amount");
        tvAmount.setText(WordUtil.getString(R.string.amount, mCurrency) + "：" + mAmount);

        mEtRemittanceName = findViewById(R.id.et_remittance_name);
        mEtRemittanceRemark = findViewById(R.id.et_remittance_remark);

        mTvBackCardNumber = findViewById(R.id.tv_back_card_number);
        mTvPayeeName = findViewById(R.id.tv_payee_name);
        mTvBackAccount = findViewById(R.id.tv_back_account);

        //充值客服
        findViewById(R.id.recharge_customer_service).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebViewsActivity.class);
                intent.putExtra("title", WordUtil.getString(R.string.customer_service));
                intent.putExtra("url", CommonAppConfig.getInstance().getConfig().getChargeKefuUrl());
                mContext.startActivity(intent);
            }
        });


        // 复制
        copy();
        // 提交
        commit();

        getAdminBankInfo(id);
    }

    /**
     * 获取银行信息
     */
    private void getAdminBankInfo(String id) {
        MainHttpUtil.getBankInfo(id, mCurrency, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    L.e(msg);
                    ToastUtil.show(msg);
                }
                if (info.length == 0) {
                    return;
                }
                BankCardBean cardBean = new Gson().fromJson(info[0], BankCardBean.class);
                if (cardBean == null) {
                    return;
                }
                mTvBackCardNumber.setText(WordUtil.getString(R.string.bank_name) + cardBean.getBank_name());
                mTvPayeeName.setText(WordUtil.getString(R.string.send_name) + cardBean.getName());
                mTvBackAccount.setText(WordUtil.getString(R.string.bank_card_account) + cardBean.getBank_number());

            }
        });
    }


    /**
     * 复制
     */
    private void copy() {
        findViewById(R.id.btn_card_number_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = mTvBackCardNumber.getText().toString().trim();
                if (TextUtils.isEmpty(value)) return;
                value = value.substring(value.indexOf(":") + 1).trim(); //一个半角
                value = value.substring(value.indexOf("：") + 1).trim(); //一个全角
                CopyUtils.copy(mContext, value);
            }
        });
        findViewById(R.id.btn_payee_name_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = mTvPayeeName.getText().toString().trim();
                if (TextUtils.isEmpty(value)) return;
                value = value.substring(value.indexOf(":") + 1).trim(); //一个半角
                value = value.substring(value.indexOf("：") + 1).trim(); //一个全角
                CopyUtils.copy(mContext, value);
            }
        });
        findViewById(R.id.btn_back_account_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = mTvBackAccount.getText().toString().trim();
                if (TextUtils.isEmpty(value)) return;
                value = value.substring(value.indexOf(":") + 1).trim(); //一个半角
                value = value.substring(value.indexOf("：") + 1).trim(); //一个全角
                CopyUtils.copy(mContext, value);

            }
        });
    }

    /**
     * 提交
     */
    private void commit() {
        final Button btnCommit = findViewById(R.id.btn_commit);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCommit.setClickable(false);
                String remittanceName = mEtRemittanceName.getText().toString().trim();
                if (TextUtils.isEmpty(remittanceName)) {
                    ToastUtil.show(R.string.please_enter_the_remittance_name);
                    btnCommit.setClickable(true);
                    return;
                }
                String remittanceRemark = mEtRemittanceRemark.getText().toString().trim();

                MainHttpUtil.chargeOffline(24, mAmount, remittanceName, remittanceRemark, mCurrency, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            ToastUtil.show(R.string.recharge_success);
                            finish();
                        } else {
                            L.e(msg);
                            ToastUtil.show(msg);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        btnCommit.setClickable(true);
                    }
                });

            }
        });
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.TOP_UP_ACT);
        super.onDestroy();
    }
}