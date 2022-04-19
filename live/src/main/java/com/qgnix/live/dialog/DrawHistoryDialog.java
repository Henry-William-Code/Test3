package com.qgnix.live.dialog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qgnix.common.CommonAppContext;
import com.qgnix.common.custom.ItemDecoration;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.R;
import com.qgnix.live.adapter.DrawHistoryAdapter;
import com.qgnix.live.adapter.DrawHistoryDetailAdapter;
import com.qgnix.live.bean.DrawHistoryBean;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 开奖记录
 */
public class DrawHistoryDialog extends BaseCustomDialog {
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

    private Context mContext;

    public DrawHistoryDialog(Context context,String tId) {
        super(context);
        this.mContext = context;
        this.mTId = tId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_draw_history;
    }

    @Override
    public void initView() {
        mRefresh = findViewById(com.qgnix.live.R.id.refresh);
        RecyclerView recyclerView = findViewById(com.qgnix.live.R.id.recyclerView);
        recyclerView.addItemDecoration(new ItemDecoration(mContext));
        mAdapter = new DrawHistoryDetailAdapter(mContext, CommonAppContext.map.get(mTId)+"", mList);
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

    @Override
    public void initData() {
        getData(false);
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
    public boolean isCancelable() {
        return true;
    }

    @Override
    public int showGravity() {
        return Gravity.BOTTOM;
    }
}
