package com.qgnix.live.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.R;
import com.qgnix.live.adapter.BettingRecordAdapter;
import com.qgnix.live.bean.BettingBean;
import com.qgnix.live.http.LiveHttpUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 投注记录dialog
 */
public class BettingRecordDialog extends BaseCustomDialog {
    private SmartRefreshLayout refresh;
    private RecyclerView recyclerView;

    private BettingRecordAdapter adapter;

    private List<BettingBean> list = new ArrayList<>();
    /**
     * 页数
     */
    private int mPage = 1;
    /**
     * 彩票id
     */
    private String mTId="";
    private final Context mContext;


    public BettingRecordDialog(Context context,String tId) {
        super(context);
        this.mContext = context;
        this.mTId = tId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_betting_record;
    }

    @Override
    public void initView() {
        refresh = findViewById(R.id.refresh);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new BettingRecordAdapter(mContext, list);
        recyclerView.setAdapter(adapter);

        refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
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

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public int showGravity() {
        return Gravity.BOTTOM;
    }

    public void getData(boolean isRefresh) {
        LiveHttpUtil.getBettingRecord(mTId,mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    ToastUtil.show(msg);
                    return;
                }
                if (info.length == 0) {
                    return;
                }
                List<BettingBean> lists = JSON.parseArray(info[0],BettingBean.class);
                if (null == lists || lists.isEmpty()) {
                    refresh.setEnableLoadMore(false);
                }
                if (isRefresh) {
                    list.addAll(lists);
                } else {
                    list = lists;
                }
                adapter.onDataChange(list);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isRefresh) {
                    refresh.finishLoadMore();
                } else {
                    refresh.finishRefresh();
                }
            }
        });
    }
}
