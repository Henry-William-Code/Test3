package com.qgnix.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.HtmlConfig;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.fragment.BaseFragment;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.HtmlUtils;
import com.qgnix.common.utils.RouteUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.bean.LiveBean;
import com.qgnix.live.dialog.TopUpDialogFragment;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.TicketData;
import com.qgnix.main.R;
import com.qgnix.main.activity.BettingSelectActivity;
import com.qgnix.main.activity.MainActivity;
import com.qgnix.main.activity.MyDistributionActivity;
import com.qgnix.main.activity.RechargeActivity;
import com.qgnix.main.activity.WebViewsActivity;
import com.qgnix.main.activity.WithdrawActivity;
import com.qgnix.main.adapter.HomeGameAdapter;
import com.qgnix.main.adapter.LiveAdapter;
import com.qgnix.main.bean.BannerBean;
import com.qgnix.main.bean.HomeGameBean;
import com.qgnix.common.bean.LoginBean;
import com.qgnix.main.bean.NoticeEntry;
import com.qgnix.main.common.BannerJumpHelper;
import com.qgnix.main.event.MoreGame;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * ????????????fragment
 */
public class HomeHotFragment extends BaseFragment {
    private SmartRefreshLayout mRefresh;
    /**
     * ?????????
     */
    private Banner mBanner;
    /**
     * ??????
     */
    private TextView mTvNotice;
    /**
     * ??????rv
     */
    private RecyclerView mRvGame;
    /**
     * rv
     */
    private RecyclerView mRecyclerView;

    /**
     * ??????????????????
     */
    private final List<LiveBean> mList = new ArrayList<>();

    /**
     * ??????
     */
    private int mPage = 1;
    private long searchTime = 0;

    private LiveAdapter mLiveAdapter;

    /**
     * ??????
     */
    private HomeGameAdapter mGameAdapter;

    private final List<HomeGameBean> mGameBeans = new ArrayList<>();

    /**
     * ????????????
     */
    private List<BannerBean> mBannerBeans;
    /**
     * ??????
     */
    private NoticeEntry mNoticeEntry;

    /**
     * item ???????????????
     */
    private boolean misItemClick;

    public static HomeHotFragment newInstance() {
        return new HomeHotFragment();
    }


    @Override
    public void initData( Bundle bundle) {
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_home_hot;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        mBanner = view.findViewById(R.id.banner);
        mTvNotice = view.findViewById(R.id.tv_notice);
        mRvGame = view.findViewById(R.id.rv_game);

        mTvNotice.setOnClickListener(this);
        mTvNotice.setClickable(false);
        mTvNotice.setSelected(true);
        mTvNotice.setFocusable(true);
        mTvNotice.setFocusableInTouchMode(true);
        view.findViewById(R.id.ll_see_more).setOnClickListener(this);

        mRefresh = view.findViewById(R.id.refresh);
        mRecyclerView = view.findViewById(R.id.recyclerView);

        // ??????
        view.findViewById(R.id.rela_chongzhi).setOnClickListener(this);
        // ??????
        view.findViewById(R.id.rela_tixian).setOnClickListener(this);
        // ??????
        view.findViewById(R.id.rela_zhuanqian).setOnClickListener(this);
        // ??????
        view.findViewById(R.id.rela_customer_support).setOnClickListener(this);
    }

    @Override
    public void doBusiness() {
        // ?????????
        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                ImgLoader.display(mContext, ((BannerBean) path).getSlide_pic(), imageView);
            }
        });

        mBanner.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
//                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 15);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 0);
            }
        });

        mBanner.setClipToOutline(true);
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int p) {
                BannerBean bean = mBannerBeans.get(p);
                new BannerJumpHelper().jump(HomeHotFragment.this.mContext, bean);
            }
        });

        //??????
        mRvGame.setLayoutManager(new GridLayoutManager(mContext, 4));
        mGameAdapter = new HomeGameAdapter(mContext, mGameBeans);
        mGameAdapter.setOnItemClickListener(new HomeGameAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                //???????????????????????????
                HomeGameBean bean = mGameBeans.get(position);
                if (bean.getPlat_type().equals("cp")) {
                    //?????????????????????
                    TicketData ticketData = new TicketData();
                    ticketData.setTitle(bean.getTitle());
                    ticketData.setImage(bean.getIcon());
                    ticketData.setId(bean.getId());
                    ticketData.setType(bean.getType());
                    Intent intent = new Intent(mContext, BettingSelectActivity.class);
                    intent.putExtra("data", new Gson().toJson(ticketData));
                    mContext.startActivity(intent);
                } else {
                    //????????????
//                    registerGame(bean);
                    loginGame(bean);
                }
            }
        });
        mRvGame.setAdapter(mGameAdapter);


        // ??????
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mLiveAdapter = new LiveAdapter(mContext, mList);
        mLiveAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                if (misItemClick) {
                    return;
                }
                misItemClick = true;
                LiveBean liveBean = mList.get(position);
                if ("1".equals(liveBean.getIslive())) {
//                    ((MainActivity) mContext).watchLive(liveBean, Constants.LIVE_HOME, position);
                    ((MainActivity) mContext).watchLive(mList, liveBean, Constants.LIVE_HOME, position);
                } else {
                    ToastUtil.show(R.string.rest_time);
                }
                misItemClick = false;
            }
        });
        mRecyclerView.setAdapter(mLiveAdapter);

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

        // ?????????
        MainHttpUtil.getBannerInfo(new CommonCallback<String>() {
            @Override
            public void callback(String info) {
                mBannerBeans = JSON.parseArray(info, BannerBean.class);
                mBanner.setImages(mBannerBeans);
                mBanner.start();
            }
        });
        // ????????????
        MainHttpUtil.getNotice(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    ToastUtil.show(msg);
                    return;
                }
                List<NoticeEntry> notice = JSON.parseArray(info[0], NoticeEntry.class);
                if (null == notice || notice.size() == 0) {
                    mTvNotice.setText(WordUtil.getString(R.string.No_announcement));
                    mTvNotice.setClickable(false);
                } else {
                    mNoticeEntry = notice.get(0);
                    mTvNotice.setText(Html.fromHtml(mNoticeEntry.getContent().contains("http")?mNoticeEntry.getTitle():mNoticeEntry.getContent()));
                    mTvNotice.setClickable(true);
                }
            }
        });

        // ??????
        MainHttpUtil.getHomeAdData(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                List<HomeGameBean> games = JSON.parseArray(info[0], HomeGameBean.class);
                mGameBeans.clear();
                if(CommonAppConfig.getInstance().getSwitchPlay()) {
                    for (HomeGameBean gameBean : games) {
                        if ("Soccer".equals(gameBean.getTitle())) {
                        } else {
                            mGameBeans.add(gameBean);
                        }
                    }
                }else {
                    mGameBeans.addAll(games);
                }
                mGameAdapter.notifyDataSetChanged();
            }
        });

        getData(false);
    }

    @Override
    public void onWidgetClick(View view) {
        super.onWidgetClick(view);
        int vId = view.getId();
        if (vId == R.id.ll_see_more) {
            EventBus.getDefault().post(new MoreGame());
        } else if (vId == R.id.tv_notice) {
            //TopUpDialogFragment dialogFragment = new TopUpDialogFragment(mContext, mNoticeEntry.getTitle(), mNoticeEntry.getContent()).builder();
           // dialogFragment.show();

            WebViewsActivity.forward(mContext, HtmlConfig.HELP_CENTER,"Activity Center");
        } else if (vId == R.id.rela_chongzhi) {
            //??????
            Intent intent = new Intent(mContext, RechargeActivity.class);
            startActivity(intent);
        } else if (vId == R.id.rela_tixian) {
            // ??????
            mContext.startActivity(new Intent(mContext, WithdrawActivity.class));
        } else if (vId == R.id.rela_zhuanqian) {
            //????????????
            mContext.startActivity(new Intent(mContext, MyDistributionActivity.class));
        } else if (vId == R.id.rela_customer_support) {
            //????????????
            Intent intent = new Intent(mContext, WebViewsActivity.class);
            intent.putExtra("title", WordUtil.getString(R.string.customer_service));
            intent.putExtra("url", CommonAppConfig.getInstance().getConfig().getKefuUrl());
            mContext.startActivity(intent);
        }
    }

    /**
     * ????????????
     *
     * @param isRefresh ??????????????????
     */
    private void getData(final boolean isRefresh) {
        if(mPage<=1 || searchTime==0)
            searchTime = System.currentTimeMillis()/1000;

        MainHttpUtil.getHotData(mPage,searchTime,new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                List<LiveBean> liveBeans = JSON.parseArray(info[0], LiveBean.class);
                /* if (null == liveBeans || liveBeans.isEmpty()) {
                    return;
                }*/

                if (!isRefresh) {
                    mList.clear();
                }
                if (!liveBeans.isEmpty()) {
                    for (LiveBean bean : liveBeans) {
                        if (mList.contains(bean)) {
                            continue;
                        }


                        if(CommonAppConfig.getInstance().getSwitchStatu()) {
                            if(2==bean.getIsvideo()||1==bean.getIsvideo()){
                                mList.add(bean);
                            }
                        }else{
                            mList.add(bean);
                        }

                        //mList.add(bean);
                    }
                }
                mLiveAdapter.notifyDataSetChanged();
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

    /**
     * ????????????
     */
    private void registerGame(final HomeGameBean bean) {
        MainHttpUtil.ngRegister(bean.getGame_code(), bean.getGame_type(), bean.getPlat_type(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    ToastUtil.show(msg);
                    return;
                }
                loginGame(bean);
            }
        });
    }

    /**
     * ????????????
     */
    private void loginGame(final HomeGameBean bean) {
        CommonHttpUtil.ngLogin(bean.getPlat_type(), bean.getGame_type(), bean.getGame_code(), new HttpCallback() {
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
                        RouteUtil.toGame(null, loginBean.getData(), bean.getPlat_type(), coin, null);
                    }
                });

            }
        });
    }


    @Override
    public void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.HOME_GET_SLIDE);
        MainHttpUtil.cancel(MainHttpConsts.HOME_GET_INDEX_MSG);
        MainHttpUtil.cancel(MainHttpConsts.HOME_GET_HOT);
        super.onDestroy();
    }
}