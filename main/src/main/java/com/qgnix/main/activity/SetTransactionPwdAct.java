package com.qgnix.main.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;

/**
 * 设置交易密码
 */
public class SetTransactionPwdAct extends AbsActivity {

    public static int RETURN_CODE = 11;
    //设置密码状态 0 失败 1成功
    private int setPwResult = 0;
    /**
     * 原密码
     */
    private EditText mEtOriginalPassword;

    /**
     * 新密码
     */
    private EditText mEtNewPassword;
    /**
     * 确认密码
     */
    private EditText mEtConfirmPassword;

    @Override
    protected int getLayoutId() {
        return R.layout.act_set_transaction_pwd;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.up_pay_pwd));

        mEtOriginalPassword = findViewById(R.id.et_original_password);
        mEtNewPassword = findViewById(R.id.et_new_password);
        mEtConfirmPassword = findViewById(R.id.et_confirm_password);

        findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }

        });
    }

    /**
     * 提交
     */
    private void commit() {
        String originalPassword = mEtOriginalPassword.getText().toString().trim();
        if (TextUtils.isEmpty(originalPassword)) {
            ToastUtil.show(R.string.please_original_password);
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
        MainHttpUtil.setTransactionPwd(originalPassword, newPassword, confirmPassword, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code!=0){
                    ToastUtil.show(msg);
                    setPwResult = 0; //设置密码失败
                    return;
                }
                setPwResult = 1; //设置密码成功
                CommonAppConfig.getInstance().clearUserInfo();
                Intent intent = new Intent();
                intent.putExtra("setPwResult", setPwResult);
                setResult(RETURN_CODE, intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.SET_TRANSACTION_PWD_ACT);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("setPwResult", setPwResult);
        this.setResult(RETURN_CODE, intent);
        this.finish();
    }
}