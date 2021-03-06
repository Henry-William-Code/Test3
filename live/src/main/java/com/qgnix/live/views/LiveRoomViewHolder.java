package com.qgnix.live.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.opensource.svgaplayer.SVGAImageView;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.bean.LevelBean;
import com.qgnix.common.bean.LoginBean;
import com.qgnix.common.bean.TranslateBean;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.cache.CacheData;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.interfaces.OnItemClickListener;
import com.qgnix.common.interfaces.UserNumChangeCallback;
import com.qgnix.common.utils.BaiDuTranslationUtil;
import com.qgnix.common.utils.ClickUtil;
import com.qgnix.common.utils.DialogUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.LanSwitchUtil;
import com.qgnix.common.utils.NetWorkUtil;
import com.qgnix.common.utils.RouteUtil;
import com.qgnix.common.utils.StringUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.common.views.AbsViewHolder;
import com.qgnix.live.R;
import com.qgnix.live.activity.LiveActivity;
import com.qgnix.live.activity.LiveAnchorActivity;
import com.qgnix.live.activity.LiveAudienceActivity;
import com.qgnix.live.activity.LiveReportActivity;
import com.qgnix.live.adapter.LiveChatAdapter;
import com.qgnix.live.adapter.LiveUserAdapter;
import com.qgnix.live.bean.LiveBean;
import com.qgnix.live.bean.LiveBuyGuardMsgBean;
import com.qgnix.live.bean.LiveChatBean;
import com.qgnix.live.bean.LiveDanMuBean;
import com.qgnix.live.bean.LiveEnterRoomBean;
import com.qgnix.live.bean.LiveGiftPrizePoolWinBean;
import com.qgnix.live.bean.LiveLuckGiftWinBean;
import com.qgnix.live.bean.LiveReceiveGiftBean;
import com.qgnix.live.bean.LiveUserGiftBean;
import com.qgnix.live.custom.TopGradual;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.live.dialog.LiveUserDialogFragment;
import com.qgnix.live.http.LiveHttpConsts;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.live.lottery.BettingSelectDialog;
import com.qgnix.live.lottery.ConfirmBettingDialog;
import com.qgnix.live.lottery.LotteryTypeDialog;
import com.qgnix.live.lottery.adapter.KjNumberAdapter;
import com.qgnix.live.lottery.entry.BettingCategoryBean;
import com.qgnix.live.lottery.entry.BettingDetailsBean;
import com.qgnix.live.lottery.entry.ConfirmTzBean;
import com.qgnix.live.lottery.entry.GameData;
import com.qgnix.live.lottery.entry.TicketData;
import com.qgnix.live.lottery.entry.TimeBean;
import com.qgnix.live.presenter.LiveDanmuPresenter;
import com.qgnix.live.presenter.LiveEnterRoomAnimPresenter;
import com.qgnix.live.presenter.LiveGiftAnimPresenter;
import com.qgnix.live.utils.CpUtils;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/9.
 * ?????????????????????
 */

public class LiveRoomViewHolder extends AbsViewHolder implements View.OnClickListener {
    private int mOffsetY;
    private ViewGroup mRoot;
    private ImageView mAvatar;
    private ImageView mLevelAnchor, img_cai_logo;
    private TextView mName;
    private TextView mID;
    private View mBtnFollow;
    private TextView mVotesName;//????????????
    private TextView mVotes;
    private TextView mGuardNum;//????????????
    private RecyclerView mUserRecyclerView;
    private RecyclerView mChatRecyclerView;
    private LiveUserAdapter mLiveUserAdapter;
    private LiveChatAdapter mLiveChatAdapter;
    private View mBtnRedPack;
    private String mLiveUid;
    private String mStream;
    private LiveEnterRoomAnimPresenter mLiveEnterRoomAnimPresenter;
    private LiveDanmuPresenter mLiveDanmuPresenter;
    private LiveGiftAnimPresenter mLiveGiftAnimPresenter;
    private LiveRoomHandler mLiveRoomHandler;
    private HttpCallback mTimeChargeCallback;

    private UserNumChangeCallback userNumChangeCallback;//??????????????????

    protected int mUserListInterval;//?????????????????????????????????
    private GifImageView mGifImageView;
    private SVGAImageView mSVGAImageView;
    private ViewGroup mLiveGiftPrizePoolContainer;
    private TextView mLiveTimeTextView;//?????????????????????
    private long mAnchorLiveTime;//??????????????????
    private View mBtnPrizePool;//????????????
    private TextView mPrizePoolLevel;//????????????


    /**
     * ????????????
     */
    private Switch mSwTranslate;
    /**
     * ????????????
     */
    private List<String> kjNumberList;
    /**
     * ?????????
     */
    private RecyclerView mRvDrawNum;
    /**
     * ????????????
     */
    private TextView mTvTitle;
    /**
     * ????????????
     */
    private TextView mTvOpenTime;
    /**
     * ??????
     */
    private TextView mTvNetSpeed;

    private KjNumberAdapter kjNumberAdapter;
    /**
     * ??????????????????
     */
    private RelativeLayout mRvDrawInfo;
    /**
     * ????????????
     */
    private TicketData mTicketData;
    private GameData mGameData;
    /**
     * ???????????????????????????(??????20???)
     */
    private CountDownTimer mDrawNoShow;
    /**
     * ????????????????????????
     */
    private static final int DRAW_NO_SHOW_TIME = 30;
    /**
     * ???????????????dialog
     */
    private BaseCustomDialog mCustomDialog;


    /**
     * ???????????????
     */
    private String mCurrentDrawNo;
    /**
     * ????????????????????????????????????
     */
    private CountDownTimer mDownTimer;
    /**
     * bc ?????????????????????
     */
    private boolean isClosing;
    /**
     * ?????????????????????
     */
    private Timer mTimer;
    /**
     * ????????????????????? TimerTask
     */
    private TimerTask mTimerTask;
    /**
     * ????????????
     */
    private boolean mIsTranslate;

    private ImageView mJbBt;

    public LiveRoomViewHolder(Context context, ViewGroup parentView, GifImageView gifImageView, SVGAImageView svgaImageView, ViewGroup liveGiftPrizePoolContainer) {
        super(context, parentView);
        mGifImageView = gifImageView;
        mSVGAImageView = svgaImageView;
        mLiveGiftPrizePoolContainer = liveGiftPrizePoolContainer;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.view_live_room;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void init() {
        mRoot = (ViewGroup) findViewById(R.id.root);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mLevelAnchor = (ImageView) findViewById(R.id.level_anchor);
        mName = (TextView) findViewById(R.id.name);
        mID = (TextView) findViewById(R.id.id_val);
        mBtnFollow = findViewById(R.id.btn_follow);
        mVotesName = (TextView) findViewById(R.id.votes_name);
        mVotes = (TextView) findViewById(R.id.votes);
        mGuardNum = (TextView) findViewById(R.id.guard_num);
        mTvOpenTime = (TextView) findViewById(R.id.tv_open_time);
        mTvNetSpeed = (TextView) findViewById(R.id.tv_net_speed);
        //????????????
        mSwTranslate = (Switch) findViewById(R.id.sw_translate);

        img_cai_logo = (ImageView) findViewById(R.id.img_cai_logo);
        //????????????
        mRvDrawInfo = (RelativeLayout) findViewById(R.id.rv_draw_info);
        //??????????????????
        mUserRecyclerView = (RecyclerView) findViewById(R.id.user_recyclerView);
        mUserRecyclerView.setHasFixedSize(true);
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        userNumChangeCallback = new UserNumChangeCallback() {
            @Override
            public void onChange(int num) {
                setRoomNum(num + "");
            }
        };
        mLiveUserAdapter = new LiveUserAdapter(mContext, userNumChangeCallback);

        mUserRecyclerView.setAdapter(mLiveUserAdapter);
        //?????????
        mChatRecyclerView = (RecyclerView) findViewById(R.id.chat_recyclerView);
        mChatRecyclerView.setHasFixedSize(true);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mChatRecyclerView.addItemDecoration(new TopGradual());
        mLiveChatAdapter = new LiveChatAdapter(mContext);

        mTvTitle = (TextView) findViewById(R.id.tv_title);

        mLiveChatAdapter.setOnItemClickListener(new OnItemClickListener<LiveChatBean>() {
            @Override
            public void onItemClick(LiveChatBean bean, int position) {
                showUserDialog(bean.getId());
            }
        });

        // ????????????
        mLiveChatAdapter.setOnFollowUpListener(new LiveChatAdapter.OnFollowUpListener() {
            @Override
            public void onFollowUp(List<ConfirmTzBean> tz) {
                if (tz.isEmpty()) {
                    return;
                }
                handleFollowUp(tz);
            }
        });
        mChatRecyclerView.setAdapter(mLiveChatAdapter);
        mVotesName.setText(CommonAppConfig.getInstance().getVotesName());
        mBtnFollow.setOnClickListener(this);
        mAvatar.setOnClickListener(this);
        //??????????????????
        findViewById(R.id.ll_cur_draw_time).setOnClickListener(this);
        findViewById(R.id.btn_votes).setOnClickListener(this);
        findViewById(R.id.iv_no_close).setOnClickListener(this);
        findViewById(R.id.btn_guard).setOnClickListener(this);
        findViewById(R.id.ly_bsyj).setOnClickListener(this);
        findViewById(R.id.ly_jbyj).setOnClickListener(this);

        mJbBt = (ImageView) findViewById(R.id.iv_jb);
        if(CommonAppConfig.getInstance().getSwitchPlay()){
            findViewById(R.id.ly_jbyj).setVisibility(View.GONE);
        }

        mBtnPrizePool = findViewById(R.id.btn_prize_pool_level);
        mPrizePoolLevel = (TextView) findViewById(R.id.prize_pool_level);
        mBtnPrizePool.setOnClickListener(this);
        mBtnRedPack = findViewById(R.id.btn_red_pack);
        mBtnRedPack.setOnClickListener(this);
        if (mContext instanceof LiveAnchorActivity) {
            mLiveTimeTextView = (TextView) findViewById(R.id.live_time);
            mLiveTimeTextView.setVisibility(View.VISIBLE);
        }
        mLiveEnterRoomAnimPresenter = new LiveEnterRoomAnimPresenter(mContext, mContentView);
        mLiveRoomHandler = new LiveRoomHandler(this);

        mTimeChargeCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (mContext instanceof LiveAudienceActivity) {
                    final LiveAudienceActivity liveAudienceActivity = (LiveAudienceActivity) mContext;
                    if (code == 0) {
                        liveAudienceActivity.roomChargeUpdateVotes();
                    } else {
                        if (mLiveRoomHandler != null) {
                            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_TIME_CHARGE);
                        }
                        liveAudienceActivity.pausePlay();
                        if (code == 1008) {//????????????
                            liveAudienceActivity.setCoinNotEnough(true);
                            DialogUtil.showSimpleDialog(mContext, WordUtil.getString(R.string.live_coin_not_enough), false,
                                    new DialogUtil.SimpleCallback2() {
                                        @Override
                                        public void onConfirmClick(Dialog dialog, String content) {
                                            //????????????
                                            RouteUtil.forwardRecharge();
                                        }

                                        @Override
                                        public void onCancelClick() {
                                            liveAudienceActivity.exitLiveRoom();
                                        }
                                    });
                        }
                    }
                }
            }
        };
        // ????????????
        mSwTranslate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsTranslate = isChecked;
            }
        });
        // ?????????
        mRvDrawNum = (RecyclerView) findViewById(R.id.rv_draw_num);
        mRvDrawNum.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));


        //???????????????????????????
        mDrawNoShow = new CountDownTimer(DRAW_NO_SHOW_TIME * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                mRvDrawInfo.setVisibility(View.GONE);
                mDrawNoShow.cancel();
            }
        };
        mDrawNoShow.start();
        // ?????????????????????1s???????????????2s????????????
        handleNetSpeed();


    }

    /**
     * ????????????
     * ?????????????????????1s???????????????2s????????????
     */
    private void handleNetSpeed() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override

            public void run() {
                long netSpeed = NetWorkUtil.getNetSpeed();
                mTvNetSpeed.post(new Runnable() {
                    @Override
                    public void run() {
                        if (netSpeed > 100) {
                            mTvNetSpeed.setTextColor(mContext.getResources().getColor(R.color.green));
                        } else {
                            mTvNetSpeed.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                        mTvNetSpeed.setText(netSpeed + " kb/s");
                    }
                });


            }
        };

        mTimer.schedule(mTimerTask, 1000, 2000);
    }

    /**
     * ????????????
     *
     * @param tz
     */
    private void handleFollowUp(List<ConfirmTzBean> tz) {
        if (null == tz || tz.isEmpty()) {
            L.e("??????????????????");
            return;
        }

        String ticketOodsData = CacheData.getOodsData(mTicketData.getId());
        if (!TextUtils.isEmpty(ticketOodsData)) {
            L.e("===??????????????????===");
            handleData(ticketOodsData, tz);
            return;
        }

        CommonHttpUtil.getBCTicketGetOddsV2(mTicketData.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                L.e("===??????????????????===");
                CacheData.setOodsData(info[0], mTicketData.getId());

                handleData(info[0], tz);
            }

        });
    }


    /**
     * ????????????
     *
     * @param data
     */
    private void handleData(String data, List<ConfirmTzBean> tz) {
        ConfirmTzBean tzBean = tz.get(0);
        String bId = tzBean.getB_id();
        if (TextUtils.isEmpty(bId)) {
            L.e("???????????????????????????");
            return;
        }
        // ????????????id
        String[] parentIds = bId.split("\\|");
        // ????????????Id
        String firstId = parentIds[0];
        //??????????????????
        final List<BettingDetailsBean> confirmList = new ArrayList<>();

        boolean isFinish = false;
        List<BettingCategoryBean> list = JSON.parseArray(data, BettingCategoryBean.class);
        for (BettingCategoryBean b : list) {
            if (!b.getId().equals(firstId)) {
                continue;
            }
            if (b.getIsLast() == 1) {
                List<BettingDetailsBean> items = b.getItems();
                // ??????
                if (b.getZs() > 0) {
                    // ??????
                    String[] oIds = tzBean.getO_id().split(",");
                    StringBuilder sb = new StringBuilder();
                    for (String oId : oIds) {
                        for (BettingDetailsBean item : items) {
                            if (oId.equals(item.getId())) {
                                sb.append(item.getOdds_name()).append(",");
                            }
                        }
                    }
                    BettingDetailsBean temp = new BettingDetailsBean();
                    temp.setId(tzBean.getO_id());
                    temp.setOdds_name(sb.deleteCharAt(sb.length() - 1).toString());
                    temp.setB_id(tzBean.getB_id());
                    temp.setChildTitle(b.getName());
                    temp.setTicketName(mTicketData.getTitle());
                    confirmList.add(temp);
                } else {
                    //????????????
                    for (ConfirmTzBean tempTz : tz) {
                        for (BettingDetailsBean item : items) {
                            if (item.getId().equals(tempTz.getO_id())) {
                                item.setChildTitle(b.getName());
                                item.setTicketName(mTicketData.getTitle());
                                item.setB_id(tempTz.getB_id());
                                confirmList.add(item);
                                break;
                            }
                        }
                    }
                }
                isFinish = true;
            } else {
                // ??????
                List<BettingCategoryBean> beans2 = b.getBet();
                if (b.getZs() > 0) {
                    // ??????
                    StringBuilder sb = new StringBuilder();
                    String[] secondIds = parentIds[1].split("-");
                    String[] oIds = tzBean.getO_id().split(",");
                    for (int i = 0; i < secondIds.length; i++) {
                        for (BettingCategoryBean bean2 : beans2) {
                            if (bean2.getId().equals(secondIds[i])) {
                                for (BettingDetailsBean item : bean2.getItems()) {
                                    if (oIds[i].equals(item.getId())) {
                                        sb.append(item.getOdds_name()).append(",");
                                    }
                                }
                            }
                        }

                    }

                    BettingDetailsBean temp = new BettingDetailsBean();
                    temp.setId(tzBean.getO_id());
                    temp.setOdds_name(sb.deleteCharAt(sb.length() - 1).toString());
                    temp.setB_id(tzBean.getB_id());
                    temp.setChildTitle(b.getName());
                    temp.setTicketName(mTicketData.getTitle());
                    confirmList.add(temp);
                    isFinish = true;
                } else {
                    for (ConfirmTzBean tempTz : tz) {
                        String secondId = tempTz.getB_id().split("\\|")[1];
                        for (BettingCategoryBean bean2 : beans2) {
                            if (secondId.equals(bean2.getId())) {
                                List<BettingDetailsBean> items = bean2.getItems();
                                for (BettingDetailsBean item : items) {
                                    if (item.getId().equals(tempTz.getO_id())) {
                                        item.setChildTitle(b.getName());
                                        item.setTicketName(mTicketData.getTitle());
                                        item.setB_id(tempTz.getB_id());
                                        confirmList.add(item);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    isFinish = true;

                }
            }
        }
        if (isFinish) {
            new ConfirmBettingDialog(mContext, Integer.parseInt(tzBean.getTz_zs()), Integer.parseInt(tzBean.getTz_money()), confirmList, new ConfirmBettingDialog.OnSuccessListener() {
                @Override
                public void onSuccess(int code, int totalPrice, String balance) {
                    //??????????????????
                    confirmList.clear();
                }

                @Override
                public void onFail() {
                    //??????????????????
                    confirmList.clear();
                }
            }).show();
        }
    }

    /**
     * ??????????????????
     */
    public void setAvatar(String url) {
        if (mAvatar != null) {
            ImgLoader.displayAvatar(mContext, url, mAvatar);
        }
    }

    /**
     * ??????????????????
     */
    public void setAnchorLevel(int anchorLevel) {
        if (mLevelAnchor != null) {
            LevelBean levelBean = CommonAppConfig.getInstance().getAnchorLevel(anchorLevel);
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumbIcon(), mLevelAnchor);
            }
        }
    }

    /**
     * ???????????????
     */
    public void setName(String name) {
        if (mName != null) {
            mName.setText(name);
        }
    }

    /**
     * ???????????????
     */
    public void setRoomNum(String roomNum) {
        if (mID != null) {
            mLiveUserAdapter.setVisitorNum(Integer.parseInt(roomNum));
            mID.setText(WordUtil.getString(R.string.People_watching, roomNum));
        }
    }

    /**
     * ???????????????
     */
    public void setRoomNumStr(String roomNum) {
        if (mID != null) {
            mID.setText(WordUtil.getString(R.string.People_watching, roomNum));
        }
    }

    /**
     * ??????????????????
     */
    public void setAttention(int attention) {
        if (mBtnFollow != null) {
            if (attention == 0) {
                if (mBtnFollow.getVisibility() != View.VISIBLE) {
                    mBtnFollow.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnFollow.getVisibility() == View.VISIBLE) {
                    mBtnFollow.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * ?????????????????????
     */
    public void setVotes(String votes) {
        if (mVotes != null) {
            mVotes.setText(votes);
        }
    }

    /**
     * ????????????????????????
     */
    public void setGuardNum(int guardNum) {
        if (mGuardNum != null) {
            if (guardNum > 0) {
                mGuardNum.setText(guardNum + WordUtil.getString(R.string.ren));
            } else {
                mGuardNum.setText(R.string.main_list_no_data);
            }
        }
    }


    public void setLiveInfo(String liveUid, String stream, int userListInterval, LiveBean liveBean) {
        mLiveUid = liveUid;
        mStream = stream;
        mUserListInterval = userListInterval;
        mGameData = liveBean.getGameData();
        if (null != mGameData) {
            ImgLoader.display(mContext, mGameData.getImage(), mJbBt);
        }
        //????????????
        mTicketData = liveBean.getTicket_tag();
        if (null == mTicketData) {
            mTvOpenTime.setVisibility(View.GONE);
            img_cai_logo.setImageResource(R.mipmap.icon_live_game);
            return;
        }
        ImgLoader.display(mContext, mTicketData.getImage(), img_cai_logo);
        // ?????????
        kjNumberList = new ArrayList<>();
        kjNumberAdapter = new KjNumberAdapter(mContext, kjNumberList, mTicketData.getType());
        mRvDrawNum.setAdapter(kjNumberAdapter);
    }

    /**
     * ????????????????????????
     */
    public void onGuardInfoChanged(LiveBuyGuardMsgBean bean) {
        setGuardNum(bean.getGuardNum());
        setVotes(bean.getVotes());
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.onGuardChanged(bean.getUid(), bean.getGuardType());
        }
    }

    /**
     * ??????????????????????????????
     */
    public void setRedPackBtnVisible(boolean visible) {
        if (mBtnRedPack != null) {
            if (visible) {
                if (mBtnRedPack.getVisibility() != View.VISIBLE) {
                    mBtnRedPack.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnRedPack.getVisibility() == View.VISIBLE) {
                    mBtnRedPack.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (!ClickUtil.canClick(2000)) {
            return;
        }
        if (i == R.id.avatar) {
            showAnchorUserDialog();
        } else if (i == R.id.btn_follow) {
            follow();
        } else if (i == R.id.btn_votes) {
            openContributeWindow();
        } else if (i == R.id.btn_guard) {
            ((LiveActivity) mContext).openGuardListWindow();
        } else if (i == R.id.btn_red_pack) {
            ((LiveActivity) mContext).openRedPackListWindow();
        } else if (i == R.id.btn_prize_pool_level) {
            ((LiveActivity) mContext).openPrizePoolWindow();
        } else if (i == R.id.iv_no_close) {
            mRvDrawInfo.setVisibility(View.GONE);
        } else if (i == R.id.ll_cur_draw_time) {
            if (isClosing) {
                ToastUtil.show(R.string.closing);
                return;
            }
            //?????????????????? ????????????????????? ?????????????????????????????????
            if (null == mTicketData || TextUtils.isEmpty(mTicketData.getId())) {
                mCustomDialog = new LotteryTypeDialog(mContext);
            } else {
                mCustomDialog = new BettingSelectDialog(mContext, mTicketData);
            }
            mCustomDialog.show();
        } else if (i == R.id.iv_game) {
            new LotteryTypeDialog(mContext).show();
        } else if (i == R.id.ly_bsyj) {
            ((LiveActivity) mContext).jumpListActivity();
        } else if (i == R.id.ly_jbyj) {
            if (null == mGameData) {
                if (TextUtils.isEmpty(mLiveUid)) {
                    return;
                }
                LiveReportActivity.forward(mContext, mLiveUid);
                return;
            }
            //??????
            loginGame(mGameData.getPlat_type(), mGameData.getGame_code(), mGameData.getGame_code());
        }
    }

    /**
     * ????????????
     */
    private void loginGame(String platType, String gameType, String gameCode) {
        CommonHttpUtil.ngLogin(platType, gameType, gameCode, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, final String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
                    @Override
                    public void callback(UserBean userBean) {
                        String coin = userBean == null ? "" : userBean.getCoin();
                        LoginBean loginBean = new Gson().fromJson(info[0], LoginBean.class);
                        RouteUtil.toGame(null, loginBean.getData(), platType, userBean.getCoin(), null);
                    }
                });
            }
        });
    }

    /**
     * ???????????????
     */
    private void close() {
        ((LiveAudienceActivity) mContext).onBackPressed();
    }


    /**
     * ????????????
     */
    private void follow() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        CommonHttpUtil.setAttention(mLiveUid, new CommonCallback<Integer>() {
            @Override
            public void callback(Integer isAttention) {
                if (isAttention == 1) {
                    CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
                        @Override
                        public void callback(UserBean bean) {
                            ((LiveActivity) mContext).sendSystemMessage(bean.getUserNiceName() + WordUtil.getString(R.string.live_follow_anchor));
                        }
                    });
                }
            }
        });
    }

    /**
     * ????????????????????????????????????????????????
     */
    public void insertUser(LiveUserGiftBean bean) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.insertItem(bean);
        }
    }

    /**
     * ????????????????????????????????????
     */
    public void insertUser(List<LiveUserGiftBean> list) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.insertList(list);
        }
    }

    /**
     * ????????????????????????????????????????????????
     */
    public void removeUser(String uid) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.removeItem(uid);
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void requestTimeCharge() {
        if (!TextUtils.isEmpty(mLiveUid) && mTimeChargeCallback != null) {
            LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
            LiveHttpUtil.timeCharge(mLiveUid, mStream, mTimeChargeCallback);
            startRequestTimeCharge();
        }
    }

    /**
     * ???????????????????????????????????????
     */
    public void startRequestTimeCharge() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_TIME_CHARGE, 60000);
        }
    }


    /**
     * ??????????????????????????????
     */
    public void insertChat(LiveChatBean bean) {
        // TODO: 2020/11/9 1111
        if (mLiveChatAdapter != null) {
            int type = bean.getType();
            //???????????????????????????
            if (mIsTranslate && (type == LiveChatBean.NORMAL || type == LiveChatBean.SYSTEM)) {
                try {
                    BaiDuTranslationUtil.getTranslationData(CommonAppConfig.getInstance().getTransserver(), bean.getContent(), LanSwitchUtil.getLanStr(), new AbsCallback<TranslateBean>() {
                        @Override
                        public void onSuccess(Response<TranslateBean> response) {
                            TranslateBean body = response.body();
                            if (null == body || null == body.getTrans_result() || body.getTrans_result().isEmpty()) {
                                mLiveChatAdapter.insertItem(bean);
                                return;
                            }
                            bean.setContent(body.getTrans_result().get(0).getDst());
                            mLiveChatAdapter.insertItem(bean);
                        }

                        @Override
                        public TranslateBean convertResponse(okhttp3.Response response) throws
                                Throwable {
                            ResponseBody body = response.body();
                            if (null == body) {
                                return null;
                            }
                            return JSON.parseObject(body.string(), TranslateBean.class);
                        }

                        @Override
                        public void onError(Response<TranslateBean> response) {
                            mLiveChatAdapter.insertItem(bean);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    mLiveChatAdapter.insertItem(bean);
                    e.printStackTrace();
                }
            } else {
                mLiveChatAdapter.insertItem(bean);
            }

        }
    }

    /**
     * ??????????????? ????????????????????????
     */
    public void insertFollowUp(LiveChatBean bean) {
        if (mLiveChatAdapter != null && null != mTicketData) {
            mLiveChatAdapter.setTicketName(mTicketData.getTitle());
            mLiveChatAdapter.insertItem(bean);
        }
    }

    /**
     * ??????????????? ????????? ????????????????????????
     */
    public void insertWinning(List<LiveChatBean> bean) {
        if (mLiveChatAdapter != null && null != mTicketData) {
            mLiveChatAdapter.setTicketName(mTicketData.getTitle());
            for (LiveChatBean chatBean : bean) {
                mLiveChatAdapter.insertItem(chatBean);
            }
        }
    }

    public void setOffsetY(int offsetY) {
        mOffsetY = offsetY;
    }


    /**
     * ??????????????????
     */
    public void onKeyBoardChanged(int visibleHeight, int keyBoardHeight) {
        if (mRoot != null) {
            if (keyBoardHeight == 0) {
                mRoot.setTranslationY(0);
                return;
            }
            if (mOffsetY == 0) {
                mRoot.setTranslationY(-keyBoardHeight);
                return;
            }
            if (mOffsetY > 0 && mOffsetY < keyBoardHeight) {
                mRoot.setTranslationY(mOffsetY - keyBoardHeight);
            }
        }
    }

    /**
     * ????????????????????????
     */
    public void chatScrollToBottom() {
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.scrollToBottom();
        }
    }

    /**
     * ?????????????????? ????????????,????????????
     */
    public void onEnterRoom(LiveEnterRoomBean bean) {
        if (bean == null) {
            return;
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.enterRoom(bean);
        }
    }

    /**
     * ????????????
     */
    public void showDanmu(LiveDanMuBean bean) {
        if (mVotes != null) {
            mVotes.setText(bean.getVotes());
        }
        if (mLiveDanmuPresenter == null) {
            mLiveDanmuPresenter = new LiveDanmuPresenter(mContext, mParentView);
        }
        mLiveDanmuPresenter.showDanmu(bean);
    }

    /**
     * ?????????????????????????????????
     */
    private void showAnchorUserDialog() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        showUserDialog(mLiveUid);
    }

    /**
     * ????????????????????????
     */
    private void showUserDialog(String toUid) {
        if (!TextUtils.isEmpty(mLiveUid) && !TextUtils.isEmpty(toUid)) {
            LiveUserDialogFragment fragment = new LiveUserDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.LIVE_UID, mLiveUid);
            bundle.putString(Constants.STREAM, mStream);
            bundle.putString(Constants.TO_UID, toUid);
            fragment.setArguments(bundle);
            fragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "LiveUserDialogFragment");
        }
    }

    /**
     * ????????????????????????
     */
    private void openContributeWindow() {
        ((LiveActivity) mContext).openContributeWindow();
    }


    /**
     * ??????????????????
     */
    public void showGiftMessage(LiveReceiveGiftBean bean) {
        mVotes.setText(bean.getVotes());
        if (mLiveGiftAnimPresenter == null) {
            mLiveGiftAnimPresenter = new LiveGiftAnimPresenter(mContext, mContentView, mGifImageView, mSVGAImageView, mLiveGiftPrizePoolContainer);
        }
        mLiveGiftAnimPresenter.showGiftAnim(bean);
    }

    /**
     * ?????????????????????
     *
     * @param deltaVal ?????????????????????
     */
    public void updateVotes(String deltaVal) {
        if (mVotes == null) {
            return;
        }
        String votesVal = mVotes.getText().toString().trim();
        if (TextUtils.isEmpty(votesVal)) {
            return;
        }
        try {
            double votes = Double.parseDouble(votesVal);
            double addVotes = Double.parseDouble(deltaVal);
            votes += addVotes;
            mVotes.setText(StringUtil.format(votes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ViewGroup getInnerContainer() {
        return (ViewGroup) findViewById(R.id.inner_container);
    }


    /**
     * ????????????????????????
     */
    private void showAnchorLiveTime() {
        if (mLiveTimeTextView != null) {
            mAnchorLiveTime += 1000;
            mLiveTimeTextView.setText(StringUtil.getDurationText(mAnchorLiveTime));
            startAnchorLiveTime();
        }
    }

    public void startAnchorLiveTime() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_ANCHOR_LIVE_TIME, 1000);
        }
    }

    /**
     * ??????????????????50??????????????????
     */
    public void anchorPause() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_ANCHOR_PAUSE, 50000);
        }
    }

    /**
     * ?????????????????????????????????
     */
    public void anchorResume() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_ANCHOR_PAUSE);
        }
    }

    /**
     * ??????????????????
     */
    private void anchorEndLive() {
        if (mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).endLive();
        }
    }

    /**
     * ??????????????????
     */
    public void onLuckGiftWin(LiveLuckGiftWinBean bean) {
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.showLuckGiftWinAnim(bean);
        }
    }


    /**
     * ????????????
     */
    public void onPrizePoolWin(LiveGiftPrizePoolWinBean bean) {
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.showPrizePoolWinAnim(bean);
        }
    }

    /**
     * ????????????
     */
    public void onPrizePoolUp(String level) {
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.showPrizePoolUp(level);
        }
    }

    /**
     * ??????????????????
     */
    public void showPrizePoolLevel(String level) {
        if (mBtnPrizePool != null && mBtnPrizePool.getVisibility() != View.VISIBLE) {
            mBtnPrizePool.setVisibility(View.VISIBLE);
        }
        if (mPrizePoolLevel != null) {
            mPrizePoolLevel.setText(WordUtil.getString(R.string.live_gift_prize_pool_3, level));
        }
    }


    /**
     * ????????????
     *
     * @param data ????????????
     */
    public void onLotteryInfo(String data) {
        if (TextUtils.isEmpty(data)) {
            L.e("?????????????????????");
            return;
        }
        TimeBean bean = JSON.parseObject(data, TimeBean.class);
        if (!TextUtils.isEmpty(bean.getKj_no()) && !TextUtils.isEmpty(bean.getLast_kj_number())) {
            Log.e("mCurrentDrawNo", "mCurrentDrawNo: " + mCurrentDrawNo);
            Log.e("mCurrentDrawNo", "getKj_no: " + bean.getKj_no());

            if (null == mCurrentDrawNo || !mCurrentDrawNo.equals(bean.getKj_no())) { //???????????? ????????????????????????
                mCurrentDrawNo = bean.getKj_no();//????????????
                mRvDrawInfo.setVisibility(View.VISIBLE);
                mTvTitle.setText(WordUtil.getString(R.string.live_current_draw, mTicketData.getTitle(), bean.getLast_kj_no()));
                // ??????????????????
                String[] curKjNumber = bean.getLast_kj_number().split(",");
                kjNumberList.clear();
                kjNumberList.addAll(CpUtils.formatDrawNum(mTicketData.getType(), curKjNumber));
                kjNumberAdapter.notifyDataSetChanged();
                // ??????????????????
                int nowTime = bean.getNow_time();
                // Log.e("==???????????????????????????====", "????????????????????????????????????" + (Long.parseLong(bean.getKj_end()) - System.currentTimeMillis() / 1000));
                Log.e("==???????????????????????????====", "????????????????????????????????????" + nowTime);

                // ?????????
                if (nowTime <= 2) {
                    mTvOpenTime.setText(R.string.closing);
                    isClosing = true;
                    // mLyTime.setClickable(false);
                } else {
                    isClosing = false;
                    if (null != mDownTimer) {
                        mDownTimer.cancel();
                        mDownTimer = null;
                    }
                    // ????????????????????????
                    mDownTimer = new CountDownTimer(nowTime * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            //  L.e("====??????????????????????????????===== ticket :" + bean.getT_id() + " time:" + StringUtil.stringForTime(millisUntilFinished / 1000));
                            if (!isClosing) {
                                mTvOpenTime.setText(StringUtil.stringForTime(millisUntilFinished / 1000));
                            } else
                                mTvOpenTime.setText(R.string.closing);
                        }

                        @Override
                        public void onFinish() {
                            isClosing = true;
                            mTvOpenTime.setText(R.string.closing);
                        }
                    };
                    mDownTimer.start();
                }
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param status
     */
    public void onTranslate(int status) {
        mIsTranslate = status == 1;
        mSwTranslate.setChecked(mIsTranslate);
    }

    /**
     * ????????????
     *
     * @param data
     */
    public void onUserList(List<LiveUserGiftBean> data) {
        if (null == data) {
            data = Collections.emptyList();
        }
        mLiveUserAdapter.setVisitorNum(data.size());
        mLiveUserAdapter.refreshList(data);
    }

    public void release() {
        LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.release();
        }
        mLiveRoomHandler = null;
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.release();
        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.release();
        }
        mTimeChargeCallback = null;

        if (mDownTimer != null) {
            mDownTimer.cancel();
            mDownTimer = null;
        }

        if (mDrawNoShow != null) {
            mDrawNoShow.cancel();
            mDrawNoShow = null;
        }

        if (mCustomDialog != null) {
            if (mCustomDialog.isShowing()) {
                mCustomDialog.dismiss();
            }
            mCustomDialog = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        if (mTimer != null) {
            mTimer.purge();
            mTimer.cancel();
            mTimer = null;
        }


    }

    public void clearData() {
        LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeCallbacksAndMessages(null);
        }
        if (mAvatar != null) {
            mAvatar.setImageDrawable(null);
        }
        if (mLevelAnchor != null) {
            mLevelAnchor.setImageDrawable(null);
        }
        if (mName != null) {
            mName.setText("");
        }
        if (mID != null) {
            mID.setText("");
        }
        if (mVotes != null) {
            mVotes.setText("");
        }
        if (null != mCurrentDrawNo) {
            mCurrentDrawNo = null;
        }
        if (mGuardNum != null) {
            mGuardNum.setText("");
        }
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.clear();
        }
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.clear();
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.cancelAnim();
            mLiveEnterRoomAnimPresenter.resetAnimView();
        }
        if (mLiveDanmuPresenter != null) {
            mLiveDanmuPresenter.release();
            mLiveDanmuPresenter.reset();
        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.cancelAllAnim();
        }
    }

    private static class LiveRoomHandler extends Handler {

        private LiveRoomViewHolder mLiveRoomViewHolder;
        private static final int WHAT_TIME_CHARGE = 2;//??????????????????????????????????????????
        private static final int WHAT_ANCHOR_LIVE_TIME = 3;//?????????????????????
        private static final int WHAT_ANCHOR_PAUSE = 4;//???????????????

        public LiveRoomHandler(LiveRoomViewHolder liveRoomViewHolder) {
            mLiveRoomViewHolder = new WeakReference<>(liveRoomViewHolder).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mLiveRoomViewHolder != null) {
                switch (msg.what) {
                    case WHAT_TIME_CHARGE:
                        mLiveRoomViewHolder.requestTimeCharge();
                        break;
                    case WHAT_ANCHOR_LIVE_TIME:
                        mLiveRoomViewHolder.showAnchorLiveTime();
                        break;
                    case WHAT_ANCHOR_PAUSE:
                        mLiveRoomViewHolder.anchorEndLive();
                        break;
                }
            }
        }

        public void release() {
            removeCallbacksAndMessages(null);
            mLiveRoomViewHolder = null;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
        LiveHttpUtil.cancel(LiveHttpConsts.GET_DRAW_RESULTS);
        LiveHttpUtil.cancel(LiveHttpConsts.GET_TIK);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);

    }
}
