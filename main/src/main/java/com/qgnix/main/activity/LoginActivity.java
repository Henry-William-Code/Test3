package com.qgnix.main.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.CommonAppContext;
import com.qgnix.common.HtmlConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.LanSwitchUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.main.event.RegSuccessEvent;
import com.qgnix.main.glide.util.Utils;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;


/**
 * Created by cxf on 2018/9/17.
 * 登录
 */

public class LoginActivity extends AbsActivity implements View.OnClickListener {

    private EditText mEditPhone;
    private EditText mEditPwd;
    private TextView mBtnLogin;
    /**
     * 国家名称
     */
    private TextView mCountryName;
    /**
     * 国家码
     */
    private TextView mCountryCode;

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN_BY_THIRD);
        MainHttpUtil.cancel(CommonHttpConsts.GET_BASE_INFO);
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void main() {

        mEditPhone = (EditText) findViewById(R.id.edit_phone);
        mEditPwd = (EditText) findViewById(R.id.edit_pwd);
        mBtnLogin = (TextView) findViewById(R.id.btn_login);
        ImageView show_eightheen = (ImageView) findViewById(R.id.show_eightheen);

        mCountryName = findViewById(R.id.tv_country_name);
        mCountryCode = findViewById(R.id.tv_country_code);
        // 选择归属地
        findViewById(R.id.ll_select_country).setOnClickListener(this);
        // 客服
        findViewById(R.id.ibtn_customer).setOnClickListener(this);
        //切换语言
        findViewById(R.id.ibtn_change_lan).setOnClickListener(this);
        // 注册
        findViewById(R.id.btn_register).setOnClickListener(this);
        // 登录
        findViewById(R.id.btn_login).setOnClickListener(this);
        // 忘记密码
        findViewById(R.id.btn_forget_pwd).setOnClickListener(this);
        // 服务条款
        findViewById(R.id.btn_tip).setOnClickListener(this);
        // 关闭
        findViewById(R.id.img_close).setOnClickListener(this);
        if(CommonAppConfig.getInstance().getSwitchStatu()){
            show_eightheen.setVisibility(View.GONE);
        }

        mBtnLogin.setEnabled(true);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEditPhone.getText().toString();
                String pwd = mEditPwd.getText().toString();
                mBtnLogin.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd));
                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
                    mBtnLogin.setTextColor(getResources().getColor(R.color.white));
                } else {
                    mBtnLogin.setTextColor(Color.parseColor("#ABABAB"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mEditPhone.addTextChangedListener(textWatcher);
        mEditPwd.addTextChangedListener(textWatcher);

        EventBus.getDefault().register(this);

    }

    public static void forward() {
        Intent intent = new Intent(CommonAppContext.sInstance, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        CommonAppContext.sInstance.startActivity(intent);
    }

    //手机号密码登录
    private void login() {
        //todo 登录的时候清楚各种缓存
        CommonAppConfig.getInstance().clearUserInfo();//启动时清除getBaseInfo缓存

        //需要清除用户信息，否则会导致切换用户带来的不必要的问题
        CommonAppConfig.getInstance().clearUserInfo();
        String phoneNum = mEditPhone.getText().toString().trim();
        String countryCode = mCountryCode.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            mEditPhone.setError(WordUtil.getString(R.string.login_input_phone));
            mEditPhone.requestFocus();
            return;
        }
        String pwd = mEditPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            mEditPwd.setError(WordUtil.getString(R.string.login_input_pwd));
            mEditPwd.requestFocus();
            return;
        }
        MainHttpUtil.login(countryCode + phoneNum, pwd, CommonAppConfig.getInstance().getDeviceId(mContext), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                //上报日志
                CommonHttpUtil.saveUserLog("登录失败", 2, CommonAppConfig.getInstance().getDeviceId(mContext), "");
                onLoginSuccess(code, msg, info);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mBtnLogin.setEnabled(true);
            }

            @Override
            public void onError() {
                super.onError();
                //上报日志
                CommonHttpUtil.saveUserLog("登录失败", 2, CommonAppConfig.getInstance().getDeviceId(mContext), "");
            }
        });
    }

    //登录成功！
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            JSONObject obj = JSON.parseObject(info[0]);
            String uid = obj.getString("id");
            String token = obj.getString("token");
            //保存语言信息
            LanSwitchUtil.setLanToLocale(obj.getIntValue("lan"));
            // 保存用户信息
            CommonAppConfig.getInstance().setLoginInfo(uid, token, true);
            getBaseUserInfo();
            //设置国家码
            CommonAppConfig.getInstance().setCountry(obj.getString("country"));
            Log.e("LoginActivity","33333333:"+obj.getString("country"));
            //登录成功设置邀请状态和绑卡状态
            CommonAppConfig.getInstance().inviteCodePopupCode = obj.getInteger("invite_state");
            CommonAppConfig.getInstance().setBlankStatus(obj.getInteger("tie_card_state"));
        } else {
            ToastUtil.show(msg);
        }
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
//        CommonHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
//            @Override
//            public void callback(UserBean bean) {
                MainActivity.forward(mContext);
                finish();
//            }
//        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegSuccessEvent(RegSuccessEvent e) {
        finish();
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.btn_login) {
            //登录
            mBtnLogin.setEnabled(false);
            login();
        } else if (vId == R.id.btn_register) {
            //注册
            startActivity(new Intent(mContext, RegisterActivity.class));
        } else if (vId == R.id.btn_forget_pwd) {


            if (CommonAppConfig.getInstance().sendCodeSwitch()) {
                // 开启短信验证
                //忘记密码
                startActivity(new Intent(mContext, FindPwdActivity.class));
            } else {
                toCustomer();
            }

        } else if (vId == R.id.btn_tip) {
            // 服务和隐私
            WebViewsActivity.forward(mContext, HtmlConfig.LOGIN_PRIVCAY);
        } else if (vId == R.id.img_close) {
            //关闭
            finish();
        } else if (vId == R.id.ibtn_customer) {
            toCustomer();
        } else if (vId == R.id.ibtn_change_lan) {
            LanSwitchActivity.toForward(mContext, LoginActivity.class, true);
        } else if (vId == R.id.ll_select_country) {
            //选择归属地
            startActivityForResult(new Intent(mContext, SelectCountryActivity.class), 200);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 200) {
            String countryName = data.getStringExtra("countryName");
            String countryCode = data.getStringExtra("countryCode");
            mCountryName.setText(countryName);
            mCountryCode.setText(countryCode);
        }
    }





    //=======================================code========================//













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




    // 传入进来的形参 Activity 释放被销毁了，如果销毁了，就抛出异常You cannot start a load for a destroyed activity
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void assertNotDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }

    //===============================================================//
}
