package com.qgnix.main.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.main.R;
import com.qgnix.main.bean.BankNameBean;
import com.qgnix.main.bean.CurrencyBean;
import com.qgnix.main.dialog.SelectBankDialog;
import com.qgnix.main.dialog.SelectCurrencyDialog;
import com.qgnix.main.glide.util.Utils;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.qgnix.main.interfaces.OnSelectBankListener;
import com.qgnix.main.interfaces.OnSelectCurrencyListener;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

/**
 * @author sameal
 * @date 2020/7/19 12:14
 * 添加银行卡
 */
public class RegistBankActivity extends AbsActivity {
    /**
     * 印度IFSC
     */
    private LinearLayout mLlIfscCode;
    /**
     * 选择银行
     */
    private LinearLayout mLlBank;
    /**
     * 货币种类
     */
    private TextView mTvCurrency;
    /**
     * 汇率
     */
    private TextView mTvExchangeRate;
    /**
     * 银行
     */
    private TextView mTvBank;
    /**
     * 持卡人姓名
     */
    private EditText mEtCardholdersName;
    /**
     * 银行卡号
     */
    private EditText mEtBankCardNo;

    /**
     * 印度银行IFSC
     */
    private EditText mEtIfscCode;

    /**
     * 是否需要回调
     */
    private boolean mCallback;
    /**
     * 银行code
     */
    private String mBankCode;
    /**
     * 选择的货币种类
     */
    private String mCurrency;
    private BaseCustomDialog mSelectBankDialog;

    public static void newIntent(Context context) {
        Intent intent = new Intent(context, RegistBankActivity.class);
        context.startActivity(intent);
    }



    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_bank;
    }

    @Override
    protected void main() {
        super.main();
        TextView tvTitle = findViewById(R.id.titleView);
        tvTitle.setText(WordUtil.getString(R.string.add));

        mTvCurrency = findViewById(R.id.tv_currency);
        mTvExchangeRate = findViewById(R.id.tv_exchange_rate);

        mTvBank = findViewById(R.id.tv_bank);
        mEtCardholdersName = findViewById(R.id.et_cardholders_name);
        mEtBankCardNo = findViewById(R.id.et_bank_card_no);
        mEtIfscCode = findViewById(R.id.et_ifsc_code);

        mLlIfscCode = findViewById(R.id.ll_ifsc_code);
        mLlBank = findViewById(R.id.ll_bank);

        // 选择货币类型
        findViewById(R.id.ll_currency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectBankDialog = new SelectCurrencyDialog(mContext, new OnSelectCurrencyListener() {
                    @Override
                    public void onSelectCurrency(CurrencyBean bean) {
                        mCurrency = bean.getKey();
                        mTvCurrency.setText(bean.getValue());
                        if ("INR".equals(mCurrency)) {
                         //   mTvExchangeRate.setVisibility(View.GONE);
                            mLlBank.setVisibility(View.GONE);
                            mLlIfscCode.setVisibility(View.VISIBLE);
                         //   mTvExchangeRate.setText(WordUtil.getString(R.string.exchange_rate, bean.getValue(), bean.getRate()));
                            mTvExchangeRate.setText(bean.getRate()+ " COIN/"+bean.getKey()+"");
                        } else {
                            mLlBank.setVisibility(View.VISIBLE);
                            mLlIfscCode.setVisibility(View.GONE);
                          //  mTvExchangeRate.setVisibility(View.VISIBLE);
                         //   mTvExchangeRate.setText(WordUtil.getString(R.string.exchange_rate, bean.getValue(), bean.getRate()));
                            mTvExchangeRate.setText(bean.getRate()+ " COIN/"+bean.getKey()+"");
                        }
                    }
                });
                mSelectBankDialog.show();
            }
        });

        // 选择银行
        mLlBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mCurrency)) {
                    ToastUtil.show(R.string.please_select_currency);
                    return;
                }
                mSelectBankDialog = new SelectBankDialog(mContext, mCurrency, new OnSelectBankListener() {
                    @Override
                    public void onSelectBank(BankNameBean bean) {
                        mBankCode = bean.getKey();
                        mTvBank.setText(bean.getValue());
                    }
                });
                mSelectBankDialog.show();
            }
        });
        findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        findViewById(R.id.btn_skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        mCallback = getIntent().getBooleanExtra("callback", false);
    }

    private void submit() {
        if (TextUtils.isEmpty(mCurrency)) {
            ToastUtil.show(R.string.please_select_currency);
            return;
        }

        if ("INR".equals(mCurrency)) {
            // 印度
            mBankCode = mEtIfscCode.getText().toString().trim();
            if (TextUtils.isEmpty(mBankCode)) {
                ToastUtil.show(R.string.reg_input_bank_code);
                return;
            }
        } else {
            if (TextUtils.isEmpty(mBankCode)) {
                ToastUtil.show(R.string.please_select_bank);
                return;
            }
        }

        String etCardholdersName = mEtCardholdersName.getText().toString().trim();
        if (TextUtils.isEmpty(etCardholdersName)) {
            ToastUtil.show(R.string.please_enter_the_cardholders_name);
            return;
        }
        String etBankCardNo = mEtBankCardNo.getText().toString().trim();
        if (TextUtils.isEmpty(etBankCardNo)) {
            ToastUtil.show(R.string.please_enter_bank_account_no);
            return;
        }

        MainHttpUtil.addUserBank(mCurrency, mBankCode, etCardholdersName, etBankCardNo, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    ToastUtil.show(msg);
                    return;
                }
                CommonAppConfig.getInstance().clearUserInfo();
                CommonAppConfig.getInstance().setBlankStatus(1);//绑定后，设置本地状态
                //服务端有合适的提示
                ToastUtil.show(msg);
                setResult(RESULT_OK);
                finish();
            }
        });


    }

    @Override
    protected void onDestroy() {
        if (null != mSelectBankDialog && mSelectBankDialog.isShowing()) {
            mSelectBankDialog.dismiss();
            mSelectBankDialog = null;
        }
        MainHttpUtil.cancel(MainHttpConsts.USER_SET_USER_BANK);
        super.onDestroy();
    }












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
}