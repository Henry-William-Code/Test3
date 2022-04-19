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
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.custom.ItemDecoration;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.adapter.DrawHistoryDetailAdapter;
import com.qgnix.live.bean.DrawHistoryBean;
import com.qgnix.live.http.LiveHttpConsts;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.main.R;
import com.qgnix.main.glide.util.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

/**
 * 开奖历史明细
 */
public class DrawHistoryDetailActivity extends AbsActivity {
    private SmartRefreshLayout mRefresh;

    private DrawHistoryDetailAdapter mAdapter;

    private List<DrawHistoryBean> mList = new ArrayList<>();
    /**
     * 页数
     */
    private int mPage = 1;

    /**
     * 彩票id
     */
    private String mTId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_draw_history_detail;
    }

    @Override
    protected void main() {
        super.main();
        initView();
        getData(false);
    }

    private void initView() {
        Intent intent = getIntent();
        mTId = intent.getStringExtra("tId");
        String type = intent.getStringExtra("tType");
        setTitle(intent.getStringExtra("tName"));

        mRefresh = findViewById(com.qgnix.live.R.id.refresh);
        RecyclerView recyclerView = findViewById(com.qgnix.live.R.id.recyclerView);
//        recyclerView.addItemDecoration(new ItemDecoration(mContext));
        mAdapter = new DrawHistoryDetailAdapter(mContext, type, mList);
        recyclerView.setAdapter(mAdapter);

        mRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                ++mPage;
                getData(true);
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mPage = 1;
                getData(false);
            }
        });

    }

    public void getData(final boolean isRefresh) {
        LiveHttpUtil.getOpenLotteryHistoryDetail(mPage, mTId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    ToastUtil.show(msg);
                    return;
                }
                if (info.length == 0) {
                    return;
                }
                List<DrawHistoryBean> lists = new Gson().fromJson(Arrays.toString(info), new TypeToken<List<DrawHistoryBean>>() {
                }.getType());
                if (null == lists || lists.isEmpty()) {
                    mRefresh.setEnableLoadMore(false);
                }
                if (isRefresh) {
                    mList.addAll(lists);
                } else {
                    mList = lists;
                }
                mAdapter.onDataChange(mList);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isRefresh) {
                    mRefresh.finishLoadMore();
                } else {
                    mRefresh.finishRefresh();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.OPEN_LOTTERY_HISTORY_API);
        super.onDestroy();
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