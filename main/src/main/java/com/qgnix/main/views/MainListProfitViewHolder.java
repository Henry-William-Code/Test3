package com.qgnix.main.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.adapter.RefreshAdapter;
import com.qgnix.common.custom.CommonRefreshView;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.main.R;
import com.qgnix.main.adapter.MainListAdapter;
import com.qgnix.main.bean.ListBean;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/27.
 * 首页 排行 收益榜
 */

public class MainListProfitViewHolder extends AbsMainListChildViewHolder {
    RadioButton btn_day, btn_week, btn_month, btn_total, btn_yesterday;

    public MainListProfitViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {
        super.init();
        btn_day = (RadioButton) findViewById(R.id.btn_day);
        btn_week = (RadioButton) findViewById(R.id.btn_week);
        btn_month = (RadioButton) findViewById(R.id.btn_month);
        btn_total = (RadioButton) findViewById(R.id.btn_total);
        btn_yesterday = (RadioButton) findViewById(R.id.btn_yesterday);

        btn_day.setVisibility(View.VISIBLE);
        btn_week.setVisibility(View.GONE);
        btn_month.setVisibility(View.GONE);
        btn_total.setVisibility(View.GONE);
        btn_yesterday.setVisibility(View.VISIBLE);

        btn_day.setChecked(true);
        btn_day.setOnClickListener(this);
        btn_week.setOnClickListener(this);
        btn_month.setOnClickListener(this);
        btn_total.setOnClickListener(this);
        btn_yesterday.setOnClickListener(this);
        btn_day.setOnClickListener(this);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<ListBean>() {
            @Override
            public RefreshAdapter<ListBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainListAdapter(mContext, MainListAdapter.TYPE_PROFIT);
                    mAdapter.setOnItemClickListener(MainListProfitViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (!mType.isEmpty()) {
                    MainHttpUtil.profitList(mType, p, callback);
                }
            }

            @Override
            public List<ListBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), ListBean.class);
            }

            @Override
            public void onRefreshSuccess(List<ListBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<ListBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainHttpUtil.cancel(MainHttpConsts.PROFIT_LIST);
    }


}
