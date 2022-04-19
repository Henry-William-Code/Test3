package com.qgnix.main.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.fragment.BaseFragment;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.bean.LiveBean;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.activity.MainActivity;
import com.qgnix.main.activity.SearchActivity;
import com.qgnix.main.adapter.LiveAdapter;
import com.qgnix.main.bean.LiveTypeBean;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.qgnix.main.interfaces.OnNavigatorClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直播fragment
 */
public class LiveFragment extends BaseFragment {

    /**
     * 刷新view
     */
    private SmartRefreshLayout mRefresh;
    /**
     * rv
     */
    private RecyclerView mRecyclerView;

    /**
     * 指示器
     */
    private MagicIndicator mMagicIndicator;

    /**
     * 分类id
     */
    private String mCategoryId;

    /**
     * 页数
     */
    private int mPage = 1;
    private long searchTime = 0;
    private int selectIndex = 1;
    /**
     * 直播数据
     */
    private final List<LiveBean> mLiveBeans = new ArrayList<>();
    /**
     * 直播数据适配器
     */
    private LiveAdapter mLiveAdapter;
    /**
     * item 是否已点击
     */
    private boolean misItemClick;

    public static LiveFragment newInstance() {
        return new LiveFragment();
    }

    @Override
    public void initData( Bundle bundle) {


    }

    /**
     * 处理指示器
     */
    private void handleMagicIndicator(final List<LiveTypeBean> categoryBeans) {

        final int size = categoryBeans.size();
        final String[] titles = new String[size];
        for (int i = 0; i < size; i++) {
            titles[i] = categoryBeans.get(i).getName();
        }

        //指示器适配器
        NavigatorAdapter adapter = new NavigatorAdapter(mContext, titles, new int[]{R.color.color1, R.color.color2});
        adapter.setOnNavigatorClickListener(new OnNavigatorClickListener() {
            @Override
            public void onNavigatorClick(int index) {
                selectIndex = index+1;
                mMagicIndicator.onPageSelected(index);
                mMagicIndicator.onPageScrolled(index, 0.0F, 0);

                mCategoryId = categoryBeans.get(index).getId();
                // 清空上一个tab数据
                mLiveBeans.clear();
                mLiveAdapter.onDataChange(mLiveBeans);
                getData(false);
            }
        });
        // 指示器
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(adapter);
        mMagicIndicator.setNavigator(commonNavigator);

        mCategoryId = categoryBeans.get(0).getId();
        getData(false);


    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_live;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {

        // 搜索
        view.findViewById(R.id.btn_live_search).setOnClickListener(this);

        mRefresh = view.findViewById(R.id.refresh);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        // 指示器
        mMagicIndicator = view.findViewById(R.id.magic_indicator);
    }

    @Override
    public void doBusiness() {
        //获取直播tab
        MainHttpUtil.getLivecate(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                List<LiveTypeBean> categoryBeans = JSON.parseArray(Arrays.toString(info), LiveTypeBean.class);
                handleMagicIndicator(categoryBeans);
            }
        });

        mLiveAdapter = new LiveAdapter(mContext, mLiveBeans);
        mLiveAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                if (misItemClick) {
                    return;
                }
                misItemClick = true;
                // 观看直播
//                ((MainActivity) mContext).watchLive(mLiveBeans.get(position), Constants.LIVE_HOME, position);
                ((MainActivity) mContext).watchLive(mLiveBeans, mLiveBeans.get(position), Constants.LIVE_HOME, position);
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

    }


    /**
     * @param isLoadMore 是否加载更多
     */
    private void getData(final boolean isLoadMore) {
        if(mPage<=1 || searchTime==0) {
            searchTime = System.currentTimeMillis() / 1000;
        }
        if (!isLoadMore) {
            mPage = 1;
        }
        Map<String, String> map = new HashMap<>();
        map.put("p", String.valueOf(mPage));
        map.put("pnum", "20");
        map.put("liveclassid", mCategoryId);
        map.put("uid", CommonAppConfig.getInstance().getUid());
        map.put("token", CommonAppConfig.getInstance().getToken());
        map.put("isAdult", String.valueOf(CommonAppConfig.ADULT_MODE));
        map.put("searchTime",""+searchTime);
        MainHttpUtil.getClassLive(map, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                List<LiveBean> lists = JSON.parseArray(Arrays.toString(info), LiveBean.class);

                if (!isLoadMore) {
                    mLiveBeans.clear();
                }
                if (!lists.isEmpty()){
                    for (LiveBean bean : lists) {
                        if (mLiveBeans.contains(bean)){
                            continue;
                        }

                        if(CommonAppConfig.getInstance().getSwitchStatu()) {
                            if(2==bean.getIsvideo()||1==bean.getIsvideo()){
                                mLiveBeans.add(bean);
                            }
                        }else{
                            mLiveBeans.add(bean);
                        }
                    }
                }
                mLiveAdapter.onDataChange(mLiveBeans);
            }


            @Override
            public void onFinish() {
                super.onFinish();
                if (isLoadMore) {
                    mRefresh.finishLoadMore();
                } else {
                    mRefresh.finishRefresh();
                }
            }
        });
    }

    @Override
    public void onWidgetClick(View view) {
        super.onWidgetClick(view);
        if (view.getId() == R.id.btn_live_search) {
            // 搜索
            SearchActivity.forward(mContext,selectIndex);
        }
    }

    @Override
    public void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.MAIN_LIVE_VIEW_HOLDER);
        MainHttpUtil.cancel(MainHttpConsts.GET_LIVECATE);
        super.onDestroy();
    }
}