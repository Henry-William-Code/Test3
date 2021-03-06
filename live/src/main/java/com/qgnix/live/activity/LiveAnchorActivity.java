package com.qgnix.live.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.faceunity.FURenderer;
import com.faceunity.utils.CameraUtils;
import com.lovense.sdklibrary.Lovense;
import com.lovense.sdklibrary.LovenseToy;
import com.lovense.sdklibrary.callBack.LovenseError;
import com.lovense.sdklibrary.callBack.OnConnectListener;
import com.faceunity.interfaces.BeautyViewHolder;
import com.faceunity.views.FUViewHolder;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.event.LoginInvalidEvent;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.DialogUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.game.bean.GameParam;
import com.qgnix.game.dialog.GameDialogFragment;
import com.qgnix.game.event.GameWindowChangedEvent;
import com.qgnix.game.util.GamePresenter;
import com.qgnix.live.R;
import com.qgnix.live.bean.LiveBean;
import com.qgnix.live.bean.LiveGuardInfo;
import com.qgnix.live.bean.LiveKsyConfigBean;
import com.qgnix.live.bean.LiveReceiveGiftBean;
import com.qgnix.live.dialog.LiveFunctionDialogFragment;
import com.qgnix.live.dialog.LiveLinkMicListDialogFragment;
import com.qgnix.live.dialog.VibratorDialog;
import com.qgnix.live.event.LinkMicTxMixStreamEvent;
import com.qgnix.live.http.LiveHttpConsts;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.live.interfaces.LiveFunctionClickListener;
import com.qgnix.live.interfaces.LivePushListener;
import com.qgnix.live.interfaces.OnVibratorClickListener;
import com.qgnix.live.lottery.entry.TicketData;
import com.qgnix.live.music.LiveMusicDialogFragment;
import com.qgnix.live.presenter.LiveLinkMicAnchorPresenter;
import com.qgnix.live.presenter.LiveLinkMicPkPresenter;
import com.qgnix.live.presenter.LiveLinkMicPresenter;
import com.qgnix.live.socket.GameActionListenerImpl;
import com.qgnix.live.socket.SocketClient;
import com.qgnix.live.views.AbsLivePushViewHolder;
import com.qgnix.live.views.LiveAnchorViewHolder;
import com.qgnix.live.views.LiveEndViewHolder;
import com.qgnix.live.views.LiveMusicViewHolder;
import com.qgnix.live.views.LivePushTxViewHolder;
import com.qgnix.live.views.LiveReadyViewHolder;
import com.qgnix.live.views.LiveRoomViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by cxf on 2018/10/7.
 * ???????????????
 */

public class LiveAnchorActivity extends LiveActivity implements LiveFunctionClickListener, FURenderer.OnFUDebugListener,
        FURenderer.OnTrackingStatusChangedListener {

    private static final String TAG = "LiveAnchorActivity";

    public static void forward(Context context, int liveSdk, LiveKsyConfigBean bean) {
        Intent intent = new Intent(context, LiveAnchorActivity.class);
        intent.putExtra(Constants.LIVE_SDK, liveSdk);
        intent.putExtra(Constants.LIVE_KSY_CONFIG, bean);
        context.startActivity(intent);
    }

    private ViewGroup mRoot;
    private ViewGroup mContainerWrap;
    private AbsLivePushViewHolder mLivePushViewHolder;
    private LiveReadyViewHolder mLiveReadyViewHolder;
    private BeautyViewHolder mLiveBeautyViewHolder;
    private LiveAnchorViewHolder mLiveAnchorViewHolder;
    private LiveMusicViewHolder mLiveMusicViewHolder;
    private boolean mStartPreview;//??????????????????
    private boolean mStartLive;//?????????????????????
    private List<Integer> mGameList;//????????????
    private boolean mBgmPlaying;//???????????????????????????
    private LiveKsyConfigBean mLiveKsyConfigBean;

    private FURenderer mFURenderer;
    private boolean mFrontCamera = true;


    protected int mFrontCameraOrientation;
    /**
     * ????????????ID
     */
    private String mToyId;
    /**
     * ??????
     */
    private VibratorDialog mVibratorDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_anchor;
    }

    @Override
    protected void main() {
        //????????????????????????token
        Lovense.getInstance(getApplication()).setDeveloperToken("hgSnK1y5y1mw83VunmFjSqrF6AV2mjNDMygLmdS5OvzDtY4i2p0K5N/sys0/Ay3L");
        super.main();


        mFrontCameraOrientation = CameraUtils.getFrontCameraOrientation();
        Intent intent = getIntent();
        mLiveSDK = intent.getIntExtra(Constants.LIVE_SDK, Constants.LIVE_SDK_KSY);
        mLiveKsyConfigBean = intent.getParcelableExtra(Constants.LIVE_KSY_CONFIG);
        L.e(TAG, "??????sdk----->" + (mLiveSDK == Constants.LIVE_SDK_KSY ? "?????????" : "?????????"));
        mRoot = findViewById(R.id.root);
        mSocketUserType = Constants.SOCKET_USER_TYPE_ANCHOR;
        CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean u) {
                mLiveUid = u.getId();
                mLiveBean = new LiveBean();
                mLiveBean.setUid(mLiveUid);
                mLiveBean.setUserNiceName(u.getUserNiceName());
                mLiveBean.setAvatar(u.getAvatar());
                mLiveBean.setAvatarThumb(u.getAvatarThumb());
                mLiveBean.setLevelAnchor(u.getLevelAnchor());
                mLiveBean.setGoodNum(u.getGoodName());
                mLiveBean.setCity(u.getCity());
                initFuRenderer();

                //????????????????????????
                if (mLiveSDK == Constants.LIVE_SDK_TX) {
                    initFuRenderer();
                    mLivePushViewHolder = new LivePushTxViewHolder(mContext, findViewById(R.id.preview_container), mFURenderer);
                }
                mLivePushViewHolder.setLivePushListener(new LivePushListener() {
                    @Override
                    public void onPreviewStart() {
                        //??????????????????
                        mStartPreview = true;
                    }

                    @Override
                    public void onPushStart() {
                        //??????????????????
                        LiveHttpUtil.changeLive(mStream);
                    }

                    @Override
                    public void onPushFailed() {
                        //??????????????????
                        ToastUtil.show(R.string.live_push_failed);
                    }
                });
                mLivePushViewHolder.addToParent();
                mLivePushViewHolder.subscribeActivityLifeCycle();
                mContainerWrap = findViewById(R.id.container_wrap);
                mContainer = findViewById(R.id.container);
                //???????????????????????????
                mLiveReadyViewHolder = new LiveReadyViewHolder(mContext, mContainer);
                mLiveReadyViewHolder.addToParent();
                mLiveReadyViewHolder.subscribeActivityLifeCycle();
                mLiveLinkMicPresenter = new LiveLinkMicPresenter(mContext, mLivePushViewHolder, true, mLiveSDK, mContainer);
                mLiveLinkMicPresenter.setLiveUid(mLiveUid);
                mLiveLinkMicAnchorPresenter = new LiveLinkMicAnchorPresenter(mContext, mLivePushViewHolder, true, mLiveSDK, mContainer);
                mLiveLinkMicPkPresenter = new LiveLinkMicPkPresenter(mContext, mLivePushViewHolder, true, mContainer);
            }
        });
    }

    private void initFuRenderer() {
        if (mFURenderer == null) {
            mFURenderer = new FURenderer
                    .Builder(this)
                    .maxFaces(4)
                    .inputImageOrientation(mFrontCameraOrientation)
                    .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                    .setOnFUDebugListener(this)
                    .setOnTrackingStatusChangedListener(this)
                    .build();
        }
    }

    public boolean isStartPreview() {
        return mStartPreview;
    }


    /**
     * ???????????????????????????????????????
     *
     * @param functionID
     */
    @Override
    public void onClick(int functionID) {
        switch (functionID) {
            case Constants.LIVE_FUNC_BEAUTY://??????
                beauty();
                break;
            case Constants.LIVE_FUNC_CAMERA://????????????
                toggleCamera();
                break;
            case Constants.LIVE_FUNC_FLASH://???????????????
                toggleFlash();
                break;
            case Constants.LIVE_FUNC_MUSIC://??????
                openMusicWindow();
                break;
            case Constants.LIVE_FUNC_SHARE://??????
                openShareWindow();
                break;
            case Constants.LIVE_FUNC_GAME://??????
                //todo ????????? ???bug ?????????????????????
                if (1 == 1) {
                    ToastUtil.show(R.string.in_developing);
                    break;
                } else {
                    openGameWindow();
                    break;
                }
            case Constants.LIVE_FUNC_RED_PACK://??????
                openRedPackSendWindow();
                break;
            case Constants.LIVE_FUNC_LINK_MIC://??????
                openLinkMicAnchorWindow();
                break;
            case Constants.LIVE_FUNC_VIBRATING_EGG://??????
                scanToys();
                break;
            default:
                break;
        }
    }

    //?????????????????????
    public void beforeCamera() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.setOpenCamera(true);
        }
    }


    /**
     * ????????????
     */
    public void toggleCamera() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.toggleCamera();
        }
        if (mFURenderer != null) {
            mFrontCamera = !mFrontCamera;
            mFURenderer.onCameraChange(mFrontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT :
                    Camera.CameraInfo.CAMERA_FACING_BACK, 0);
        }
    }

    /**
     * ???????????????
     */
    public void toggleFlash() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.toggleFlash();
        }
    }

    /**
     * ????????????
     */
    public void beauty() {
        if (mLiveBeautyViewHolder == null) {
            initFuRenderer();
            mLiveBeautyViewHolder = new FUViewHolder(mContext, mContainer, mFURenderer);

            mLiveBeautyViewHolder.setVisibleListener(new BeautyViewHolder.VisibleListener() {
                @Override
                public void onVisibleChanged(boolean visible) {
                    if (mLiveReadyViewHolder != null) {
                        if (visible) {
                            mLiveReadyViewHolder.hide();
                        } else {
                            mLiveReadyViewHolder.show();
                        }
                    }
                }
            });
            if (mLivePushViewHolder != null) {
                mLiveBeautyViewHolder.setEffectListener(mLivePushViewHolder.getEffectListener());
            }
        }
        mLiveBeautyViewHolder.show();
    }

    /**
     * ??????????????????
     */
    private void openMusicWindow() {
        if (isLinkMicAnchor() || isLinkMicAnchor()) {
            ToastUtil.show(R.string.link_mic_not_bgm);
            return;
        }
        LiveMusicDialogFragment fragment = new LiveMusicDialogFragment();
        fragment.setActionListener(new LiveMusicDialogFragment.ActionListener() {
            @Override
            public void onChoose(String musicId) {
                if (mLivePushViewHolder != null) {
                    if (mLiveMusicViewHolder == null) {
                        mLiveMusicViewHolder = new LiveMusicViewHolder(mContext, mContainer, mLivePushViewHolder);
                        mLiveMusicViewHolder.subscribeActivityLifeCycle();
                        mLiveMusicViewHolder.addToParent();
                    }
                    mLiveMusicViewHolder.play(musicId);
                    mBgmPlaying = true;
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "LiveMusicDialogFragment");
    }

    /**
     * ??????????????????
     */
    public void stopBgm() {
        if (mLiveMusicViewHolder != null) {
            mLiveMusicViewHolder.release();
        }
        mLiveMusicViewHolder = null;
        mBgmPlaying = false;
    }

    public boolean isBgmPlaying() {
        return mBgmPlaying;
    }

    /**
     * ??????????????????
     */
    public void showFunctionDialog() {
        LiveFunctionDialogFragment fragment = new LiveFunctionDialogFragment();
        Bundle bundle = new Bundle();
        boolean hasGame = false;
        if (CommonAppConfig.GAME_ENABLE && mGameList != null) {
            hasGame = mGameList.size() > 0;
        }
        bundle.putBoolean(Constants.HAS_GAME, hasGame);
        fragment.setArguments(bundle);
        fragment.setFunctionClickListener(this);
        fragment.show(getSupportFragmentManager(), "LiveFunctionDialogFragment");
    }

    /**
     * ????????????????????????
     */
    private void openLinkMicAnchorWindow() {
        if (mLiveLinkMicAnchorPresenter != null && !mLiveLinkMicAnchorPresenter.canOpenLinkMicAnchor()) {
            return;
        }
        LiveLinkMicListDialogFragment fragment = new LiveLinkMicListDialogFragment();
        fragment.show(getSupportFragmentManager(), "LiveLinkMicListDialogFragment");
    }


    /**
     * ????????????????????????
     */
    private void openGameWindow() {
        if (isLinkMic() || isLinkMicAnchor()) {
            ToastUtil.show(R.string.live_link_mic_cannot_game);
            return;
        }
        if (mGamePresenter != null) {
            GameDialogFragment fragment = new GameDialogFragment();
            fragment.setGamePresenter(mGamePresenter);
            fragment.show(getSupportFragmentManager(), "GameDialogFragment");
        }
    }

    /**
     * ????????????
     */
    public void closeGame() {
        if (mGamePresenter != null) {
            mGamePresenter.closeGame();
        }
    }

    /**
     * ????????????
     *
     * @param data createRoom???????????????
     */
    public void startLiveSuccess(String data, int liveType, int liveTypeVal, TicketData ticketData) {
        mTicketId = ticketData.getId();
        mLiveType = liveType;
        mLiveTypeVal = liveTypeVal;
        //??????createRoom???????????????
        JSONObject obj = JSON.parseObject(data);
        mStream = obj.getString("stream");
        mDanmuPrice = obj.getString("barrage_fee");
        String playUrl = obj.getString("pull");
        L.e("createRoom----????????????--->" + playUrl);
        mLiveBean.setPull(playUrl);
        mLiveBean.setTicket_tag(ticketData);
        mTxAppId = obj.getString("tx_appid");
        //??????????????????????????????????????????????????????
        if (mLiveReadyViewHolder != null) {
            mLiveReadyViewHolder.removeFromParent();
            mLiveReadyViewHolder.release();
        }
        mLiveReadyViewHolder = null;
        if (mLiveRoomViewHolder == null) {
            mLiveRoomViewHolder = new LiveRoomViewHolder(mContext, mContainer, findViewById(R.id.gift_gif), findViewById(R.id.gift_svga), mContainerWrap);
            mLiveRoomViewHolder.addToParent();
            mLiveRoomViewHolder.subscribeActivityLifeCycle();
            mLiveRoomViewHolder.setLiveInfo(mLiveUid, mStream, obj.getIntValue("userlist_time") * 1000, mLiveBean);
            mLiveRoomViewHolder.setVotes(obj.getString("votestotal"));
            CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
                @Override
                public void callback(UserBean u) {
                    if (u != null) {
                        mLiveRoomViewHolder.setRoomNumStr(u.getLiangNameTip());
                        mLiveRoomViewHolder.setName(u.getUserNiceName());
                        mLiveRoomViewHolder.setAvatar(u.getAvatar());
                        mLiveRoomViewHolder.setAnchorLevel(u.getLevelAnchor());
                    }
                }
            });
        }
        if (mLiveAnchorViewHolder == null) {
            mLiveAnchorViewHolder = new LiveAnchorViewHolder(mContext, mContainer);
            mLiveAnchorViewHolder.addToParent();
            mLiveAnchorViewHolder.setUnReadCount(((LiveActivity) mContext).getImUnReadCount());
        }
        //??????socket
        if (mSocketClient == null) {
            mSocketClient = new SocketClient(LiveAnchorActivity.this);
            if (mLiveLinkMicPresenter != null) {
                mLiveLinkMicPresenter.setSocketClient(mSocketClient);
            }
            if (mLiveLinkMicAnchorPresenter != null) {
                mLiveLinkMicAnchorPresenter.setSocketClient(mSocketClient);
                mLiveLinkMicAnchorPresenter.setPlayUrl(playUrl);
                mLiveLinkMicAnchorPresenter.setSelfStream(mStream);
            }
            if (mLiveLinkMicPkPresenter != null) {
                mLiveLinkMicPkPresenter.setSocketClient(mSocketClient);
                mLiveLinkMicPkPresenter.setLiveUid(mLiveUid);
                mLiveLinkMicPkPresenter.setSelfStream(mStream);
            }
        }
        mSocketClient.connect(mLiveUid, mStream, mTicketId);

        //????????????
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.startPush(obj.getString("push"));
        }
        //????????????????????????
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.startAnchorLiveTime();
        }
        mStartLive = true;
        // ??????????????????????????????
        //  mLiveRoomViewHolder.startRefreshUserList();

        //????????????
        mLiveGuardInfo = new LiveGuardInfo();
        int guardNum = obj.getIntValue("guard_nums");
        mLiveGuardInfo.setGuardNum(guardNum);
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setGuardNum(guardNum);
        }

        //????????????
        int giftPrizePoolLevel = obj.getIntValue("jackpot_level");
        if (giftPrizePoolLevel >= 0) {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.showPrizePoolLevel(String.valueOf(giftPrizePoolLevel));
            }
        }

        //????????????
        if (CommonAppConfig.GAME_ENABLE) {
            mGameList = JSON.parseArray(obj.getString("game_switch"), Integer.class);
            GameParam param = new GameParam();
            param.setContext(mContext);
            param.setParentView(mContainerWrap);
            param.setTopView(mContainer);
            param.setInnerContainer(mLiveRoomViewHolder.getInnerContainer());
            param.setGameActionListener(new GameActionListenerImpl(LiveAnchorActivity.this, mSocketClient));
            param.setLiveUid(mLiveUid);
            param.setStream(mStream);
            param.setAnchor(true);
            param.setCoinName(mCoinName);
            param.setObj(obj);
            mGamePresenter = new GamePresenter(param);
            mGamePresenter.setGameList(mGameList);
        }
    }

    /**
     * ????????????
     */
    public void closeLive() {
        DialogUtil.showSimpleDialog(mContext, WordUtil.getString(R.string.live_end_live), new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                endLive();
            }
        });
    }

    /**
     * ????????????
     */
    public void endLive() {
        //??????socket
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        //?????????????????????
        LiveHttpUtil.stopLive(mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mLiveEndViewHolder == null) {
                        mLiveEndViewHolder = new LiveEndViewHolder(mContext, mRoot);
                        mLiveEndViewHolder.subscribeActivityLifeCycle();
                        mLiveEndViewHolder.addToParent();
                        mLiveEndViewHolder.showData(mLiveBean, mStream);
                    }
                    release();
                    mStartLive = false;
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUtil.loadingDialog(mContext, WordUtil.getString(R.string.live_end_ing));
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mLiveBeautyViewHolder != null && mLiveBeautyViewHolder.isShowed()) {
            mLiveBeautyViewHolder.hide();
            return;
        }
        if (mStartLive) {
            if (!canBackPressed()) {
                return;
            }
            closeLive();
        } else {
            if (mLivePushViewHolder != null) {
                mLivePushViewHolder.release();
            }
            if (mLiveLinkMicPresenter != null) {
                mLiveLinkMicPresenter.release();
            }
            mLivePushViewHolder = null;
            mLiveLinkMicPresenter = null;
            superBackPressed();
        }
    }

    public void superBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void release() {
        if(mFURenderer!=null){
            mFURenderer.onSurfaceDestroyed();
        }
        LiveHttpUtil.cancel(LiveHttpConsts.CHANGE_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.STOP_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.LIVE_PK_CHECK_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.SET_LINK_MIC_ENABLE);
        if (mLiveReadyViewHolder != null) {
            mLiveReadyViewHolder.release();
        }
        if (mLiveMusicViewHolder != null) {
            mLiveMusicViewHolder.release();
        }
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.release();
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.release();
        }
        if (mLiveBeautyViewHolder != null) {
            mLiveBeautyViewHolder.release();
        }
        if (mGamePresenter != null) {
            mGamePresenter.release();
        }
        if (null != mVibratorDialog && mVibratorDialog.isShowing()) {
            mVibratorDialog.dismiss();
            mVibratorDialog = null;
        }

        mLiveMusicViewHolder = null;
        mLiveReadyViewHolder = null;
        mLivePushViewHolder = null;
        mLiveLinkMicPresenter = null;
        mLiveBeautyViewHolder = null;
        mGamePresenter = null;
        super.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.e("LiveAnchorActivity-------onDestroy------->");
        ((AudioManager) this.getSystemService(Context.AUDIO_SERVICE)).setMicrophoneMute(false);
    }


    @Override
    protected void onPause() {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.anchorPause();
        }
        super.onPause();
        sendSystemMessage(WordUtil.getString(R.string.live_anchor_leave));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.anchorResume();
        }
        sendSystemMessage(WordUtil.getString(R.string.live_anchor_come_back));
    }

    /**
     * ?????????????????????
     */
    @Override
    public void onSuperCloseLive() {
        endLive();
        DialogUtil.showSimpleTipDialog(mContext, WordUtil.getString(R.string.live_illegal));
    }

    /**
     * ?????????????????????
     */
    @Override
    public void onSuperCloseLive(String msg) {
        endLive();
        DialogUtil.showSimpleTipDialog(mContext, msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginInvalidEvent(LoginInvalidEvent e) {
        release();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameWindowChangedEvent(GameWindowChangedEvent e) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setOffsetY(e.getGameViewHeight());
        }
        if (mLiveAnchorViewHolder != null) {
            mLiveAnchorViewHolder.setGameBtnVisible(e.isOpen());
        }
    }

    public void setBtnFunctionDark() {
        if (mLiveAnchorViewHolder != null) {
            mLiveAnchorViewHolder.setBtnFunctionDark();
        }
    }

    /**
     * ?????????????????????  ????????????????????????????????????????????????
     */
    @Override
    public void onLinkMicAnchorApply(UserBean u, String stream) {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorApply(u, stream);
        }
    }

    /**
     * ?????????????????????  ?????????????????????????????????
     */
    @Override
    public void onLinkMicAnchorRefuse() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorRefuse();
        }
    }

    /**
     * ?????????????????????  ??????????????????????????????
     */
    @Override
    public void onLinkMicAnchorNotResponse() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicNotResponse();
        }
    }

    /**
     * ?????????????????????  ????????????????????????
     */
    @Override
    public void onlinkMicPlayGaming() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onlinkMicPlayGaming();
        }
    }


    /**
     * ?????????????????????  ??????????????????????????????
     */
    @Override
    public void onLinkMicAnchorBusy() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorBusy();
        }
    }

    /**
     * ????????????????????????
     *
     * @param pkUid  ???????????????uid
     * @param stream ???????????????stream
     */
    public void linkMicAnchorApply(final String pkUid, String stream) {
        LiveHttpUtil.livePkCheckLive(pkUid, stream, mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mLiveSDK == Constants.LIVE_SDK_TX) {
                        String playUrl = null;
                        JSONObject obj = JSON.parseObject(info[0]);
                        if (obj != null) {
                            String accUrl = obj.getString("pull");//???????????????????????????
                            if (!TextUtils.isEmpty(accUrl)) {
                                playUrl = accUrl;
                            }
                        }
                        if (mLiveLinkMicAnchorPresenter != null) {
                            mLiveLinkMicAnchorPresenter.applyLinkMicAnchor(pkUid, playUrl, mStream);
                        }
                    } else {
                        if (mLiveLinkMicAnchorPresenter != null) {
                            mLiveLinkMicAnchorPresenter.applyLinkMicAnchor(pkUid, null, mStream);
                        }
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * ????????????pk??????????????????
     */
    public void setPkBtnVisible(boolean visible) {
        if (mLiveAnchorViewHolder != null) {
            if (visible) {
                if (mLiveLinkMicAnchorPresenter.isLinkMic()) {
                    mLiveAnchorViewHolder.setPkBtnVisible(true);
                }
            } else {
                mLiveAnchorViewHolder.setPkBtnVisible(false);
            }
        }
    }

    /**
     * ??????????????????pk
     */
    public void applyLinkMicPk() {
        String pkUid = null;
        if (mLiveLinkMicAnchorPresenter != null) {
            pkUid = mLiveLinkMicAnchorPresenter.getPkUid();
        }
        if (!TextUtils.isEmpty(pkUid) && mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.applyLinkMicPk(pkUid, mStream);
        }
    }

    /**
     * ???????????????PK  ????????????????????????????????????PK???????????????
     *
     * @param u      ?????????????????????
     * @param stream ???????????????stream
     */
    @Override
    public void onLinkMicPkApply(UserBean u, String stream) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkApply(u, stream);
        }
    }

    /**
     * ???????????????PK  ??????????????????pk?????????
     */
    @Override
    public void onLinkMicPkRefuse() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkRefuse();
        }
    }

    /**
     * ???????????????PK   ??????????????????????????????
     */
    @Override
    public void onLinkMicPkBusy() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkBusy();
        }
    }

    /**
     * ???????????????PK   ??????????????????????????????
     */
    @Override
    public void onLinkMicPkNotResponse() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkNotResponse();
        }
    }

    /**
     * ??????sdk????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLinkMicTxMixStreamEvent(LinkMicTxMixStreamEvent e) {
        if (mLivePushViewHolder != null && mLivePushViewHolder instanceof LivePushTxViewHolder) {
            ((LivePushTxViewHolder) mLivePushViewHolder).onLinkMicTxMixStreamEvent(e.getType(), e.getToStream());
        }
    }

    @Override
    public void onTrackingStatusChanged(int status) {

    }

    @Override
    public void onFpsChange(double fps, double renderTime) {

    }

    /**
     * ??????????????????
     */
    private void scanToys() {
        requestBlueToothPermission(new Runnable() {
            @Override
            public void run() {
                mVibratorDialog = new VibratorDialog(mContext, getApplication());
                mVibratorDialog.setOnVibratorClickListener(new OnVibratorClickListener() {
                    @Override
                    public void onVibratorClick(LovenseToy toy) {
                        connectToy(toy.getToyId());
                    }
                });
                mVibratorDialog.show();
            }
        });

    }

    /**
     * ????????????
     *
     * @param toyId ??????ID
     */
    private void connectToy(String toyId) {
        this.mToyId = toyId;
        if (Lovense.getInstance(getApplication()).isConnected(toyId)) {
            //?????????
            Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_GET_DEVICE_TYPE);
            Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_GET_BATTERY);
        } else {
            //?????????--?????????
            OnConnectListener onConnectListener = new OnConnectListener() {
                @Override
                public void onConnect(String toyId, String status) {
                    switch (status) {
                        case LovenseToy.STATE_CONNECTING:

                            break;
                        case LovenseToy.STATE_CONNECTED:
                            ToastUtil.show(R.string.connection_succeeded);
                            if (null != mVibratorDialog) {
                                mVibratorDialog.dismiss();
                            }
                            break;
                        case LovenseToy.STATE_FAILED:
                            ToastUtil.show(R.string.connection_failed);
                            break;
                        case LovenseToy.SERVICE_DISCOVERED:
                            Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_GET_DEVICE_TYPE);
                            Lovense.getInstance(getApplication()).sendCommand(toyId, LovenseToy.COMMAND_GET_BATTERY);
                            break;
                    }

                }

                @Override
                public void onError(LovenseError lovenseError) {

                }
            };
            Lovense.getInstance(getApplication()).connectToy(toyId, onConnectListener);
        }
    }

    // ?????????????????????????????????
    @Override
    public void onSendGift(LiveReceiveGiftBean bean) {
        super.onSendGift(bean);
        if (!TextUtils.isEmpty(mToyId)) {
            Lovense.getInstance(getApplication()).sendCommand(mToyId, LovenseToy.COMMAND_LIGHT_ON);
            // Lovense.getInstance(getApplication()).sendCommand(mToyId, LovenseToy.COMMAND_PRESET,5);
            Lovense.getInstance(getApplication()).sendCommand(mToyId, LovenseToy.COMMAND_VIBRATE, 5);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Lovense.getInstance(getApplication()).sendCommand(mToyId, LovenseToy.COMMAND_LIGHT_OFF);
                //  Lovense.getInstance(getApplication()).sendCommand(mToyId, LovenseToy.COMMAND_PRESET,0);
                Lovense.getInstance(getApplication()).sendCommand(mToyId, LovenseToy.COMMAND_VIBRATE, 0);
            }
        }
    }
}
