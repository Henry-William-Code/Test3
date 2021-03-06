package com.qgnix.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.appsflyer.AppsFlyerLib;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.http.ServiceName;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.CopyUtils;
import com.qgnix.common.utils.DialogUtil;
import com.qgnix.common.utils.LanSwitchUtil;
import com.qgnix.common.utils.StringUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.LiveConfig;
import com.qgnix.main.R;
import com.qgnix.main.bean.PhoneRuleBean;
import com.qgnix.main.dialog.RegisterInfoDialog;
import com.qgnix.main.event.RegSuccessEvent;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.qgnix.main.utils.ReportEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by cxf on 2018/9/25.
 */

public class RegisterActivity extends AbsActivity implements View.OnClickListener {

    private EditText mEditPhone;
    private EditText mEditCode;
    private EditText mEditPwd1;
    private EditText mEditPwd2;
    private EditText mEditInviteCode;
    /**
     * ????????????
     */
    private TextView mCountryName;
    /**
     * ????????????
     */
    private String mCountryCodeStr = "en-IN";

    /**
     * ?????????
     */
    private TextView mCountryCode;
    /**
     * ???????????????
     */
    private LinearLayout mLlBankInfo;
    /**
     * ????????????
     */
    private EditText mEtRealName;
    /**
     * ?????????????????????
     */
    private EditText mEtBankNum;
    /**
     * IFSC code
     */
    private EditText mEtBankCode;
    /**
     * ???????????????
     */
    // private String mPhoneRule = "/^(\\+?6?01){1}(([145]{1}(\\-|\\s)?\\d{7,8})|([236789]{1}(\\s|\\-)?\\d{7}))$/";
    private String mPhoneRule;


    private TextView mBtnCode;
    private View mBtnRegister;
    private Handler mHandler;
    private static final int TOTAL = 60;
    private int mCount = TOTAL;
    private String mGetCode;
    private String mGetCodeAgain;
    private Dialog mDialog;

    /**
     * ???????????????
     */
    private ImageView mIvCheckCode;

    /**
     * ????????????
     */
    private static String mFrom = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.reg_register));
        mEditPhone = findViewById(R.id.edit_phone);
        mEditCode = findViewById(R.id.edit_code);
        mEditPwd1 = findViewById(R.id.edit_pwd_1);
        mEditPwd2 = findViewById(R.id.edit_pwd_2);
        mBtnCode = findViewById(R.id.btn_code);
        mEditInviteCode = findViewById(R.id.edit_invite_code);

        mCountryName = findViewById(R.id.tv_country_name);
        mCountryCode = findViewById(R.id.tv_country_code);

        mBtnRegister = findViewById(R.id.btn_register);
        mIvCheckCode = findViewById(R.id.iv_check_code);

        mLlBankInfo = findViewById(R.id.ll_bank_info);
        mEtRealName = findViewById(R.id.et_real_name);
        mEtBankNum = findViewById(R.id.et_bank_num);
        mEtBankCode = findViewById(R.id.et_bank_code);

        // ??????
        findViewById(R.id.ibtn_customer).setOnClickListener(this);
        //????????????
        findViewById(R.id.ibtn_change_lan).setOnClickListener(this);
        // ???????????????
        findViewById(R.id.btn_code).setOnClickListener(this);
        // ??????
        findViewById(R.id.btn_register).setOnClickListener(this);

        // ???????????????
        findViewById(R.id.ll_select_country).setOnClickListener(this);


        boolean codeSwitch = CommonAppConfig.getInstance().sendCodeSwitch();
        mBtnCode.setVisibility(codeSwitch ? View.VISIBLE : View.GONE);
        mIvCheckCode.setVisibility(codeSwitch ? View.GONE : View.VISIBLE);



        if (!codeSwitch) {
            final String codeUrl = ServiceName.CHECK_CODE + CommonAppConfig.getInstance().getDeviceId(mContext);
            Glide.with(mContext).load(codeUrl).signature(new ObjectKey(UUID.randomUUID())).into(mIvCheckCode);
            mIvCheckCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Glide.with(mContext).load(codeUrl).signature(new ObjectKey(UUID.randomUUID())).into(mIvCheckCode);
                }
            });
        }else{
            mEditCode.setHint(getString(R.string.reg_input_code));
        }

        mGetCode = WordUtil.getString(R.string.reg_get_code);
        mGetCodeAgain = WordUtil.getString(R.string.reg_get_code_again);

        mEditPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //???????????????????????????????????????????????????
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBtnCode.setEnabled(s.length() > 0);
                changeEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mEditCode.addTextChangedListener(textWatcher);
        mEditPwd1.addTextChangedListener(textWatcher);
        mEditPwd2.addTextChangedListener(textWatcher);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mCount--;
                if (mCount > 0) {
                    mBtnCode.setText(mGetCodeAgain + "(" + mCount + "s)");
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                } else {
                    mBtnCode.setText(mGetCode);
                    mCount = TOTAL;
                    if (mBtnCode != null) {
                        mBtnCode.setEnabled(true);
                    }
                }
            }
        };
        mDialog = DialogUtil.loadingDialog(mContext, getString(R.string.reg_register_ing));
        EventBus.getDefault().register(this);

        // ?????????????????????
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
                mCountryCodeStr = ruleBean.getCode();
                mLlBankInfo.setVisibility(isBankInfo() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            this.getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    // ???????????????
                    String inviteCode = getInviteCode();
                    if (!TextUtils.isEmpty(inviteCode)) {
                        mEditInviteCode.setText(inviteCode);
                        // ??????????????????
                        setEnableEdiText(false);
                    }
                }
            });
        } else {
            // ???????????????
            String inviteCode = getInviteCode();
            if (!TextUtils.isEmpty(inviteCode)) {
                mEditInviteCode.setText(inviteCode);
                // ??????????????????
                setEnableEdiText(false);
            }
        }
    }

    /**
     * ??????edittext???????????????
     */
    private void setEnableEdiText(boolean enable) {
        mEditInviteCode.setFocusable(enable);
        mEditInviteCode.setFocusableInTouchMode(enable);
    }

    /**
     * ?????????????????????????????????????????????
     * app???????????????????????????????????????
     * <p>
     * ??????: ????????????????????????????????????????????????????????????????????????????????????????????????  574749.xyz ????????????EPH55
     * ?????????????????????    cherry200.com?i=EPH55,baidu
     *
     * @return
     */
    private String getInviteCode() {
        // ??????????????????
        String clipboardInviteCode = CopyUtils.getText(mContext);
        if (null != clipboardInviteCode) {
            int index = clipboardInviteCode.lastIndexOf("i="); //todo ??? i= ????????? = ?????? ????????????????????????????????????????????????
            if (index != -1) {
                CharSequence sequence = clipboardInviteCode.subSequence(index + 2, clipboardInviteCode.length());
                String temp = sequence.toString();
                if (temp.contains(",") || temp.contains("-")) {
                    String[] arr = null;
                    if (temp.contains(","))
                        arr = temp.split(",");
                    else
                        arr = temp.split("-");
                    if (StringUtil.isNotBlank(arr[1])) //todo ???????????????????????? ???????????? i=W3A9W7,baidu ????????? ??????????????????????????? ??????,  ????????????????????????????????????????????????????????????????????????  ????????? ???????????????????????? ????????? ???????????????baidu ??????
                        mFrom = arr[1];
                    return arr[0];
                } else {
                    return temp;
                }
            } else {
                /*if(clipboardInviteCode.length()==6){  //todo ?????????????????????????????? ???????????????????????????????????????????????????????????? : W3A9W7  ??????????????? ?????????????????????????????????
                    return clipboardInviteCode;
                }*/
            }
        }
        return "";
    }

    private void changeEnable() {
        String phone = mEditPhone.getText().toString();
        String code = mEditCode.getText().toString();
        String pwd1 = mEditPwd1.getText().toString();
        String pwd2 = mEditPwd2.getText().toString();
        mBtnRegister.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(code) && !TextUtils.isEmpty(pwd1) && !TextUtils.isEmpty(pwd2));
    }

    /**
     * ???????????????
     */
    private void getCode() {
        String phoneNum = mEditPhone.getText().toString().trim();
        String countryCode = mCountryCode.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.reg_input_phone));
            mEditPhone.requestFocus();
            return;
        }
        // ?????????+?????????
        final String tempPhone = countryCode + phoneNum;

        mPhoneRule = StringUtil.myTrim(mPhoneRule, '/');
        // ???????????????
        if (!Pattern.matches(mPhoneRule, tempPhone)) {
            ToastUtil.show(R.string.login_phone_error);
            return;
        }
        mEditCode.requestFocus();
        MainHttpUtil.getRegisterCode(tempPhone, CommonAppConfig.getInstance().getDeviceId(mContext),mGetCodeCallback);
    }

    private final HttpCallback mGetCodeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                mBtnCode.setEnabled(false);
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(0);
                }
                if (!TextUtils.isEmpty(msg) && msg.contains("123456")) {
                    ToastUtil.show(msg);
                }
            } else {
                ToastUtil.show(msg);
            }
        }

    };

    /**
     * ???????????????
     */
    private void register() {
        final String phoneNum = mEditPhone.getText().toString().trim();
        final String countryCode = mCountryCode.getText().toString().trim();
        // ?????????+?????????
        final String tempPhone = countryCode + phoneNum;
        if (TextUtils.isEmpty(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.reg_input_phone));
            mEditPhone.requestFocus();
            return;
        }
        mPhoneRule = StringUtil.myTrim(mPhoneRule, '/');
        // ???????????????
        if (!Pattern.matches(mPhoneRule, tempPhone)) {
            ToastUtil.show(R.string.login_phone_error);
            return;
        }

        String checkCode = mEditCode.getText().toString().trim();
        if (TextUtils.isEmpty(checkCode)) {
            mEditCode.setError(WordUtil.getString(R.string.reg_input_code));
            mEditCode.requestFocus();
            return;
        }
        final String pwd = mEditPwd1.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            mEditPwd1.setError(WordUtil.getString(R.string.reg_input_pwd_1));
            mEditPwd1.requestFocus();
            return;
        }
        String pwd2 = mEditPwd2.getText().toString().trim();
        if (TextUtils.isEmpty(pwd2)) {
            mEditPwd2.setError(WordUtil.getString(R.string.reg_input_pwd_2));
            mEditPwd2.requestFocus();
            return;
        }
        if (!pwd.equals(pwd2)) {
            mEditPwd2.setError(WordUtil.getString(R.string.reg_pwd_error));
            mEditPwd2.requestFocus();
            return;
        }
        String realName = "";
        String bankNumber = "";
        String bankCode = "";
        if (isBankInfo()) {
            /*realName = mEtRealName.getText().toString().trim();
            if (TextUtils.isEmpty(realName)) {
                ToastUtil.show(R.string.reg_input_real_name);
                return;
            }*/
            //bankNumber = mEtBankNum.getText().toString().trim();
            /*if (TextUtils.isEmpty(bankNumber)) {
                ToastUtil.show(R.string.reg_input_bank_num);
                return;
            }*/
            //bankCode = mEtBankCode.getText().toString().trim();
            /*if (TextUtils.isEmpty(bankCode)) {
                ToastUtil.show(R.string.reg_input_bank_code);
                return;
            }*/
        }

        String invitationCode = mEditInviteCode.getText().toString().trim();
        if (mDialog != null) {
            mDialog.show();
        }

        Map customDataMap = new HashMap<String, String>();
        customDataMap.put("?????????key","?????????value");
        AppsFlyerLib.getInstance().trackEvent(RegisterActivity.this, "??????????????????" , customDataMap);//??????????????????????????????????????????????????????

        Map<String, String> params = new HashMap<>();
        params.put("user_login", tempPhone);
        params.put("user_pass", pwd);
        params.put("user_pass2", pwd2);
        params.put("code", checkCode);
        params.put("inviteCode", invitationCode);
        params.put("appInviteCode", CommonAppConfig.LOCAL_INVITATION_CODE);
        params.put("source", MainHttpUtil.DEVICE);
        params.put("deviceinfo", LiveConfig.getSystemParams());
        params.put("device_id", CommonAppConfig.getInstance().getDeviceId(mContext));
        params.put("from", mFrom);
        params.put("country", mCountryCodeStr);
        params.put("source", "android2");
       // params.put("real_name", realName);
       // params.put("bank_number", bankNumber);
        //params.put("bank_code", bankCode);
        Map<String,Object> reportMap = new HashMap<>();
        reportMap.put("value",true);
        ReportEvent.report(this,"registration",reportMap);
        MainHttpUtil.register(params, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 || code == 1100) {//????????????????????? ????????????
                    if (code == 1100) {
                        ToastUtil.show(msg);
                    }
                    // ????????????
                    login(tempPhone, pwd);
                    //????????????
                    CommonHttpUtil.saveUserLog("????????????", 1, CommonAppConfig.getInstance().getDeviceId(mContext), "");
                } else {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onError() {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                //????????????
                CommonHttpUtil.saveUserLog("????????????", 1, CommonAppConfig.getInstance().getDeviceId(mContext), "");
            }
        });
    }

    private void login(String phoneNum, String pwd) {
        MainHttpUtil.login(phoneNum, pwd, CommonAppConfig.getInstance().getDeviceId(mContext), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String uid = obj.getString("id");
                    String token = obj.getString("token");
                    //??????????????????
                    LanSwitchUtil.setLanToLocale(obj.getIntValue("lan"));
                    //??????????????????
                    CommonAppConfig.getInstance().setLoginInfo(uid, token, true);
                    //???????????????
                    CommonAppConfig.getInstance().setCountry(obj.getString("country"));

                    //?????????????????????????????????????????????
                    CommonAppConfig.getInstance().inviteCodePopupCode = obj.getInteger("invite_state");
                    CommonAppConfig.getInstance().setBlankStatus(obj.getInteger("tie_card_state"));
                    getBaseUserInfo();
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onError() {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            }
        });
    }

    /**
     * ??????????????????
     */
    private void getBaseUserInfo() {
//        CommonHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
//            @Override
//            public void callback(UserBean bean) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
//                if (bean != null) {
        RegisterInfoDialog registerInfoDialog = new RegisterInfoDialog(this);
                registerInfoDialog.setOnDialogClickListener(new RegisterInfoDialog.IOnDialogClickListener() {
                    @Override
                    public void onOkListener() {
                        MainActivity.forwardFromRegister(mContext);
                        EventBus.getDefault().post(new RegSuccessEvent());
                    }

                    @Override
                    public void onCancleListener() {
                        MainActivity.forward(mContext);
                        EventBus.getDefault().post(new RegSuccessEvent());
                    }
                });
        registerInfoDialog.show();
//                }
//            }
//        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegSuccessEvent(RegSuccessEvent e) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        MainHttpUtil.cancel(MainHttpConsts.GET_REGISTER_CODE);
        MainHttpUtil.cancel(MainHttpConsts.REGISTER);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN);
        MainHttpUtil.cancel(CommonHttpConsts.GET_BASE_INFO);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.btn_code) {
            // ???????????????
            getCode();
        } else if (vId == R.id.btn_register) {
            // ??????
            register();
        } else if (vId == R.id.ibtn_customer) {
            Intent intent = new Intent(mContext, WebViewsActivity.class);
            intent.putExtra("title", WordUtil.getString(R.string.customer_service));
            intent.putExtra("url", CommonAppConfig.getInstance().getConfig().getKefuUrl());
            mContext.startActivity(intent);
        } else if (vId == R.id.ibtn_change_lan) {
            LanSwitchActivity.toForward(mContext, RegisterActivity.class, true);
        } else if (vId == R.id.ll_select_country) {
            //???????????????
            startActivityForResult(new Intent(mContext, SelectCountryActivity.class), 200);
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
            mCountryCodeStr = data.getStringExtra("country");
            mLlBankInfo.setVisibility(isBankInfo() ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    private boolean isBankInfo() {
        return "en-IN".equals(mCountryCodeStr);
    }
}
