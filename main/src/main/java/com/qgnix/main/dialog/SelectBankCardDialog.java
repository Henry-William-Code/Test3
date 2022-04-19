package com.qgnix.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.qgnix.common.custom.ItemDecoration;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.adapter.SelectBankCardAdapter;
import com.qgnix.main.bean.BankCardBean;
import com.qgnix.main.interfaces.OnSelectBankCardListener;

import java.util.List;

/**
 * 选择银行卡dialog
 */
public class SelectBankCardDialog extends BaseCustomDialog {

    private final OnSelectBankCardListener mOnSelectBankCardListener;
    /**
     * 银行卡信息
     */
    private final List<BankCardBean> mData;

    private final Context mContext;

    public SelectBankCardDialog(Context context, List<BankCardBean> data, OnSelectBankCardListener onSelectBankCardListener) {
        super(context);
        this.mOnSelectBankCardListener = onSelectBankCardListener;
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_select_bank_card;
    }

    @Override
    public void initView() {
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.addItemDecoration(new ItemDecoration(mContext));
        SelectBankCardAdapter adapter = new SelectBankCardAdapter(mContext, mData);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                mOnSelectBankCardListener.onSelectBankCard(mData.get(position));
                dismiss();
            }
        });
        rv.setAdapter(adapter);
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void initData() {

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
