package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.CurrencyBean;

import java.util.List;

/**
 * 选择货币类型适配器
 */
public class SelectCurrencyAdapter extends BaseRecyclerViewAdapter<SelectCurrencyAdapter.ViewHolder> {
    private final List<CurrencyBean> mData;

    public SelectCurrencyAdapter(Context context, List<CurrencyBean> mData) {
        super(context);
        this.mData = mData;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_dialog_select_bank_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CurrencyBean bean = mData.get(position);
        holder.tvBankName.setText(bean.getValue());
        holder.tvrate.setText( bean.getRate()+ " COIN/"+ bean.getKey());
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        TextView tvBankName;
        TextView tvrate;
        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvBankName = (TextView) findViewById(R.id.tv_bank_name);
            tvrate= (TextView) findViewById(R.id.tv_rate);
        }
    }
}
