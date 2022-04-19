package com.qgnix.live.views;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.faceunity.utils.BitmapUtil;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ScreenDimenUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.R;
import com.qgnix.live.activity.LiveActivity;
import com.qgnix.live.activity.LiveAudienceActivity;
import com.qgnix.live.bean.LiveBean;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.live.dialog.LottleDialog;
import com.qgnix.live.dialog.VideoIntroductionDialog;
import com.qgnix.live.event.FullScreenEvent;
import com.qgnix.live.interfaces.OnPlayVideoCallback;
import com.qgnix.live.lottery.LotteryTypeDialog;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.tencent.liteav.superplayer.SuperPlayerDef;
import com.tencent.liteav.superplayer.SuperPlayerGlobalConfig;
import com.tencent.liteav.superplayer.SuperPlayerModel;
import com.tencent.liteav.superplayer.SuperPlayerView;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/10/9.
 * 观众直播间逻辑
 */
public class LiveAudienceViewHolder extends AbsLiveViewHolder implements SuperPlayerView.OnSuperPlayerViewCallback {
    private static final String TAG = "===" + LiveAudienceViewHolder.class.getSimpleName();
    /**
     * 超级播放器View
     */
    public SuperPlayerView mSuperPlayerView;
    /**
     * 操作栏
     */
    private LinearLayout mRealBottom;
    /**
     * 链接互动
     */
    private LinearLayout mBtnLinkMic;
    /**
     * 小屏返回回调
     */
    private OnPlayVideoCallback mOnPlayVideoCallback;
    /**
     * 是否竖屏
     */
    private boolean mIsPortrait;
    /**
     * 是否视频
     */
    private boolean mIsVideo;
    /**
     * 彩票id
     */
    private String mTId = "";
    /**
     * 直播id
     */
    private String mLiveId;
    private StandardGSYVideoPlayer gsyVideoPlayer;
    OrientationUtils orientationUtils;

    public void setTId(String tId) {
        this.mTId = tId;
    }

    private WebView mPlayerH5;

    public void setLiveId(String liveId) {
        this.mLiveId = liveId;
    }

    private BaseCustomDialog mCustomDialog;

    public void setOnPlayVideoCallback(OnPlayVideoCallback onPlayVideoCallback) {
        this.mOnPlayVideoCallback = onPlayVideoCallback;
    }

    public LiveAudienceViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        mIsPortrait = (boolean) args[0];
        mIsVideo = (boolean) args[1];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_audience;
    }

    @Override
    public void init() {
        super.init();
        // 视频
        if (mIsVideo && (null != mContext && mContext instanceof LiveAudienceActivity)) {
            ((LiveAudienceActivity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        mRealBottom = (LinearLayout) findViewById(R.id.rela_bottom);
        mBtnLinkMic = (LinearLayout) findViewById(R.id.btn_link_mic);
        if (!EventBus.getDefault().isRegistered(mContext)) {
            EventBus.getDefault().register(mContext);
        }

        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
        findViewById(R.id.btn_red_pack).setOnClickListener(this);
        findViewById(R.id.btn_gift).setOnClickListener(this);
        findViewById(R.id.iv_game).setOnClickListener(this);
        findViewById(R.id.btn_menu).setOnClickListener(this);
        findViewById(R.id.btn_chat).setOnClickListener(this);
        findViewById(R.id.img_close).setOnClickListener(this);

        gsyVideoPlayer = (StandardGSYVideoPlayer) findViewById(R.id.video_player);
        gsyVideoPlayer.setThumbPlay(false);

        //设置旋转
        orientationUtils = new OrientationUtils((Activity) mContext, gsyVideoPlayer);

        // 视频
        if (mIsVideo) {
            mBtnLinkMic.setVisibility(View.GONE);
            // 视频简介
            LinearLayout llVideoIntroduction = (LinearLayout) findViewById(R.id.ll_video_introduction);
            //llVideoIntroduction.setVisibility(View.VISIBLE);   //暂时不需要这个功能 先隐藏
            llVideoIntroduction.setOnClickListener(this);


            // 保持屏幕常亮
            mSuperPlayerView = (SuperPlayerView) findViewById(R.id.super_player_view);
            mSuperPlayerView.setPlayerViewCallback(this);
            ViewGroup.LayoutParams params = mSuperPlayerView.getLayoutParams();
//            if (mIsPortrait) {
//                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                mSuperPlayerView.setLayoutParams(params);
//                mSuperPlayerView.toggleLayoutTopView(true);
//            } else {
//                toggleView(false);
//            }
            toggleView(true);


            initSuperVodGlobalSetting();
        }

        int theHeight = ScreenDimenUtil.getInstance().getScreenWdith() * 9 / 16;
        mPlayerH5 = (WebView) findViewById(R.id.play_h5);
        mPlayerH5.getLayoutParams().height = theHeight;
        WebSettings webSettings = mPlayerH5.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setAllowContentAccess(true);
//        webSettings.setDatabaseEnabled(true);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setSavePassword(false);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
//        webSettings.setSaveFormData(false);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webSettings.setLoadWithOverviewMode(true);
    }

    /**
     * 设置连麦按钮 显示状态
     *
     * @param isVideo true/false
     */
    public void setMicState(boolean isVideo) {
        mBtnLinkMic.setVisibility(isVideo ? View.GONE : View.VISIBLE);
    }

    /**
     * 初始化超级播放器全局配置
     */
    private void initSuperVodGlobalSetting() {
        SuperPlayerGlobalConfig prefs = SuperPlayerGlobalConfig.getInstance();
        // 开启悬浮窗播放
        prefs.enableFloatWindow = false;
        // 播放器默认缓存个数
        prefs.maxCacheItem = 5;
        // 设置播放器渲染模式
        prefs.enableHWAcceleration = true;
        prefs.renderMode = TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN;

        //需要修改为自己的时移域名
        // prefs.playShiftDomain = "liteavapp.timeshift.qcloud.com";
    }

    @Override
    public void onResume() {
        if (null != mSuperPlayerView) {
            if (mSuperPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.PAUSE) {
                L.d(TAG, "PlayerState:" + mSuperPlayerView.getPlayerState());
                mSuperPlayerView.onResume();
            }
            if (mSuperPlayerView.getPlayerMode() == SuperPlayerDef.PlayerMode.FULLSCREEN) {
                //隐藏虚拟按键，并且全屏
                View decorView = ((LiveAudienceActivity) mContext).getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    @Override
    public void onPause() {
        if (null != mSuperPlayerView) {
            L.d(TAG, "PlayerState:" + mSuperPlayerView.getPlayerState());
            mSuperPlayerView.onPause();
        }
        super.onPause();
    }

    @Override
    public void release() {
        super.release();
        if (null != mSuperPlayerView) {
            mSuperPlayerView.release();
            mSuperPlayerView.resetPlayer();
        }
        onBackPressed();
        if (null != mCustomDialog && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
            mCustomDialog = null;
        }
        //释放所有
        if (null != gsyVideoPlayer) {
            gsyVideoPlayer.setVideoAllCallBack(null);
        }
        if (null != mPlayerH5) {
            mPlayerH5.stopLoading();
            mPlayerH5.destroy();
        }
    }

    /**
     * 播放视频
     *
     * @param videoPath 地址
     */
    public void playVideo(String videoPath) {
        if (null != mSuperPlayerView) {
            mSuperPlayerView.setVisibility(View.VISIBLE);
            SuperPlayerModel model = new SuperPlayerModel();
            L.e(TAG, "视频播放地址：" + videoPath);
            model.url = videoPath;
            mSuperPlayerView.playWithModel(model);
        }
    }

    public void stopH5() {
        if (mPlayerH5 != null) {
            mPlayerH5.setVisibility(View.GONE);
            mPlayerH5.loadUrl("");
        }
    }

    public void playH5(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mPlayerH5.setVisibility(View.VISIBLE);
        mPlayerH5.loadUrl(url);
        L.e(TAG, "play----url--->" + url);
    }

    public void playVideo(LiveBean liveBean) {
        if (null == gsyVideoPlayer) {
            return;
        }
        gsyVideoPlayer.setVisibility(View.VISIBLE);
        gsyVideoPlayer.setDismissControlTime(2000);
//        gsyVideoPlayer.setBottomShowProgressBarDrawable(null,null);

        //视频播放地址
        String videoUrl = liveBean.getPull();
        gsyVideoPlayer.setEnlargeImageRes(R.mipmap.video_large);
        gsyVideoPlayer.setShrinkImageRes(R.mipmap.video_shrink);
        gsyVideoPlayer.setUp(videoUrl, true, "");
        if (liveBean.getAnyway() == 0) { //竖屏
            //设置全屏
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        } else { //横屏
            //设置默认
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        }

        //增加封面
        ImageView imageView = new ImageView((Activity) mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String thumUrl = liveBean.getThumb();
//        imageView.setImageResource(R.mipmap.ic_launcher);
        Glide.with(mContext).asBitmap().load(thumUrl).into(imageView);
        gsyVideoPlayer.setThumbImageView(imageView);

        //设置返回键
        gsyVideoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        orientationUtils = new OrientationUtils((Activity) mContext, gsyVideoPlayer);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
                //1横屏 0 竖屏
                boolean isLand = orientationUtils.getIsLand() == 1 ? true : false;
                EventBus.getDefault().post(new FullScreenEvent(isLand));

            }
        });
        //是否可以滑动调整
        gsyVideoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        gsyVideoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //1横屏 0 竖屏
                boolean isLand = orientationUtils.getIsLand() == 1 ? true : false;
                EventBus.getDefault().post(new FullScreenEvent(isLand));
            }
        });
        gsyVideoPlayer.startPlayLogic();
    }

    public boolean onPhoneBack() {
        boolean isLand = orientationUtils.getIsLand() == 1 ? true : false;
        if (isLand) {
            onBackPressed();//如果是竖屏状态，就模拟播放控件的返回按钮
            isLand = orientationUtils.getIsLand() == 1 ? true : false;
            EventBus.getDefault().post(new FullScreenEvent(isLand));
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置是否显示
     *
     * @param state View.GONE View.Visible
     */
    public void setIsVideoVisible(int state) {
        if (null != gsyVideoPlayer) {
            gsyVideoPlayer.setVisibility(state);
        }
    }

    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            gsyVideoPlayer.getFullscreenButton().performClick();
            return;
        }
        //关闭视频播放
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();

    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_msg) {
            // ((LiveActivity) mContext).openChatListWindow();
        } else if (i == R.id.btn_chat) {
            CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
                @Override
                public void callback(UserBean bean) {
                    if (bean.getLevel() >= 1) {
                        ((LiveActivity) mContext).openChatWindow();
                    } else {
                        ToastUtil.show(R.string.live_chat_tip);
                    }
                }
            });
        } else if (i == R.id.btn_close) {
            close();
        } else if (i == R.id.btn_share) {
            openShareWindow();
        } else if (i == R.id.btn_red_pack) {
            ((LiveActivity) mContext).openRedPackSendWindow();
        } else if (i == R.id.btn_gift) {
            openGiftWindow();
        } else if (i == R.id.iv_game) {
            mCustomDialog = new LotteryTypeDialog(mContext);
            mCustomDialog.show();
        } else if (i == R.id.btn_menu) {
            mCustomDialog = new LottleDialog(mContext, mTId);
            mCustomDialog.show();
        } else if (i == R.id.ll_video_introduction) {
            mCustomDialog = new VideoIntroductionDialog(mContext, mLiveId);
            mCustomDialog.show();
        }
    }

    /**
     * 退出直播间
     */
    private void close() {
        onBackPressed();
        ((LiveAudienceActivity) mContext).onBackPressed();
    }


    /**
     * 打开礼物窗口
     */
    private void openGiftWindow() {
        ((LiveAudienceActivity) mContext).openGiftWindow();
    }

    /**
     * 打开分享窗口
     */
    private void openShareWindow() {
        ((LiveActivity) mContext).openShareWindow();
    }

    @Override
    public void onStartFullScreenPlay() {
        // 横屏执行
        if (!mIsPortrait) {
            toggleView(false);
        }
    }

    @Override
    public void onStopFullScreenPlay() {
        // 横屏执行
        if (!mIsPortrait) {
            toggleView(true);
        }
    }

    @Override
    public void onClickFloatCloseBtn() {

    }

    @Override
    public void onClickSmallReturnBtn() {
        if (null != mOnPlayVideoCallback) {
            mOnPlayVideoCallback.onPlayVideoSmallBack();
        }
    }

    @Override
    public void onStartFloatWindowPlay() {

    }

    private void toggleView(boolean visibility) {
        mRealBottom.setVisibility(visibility ? View.VISIBLE : View.GONE);
        mBtnLinkMic.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
}
