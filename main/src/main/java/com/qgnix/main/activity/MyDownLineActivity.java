package com.qgnix.main.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.KeyboardUtils;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.adapter.MyDownLineAdapter;
import com.qgnix.main.bean.MyDownLineBean;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 我的下线
 */
public class MyDownLineActivity extends AbsActivity implements View.OnClickListener, MyDownLineAdapter.OnWeedOutListener {
    /**
     * 搜索关键字
     */
    private EditText mEtKeywords;

    private RecyclerView mRv;

    private SmartRefreshLayout mRefresh;

    private List<MyDownLineBean> mList = new ArrayList<>();

    private MyDownLineAdapter mAdapter;
    /**
     * 页数
     */
    private int mPage = 1;
    /**
     * 搜索关键字
     */
    private String mKeyWords = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_down_line;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.my_downline));
        mEtKeywords = findViewById(R.id.et_keywords);
        mRefresh = findViewById(R.id.refresh);
        mRv = findViewById(R.id.rv);
        findViewById(R.id.btn_search).setOnClickListener(this);


        mAdapter = new MyDownLineAdapter(mContext, mList);
        mRv.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                MyDownLineBean bean = mList.get(position);
                String profit = bean.getProfit();
                if (TextUtils.isEmpty(profit) || "0".equals(profit)) {
                    ToastUtil.show(R.string.rebate_amount_is_zero);
                    return;
                }
                //返点金额
                Intent intent = new Intent(mActivity, WithdrawActivity.class);
                intent.putExtra("balanceType", 2);
                intent.putExtra("rebateAmount", profit);
                startActivity(intent);
            }
        });
        mRv.setAdapter(mAdapter);
        // 踢除回调
        mAdapter.setOnWeedOutListener(this);


        mRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                ++mPage;
                getData(mKeyWords, true);
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mPage = 1;
                getData(mKeyWords, false);
            }
        });

        getData(mKeyWords, false);

        mEtKeywords.setOnClickListener(this);
    }

    /**
     * 获取数据
     */
    private void getData(String keywords, final boolean isLoadMore) {
        MainHttpUtil.getAgentSubMember(keywords, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                L.e("====", new Gson().toJson(info));
                if (code != 0) {
                    ToastUtil.show(msg);
                }
                if (info.length == 0) {
                    return;
                }
                MyDownLineBean[] lists = new Gson().fromJson(Arrays.toString(info), MyDownLineBean[].class);
                if (null == lists || lists.length == 0) {
                    mRefresh.setEnableLoadMore(false);
                    return;
                }
                if (!isLoadMore) {
                    mList.clear();
                }
                Collections.addAll(mList,lists);
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
                if (!TextUtils.isEmpty(mKeyWords)) {
                    mKeyWords = "";
                    mEtKeywords.setText("");
                    KeyboardUtils.hideSoftInput(mActivity);
                    mEtKeywords.clearFocus();
                    mEtKeywords.setFocusable(false);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.btn_search) {
            mPage = 1;
            mKeyWords = mEtKeywords.getText().toString().trim();
            getData(mKeyWords, false);
        } else if (vId == R.id.et_keywords) {
            mEtKeywords.setFocusable(true);
            mEtKeywords.setFocusableInTouchMode(true);
            mEtKeywords.requestFocus();
        }
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.AGENT_GET_SUB_MEMBER);
        super.onDestroy();
    }

    /**
     * 踢除回调
     */
    @Override
    public void onWeedOut() {
        ToastUtil.show("踢除回调");
    }
}