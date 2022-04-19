package com.qgnix.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.custom.ItemDecoration;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.adapter.SelectCurrencyAdapter;
import com.qgnix.main.bean.CurrencyBean;
import com.qgnix.main.http.MainHttpUtil;
import com.qgnix.main.interfaces.OnSelectCurrencyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择银行dialog
 */
public class SelectCurrencyDialog extends BaseCustomDialog {

    private final OnSelectCurrencyListener mOnSelectCurrencyListener;

    private SelectCurrencyAdapter mAdapter;
    private final List<CurrencyBean> mData = new ArrayList<>();

    private final Context mContext;


    public SelectCurrencyDialog(Context context, OnSelectCurrencyListener onSelectCurrencyListener) {
        super(context);
        this.mOnSelectCurrencyListener = onSelectCurrencyListener;
        this.mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_select_currency;
    }

    @Override
    public void initView() {
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.addItemDecoration(new ItemDecoration(mContext));
        mAdapter = new SelectCurrencyAdapter(mContext, mData);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                mOnSelectCurrencyListener.onSelectCurrency(mData.get(position));
                dismiss();
            }
        });
        rv.setAdapter(mAdapter);
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void initData() {
        MainHttpUtil.getCurrency(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                List<CurrencyBean> currencyBeans = JSON.parseArray(info[0], CurrencyBean.class);
                mData.clear();
                mData.addAll(currencyBeans);
                mAdapter.notifyDataSetChanged();
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
