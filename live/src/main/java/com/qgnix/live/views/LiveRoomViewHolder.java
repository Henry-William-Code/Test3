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
 * 直播间公共逻辑
 */

public class LiveRoomViewHolder extends AbsViewHolder implements View.OnClickListener {
    private int mOffsetY;
    private ViewGroup mRoot;
    private ImageView mAvatar;
    private ImageView mLevelAnchor, img_cai_logo;
    private TextView mName;
    private TextView mID;
    private View mBtnFollow;
    private TextView mVotesName;//映票名称
    private TextView mVotes;
    private TextView mGuardNum;//守护人数
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

    private UserNumChangeCallback userNumChangeCallback;//用户数量变化

    protected int mUserListInterval;//用户列表刷新时间的间隔
    private GifImageView mGifImageView;
    private SVGAImageView mSVGAImageView;
    private ViewGroup mLiveGiftPrizePoolContainer;
    private TextView mLiveTimeTextView;//主播的直播时长
    private long mAnchorLiveTime;//主播直播时间
    private View mBtnPrizePool;//奖池按钮
    private TextView mPrizePoolLevel;//奖池等级


    /**
     * 翻译开关
     */
    private Switch mSwTranslate;
    /**
     * 开奖号码
     */
    private List<String> kjNumberList;
    /**
     * 开奖号
     */
    private RecyclerView mRvDrawNum;
    /**
     * 开奖期数
     */
    private TextView mTvTitle;
    /**
     * 开奖时间
     */
    private TextView mTvOpenTime;
    /**
     * 网速
     */
    private TextView mTvNetSpeed;

    private KjNumberAdapter kjNumberAdapter;
    /**
     * 上期开奖结果
     */
    private RelativeLayout mRvDrawInfo;
    /**
     * 彩票信息
     */
    private TicketData mTicketData;
    private GameData mGameData;
    /**
     * 开奖号显示的定时器(显示20秒)
     */
    private CountDownTimer mDrawNoShow;
    /**
     * 开奖号码显示时间
     */
    private static final int DRAW_NO_SHOW_TIME = 30;
    /**
     * 投注、彩票dialog
     */
    private BaseCustomDialog mCustomDialog;


    /**
     * 当前开奖号
     */
    private String mCurrentDrawNo;
    /**
     * 直播间右上的倒计时计数器
     */
    private CountDownTimer mDownTimer;
    /**
     * bc 游戏是否封盘中
     */
    private boolean isClosing;
    /**
     * 实时网速定时器
     */
    private Timer mTimer;
    /**
     * 实时网速定时器 TimerTask
     */
    private TimerTask mTimerTask;
    /**
     * 翻译开关
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
        //翻译开关
        mSwTranslate = (Switch) findViewById(R.id.sw_translate);

        img_cai_logo = (ImageView) findViewById(R.id.img_cai_logo);
        //开奖信息
        mRvDrawInfo = (RelativeLayout) findViewById(R.id.rv_draw_info);
        //用户头像列表
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
        //聊天栏
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

        // 跟投回调
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
        //当前开奖时间
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
                        if (code == 1008) {//余额不足
                            liveAudienceActivity.setCoinNotEnough(true);
                            DialogUtil.showSimpleDialog(mContext, WordUtil.getString(R.string.live_coin_not_enough), false,
                                    new DialogUtil.SimpleCallback2() {
                                        @Override
                                        public void onConfirmClick(Dialog dialog, String content) {
                                            //选择币种
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
        // 翻译开关
        mSwTranslate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsTranslate = isChecked;
            }
        });
        // 开奖号
        mRvDrawNum = (RecyclerView) findViewById(R.id.rv_draw_num);
        mRvDrawNum.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));


        //开奖号显示的定时器
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
        // 实时获取网速，1s后启动，每2s执行一次
        handleNetSpeed();


    }

    /**
     * 处理网速
     * 实时获取网速，1s后启动，每2s执行一次
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
     * 处理跟投
     *
     * @param tz
     */
    private void handleFollowUp(List<ConfirmTzBean> tz) {
        if (null == tz || tz.isEmpty()) {
            L.e("跟头信息为空");
            return;
        }

        String ticketOodsData = CacheData.getOodsData(mTicketData.getId());
        if (!TextUtils.isEmpty(ticketOodsData)) {
            L.e("===本地获取数据===");
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
                L.e("===网络获取数据===");
                CacheData.setOodsData(info[0], mTicketData.getId());

                handleData(info[0], tz);
            }

        });
    }


    /**
     * 处理数据
     *
     * @param data
     */
    private void handleData(String data, List<ConfirmTzBean> tz) {
        ConfirmTzBean tzBean = tz.get(0);
        String bId = tzBean.getB_id();
        if (TextUtils.isEmpty(bId)) {
            L.e("跟头彩票类型为空！");
            return;
        }
        // 彩票上级id
        String[] parentIds = bId.split("\\|");
        // 一级分类Id
        String firstId = parentIds[0];
        //确认投注信息
        final List<BettingDetailsBean> confirmList = new ArrayList<>();

        boolean isFinish = false;
        List<BettingCategoryBean> list = JSON.parseArray(data, BettingCategoryBean.class);
        for (BettingCategoryBean b : list) {
            if (!b.getId().equals(firstId)) {
                continue;
            }
            if (b.getIsLast() == 1) {
                List<BettingDetailsBean> items = b.getItems();
                // 一级
                if (b.getZs() > 0) {
                    // 组合
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
                    //最后一级
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
                // 二级
                List<BettingCategoryBean> beans2 = b.getBet();
                if (b.getZs() > 0) {
                    // 组合
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
                    //清空选中数量
                    confirmList.clear();
                }

                @Override
                public void onFail() {
                    //清空选中数量
                    confirmList.clear();
                }
            }).show();
        }
    }

    /**
     * 显示主播头像
     */
    public void setAvatar(String url) {
        if (mAvatar != null) {
            ImgLoader.displayAvatar(mContext, url, mAvatar);
        }
    }

    /**
     * 显示主播等级
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
     * 显示用户名
     */
    public void setName(String name) {
        if (mName != null) {
            mName.setText(name);
        }
    }

    /**
     * 显示房间号
     */
    public void setRoomNum(String roomNum) {
        if (mID != null) {
            mLiveUserAdapter.setVisitorNum(Integer.parseInt(roomNum));
            mID.setText(WordUtil.getString(R.string.People_watching, roomNum));
        }
    }

    /**
     * 显示房间号
     */
    public void setRoomNumStr(String roomNum) {
        if (mID != null) {
            mID.setText(WordUtil.getString(R.string.People_watching, roomNum));
        }
    }

    /**
     * 显示是否关注
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
     * 显示主播映票数
     */
    public void setVotes(String votes) {
        if (mVotes != null) {
            mVotes.setText(votes);
        }
    }

    /**
     * 显示主播守护人数
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
        //彩票信息
        mTicketData = liveBean.getTicket_tag();
        if (null == mTicketData) {
            mTvOpenTime.setVisibility(View.GONE);
            img_cai_logo.setImageResource(R.mipmap.icon_live_game);
            return;
        }
        ImgLoader.display(mContext, mTicketData.getImage(), img_cai_logo);
        // 开奖号
        kjNumberList = new ArrayList<>();
        kjNumberAdapter = new KjNumberAdapter(mContext, kjNumberList, mTicketData.getType());
        mRvDrawNum.setAdapter(kjNumberAdapter);
    }

    /**
     * 守护信息发生变化
     */
    public void onGuardInfoChanged(LiveBuyGuardMsgBean bean) {
        setGuardNum(bean.getGuardNum());
        setVotes(bean.getVotes());
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.onGuardChanged(bean.getUid(), bean.getGuardType());
        }
    }

    /**
     * 设置红包按钮是否可见
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
            //直播间下方的 游戏按钮弹出框 应该只初始化一次就行了
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
            //游戏
            loginGame(mGameData.getPlat_type(), mGameData.getGame_code(), mGameData.getGame_code());
        }
    }

    /**
     * 登录游戏
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
     * 退出直播间
     */
    private void close() {
        ((LiveAudienceActivity) mContext).onBackPressed();
    }


    /**
     * 关注主播
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
     * 用户进入房间，用户列表添加该用户
     */
    public void insertUser(LiveUserGiftBean bean) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.insertItem(bean);
        }
    }

    /**
     * 用户进入房间，添加僵尸粉
     */
    public void insertUser(List<LiveUserGiftBean> list) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.insertList(list);
        }
    }

    /**
     * 用户离开房间，用户列表删除该用户
     */
    public void removeUser(String uid) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.removeItem(uid);
        }
    }

    /**
     * 请求计时收费的扣费接口
     */
    private void requestTimeCharge() {
        if (!TextUtils.isEmpty(mLiveUid) && mTimeChargeCallback != null) {
            LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
            LiveHttpUtil.timeCharge(mLiveUid, mStream, mTimeChargeCallback);
            startRequestTimeCharge();
        }
    }

    /**
     * 开始请求计时收费的扣费接口
     */
    public void startRequestTimeCharge() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_TIME_CHARGE, 60000);
        }
    }


    /**
     * 添加聊天消息到聊天栏
     */
    public void insertChat(LiveChatBean bean) {
        // TODO: 2020/11/9 1111
        if (mLiveChatAdapter != null) {
            int type = bean.getType();
            //聊天消息、系统消息
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
     * 添加直播间 跟投消息到聊天栏
     */
    public void insertFollowUp(LiveChatBean bean) {
        if (mLiveChatAdapter != null && null != mTicketData) {
            mLiveChatAdapter.setTicketName(mTicketData.getTitle());
            mLiveChatAdapter.insertItem(bean);
        }
    }

    /**
     * 添加直播间 直播间 中奖消息到聊天栏
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
     * 键盘高度变化
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
     * 聊天栏滚到最底部
     */
    public void chatScrollToBottom() {
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.scrollToBottom();
        }
    }

    /**
     * 用户进入房间 金光一闪,坐骑动画
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
     * 显示弹幕
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
     * 显示主播的个人资料弹窗
     */
    private void showAnchorUserDialog() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        showUserDialog(mLiveUid);
    }

    /**
     * 显示个人资料弹窗
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
     * 直播间贡献榜窗口
     */
    private void openContributeWindow() {
        ((LiveActivity) mContext).openContributeWindow();
    }


    /**
     * 显示礼物动画
     */
    public void showGiftMessage(LiveReceiveGiftBean bean) {
        mVotes.setText(bean.getVotes());
        if (mLiveGiftAnimPresenter == null) {
            mLiveGiftAnimPresenter = new LiveGiftAnimPresenter(mContext, mContentView, mGifImageView, mSVGAImageView, mLiveGiftPrizePoolContainer);
        }
        mLiveGiftAnimPresenter.showGiftAnim(bean);
    }

    /**
     * 增加主播映票数
     *
     * @param deltaVal 增加的映票数量
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
     * 主播显示直播时间
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
     * 主播切后台，50秒后关闭直播
     */
    public void anchorPause() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_ANCHOR_PAUSE, 50000);
        }
    }

    /**
     * 主播切后台后又回到前台
     */
    public void anchorResume() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_ANCHOR_PAUSE);
        }
    }

    /**
     * 主播结束直播
     */
    private void anchorEndLive() {
        if (mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).endLive();
        }
    }

    /**
     * 幸运礼物中奖
     */
    public void onLuckGiftWin(LiveLuckGiftWinBean bean) {
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.showLuckGiftWinAnim(bean);
        }
    }


    /**
     * 奖池中奖
     */
    public void onPrizePoolWin(LiveGiftPrizePoolWinBean bean) {
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.showPrizePoolWinAnim(bean);
        }
    }

    /**
     * 奖池升级
     */
    public void onPrizePoolUp(String level) {
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.showPrizePoolUp(level);
        }
    }

    /**
     * 显示奖池按钮
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
     * 开奖信息
     *
     * @param data 开奖数据
     */
    public void onLotteryInfo(String data) {
        if (TextUtils.isEmpty(data)) {
            L.e("开奖数据为空！");
            return;
        }
        TimeBean bean = JSON.parseObject(data, TimeBean.class);
        if (!TextUtils.isEmpty(bean.getKj_no()) && !TextUtils.isEmpty(bean.getLast_kj_number())) {
            Log.e("mCurrentDrawNo", "mCurrentDrawNo: " + mCurrentDrawNo);
            Log.e("mCurrentDrawNo", "getKj_no: " + bean.getKj_no());

            if (null == mCurrentDrawNo || !mCurrentDrawNo.equals(bean.getKj_no())) { //初始状态 或者新的一期来临
                mCurrentDrawNo = bean.getKj_no();//本期期号
                mRvDrawInfo.setVisibility(View.VISIBLE);
                mTvTitle.setText(WordUtil.getString(R.string.live_current_draw, mTicketData.getTitle(), bean.getLast_kj_no()));
                // 上期开奖号码
                String[] curKjNumber = bean.getLast_kj_number().split(",");
                kjNumberList.clear();
                kjNumberList.addAll(CpUtils.formatDrawNum(mTicketData.getType(), curKjNumber));
                kjNumberAdapter.notifyDataSetChanged();
                // 开奖截止时间
                int nowTime = bean.getNow_time();
                // Log.e("==推送过来的开奖数据====", "客户端计算的开奖时间差：" + (Long.parseLong(bean.getKj_end()) - System.currentTimeMillis() / 1000));
                Log.e("==推送过来的开奖数据====", "服务端发送的开奖时间差：" + nowTime);

                // 封盘中
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
                    // 右上角开奖倒计时
                    mDownTimer = new CountDownTimer(nowTime * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            //  L.e("====直播间右上按钮倒计时===== ticket :" + bean.getT_id() + " time:" + StringUtil.stringForTime(millisUntilFinished / 1000));
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
     * 设置翻译状态
     *
     * @param status
     */
    public void onTranslate(int status) {
        mIsTranslate = status == 1;
        mSwTranslate.setChecked(mIsTranslate);
    }

    /**
     * 用户列表
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
        private static final int WHAT_TIME_CHARGE = 2;//计时收费房间定时请求接口扣费
        private static final int WHAT_ANCHOR_LIVE_TIME = 3;//直播间主播计时
        private static final int WHAT_ANCHOR_PAUSE = 4;//主播切后台

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
