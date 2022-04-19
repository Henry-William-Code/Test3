package com.qgnix.phonelive.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.bean.AdBean;
import com.qgnix.common.bean.ConfigBean;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.bean.VersionBean;
import com.qgnix.common.custom.CircleProgress;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.DownloadUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.LanSwitchUtil;
import com.qgnix.common.utils.MD5Util;
import com.qgnix.common.utils.NetWorkUtil;
import com.qgnix.common.utils.SpUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.VersionUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.views.LauncherAdViewHolder;
import com.qgnix.main.activity.LanSwitchActivity;
import com.qgnix.main.activity.LoginActivity;
import com.qgnix.main.activity.MainActivity;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.qgnix.phonelive.BuildConfig;
import com.qgnix.phonelive.R;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/17.
 */
public class LauncherActivity extends AbsActivity implements View.OnClickListener {

    private static final String TAG = "LauncherActivity";
    private static final int WHAT_GET_CONFIG = 0;
    private static final int WHAT_COUNT_DOWN = 1;
    private Handler mHandler;
    private ViewGroup mRoot;
    /**
     * 启动页面
     */
    private RelativeLayout mLlCover;

    private ProgressBar mPbInit;

    private ViewGroup mContainer;
    private CircleProgress mCircleProgress;
    private List<AdBean> mAdList;
    private List<ImageView> mImageViewList;
    private int mMaxProgressVal;
    private int mCurProgressVal;
    private int mAdIndex;
    private int mInterval = 2000;
    private View mBtnSkipImage;
    private View mBtnSkipVideo;
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayer mPlayer;
    private LauncherAdViewHolder mLauncherAdViewHolder;
    private boolean mPaused;

    @Override
    protected void main() {
        CommonAppConfig.getInstance().clearUserInfo();//启动时清除getBaseInfo缓存

        mRoot = findViewById(R.id.root);
        mLlCover = findViewById(R.id.ll_cover);
        //初始化信息
        TextView tvInitInfo = findViewById(R.id.tv_init_info);
        mPbInit = findViewById(R.id.pb_init);
        //版本信息
        TextView tvAppVersion = findViewById(R.id.tv_app_version);

        mCircleProgress = findViewById(R.id.progress);
        mContainer = findViewById(R.id.container);
        mBtnSkipImage = findViewById(R.id.btn_skip_img);
        mBtnSkipVideo = findViewById(R.id.btn_skip_video);
        mBtnSkipImage.setOnClickListener(this);
        mBtnSkipVideo.setOnClickListener(this);
        tvAppVersion.setText("V" + VersionUtil.getVersion());
        mPbInit.setProgress(30);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_GET_CONFIG:
                        mPbInit.setProgress(80);
//                        getConfig();
                        getVersion();
                        break;
                    case WHAT_COUNT_DOWN:
                        updateCountDown();
                        break;
                    default:
                        break;
                }
            }
        };

        // 正式环境禁止模拟器上安装
        if (BuildConfig.ENV_TYPE == 1) {
            boolean emulator = CommonAppConfig.getInstance().isEmulator(mContext);
            L.e("===emulator==" + emulator);
            if (emulator) {
                mPbInit.setProgress(50);
                tvInitInfo.setTextColor(getResources().getColor(R.color.red));
                tvInitInfo.setText(R.string.network_fail);
                return;
            }
        }

        if (NetWorkUtil.isConnected(mContext)) {
            tvInitInfo.setTextColor(getResources().getColor(R.color.black2));
            tvInitInfo.setText(R.string.app_initializing);
            Log.d("-----初始化------", WordUtil.getString(R.string.app_initializing));
            mHandler.sendEmptyMessageDelayed(WHAT_GET_CONFIG, 1000);
        } else {
            mPbInit.setProgress(50);
            tvInitInfo.setTextColor(getResources().getColor(R.color.red));
            tvInitInfo.setText(R.string.network_fail);
        }


       /*
       申请读写权限
       ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);*/
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_launcher;
    }


    /**
     * 图片倒计时
     */
    private void updateCountDown() {
        mCurProgressVal += 100;
        if (mCurProgressVal > mMaxProgressVal) {
            return;
        }
        if (mCircleProgress != null) {
            mCircleProgress.setCurProgress(mCurProgressVal);
        }
        int index = mCurProgressVal / mInterval;
        if (index < mAdList.size() && mAdIndex != index) {
            View v = mImageViewList.get(mAdIndex);
            if (v != null && v.getVisibility() == View.VISIBLE) {
                v.setVisibility(View.INVISIBLE);
            }
            mAdIndex = mCurProgressVal / mInterval;
        }
        if (mCurProgressVal < mMaxProgressVal) {
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_COUNT_DOWN, 100);
            }
        } else if (mCurProgressVal == mMaxProgressVal) {
            checkUidAndToken();
        }
    }

    private void getVersion() {
        CommonHttpUtil.getVersion(new CommonCallback<VersionBean>() {
            @Override
            public void callback(VersionBean bean) {
                getConfig();
            }
        });
    }

    /**
     * 获取Config信息
     */
    private void getConfig() {

        CommonHttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                if (null == bean) {
                    return;
                }
                CommonAppConfig.getInstance().setSwitchStatu(bean.getGoogleStore());
                CommonAppConfig.getInstance().setServerVersion(bean.getGoogleVer());
                CommonAppConfig.getInstance().setSwitchPlay(bean.getGoogleThirdGame());
                mPbInit.setProgress(100);
                //轮播图
                String adInfo = bean.getAdInfo();
                if (TextUtils.isEmpty(adInfo)) {
                    checkUidAndToken();
                    return;
                }
                JSONObject obj = JSON.parseObject(adInfo);
                if (obj.getIntValue("switch") != 1) {
                    checkUidAndToken();
                    return;
                }
                List<AdBean> list = JSON.parseArray(obj.getString("list"), AdBean.class);
                if (list == null || list.isEmpty()) {
                    checkUidAndToken();
                    return;
                }


                mAdList = list;
                mInterval = obj.getIntValue("time") * 1000;
                if (mContainer != null) {
                    mContainer.setOnClickListener(LauncherActivity.this);
                }

                playAD(obj.getIntValue("type") == 0);
            }
        });
    }

    /**
     * 检查uid和token是否存在
     */
    private void checkUidAndToken() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(SpUtil.UID, SpUtil.TOKEN);
        final String uid = uidAndToken[0];
        final String token = uidAndToken[1];
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
            CommonHttpUtil.getBaseInfo(uid, token, new CommonCallback<UserBean>() {
                @Override
                public void callback(UserBean bean) {
                    if (bean != null) {
                        CommonAppConfig.getInstance().setLoginInfo(uid, token, false);
                        CommonAppConfig.getInstance().setCoinAward(bean.getTie_card_reward());
                        //保存语言信息
                        LanSwitchUtil.setLanToLocale(bean.getLan());
                        forwardMainActivity();
                    } else {
                        toLogin();
                    }
                }
            });
        } else {
            toLogin();
        }
    }

    private void toLogin() {
        if (LanSwitchUtil.getCurLanCode() == -1) {
            // 选择语言
            LanSwitchActivity.toForward(mContext, LoginActivity.class, true);
        } else {
            releaseVideo();
            LoginActivity.forward();
        }
    }

    /**
     * 跳转到首页
     */
    private void forwardMainActivity() {
        releaseVideo();
        MainActivity.forward(mContext);
        finish();
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        MainHttpUtil.cancel(CommonHttpConsts.GET_BASE_INFO);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        releaseVideo();
        if (mLauncherAdViewHolder != null) {
            mLauncherAdViewHolder.release();
        }
        mLauncherAdViewHolder = null;
        super.onDestroy();
        L.e(TAG, "----------> onDestroy");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_skip_img || i == R.id.btn_skip_video) {
            if (mBtnSkipImage != null) {
                mBtnSkipImage.setClickable(false);
            }
            if (mBtnSkipVideo != null) {
                mBtnSkipVideo.setClickable(false);
            }
            checkUidAndToken();
        } else if (i == R.id.container) {
            clickAD();
        }
    }

    /**
     * 点击广告
     */
    private void clickAD() {
        if (mAdList != null && mAdList.size() > mAdIndex) {
            AdBean adBean = mAdList.get(mAdIndex);
            if (adBean != null) {
                String link = adBean.getLink();
                if (!TextUtils.isEmpty(link)) {
                    if (mHandler != null) {
                        mHandler.removeCallbacksAndMessages(null);
                    }
                    if (mContainer != null) {
                        mContainer.setClickable(false);
                    }
                    releaseVideo();
                    if (mLauncherAdViewHolder == null) {
                        mLauncherAdViewHolder = new LauncherAdViewHolder(mContext, mRoot, link);
                        mLauncherAdViewHolder.addToParent();
                        mLauncherAdViewHolder.loadData();
                        mLauncherAdViewHolder.show();
                        mLauncherAdViewHolder.setActionListener(new LauncherAdViewHolder.ActionListener() {
                            @Override
                            public void onHideClick() {
                                checkUidAndToken();
                            }
                        });
                    }
                }
            }
        }
    }

    private void releaseVideo() {
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
            mPlayer.setPlayListener(null);
        }
        mPlayer = null;
    }


    /**
     * 播放广告
     */
    private void playAD(boolean isImage) {
        if (mContainer == null) {
            return;
        }
        if (isImage) {
            int imgSize = mAdList.size();
            if (imgSize > 0) {
                mImageViewList = new ArrayList<>();
                for (int i = 0; i < imgSize; i++) {
                    ImageView imageView = new ImageView(mContext);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setBackgroundColor(0xffffffff);
                    mImageViewList.add(imageView);
                    ImgLoader.display(mContext, mAdList.get(i).getUrl(), imageView);
                }
                for (int i = imgSize - 1; i >= 0; i--) {
                    mContainer.addView(mImageViewList.get(i));
                }
                if (mBtnSkipImage != null && mBtnSkipImage.getVisibility() != View.VISIBLE) {
                    mBtnSkipImage.setVisibility(View.VISIBLE);
                }
                mMaxProgressVal = imgSize * mInterval;
                if (mCircleProgress != null) {
                    mCircleProgress.setMaxProgress(mMaxProgressVal);
                }
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(WHAT_COUNT_DOWN, 100);
                }
                mPbInit.setProgress(100);
                if (mLlCover != null && mLlCover.getVisibility() == View.VISIBLE) {
                    mLlCover.setVisibility(View.INVISIBLE);
                }
            } else {
                checkUidAndToken();
            }
        } else {
            if (mAdList == null || mAdList.size() == 0) {
                checkUidAndToken();
                return;
            }
            String videoUrl = mAdList.get(0).getUrl();
            if (TextUtils.isEmpty(videoUrl)) {
                checkUidAndToken();
                return;
            }
            String videoFileName = MD5Util.getMD5(videoUrl);
            if (TextUtils.isEmpty(videoFileName)) {
                checkUidAndToken();
                return;
            }
            File file = new File(getCacheDir(), videoFileName);
            if (file.exists()) {
                playAdVideo(file);
            } else {
                downloadAdFile(videoUrl, videoFileName);
            }
        }
    }

    @Override
    protected void onPause() {
        mPaused = true;
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.setMute(true);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.setMute(false);
            }
        }
        mPaused = false;
    }

    /**
     * 下载视频
     */
    private void downloadAdFile(String url, String fileName) {
        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.download("ad_video", getCacheDir(), fileName, url, new DownloadUtil.Callback() {
            @Override
            public void onSuccess(File file) {
                playAdVideo(file);
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onError(Throwable e) {
                checkUidAndToken();
            }
        });
    }

    /**
     * 播放视频
     */
    private void playAdVideo(File videoFile) {
        if (mBtnSkipVideo != null && mBtnSkipVideo.getVisibility() != View.VISIBLE) {
            mBtnSkipVideo.setVisibility(View.VISIBLE);
        }
        mTXCloudVideoView = new TXCloudVideoView(mContext);
        mTXCloudVideoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mTXCloudVideoView.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mContainer.addView(mTXCloudVideoView);
        mPlayer = new TXLivePlayer(mContext);
        mPlayer.setPlayerView(mTXCloudVideoView);
        mPlayer.setAutoPlay(true);
        mPlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int e, Bundle bundle) {
                if (e == TXLiveConstants.PLAY_EVT_PLAY_END) {//获取到视频播放完毕的回调
                    checkUidAndToken();
                    L.e(TAG, "视频播放结束------>");
                } else if (e == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {////获取到视频宽高回调
                    float videoWidth = bundle.getInt("EVT_PARAM1", 0);
                    float videoHeight = bundle.getInt("EVT_PARAM2", 0);
                    if (mTXCloudVideoView != null && videoWidth > 0 && videoHeight > 0) {
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTXCloudVideoView.getLayoutParams();
                        int targetH = 0;
                        if (videoWidth >= videoHeight) {//横屏
                            params.gravity = Gravity.CENTER_VERTICAL;
                            targetH = (int) (mTXCloudVideoView.getWidth() / videoWidth * videoHeight);
                        } else {
                            targetH = ViewGroup.LayoutParams.MATCH_PARENT;
                        }
                        if (targetH != params.height) {
                            params.height = targetH;
                            mTXCloudVideoView.requestLayout();
                        }
                    }
                } else if (e == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
                    if (mLlCover != null && mLlCover.getVisibility() == View.VISIBLE) {
                        mLlCover.setVisibility(View.INVISIBLE);
                    }
                } else if (e == TXLiveConstants.PLAY_ERR_NET_DISCONNECT ||
                        e == TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND) {
                    ToastUtil.show(WordUtil.getString(R.string.live_play_error));
                    checkUidAndToken();
                }
            }

            @Override
            public void onNetStatus(Bundle bundle) {

            }

        });
        mPlayer.startPlay(videoFile.getAbsolutePath(), TXLivePlayer.PLAY_TYPE_LOCAL_VIDEO);
    }
}
