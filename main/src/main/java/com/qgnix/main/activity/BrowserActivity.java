package com.qgnix.main.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.DialogUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.RouteUtil;
import com.qgnix.common.utils.ScreenDimenUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.bean.TransferBean;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.main.R;
import com.qgnix.main.glide.util.Utils;
import com.qgnix.main.http.MainHttpUtil;
import com.qgnix.main.utils.SonicJavaScriptInterface;
import com.qgnix.main.utils.SonicRuntimeImpl;
import com.qgnix.main.utils.SonicSessionClientImpl;
import com.tencent.sonic.sdk.SonicCacheInterceptor;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicConstants;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;
import com.tencent.sonic.sdk.SonicSessionConnection;
import com.tencent.sonic.sdk.SonicSessionConnectionInterceptor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

@Route(path = RouteUtil.BROWSER_ACTIVITY)
public class BrowserActivity extends AppCompatActivity {


    WebView webView;
    ImageView mIvPopIn;

//    public final static String PARAM_URL = "param_url";
//
//    public final static String PARAM_MODE = "param_mode";
//
//    public final static String PARAM_PLAT = "param_plat";
//
//    public final static String PARAM_TITLE = "param_title";
//
//    public final static String PARAM_BALANCE = "current_balance";
//
//    public final static String PARAM_TRANSFER = "current_transfer";

    private Context mContext;


    public static final int MODE_DEFAULT = 0;

    public static final int MODE_SONIC = 1;

    public static final int MODE_SONIC_WITH_OFFLINE_CACHE = 2;

    private List<TransferBean> list = new ArrayList<>();

//    private TransferBean currentTransfer;

    private String currentBalance;
//    private boolean isPopupShow;


    private SonicSession sonicSession;
    private TextView tvBalance;
    private String platType;
    private PopupWindow popupWindow;
    private boolean setResult;
    private Dialog mDialog;
    private int mPopOffX;
    private int mPopOffY;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getLayoutId());
        mContext = this;
        initView();
        initData();

    }

    protected int getLayoutId() {
        return R.layout.activity_game_web;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void getWebGameEvent (WebGameTransfer event) {
//        Log.d("", "getWebGameEvent: ");
//    }


    protected void initView() {
        webView = findViewById(R.id.web_view);
        mIvPopIn = findViewById(R.id.iv_pop_in);
        mIvPopIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLoadWithOverviewMode(true);
    }

    protected void initData() {
        Intent intent = getIntent();
        String url = intent.getStringExtra(Constants.PARAM_URL);
        int mode = intent.getIntExtra(Constants.PARAM_MODE, -1);
        if (TextUtils.isEmpty(url) || -1 == mode) {
            finish();
            return;
        }
        currentBalance = intent.getStringExtra(Constants.PARAM_BALANCE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        platType = intent.getStringExtra(Constants.PARAM_PLAT);
        screenModel(platType);
//        currentTransfer = (TransferBean) intent.getSerializableExtra(Constants.PARAM_TRANSFER);
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
        }

        SonicSessionClientImpl sonicSessionClient = null;

        if (MODE_DEFAULT != mode) { // sonic mode
            SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
            sessionConfigBuilder.setSupportLocalServer(true);

            if (MODE_SONIC_WITH_OFFLINE_CACHE == mode) {
                sessionConfigBuilder.setCacheInterceptor(new SonicCacheInterceptor(null) {
                    @Override
                    public String getCacheData(SonicSession session) {
                        return null; // offline pkg does not need cache
                    }
                });

                sessionConfigBuilder.setConnectionInterceptor(new SonicSessionConnectionInterceptor() {
                    @Override
                    public SonicSessionConnection getConnection(SonicSession session, Intent intent) {
                        return new OfflinePkgSessionConnection(BrowserActivity.this, session, intent);
                    }
                });
            }

            sonicSession = SonicEngine.getInstance().createSession(url, sessionConfigBuilder.build());
            if (null != sonicSession) {
                sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
            } else {

            }
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String title = view.getTitle();
                if (!TextUtils.isEmpty(title)) {

                }

                if (sonicSession != null) {
                    sonicSession.getSessionClient().pageFinish(url);
                }
                String theCoin =   getIntent().getStringExtra(Constants.PARAM_TRANSFER);
                if (theCoin != null && "0".equals(theCoin)) {
                    showDelayPopup(3000);
                }
            }

            @TargetApi(21)
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (sonicSession != null) {
                    return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
                }
                return null;
            }
        });

        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
        webView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");


        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(webView);
            sonicSessionClient.clientReady();
        } else { // default mode
            webView.loadUrl(url);
        }

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    /**
     * 设置游戏横屏
     *
     * @param platType
     */
    private void screenModel(String platType) {
        if (platType != null && (platType.equals("bng") || platType.equals("bts"))) {
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

    }

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
    }*/

    @Override
    protected void onDestroy() {
        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }
        if (null != webView) {
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (null != webView)
            webView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (null != webView) {
            webView.reload();
            webView.onPause();
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                String url = webView.getUrl();
                String originalUrl = webView.copyBackForwardList().getCurrentItem() == null ? "" : webView.copyBackForwardList().getCurrentItem().getOriginalUrl();
                if (webView.canGoBack() && !(originalUrl.equals(url) || originalUrl.equals(url + "/index"))) {
                    webView.goBack();
                    return true;
                } else {
                    //是否转入过金额
                    if (setResult) {
                        setResult(100);
                    }
                    finish();
                }
            } catch (Exception e) {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private static class OfflinePkgSessionConnection extends SonicSessionConnection {

        private final WeakReference<Context> context;

        public OfflinePkgSessionConnection(Context context, SonicSession session, Intent intent) {
            super(session, intent);
            this.context = new WeakReference<Context>(context);
        }

        @Override
        protected int internalConnect() {
            Context ctx = context.get();
            if (null != ctx) {
                try {
                    InputStream offlineHtmlInputStream = ctx.getAssets().open("sonic-demo-index.html");
                    responseStream = new BufferedInputStream(offlineHtmlInputStream);
                    return SonicConstants.ERROR_CODE_SUCCESS;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return SonicConstants.ERROR_CODE_UNKNOWN;
        }

        @Override
        protected BufferedInputStream internalGetResponseStream() {
            return responseStream;
        }

        @Override
        protected String internalGetCustomHeadFieldEtag() {
            return SonicSessionConnection.CUSTOM_HEAD_FILED_ETAG;
        }

        @Override
        public void disconnect() {
            if (null != responseStream) {
                try {
                    responseStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getResponseCode() {
            return 200;
        }

        @Override
        public Map<String, List<String>> getResponseHeaderFields() {
            return new HashMap<>(0);
        }

        @Override
        public String getResponseHeaderField(String key) {
            return "";
        }
    }

    private void showPopupWindow() {
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(this).inflate(R.layout.web_transfer, null);
            tvBalance = contentView.findViewById(R.id.tv_balance_num);
            tvBalance.setText(currentBalance);
            final EditText et_input = contentView.findViewById(R.id.et_input);
            contentView.findViewById(R.id.tv_transfer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submit(et_input, v, platType, new CommonCallback<Float>() {
                        @Override
                        public void callback(Float amount) {
                            //                            loadData();
                            CommonAppConfig.getInstance().clearUserInfo();
                            setResult = true;
                            currentBalance = "" + (Float.parseFloat(currentBalance) - amount);
                            tvBalance.setText(currentBalance);
                        }
                    });
                }
            });
            contentView.findViewById(R.id.ll_exit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //是否转入过金额
                    if (setResult) {
                        setResult(100);
                    }
                    finish();
                }
            });
            contentView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            //设置可点击
            popupWindow.setFocusable(true);
            //设置点击外面会消失
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            //在view左侧出现
//        popupWindow.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            popupWindow.setAnimationStyle(R.style.Animleft);
            int h = mIvPopIn.getMeasuredHeight();
            int h2 = ScreenDimenUtil.getInstance().dip2px(this, 87);
            mPopOffX = -mIvPopIn.getMeasuredWidth();
            mPopOffY = -((h2 - h) / 2 + h);
        }
        //mMore 为需要在某个view的左侧出现
        popupWindow.showAsDropDown(mIvPopIn, mPopOffX - 25, mPopOffY);
    }

    private void showDelayPopup(int time) {
        showPopupWindow();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != popupWindow) {
                    popupWindow.dismiss();
                }
            }
        }, time);
    }
//    private void loadData() {
//        CommonHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
//            @Override
//            public void callback(UserBean bean) {
//                setResult = true;
//                currentBalance = bean.getCoin();
//                tvBalance.setText(currentBalance);
//                //临时注解 等需求完善
////                showDelayPopup(5*1000);
//            }
//
//        });
//    }

    public void submit(final EditText etAmount, final View btnSubmit, String platType, final CommonCallback<Float> callback) {
        final String amount = etAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amount)) {
            ToastUtil.show(com.qgnix.live.R.string.red_pack_7_2);
            return;
        }
        if (mDialog == null) {
            mDialog = DialogUtil.loadingDialog(mContext, getString(R.string.loading));
        }
        mDialog.show();
        LiveHttpUtil.nGetTrans(amount, platType, 1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                L.e("===Trans_v2=", Arrays.toString(info));
                etAmount.setText("");
                btnSubmit.setEnabled(true);
                ToastUtil.show(msg);
                if (code != 0) {
                    return;
                }
                callback.callback(Float.parseFloat(amount));
            }

            @Override
            public void onError() {
                super.onError();
                btnSubmit.setEnabled(true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mDialog.dismiss();
            }
        });
    }





    //=======================================lost code========================//













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
