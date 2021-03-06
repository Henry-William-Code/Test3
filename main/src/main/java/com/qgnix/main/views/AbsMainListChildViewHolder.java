package com.qgnix.main.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.qgnix.common.custom.CommonRefreshView;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.interfaces.OnItemClickListener;
import com.qgnix.common.utils.RouteUtil;
import com.qgnix.main.R;
import com.qgnix.main.adapter.MainListAdapter;
import com.qgnix.main.bean.ListBean;

/**
 * Created by cxf on 2019/2/23.
 */

public abstract class AbsMainListChildViewHolder extends AbsMainViewHolder implements OnItemClickListener<ListBean>, View.OnClickListener {

    public static final String DAY = "day";
    public static final String WEEK = "week";
    public static final String MONTH = "month";
    public static final String TOTAL = "total";
    public static final String YESTERDAY = "yesterday";// 昨天
    protected String mType;
    protected CommonRefreshView mRefreshView;
    protected MainListAdapter mAdapter;


    public AbsMainListChildViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
        mType = DAY;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.view_main_list_page;
    }

    @Override
    public void init() {
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_list);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

    }

    @Override
    public void loadData() {
        if (!isFirstLoadData()) {
            return;
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
    }

    public void onFollowEvent(String touid, int isAttention) {
        if (mAdapter != null) {
            mAdapter.updateItem(touid, isAttention);
        }
    }

    @Override
    public void onItemClick(ListBean bean, int position) {
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        String type = null;
        if (i == R.id.btn_day) {
            type = DAY;
        } else if (i == R.id.btn_week) {
            type = WEEK;
        } else if (i == R.id.btn_month) {
            type = MONTH;
        } else if (i == R.id.btn_total) {
            type = TOTAL;
        } else if (i == R.id.btn_yesterday) {
            type = YESTERDAY;
        }
        if (!TextUtils.isEmpty(type) && !type.equals(mType)) {
            mType = type;
            if (mRefreshView != null) {
                mRefreshView.initData();
            }
        }
    }

}
