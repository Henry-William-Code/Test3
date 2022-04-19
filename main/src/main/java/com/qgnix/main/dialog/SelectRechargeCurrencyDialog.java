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
import com.qgnix.main.adapter.SelectRechargeCurrencyAdapter;
import com.qgnix.main.interfaces.OnSelectRechargeCurrencyListener;

/**
 * 选择充值币种
 */
@Deprecated
public class SelectRechargeCurrencyDialog extends BaseCustomDialog {
    /**
     * 币种
     */
    private final String[] currency;

    private final Context mContext;
    /**
     * 币种回调
     */
    private OnSelectRechargeCurrencyListener mCurrencyListener;

    public void setCurrencyListener(OnSelectRechargeCurrencyListener currencyListener) {
        this.mCurrencyListener = currencyListener;
    }

    public SelectRechargeCurrencyDialog(Context context, String[] currency) {
        super(context);
        this.mContext = context;
        this.currency = currency;
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
        SelectRechargeCurrencyAdapter adapter = new SelectRechargeCurrencyAdapter(mContext, currency);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                mCurrencyListener.onSelectCurrency(currency[position]);
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
        return false;
    }

    @Override
    public int showGravity() {
        return Gravity.BOTTOM;
    }


}
