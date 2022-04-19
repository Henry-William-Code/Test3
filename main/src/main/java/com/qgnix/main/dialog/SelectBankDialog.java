package com.qgnix.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.qgnix.common.custom.ItemDecoration;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.adapter.SelectBankAdapter;
import com.qgnix.main.bean.BankNameBean;
import com.qgnix.main.http.MainHttpUtil;
import com.qgnix.main.interfaces.OnSelectBankListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择银行dialog
 */
public class SelectBankDialog extends BaseCustomDialog {

    private final OnSelectBankListener mOnSelectBankListener;

    private SelectBankAdapter mAdapter;
    private final List<BankNameBean> mData = new ArrayList<>();

    private final Context mContext;
    /**
     * 货币类型
     */
    private final String mCurrency;

    public SelectBankDialog(Context context, String currency, OnSelectBankListener onSelectBankListener) {
        super(context);
        this.mOnSelectBankListener = onSelectBankListener;
        this.mContext = context;
        this.mCurrency = currency;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_select_bank;
    }

    @Override
    public void initView() {
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.addItemDecoration(new ItemDecoration(mContext));
        mAdapter = new SelectBankAdapter(mContext, mData);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                mOnSelectBankListener.onSelectBank(mData.get(position));
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
        MainHttpUtil.getBankList(mCurrency, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                JsonArray jsonArray = new JsonParser().parse(info[0]).getAsJsonArray();
                List<BankNameBean> beans = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<BankNameBean>>() {
                }.getType());
                mData.clear();
                mData.addAll(beans);
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
