package com.qgnix.main.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.google.gson.Gson;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.custom.ItemDecoration;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.CopyUtils;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.main.adapter.SubMemberAdapter;
import com.qgnix.main.bean.MyDistributionBean;
import com.qgnix.main.bean.SubMemberBean;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 我的分销
 */
public class MyDistributionActivity extends AbsActivity implements View.OnClickListener {
    /**
     * 可提现金额
     */
    private TextView mTvCashAmount;
    /**
     * 下线总数
     */
    private TextView mTvTotalNumberOfOffline;
    /**
     * 投注总额
     */
    private TextView mTvTotalBet;
    /**
     * 送礼总额
     */
    private TextView mTvTotalGifts;
    /**
     * 送礼总额
     */
    private TextView mTvTotalRebate;
    /**
     * 开始时间
     */
    private TextView mTvStartTime;

    /**
     * 结束时间
     */
    private TextView mTvEndTime;

    /**
     * 下拉刷新
     */
    private SmartRefreshLayout mRefresh;

    /**
     * 页数
     */
    private int mPage = 1;
    /**
     * rv
     */
    private RecyclerView mRecyclerView;

    private SubMemberAdapter mAdapter;

    private List<SubMemberBean> mList = new ArrayList<>();


    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private String mCashAmount = "0";

    private String mShareLink;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_distribution;
    }

    @Override
    protected void main() {
        //标题
        setTitle(WordUtil.getString(R.string.make_money));

        // 下级信息
        mRefresh = findViewById(R.id.refresh);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.addItemDecoration(new ItemDecoration(mContext));

        //
        mTvCashAmount = findViewById(R.id.tv_cash_amount);
        findViewById(R.id.btn_withdraw).setOnClickListener(this);

        mTvTotalNumberOfOffline = findViewById(R.id.tv_total_number_of_offline);
        mTvTotalBet = findViewById(R.id.tv_total_bet);
        mTvTotalGifts = findViewById(R.id.tv_total_gifts);
        mTvTotalRebate = findViewById(R.id.tv_total_rebate);

        mTvStartTime = findViewById(R.id.tv_start_time);
        mTvEndTime = findViewById(R.id.tv_end_time);
        mTvStartTime.setOnClickListener(this);
        mTvEndTime.setOnClickListener(this);

        // 分享
        findViewById(R.id.btn_copy_share).setOnClickListener(this);
        // 搜索
        findViewById(R.id.btn_search).setOnClickListener(this);

        // 时间默认三个月
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        mTvEndTime.setText(DateFormat.format(DATE_FORMAT, date).toString());
        calendar.add(Calendar.MONTH, -3);
        date = calendar.getTime();
        mTvStartTime.setText(DateFormat.format(DATE_FORMAT, date).toString());

        //加载数量
        getData();

        // 下属信息
        mAdapter = new SubMemberAdapter(mContext, mList);
        mRecyclerView.setAdapter(mAdapter);

        mRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                ++mPage;
                getSubMemberData(true);
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mPage = 1;
                getSubMemberData(false);
            }
        });

        getSubMemberData(false);


    }


    /**
     * 获取数据
     *
     * @param isRefresh 是否下拉刷新
     */
    private void getSubMemberData(final boolean isRefresh) {
        String startTime = mTvStartTime.getText().toString().trim();
        String endTime = mTvEndTime.getText().toString().trim();
        MainHttpUtil.getAgentSubMemberNew(mPage, startTime, endTime, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    ToastUtil.show(msg);
                    return;
                }
                List<SubMemberBean> lists = JSON.parseArray(Arrays.toString(info), SubMemberBean.class);
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

    private void getData() {
        MainHttpUtil.getAgentSummary(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                L.d("====getAgentSummary==", new Gson().toJson(info));
                if (code != 0) {
                    ToastUtil.show(msg);
                }
                if (info.length == 0) {
                    return;
                }
                MyDistributionBean bean = new Gson().fromJson(info[0], MyDistributionBean.class);
                if (null == bean) {
                    return;
                }

                handleToData(bean);
            }

            @Override
            public void onError() {
                resetData();
            }
        });
    }

    /**
     * 处理数据
     *
     * @param bean
     */
    private void handleToData(MyDistributionBean bean) {
        // 分享链接
        mShareLink = bean.getLink();
        // 分销信息
        MyDistributionBean.Summary summary = bean.getSummary();
        if (summary == null) {
            resetData();
        } else {
            //可提现金额
            mCashAmount = summary.getKtjje();
            mTvCashAmount.setText(TextUtils.isEmpty(mCashAmount) ? "0" : mCashAmount);

            mTvTotalNumberOfOffline.setText(TextUtils.isEmpty(summary.getXxzrs()) ? "0" : summary.getXxzrs());
            mTvTotalBet.setText(TextUtils.isEmpty(summary.getTzzje()) ? "0" : summary.getTzzje());
            mTvTotalGifts.setText(TextUtils.isEmpty(summary.getSlzje()) ? "0" : summary.getSlzje());
            mTvTotalRebate.setText(TextUtils.isEmpty(summary.getFdzje()) ? "0" : summary.getFdzje());
        }
    }

    public void resetData() {
        mTvCashAmount.setText("0");

        mTvTotalNumberOfOffline.setText("0");
        mTvTotalBet.setText("0");
        mTvTotalGifts.setText("0");
        mTvTotalRebate.setText("0");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.AGENT_GET_AGENT_SUMMARY);
        MainHttpUtil.cancel(MainHttpConsts.AGENT_GET_SUB_MEMBER_NER);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.btn_withdraw) {
            Intent intent = new Intent(mActivity, WithdrawActivity.class);
            intent.putExtra("balanceType", 2);
            intent.putExtra("rebateAmount", mCashAmount);
            startActivity(intent);
        } else if (vId == R.id.btn_search) {
            mPage = 1;
            getSubMemberData(false);
        } else if (vId == R.id.tv_start_time) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -3);
            selectData(mTvStartTime, calendar);
        } else if (vId == R.id.tv_end_time) {
            selectData(mTvEndTime, Calendar.getInstance());
        } else if (vId == R.id.btn_copy_share) {
            // 复制分享链接
            if (!TextUtils.isEmpty(mShareLink)) {
                CopyUtils.copy(mContext, mShareLink);
            }
        }

    }

    /**
     * 现在日期
     *
     * @param tv
     */
    private void selectData(final TextView tv, Calendar calendar) {
        //时间选择器
        new TimePickerBuilder(mContext, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv.setText(DateFormat.format(DATE_FORMAT, date));
            }
        }).setDate(calendar)
                .build().show();
    }
}