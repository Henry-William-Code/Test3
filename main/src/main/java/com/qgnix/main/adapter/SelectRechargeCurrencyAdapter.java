package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;

/**
 * 选择充值货币类型适配器
 */
public class SelectRechargeCurrencyAdapter extends BaseRecyclerViewAdapter<SelectRechargeCurrencyAdapter.ViewHolder> {
    private final String[] mData;

    public SelectRechargeCurrencyAdapter(Context context,String[] data) {
        super(context);
        this.mData = data;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_dialog_select_recharge_currency_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvCurrency.setText(mData[position]);
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.length;
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        TextView tvCurrency;
        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvCurrency = (TextView) findViewById(R.id.tv_currency);
        }
    }
}
