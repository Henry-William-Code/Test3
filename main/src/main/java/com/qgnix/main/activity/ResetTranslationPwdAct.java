package com.qgnix.main.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.StringUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.main.bean.PhoneRuleBean;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 重置交易密码
 */
public class ResetTranslationPwdAct extends AbsActivity implements View.OnClickListener {

    /**
     * 登录手机号
     */
    private EditText mEtLoginMobileNumber;

    /**
     * 国家码
     */
    private TextView mCountryCode;

    /**
     * 新密码
     */
    private EditText mEtNewPassword;
    /**
     * 确认密码
     */
    private EditText mEtConfirmPassword;
    /**
     * 验证码
     */
    private EditText mEtCheckCode;
    /**
     * 获取验证码
     */
    private Button mBtnCheckCode;
    /**
     * 国家名称
     */
    private TextView mCountryName;

    private String mCountry = "en-IN"; //国家编码 如 MY  或者 en-MY
    /**
     * 手机号规格
     */
    // private String mPhoneRule = "/^(\\+?6?01){1}(([145]{1}(\\-|\\s)?\\d{7,8})|([236789]{1}(\\s|\\-)?\\d{7}))$/";
    private String mPhoneRule = "";


    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_translation_pwd;
    }

    @Override
    protected void main() {
        super.main();

        setTitle(WordUtil.getString(R.string.modify_pwd));

        mEtLoginMobileNumber = findViewById(R.id.et_login_mobile_number);
        mEtNewPassword = findViewById(R.id.et_new_password);
        mEtConfirmPassword = findViewById(R.id.et_confirm_password);
        mEtCheckCode = findViewById(R.id.et_check_code);

        mBtnCheckCode = findViewById(R.id.btn_check_code);

        mCountryName = findViewById(R.id.tv_country_name);
        mCountryCode = findViewById(R.id.tv_country_code);

        // 选择归属地
        findViewById(R.id.ll_select_country).setOnClickListener(this);
        //获取验证码
        mBtnCheckCode.setOnClickListener(this);
        // 提交
        findViewById(R.id.btn_commit).setOnClickListener(this);

        mEtLoginMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBtnCheckCode.setEnabled(s.length() > 0);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // 获取默认归属地
        MainHttpUtil.getPhoneRuleList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    return;
                }
                List<PhoneRuleBean> list = JSON.parseArray(Arrays.toString(info), PhoneRuleBean.class);
                PhoneRuleBean ruleBean = list.get(0);
                mPhoneRule = ruleBean.getRule();
                mCountryName.setText(ruleBean.getName());
                mCountryCode.setText(ruleBean.getE164());
                mCountry = ruleBean.getCode();
            }
        });
    }

    private CountDownTimer downTimer;

    /**
     * 获取验证码
     */
    private void getCheckCode() {
        String phoneNum = mEtLoginMobileNumber.getText().toString().trim();
        String countryCode = mCountryCode.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            mEtLoginMobileNumber.setError(WordUtil.getString(R.string.reg_input_phone));
            mEtLoginMobileNumber.requestFocus();
            return;
        }

        // 归属地+手机号
        final String tempPhone = countryCode + phoneNum;

        mPhoneRule = StringUtil.myTrim(mPhoneRule, '/');
        // 判断手机号
        if (!Pattern.matches(mPhoneRule, tempPhone)) {
            ToastUtil.show(R.string.login_phone_error);
            return;
        }

        MainHttpUtil.getFindPwdCode(tempPhone, CommonAppConfig.getInstance().getDeviceId(mContext),new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 1002 || code == 0) {
                    if (!TextUtils.isEmpty(msg) && msg.contains("123456")) {
                        ToastUtil.show(msg);
                    }

                    mBtnCheckCode.setEnabled(false);
                    if (downTimer != null) downTimer.cancel();
                    downTimer = new CountDownTimer(60 * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long temp = millisUntilFinished / 1000;
                            mBtnCheckCode.setText(WordUtil.getString(R.string.reg_get_code_again) + "(" + temp + "s)");
                        }

                        @Override
                        public void onFinish() {
                            mBtnCheckCode.setText(R.string.reg_get_code);
                            mBtnCheckCode.setEnabled(true);
                        }
                    };
                    downTimer.start();


                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    /**
     * 提交
     */
    private void commit() {
        String phoneNum = mEtLoginMobileNumber.getText().toString().trim();
        final String countryCode = mCountryCode.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.show(R.string.please_login_mobile_number);
            return;
        }
        // 归属地+手机号
        final String tempPhone = countryCode + phoneNum;
        mPhoneRule = StringUtil.myTrim(mPhoneRule, '/');
        // 判断手机号
        if (!Pattern.matches(mPhoneRule, tempPhone)) {
            ToastUtil.show(R.string.login_phone_error);
            return;
        }

        String newPassword = mEtNewPassword.getText().toString().trim();
        if (TextUtils.isEmpty(newPassword)) {
            ToastUtil.show(R.string.please_new_password);
            return;
        }

        String confirmPassword = mEtConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(confirmPassword)) {
            ToastUtil.show(R.string.please_confirm_password);
            return;
        }
        String checkCode = mEtCheckCode.getText().toString().trim();
        if (TextUtils.isEmpty(checkCode)) {
            ToastUtil.show(R.string.reg_input_code);
            return;
        }
        MainHttpUtil.resetTransactionPwd(tempPhone, newPassword, confirmPassword, checkCode, mCountry, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    ToastUtil.show(msg);
                    return;
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.RESET_TRANSACTION_PWD_ACT);
        MainHttpUtil.cancel(MainHttpConsts.GET_FIND_PWD_CODE);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if (vId == R.id.ll_select_country) {
            // 选择归属地
            //选择归属地
            startActivityForResult(new Intent(mContext, SelectCountryActivity.class), 200);
        } else if (vId == R.id.btn_check_code) {
            // 获取验证码
            getCheckCode();
        } else if (vId == R.id.btn_commit) {
            // 提交
            commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 200) {
            String countryName = data.getStringExtra("countryName");
            String countryCode = data.getStringExtra("countryCode");
            mPhoneRule = data.getStringExtra("phoneRule");
            mCountryName.setText(countryName);
            mCountryCode.setText(countryCode);
            mCountry = data.getStringExtra("country");
        }
    }
}