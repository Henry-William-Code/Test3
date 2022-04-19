package com.qgnix.main.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.main.adapter.MyRecordAdapter;
import com.qgnix.main.bean.MyRecordBean;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 我的报表
 */
public class MyRecordActivity extends AbsActivity {

    private RecyclerView mRv;

    private SmartRefreshLayout mRefresh;

    private List<MyRecordBean> mList = new ArrayList<>();

    private MyRecordAdapter mAdapter;
    private String mtype="";
    /**
     * 页数
     */
    private int mPage = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_record;
    }

    @Override
    protected void main() {
        super.main();

        setTitle(WordUtil.getString(R.string.my_report));
        Intent intent = getIntent();
        mtype = intent.getStringExtra("type");
        mRefresh = findViewById(R.id.refresh);
        mRv = findViewById(R.id.rv);

        mAdapter = new MyRecordAdapter(mContext, mList);
        mRv.setLayoutManager(new LinearLayoutManager(mContext));
        mRv.setAdapter(mAdapter);

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
    }

    /**
     * 获取数据
     */
    private void getData(final boolean isLoadMore) {
        MainHttpUtil.getAgentSubRecord(mPage,mtype, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                L.e("====", new Gson().toJson(info));
                if (code != 0) {
                    ToastUtil.show(msg);
                }
                if (info.length == 0) {
                    return;
                }
                MyRecordBean[] lists = new Gson().fromJson(Arrays.toString(info), MyRecordBean[].class);
                if (null == lists || lists.length == 0) {
                    mRefresh.setEnableLoadMore(false);
                    return;
                }
                List<MyRecordBean> beans = Arrays.asList(lists);
                if (isLoadMore) {
                    mList.addAll(beans);
                } else {
                    mList = beans;
                }
                mAdapter.onDataChange(mList);
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
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.AGENT_GET_SUB_MEMBER_FIT_RECORD);
        super.onDestroy();
    }
}