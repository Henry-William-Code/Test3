package com.qgnix.live.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.CommonAppContext;
import com.qgnix.common.Constants;
import com.qgnix.common.custom.MyViewPager;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.DialogUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.RouteUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.game.bean.GameParam;
import com.qgnix.game.event.GameWindowChangedEvent;
import com.qgnix.game.util.GamePresenter;
import com.qgnix.live.R;
import com.qgnix.live.adapter.LiveRoomScrollAdapter;
import com.qgnix.live.bean.LiveBean;
import com.qgnix.live.bean.LiveGuardInfo;
import com.qgnix.live.custom.CustomVideoPlay;
import com.qgnix.live.dialog.LiveGiftDialogFragment;
import com.qgnix.live.dialog.ShareDialog;
import com.qgnix.live.event.FullScreenEvent;
import com.qgnix.live.event.LinkMicTxAccEvent;
import com.qgnix.live.event.VideoActionEvent;
import com.qgnix.live.http.LiveHttpConsts;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.live.interfaces.OnPlayVideoCallback;
import com.qgnix.live.lottery.entry.TicketData;
import com.qgnix.live.presenter.LiveLinkMicAnchorPresenter;
import com.qgnix.live.presenter.LiveLinkMicPkPresenter;
import com.qgnix.live.presenter.LiveLinkMicPresenter;
import com.qgnix.live.presenter.LiveRoomCheckLivePresenter;
import com.qgnix.live.socket.GameActionListenerImpl;
import com.qgnix.live.socket.SocketClient;
import com.qgnix.live.utils.LiveStorge;
import com.qgnix.live.view.ScrollLinearLayoutManager;
import com.qgnix.live.views.LiveAudienceViewHolder;
import com.qgnix.live.views.LivePlayTxViewHolder;
import com.qgnix.live.views.LiveRoomViewHolder;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;


/**
 * Created by cxf on 2018/10/10.
 * <p>
 * 观众
 */

public class LiveAudienceActivity extends LiveActivity {

    private static final String TAG = "LiveAudienceActivity";
    private static List<LiveBean> videoList;
    private long lastTime = 0;
    private HashMap<String, String> h5UrlMap = new HashMap<>();

    public static void forward(Context context, LiveBean liveBean, int liveType, int liveTypeVal, String key, int position, int liveSdk) {
        Intent intent = new Intent(context, LiveAudienceActivity.class);
        intent.putExtra(Constants.LIVE_BEAN, liveBean);
        intent.putExtra(Constants.LIVE_TYPE, liveType);
        intent.putExtra(Constants.LIVE_TYPE_VAL, liveTypeVal);
        intent.putExtra(Constants.LIVE_KEY, key);
        intent.putExtra(Constants.LIVE_POSITION, position);
        intent.putExtra(Constants.LIVE_SDK, liveSdk);
        intent.putExtra(Constants.VIDEO_PATH, liveBean.getPull());
        context.startActivity(intent);
    }

    public static void forward(Context context, LiveBean liveBean, int liveType, int liveTypeVal, String key, int position, int liveSdk, List<LiveBean> videoList) {
        Intent intent = new Intent(context, LiveAudienceActivity.class);
        intent.putExtra(Constants.LIVE_BEAN, liveBean);
        intent.putExtra(Constants.LIVE_TYPE, liveType);
        intent.putExtra(Constants.LIVE_TYPE_VAL, liveTypeVal);
        intent.putExtra(Constants.LIVE_KEY, key);
        intent.putExtra(Constants.LIVE_POSITION, position);
        intent.putExtra(Constants.LIVE_SDK, liveSdk);
        intent.putExtra(Constants.VIDEO_PATH, liveBean.getPull());
        LiveAudienceActivity.videoList = videoList;
        context.startActivity(intent);
    }

    private String mKey;
    private int mPosition;
    private RecyclerView mRecyclerView;
    private LiveRoomScrollAdapter mRoomScrollAdapter;
    private View mMainContentView;
    private MyViewPager mViewPager;
    private ViewGroup mSecondPage;//默认显示第二页
    private FrameLayout mContainerWrap;
    /**
     * 直播视频播放
     */
    private LivePlayTxViewHolder mLivePlayViewHolder;
    /**
     * 是否是视频
     */
    private boolean mIsVideo;

    private LiveAudienceViewHolder mLiveAudienceViewHolder;
    private boolean mEnd;
    private boolean mCoinNotEnough;//余额不足
    private LiveRoomCheckLivePresenter mCheckLivePresenter;
    /**
     * 断开连接/下线
     */
    private LinearLayout mLlDisconnect;

    @Override
    public <T extends View> T findViewById(@IdRes int id) {
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            if (mMainContentView != null) {
                return mMainContentView.findViewById(id);
            }
        }
        return super.findViewById(id);
    }

    @Override
    protected int getLayoutId() {
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            return R.layout.activity_live_audience_2;
        }
        return R.layout.activity_live_audience;
    }

    public void setScrollFrozen(boolean frozen) {
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutFrozen(frozen);
        }
    }

    @Override
    protected void main() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            mRecyclerView = super.findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            ScrollLinearLayoutManager scrollLinearLayoutManager = new ScrollLinearLayoutManager(mContext, OrientationHelper.VERTICAL);
            mRecyclerView.setLayoutManager(scrollLinearLayoutManager);
            mMainContentView = LayoutInflater.from(mContext).inflate(R.layout.activity_live_audience, null, false);
//            rvScrollListener(scrollLinearLayoutManager);


            mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    float x = e.getX();
                    float y = e.getY();
                    if (e != null) {  // 外层RV滑动与内层布局滑动冲突解决
                        View itemView = rv.findChildViewUnder(x, y);// 找到被点击位置的item的rootView
                        if (itemView != null) {
//                            LiveRoomScrollAdapter.Vh holder = (LiveRoomScrollAdapter.Vh) rv.getChildViewHolder(itemView);
                            RecyclerView childRv = itemView.findViewById(R.id.chat_recyclerView);// 通过rootView找到对应的ViewHolder
                            if (null != childRv) {
                                int outLocation[] = new int[2];
                                childRv.getLocationOnScreen(outLocation);
                                int left = outLocation[0];
                                int top = outLocation[1];
                                int w = childRv.getWidth();
                                int h = childRv.getHeight();
                                int right = left + w;
                                int bottom = top + h;
                                if (x > left && x < right && y > top && y < bottom) {
                                    ((ViewGroup) itemView).requestDisallowInterceptTouchEvent(true);
                                }
                            }
                        }
                    }
                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                }
            });
        }
        super.main();
        Intent intent = getIntent();
        mLiveSDK = intent.getIntExtra(Constants.LIVE_SDK, Constants.LIVE_SDK_KSY);
        L.e(TAG, "直播sdk----->" + (mLiveSDK == Constants.LIVE_SDK_KSY ? "金山云" : "腾讯云"));
        mLiveType = intent.getIntExtra(Constants.LIVE_TYPE, Constants.LIVE_TYPE_NORMAL);
        mLiveTypeVal = intent.getIntExtra(Constants.LIVE_TYPE_VAL, 0);
        LiveBean liveBean = intent.getParcelableExtra(Constants.LIVE_BEAN);
        TicketData ticketTag = liveBean.getTicket_tag();
        if (null != ticketTag) {
            mTicketId = ticketTag.getId();
            CommonAppContext.map.put(ticketTag.getId(), ticketTag.getType());
        }
        mIsVideo = liveBean.getIsvideo() == 1;
        //腾讯视频播放器
        ViewGroup playContainer = findViewById(R.id.play_container);
        mLivePlayViewHolder = new LivePlayTxViewHolder(mContext, playContainer);
        mLivePlayViewHolder.addToParent();
        mLivePlayViewHolder.subscribeActivityLifeCycle();
        mViewPager = findViewById(R.id.viewPager);
        mSecondPage = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.view_audience_page, mViewPager, false);
        mContainerWrap = mSecondPage.findViewById(R.id.container_wrap);
        mContainer = mSecondPage.findViewById(R.id.container);

        mLiveAudienceViewHolder = new LiveAudienceViewHolder(mContext, mContainer, (mIsVideo && liveBean.getAnyway() == 0), mIsVideo);
        mLiveAudienceViewHolder.addToParent();
        mLiveAudienceViewHolder.setUnReadCount(getImUnReadCount());
        if (mIsVideo) {
            //视频viewpage 不可滑动
            mViewPager.setCanScroll(!mIsVideo);
            mLiveAudienceViewHolder.setOnPlayVideoCallback(new OnPlayVideoCallback() {
                @Override
                public void onPlayVideoSmallBack() {
                    exitLiveRoom();
                }
            });
        }

        mLiveRoomViewHolder = new LiveRoomViewHolder(mContext, mContainer, mSecondPage.findViewById(R.id.gift_gif), mSecondPage.findViewById(R.id.gift_svga), mContainerWrap);
        mLiveRoomViewHolder.addToParent();
        mLiveRoomViewHolder.subscribeActivityLifeCycle();


        mViewPager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }


            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                LiveBean bean = videoList.get(mPosition);
                //1 视频 非1不是视频
                if (bean.getIsvideo() == 1) {
                    mViewPager.setCanScroll(false);
                    mViewPager.setCurrentItem(1);
                }
                if (position == 0) {
                    View view = new View(mContext);
                    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    container.addView(view);
                    return view;
                } else {
                    container.addView(mSecondPage);
                    return mSecondPage;
                }
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
            }
        });
        mViewPager.setCurrentItem(1);
        if (null != mLivePlayViewHolder) {
            mLiveLinkMicPresenter = new LiveLinkMicPresenter(mContext, mLivePlayViewHolder, false, mLiveSDK, mLiveAudienceViewHolder.getContentView());
            mLiveLinkMicAnchorPresenter = new LiveLinkMicAnchorPresenter(mContext, mLivePlayViewHolder, false, mLiveSDK, null);
            mLiveLinkMicPkPresenter = new LiveLinkMicPkPresenter(mContext, mLivePlayViewHolder, false, null);
        }
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            mKey = intent.getStringExtra(Constants.LIVE_KEY);
            mPosition = intent.getIntExtra(Constants.LIVE_POSITION, 0);
            ViewGroup viewGroup = mLiveRoomViewHolder.getmParentView();
            View translate = viewGroup.getChildAt(1).findViewById(R.id.sw_translate);
            List<LiveBean> list = LiveStorge.getInstance().get(mKey);
            list = list == null ? LiveAudienceActivity.videoList : list;
            mRoomScrollAdapter = new LiveRoomScrollAdapter(mContext, list, mPosition);
            mRoomScrollAdapter.setActionListener(new LiveRoomScrollAdapter.ActionListener() {
                @Override
                public void onPageSelected(LiveBean liveBean, ViewGroup container, boolean first) {
                    L.e(TAG, "onPageSelected----->" + liveBean);
                    CustomVideoPlay.isFirst = true;
                    boolean isVideo = liveBean.getIsvideo() == 1 ? true : false;
                    //视频和直播状态显示和隐藏麦克风
                    mLiveAudienceViewHolder.setMicState(isVideo || !"1".equals(liveBean.getIsmic()));
                    if (isVideo) {
                        //视频viewpage 不可滑动
                        mViewPager.setCanScroll(false);
                        mViewPager.setCurrentItem(1);
                        translate.setVisibility(View.INVISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (null != mLiveAudienceViewHolder) {
                                    View layoutBottom = mLiveAudienceViewHolder.getmParentView().getChildAt(0).findViewById(R.id.layout_bottom);
                                    if (layoutBottom.getVisibility() == View.INVISIBLE) {
                                        translate.setVisibility(View.VISIBLE);
                                    }
                                }

                            }
                        }, 3000);

                    } else {
                        mLiveAudienceViewHolder.setIsVideoVisible(View.GONE);
                        mViewPager.setCanScroll(true);
                    }
                    if (mMainContentView != null && container != null) {
                        ViewParent parent = mMainContentView.getParent();
                        if (parent != null) {
                            ViewGroup viewGroup = (ViewGroup) parent;
                            if (viewGroup != container) {
                                viewGroup.removeView(mMainContentView);
                                container.addView(mMainContentView);
                            }
                        } else {
                            container.addView(mMainContentView);
                        }
                    }
                    if (!first) {
                        mViewPager.setVisibility(View.GONE);
                        checkLive(liveBean);
                    }
                }

                @Override
                public void onPageOutWindow(String liveUid) {
                    L.e(TAG, "onPageOutWindow----->" + liveUid);
                    if (TextUtils.isEmpty(mLiveUid) || mLiveUid.equals(liveUid)) {
                        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
                        LiveHttpUtil.cancel(LiveHttpConsts.ENTER_ROOM);
                        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
                        clearRoomData();
                    }
                }
            });
            mRecyclerView.setAdapter(mRoomScrollAdapter);
        }

        setLiveRoomData(liveBean);
        enterRoom();

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLlDisconnect = findViewById(R.id.ll_disconnect);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fullScreenChange(FullScreenEvent event) {
        ViewGroup viewGroup = mLiveRoomViewHolder.getmParentView();
        //true 横屏 false竖屏
        if (event.isLand()) {
            viewGroup.getChildAt(1).setVisibility(View.GONE);
        } else {
            viewGroup.getChildAt(1).setVisibility(View.VISIBLE);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && null != mLiveAudienceViewHolder) {
            if (mLiveAudienceViewHolder.onPhoneBack()) {
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoAction(VideoActionEvent event) {
        final View layoutBottom;
        Handler actionHandler = null;
        if (null != mLiveAudienceViewHolder) {
            layoutBottom = mLiveAudienceViewHolder.getmParentView().getChildAt(0).findViewById(R.id.layout_bottom);
            ViewGroup viewGroup = mLiveRoomViewHolder.getmParentView();
            View translate = viewGroup.getChildAt(1).findViewById(R.id.sw_translate);
            if (null == actionHandler) {
                actionHandler = new Handler();
            } else {
                actionHandler.removeCallbacks(null);
            }
            //是否播放完成
            if (event.isComplete()) {
                translate.setVisibility(View.INVISIBLE);
            } else {
                translate.setVisibility(View.INVISIBLE);
                actionHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (layoutBottom.getVisibility() == View.INVISIBLE) {
                            translate.setVisibility(View.VISIBLE);
                        }

                    }
                }, 3000);
            }
        }


    }


    private void setLiveRoomData(LiveBean liveBean) {
        mLiveBean = liveBean;
        mLiveUid = liveBean.getUid();
        mStream = liveBean.getStream();
        //获取图层中翻译按钮
        ViewGroup viewGroup = mLiveRoomViewHolder.getmParentView();
        View translate = viewGroup.getChildAt(1).findViewById(R.id.sw_translate);
        //1 视频 非1不是视频
        switch (liveBean.getIsvideo()) {
            case 0:
                mLivePlayViewHolder.setCover(liveBean.getThumb());
                mLiveAudienceViewHolder.stopH5();
                mLivePlayViewHolder.play(liveBean.getPull());
                break;
            case 1:
                //            mLiveAudienceViewHolder.playVideo(liveBean.getPull());
                mLiveAudienceViewHolder.stopH5();
                mLiveAudienceViewHolder.playVideo(liveBean);
                translate.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mLiveAudienceViewHolder) {
                            View layoutBottom = mLiveAudienceViewHolder.getmParentView().getChildAt(0).findViewById(R.id.layout_bottom);
                            if (layoutBottom.getVisibility() == View.INVISIBLE) {
                                translate.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }, 3 * 1000);
                break;
            case 2:
                mLivePlayViewHolder.setCover(liveBean.getThumb());
                GSYVideoManager.releaseAllVideos();
                String channel = liveBean.getPull();
                String url = h5UrlMap.get(channel);
                if (TextUtils.isEmpty(url)) {
                    LiveHttpUtil.getGLiveURL(channel, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (mLiveAudienceViewHolder != null) {
                                if(info!=null && info.length>0){
                                    JSONObject obj = JSON.parseObject(info[0]);
                                    String h5Url = obj.getString("url");
                                    h5UrlMap.put(channel, h5Url);
                                    mLiveAudienceViewHolder.playH5(h5Url);
                                }
                            }
                        }
                    });
                } else {
                    mLiveAudienceViewHolder.playH5(url);
                }
                break;
        }
        /*if (liveBean.getIsvideo() == 1) {
        } else {

            if (liveBean.getIsvideo() == 2) {

            } else {

            }
        }*/
        //设置彩票ID
        mLiveAudienceViewHolder.setTId(mTicketId);
        mLiveAudienceViewHolder.setLiveId(mLiveUid);

        mLiveRoomViewHolder.setAvatar(liveBean.getAvatar());
        mLiveRoomViewHolder.setAnchorLevel(liveBean.getLevelAnchor());
        mLiveRoomViewHolder.setName(liveBean.getUserNiceName());
        mLiveRoomViewHolder.setRoomNum(liveBean.getNums());
        //翻译开关
        mLiveRoomViewHolder.onTranslate(liveBean.getIstranslate());
        if (null != mLiveLinkMicPkPresenter) {
            mLiveLinkMicPkPresenter.setLiveUid(mLiveUid);
        }
        if (null != mLiveLinkMicPresenter) {
            mLiveLinkMicPresenter.setLiveUid(mLiveUid);
        }
    }

    private void clearRoomData() {
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        mSocketClient = null;
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.stopPlay();
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.clearData();
        }
        if (mGamePresenter != null) {
            mGamePresenter.clearGame();
        }

        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.clearData();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.clearData();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.clearData();
        }
    }

    private void checkLive(LiveBean bean) {
        TicketData ticketTag = bean.getTicket_tag();
        if (null != ticketTag) {
            mTicketId = ticketTag.getId();
        }
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new LiveRoomCheckLivePresenter(mContext, new LiveRoomCheckLivePresenter.ActionListener() {
                @Override
                public void onLiveRoomChanged(LiveBean liveBean, int liveType, int liveTypeVal) {
                    if (liveBean == null) {
                        return;
                    }
                    mViewPager.setVisibility(View.VISIBLE);
                    setLiveRoomData(liveBean);
                    mLiveType = liveType;
                    mLiveTypeVal = liveTypeVal;
                    if (mRoomScrollAdapter != null) {
                        mRoomScrollAdapter.hideCover();
                    }
                    enterRoom();
                }
            });
        }
        mCheckLivePresenter.checkLive(bean);
    }


    private void enterRoom() {
        LiveHttpUtil.enterRoom(mLiveUid, mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mDanmuPrice = obj.getString("barrage_fee");
                    mSocketUserType = obj.getIntValue("usertype");
                    mChatLevel = obj.getIntValue("speak_limit");
                    mDanMuLevel = obj.getIntValue("barrage_limit");
                    //连接socket

                    mSocketClient = new SocketClient(LiveAudienceActivity.this);
                    if (mLiveLinkMicPresenter != null) {
                        mLiveLinkMicPresenter.setSocketClient(mSocketClient);
                    }
                    mSocketClient.connect(mLiveUid, mStream, mTicketId);
                    if (mLiveRoomViewHolder != null) {
                        mLiveRoomViewHolder.setLiveInfo(mLiveUid, mStream, obj.getIntValue("userlist_time") * 1000, mLiveBean);
                        mLiveRoomViewHolder.setVotes(obj.getString("votestotal"));
                        mLiveRoomViewHolder.setAttention(obj.getIntValue("isattention"));
                        if (mLiveType == Constants.LIVE_TYPE_TIME) {//计时收费
                            mLiveRoomViewHolder.startRequestTimeCharge();
                        }
                    }
                    //判断是否有连麦，要显示连麦窗口
                    String linkMicUid = obj.getString("linkmic_uid");
                    String linkMicPull = obj.getString("linkmic_pull");
                    if (!TextUtils.isEmpty(linkMicUid) && !"0".equals(linkMicUid) && !TextUtils.isEmpty(linkMicPull)) {
                        if (mLiveSDK == Constants.LIVE_SDK_TX && mLiveLinkMicPresenter != null) {
                            mLiveLinkMicPresenter.onLinkMicPlay(linkMicUid, linkMicPull);
                        }
                    }
                    //判断是否有主播连麦
                    JSONObject pkInfo = JSON.parseObject(obj.getString("pkinfo"));
                    if (pkInfo != null) {
                        String pkUid = pkInfo.getString("pkuid");
                        if (!TextUtils.isEmpty(pkUid) && !"0".equals(pkUid)) {
                            if (mLiveSDK == Constants.LIVE_SDK_TX) {
                                String pkPull = pkInfo.getString("pkpull");
                                if (!TextUtils.isEmpty(pkPull) && mLiveLinkMicAnchorPresenter != null) {
                                    mLiveLinkMicAnchorPresenter.onLinkMicAnchorPlayUrl(pkUid, pkPull);
                                }
                            } else {
                                mLivePlayViewHolder.setAnchorLinkMic(true, 0);
                            }
                        }
                        if (pkInfo.getIntValue("ifpk") == 1 && mLiveLinkMicPkPresenter != null) {//pk开始了
                            mLiveLinkMicPkPresenter.onEnterRoomPkStart(pkUid, pkInfo.getLongValue("pk_gift_liveuid"), pkInfo.getLongValue("pk_gift_pkuid"), pkInfo.getIntValue("pk_time") * 1000);
                        }
                    }

                    //守护相关
                    mLiveGuardInfo = new LiveGuardInfo();
                    int guardNum = obj.getIntValue("guard_nums");
                    mLiveGuardInfo.setGuardNum(guardNum);
                    JSONObject guardObj = obj.getJSONObject("guard");
                    if (guardObj != null) {
                        mLiveGuardInfo.setMyGuardType(guardObj.getIntValue("type"));
                        mLiveGuardInfo.setMyGuardEndTime(guardObj.getString("endtime"));
                    }
                    if (mLiveRoomViewHolder != null) {
                        mLiveRoomViewHolder.setGuardNum(guardNum);
                        //红包相关
                        mLiveRoomViewHolder.setRedPackBtnVisible(obj.getIntValue("isred") == 1);
                    }
                    //奖池等级
                    int giftPrizePoolLevel = obj.getIntValue("jackpot_level");
                    if (giftPrizePoolLevel >= 0) {
                        if (mLiveRoomViewHolder != null) {
                            mLiveRoomViewHolder.showPrizePoolLevel(String.valueOf(giftPrizePoolLevel));
                        }
                    }

                    //游戏相关
                    if (CommonAppConfig.GAME_ENABLE) {
                        GameParam param = new GameParam();
                        param.setContext(mContext);
                        param.setParentView(mContainerWrap);
                        param.setTopView(mContainer);
                        if (mLiveRoomViewHolder != null) {
                            param.setInnerContainer(mLiveRoomViewHolder.getInnerContainer());
                        }
                        param.setGameActionListener(new GameActionListenerImpl(LiveAudienceActivity.this, mSocketClient));
                        param.setLiveUid(mLiveUid);
                        param.setStream(mStream);
                        param.setAnchor(false);
                        param.setCoinName(mCoinName);
                        param.setObj(obj);
                        if (mGamePresenter == null) {
                            mGamePresenter = new GamePresenter();
                        }
                        mGamePresenter.setGameParam(param);
                    }
                } else if (msg != null && msg.length() > 0) {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 打开礼物窗口
     */
    public void openGiftWindow() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream)) {
            return;
        }
        LiveGiftDialogFragment fragment = new LiveGiftDialogFragment();
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putString(Constants.LIVE_STREAM, mStream);
        fragment.setArguments(bundle);
        fragment.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), "LiveGiftDialogFragment");
    }

    /**
     * 结束观看
     */
    private void endPlay() {
        LiveHttpUtil.cancel(LiveHttpConsts.ENTER_ROOM);
        if (mEnd) {
            return;
        }
        mEnd = true;
        //断开socket
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        mSocketClient = null;
        //结束播放
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.release();
            mLivePlayViewHolder = null;
        }
        if (mLiveAudienceViewHolder != null) {
            mLiveAudienceViewHolder.release();
            mLiveAudienceViewHolder = null;
        }
        release();
    }

    @Override
    protected void release() {
        super.release();
        if (mRoomScrollAdapter != null) {
            mRoomScrollAdapter.setActionListener(null);
        }
        mRoomScrollAdapter = null;
    }

    /**
     * 观众收到直播结束消息
     */
    @Override
    public void onLiveEnd() {
        super.onLiveEnd();
        if (!CommonAppConfig.LIVE_ROOM_SCROLL) {
            if (mViewPager != null) {
                if (mViewPager.getCurrentItem() != 1) {
                    mViewPager.setCurrentItem(1, false);
                }
                mViewPager.setCanScroll(false);
            }
            endPlay();
        } else {
            if (mLivePlayViewHolder != null) {
                mLivePlayViewHolder.stopPlay2();
            }
        }
        liveDisconnect();
    }


    /**
     * 观众收到踢人消息
     */
    @Override
    public void onKick(String touid) {
        if (!TextUtils.isEmpty(touid) && touid.equals(CommonAppConfig.getInstance().getUid())) {//被踢的是自己
            ToastUtil.show(WordUtil.getString(R.string.live_kicked_2));
            exitLiveRoom();
        }
    }

    /**
     * 观众收到禁言消息
     */
    @Override
    public void onShutUp(String touid, String content) {
        if (!TextUtils.isEmpty(touid) && touid.equals(CommonAppConfig.getInstance().getUid())) {
            DialogUtil.showSimpleTipDialog(mContext, content);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mEnd && !canBackPressed()) {
            return;
        }
        exitLiveRoom();
    }

    /**
     * 退出直播间
     */
    public void exitLiveRoom() {
        endPlay();
        finish();
        // super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        exitLiveRoom();
        super.onDestroy();
        L.e("LiveAudienceActivity-------onDestroy------->");
    }

    /**
     * 计时收费更新主播映票数
     */
    public void roomChargeUpdateVotes() {
        sendUpdateVotesMessage(mLiveTypeVal);
    }

    /**
     * 暂停播放
     */
    public void pausePlay() {
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.pausePlay();
        }
    }

    /**
     * 恢复播放
     */
    public void resumePlay() {
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.resumePlay();
        }
    }

    /**
     * 充值成功
     */
    public void onChargeSuccess() {
        if (mLiveType == Constants.LIVE_TYPE_TIME) {
            if (mCoinNotEnough) {
                mCoinNotEnough = false;
                LiveHttpUtil.roomCharge(mLiveUid, mStream, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            roomChargeUpdateVotes();
                            if (mLiveRoomViewHolder != null) {
                                resumePlay();
                                mLiveRoomViewHolder.startRequestTimeCharge();
                            }
                        } else {
                            if (code == 1008) {//余额不足
                                mCoinNotEnough = true;
                                DialogUtil.showSimpleDialog(mContext, WordUtil.getString(R.string.live_coin_not_enough), false,
                                        new DialogUtil.SimpleCallback2() {
                                            @Override
                                            public void onConfirmClick(Dialog dialog, String content) {
                                                //选择币种
                                                RouteUtil.forwardRecharge();
                                            }

                                            @Override
                                            public void onCancelClick() {
                                                exitLiveRoom();
                                            }
                                        });
                            }
                        }
                    }
                });
            }
        }
    }

    public void setCoinNotEnough(boolean coinNotEnough) {
        mCoinNotEnough = coinNotEnough;
    }

    /**
     * 游戏窗口变化事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameWindowChangedEvent(GameWindowChangedEvent e) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setOffsetY(e.getGameViewHeight());
        }
    }

    /**
     * 腾讯sdk连麦时候切换低延时流
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLinkMicTxAccEvent(LinkMicTxAccEvent e) {
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.onLinkMicTxAccEvent(e.isLinkMic());
        }
    }

    /**
     * 腾讯sdk时候主播连麦回调
     *
     * @param linkMic true开始连麦 false断开连麦
     */
    public void onLinkMicTxAnchor(boolean linkMic) {
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.setAnchorLinkMic(linkMic, 5000);
        }
    }

    /**
     * 主播下线
     */
    public void liveDisconnect() {
        mLlDisconnect.setVisibility(View.VISIBLE);
        //返回首页
        findViewById(R.id.tv_to_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
