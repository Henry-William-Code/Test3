package com.qgnix.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.HtmlConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.http.JsonBean;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.main.bean.WalletBean;
import com.qgnix.main.http.MainHttpUtil;

import java.math.BigDecimal;

public class WalletTransferActivity extends AbsActivity implements View.OnClickListener {
    public static String WALLET_INFO = "wallet_info";
    public static int WALLET_CODE = 10;
    private EditText etAmount;
    private TextView tvTotal;
    private Context mContext;
    private EditText etWithDrawAmount, etPayPsw;
    private boolean transferIn;
    private LinearLayout llTransOut,llTransIn;
    private TextView tvSubAmount;
    private TextView tvSubTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.wallet_balance;
    }

    @Override
    protected void main(Bundle savedInstanceState) {
        super.main();
        mContext = this;
        ImageView imageRight = (ImageView) findViewById(R.id.iv_customer);
        imageRight.setVisibility(View.VISIBLE);
        imageRight.setOnClickListener(this);
        Button btnOk = (Button) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(this);
        transferIn = getIntent().getBooleanExtra("transferIn", false);
        llTransOut = (LinearLayout) findViewById(R.id.ll_trans_out);
        llTransIn = (LinearLayout) findViewById(R.id.ll_trans_in);
        //钱包余额和chreey余额
        tvSubAmount = (TextView) findViewById(R.id.tv_sub_amount);
        tvSubTitle = (TextView) findViewById(R.id.tv_sub_title);
        WalletBean walletBean = (WalletBean) getIntent().getSerializableExtra(WALLET_INFO);
        if (null != walletBean) {
            ImageView mAvatar = (ImageView) findViewById(R.id.avatar);
            ImgLoader.displayAvatar(mContext, walletBean.getAvatar(), mAvatar);
            TextView tvNickName = (TextView) findViewById(R.id.tv_nickname);
            tvNickName.setText(walletBean.getUserNicename());
            TextView tvLevel = (TextView) findViewById(R.id.tv_level);
            tvLevel.setText(walletBean.getLevelName());
            TextView tvAccount = (TextView) findViewById(R.id.tv_account);
            tvAccount.setText(WordUtil.getString(R.string.account_no, walletBean.getId()));
            tvTotal = (TextView) findViewById(R.id.tv_total);

            etAmount = (EditText) findViewById(R.id.et_amount);
        }
        //判断状态 切换标题
        if (transferIn) {
            llTransIn.setVisibility(View.VISIBLE);
            llTransOut.setVisibility(View.GONE);
//            tvSubTitle.setText("Wallet balance");
            tvSubAmount.setText(new BigDecimal(walletBean.getWalletBalance()+"").stripTrailingZeros().toPlainString());
            tvTotal.setText(new BigDecimal(walletBean.getCoin()+"").stripTrailingZeros().toPlainString());
            setTitle(getResources().getString(R.string.Wallet_In));
        } else {
            llTransIn.setVisibility(View.GONE);
            llTransOut.setVisibility(View.VISIBLE);
//            tvSubTitle.setText("Wallet balance");
            tvSubAmount.setText(new BigDecimal(walletBean.getWalletBalance()+"").stripTrailingZeros().toPlainString());
            tvTotal.setText(new BigDecimal(walletBean.getCoin()+"").stripTrailingZeros().toPlainString());
            setTitle(getResources().getString(R.string.Wallet_Out));
            etWithDrawAmount = (EditText) findViewById(R.id.et_withdrawal_amount);
            etPayPsw = (EditText) findViewById(R.id.et_pay_psw);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_customer) {
            //帮助
            Intent intent = new Intent(mContext, WebViewsActivity.class);
            intent.putExtra("title", WordUtil.getString(R.string.customer_service));
            intent.putExtra("url", CommonAppConfig.getInstance().getConfig().getKefuUrl());
            mContext.startActivity(intent);
        } else if (v.getId() == R.id.btn_ok) {
            if (transferIn) {
                if (TextUtils.isEmpty(etAmount.getText().toString().trim())) {
                    ToastUtil.show("Please enter amount!");
                }else if(Double.parseDouble((etAmount.getText().toString()+"").trim())>Double.parseDouble((tvTotal.getText().toString()+"").trim())){
                    ToastUtil.show("transfer amount should not more than totol fund!");
                }else {
                    transferInReq(etAmount.getText().toString().trim());
                }
            } else {
                if (TextUtils.isEmpty(etPayPsw.getText().toString().trim()) || TextUtils.isEmpty(etWithDrawAmount.getText().toString().trim())) {
                    ToastUtil.show("Please enter amount and password!");
                }else if(Double.parseDouble((etWithDrawAmount.getText().toString()+"").trim())>Double.parseDouble((tvSubAmount.getText().toString()+"").trim())){
                    ToastUtil.show("transfer amount should not more than wallet balance!");
                }else {
                    String withdrawAmount = etWithDrawAmount.getText().toString().trim();
                    String payPsw  = etPayPsw.getText().toString().trim();
                    transferOutReq(withdrawAmount,payPsw);
                }
            }
        }
    }


    /**
     * 侨胞转入
     * @param amount
     */
    private void transferInReq(String amount) {
        MainHttpUtil.walletTransferIn(amount, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (null != info) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    tvSubAmount.setText(obj.get("walletBalance").toString());
                    tvTotal.setText(obj.get("coin").toString());
                    etAmount.setText("");
                    ToastUtil.show("Transfer success!");
                } else {
                    ToastUtil.show(msg);
                }

            };
            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
            }
        });
    }

    /**
     * 钱包转出
     * @param amount
     * @param payPsw
     */
    private void transferOutReq(String amount, String payPsw) {
        MainHttpUtil.walletTransferOut(amount,payPsw, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (null != info) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    tvSubAmount.setText(obj.get("walletBalance").toString());
                    tvTotal.setText(obj.get("coin").toString());
                    etWithDrawAmount.setText("");
                    etPayPsw.setText("");
                    ToastUtil.show("Transfer success!");
                } else {
                    ToastUtil.show(msg);
                }

            };
            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("walletBalance", tvSubAmount.getText().toString());
        intent.putExtra("cherryBalance", tvTotal.getText().toString());
        this.setResult(WALLET_CODE, intent);
        this.finish();
    }
}
