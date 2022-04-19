package com.qgnix.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qgnix.common.cache.CacheData;
import com.qgnix.common.custom.ItemDecoration;
import com.qgnix.common.fragment.BaseFragment;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.adapter.DrawHistoryAdapter;
import com.qgnix.live.bean.DrawHistoryBean;
import com.qgnix.live.http.LiveHttpConsts;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.TicketData;
import com.qgnix.main.R;
import com.qgnix.main.activity.DrawHistoryDetailActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 开奖
 */
public class HomeDrawFragment extends BaseFragment {


    private SmartRefreshLayout mRefresh;

    private DrawHistoryAdapter mAdapter;

    private List<DrawHistoryBean> mList = new ArrayList<>();
    /**
     * 页数
     */
    private int mPage = 1;
    /**
     * rv
     */
    private RecyclerView mRecyclerView;
    private List<TicketData> mIconData = new ArrayList<>();
    public static HomeDrawFragment newInstance() {
        return new HomeDrawFragment();
    }


    @Override
    public void initData( Bundle bundle) {

    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_home_draw;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        mRefresh = view.findViewById(R.id.refresh);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.addItemDecoration(new ItemDecoration(mContext));

    }

    @Override
    public void doBusiness() {
        mAdapter = new DrawHistoryAdapter(mContext, mList,mIconData);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                DrawHistoryBean bean = mList.get(position);
                Intent intent = new Intent(mContext, DrawHistoryDetailActivity.class);
                intent.putExtra("tId", bean.getT_id());
                intent.putExtra("tType", bean.getType());
                intent.putExtra("tName", bean.getName());
                mContext.startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

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

        getData(false);
        getIconData();
    }

    /**
     * 获取数据
     *
     * @param isRefresh 是否下拉刷新
     */
    private void getData(final boolean isRefresh) {
        LiveHttpUtil.getOpenLotteryHistory(mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }

                List<DrawHistoryBean> lists = new Gson().fromJson(Arrays.toString(info), new TypeToken<List<DrawHistoryBean>>() {
                }.getType());
                if (null == lists || lists.isEmpty()) {
                    return;
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
    public void getIconData() {
        String ticketData = CacheData.getTicketData();
        if (!TextUtils.isEmpty(ticketData)) {
            L.e("===本地获取数据===");
            List<TicketData> lists = JSON.parseArray(ticketData, TicketData.class);
            mIconData.clear();
            mIconData.addAll(lists);
            mAdapter.notifyDataSetChanged();
            return;
        }
        CommonHttpUtil.getBCTicket(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                L.e("===网络获取数据===");
                // 保存数据到本地
                CacheData.setTicketData(info[0]);
                List<TicketData> lists = JSON.parseArray(info[0], TicketData.class);
                mIconData.clear();
                mIconData.addAll(lists);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.OPEN_LOTTERY_API);
        super.onDestroy();
    }
}