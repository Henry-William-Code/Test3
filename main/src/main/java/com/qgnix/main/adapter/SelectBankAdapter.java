package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.BankNameBean;

import java.util.List;

/**
 * 选择银行适配器
 */
public class SelectBankAdapter extends BaseRecyclerViewAdapter<SelectBankAdapter.ViewHolder> {
    private final List<BankNameBean> mData;

    public SelectBankAdapter(Context context, List<BankNameBean> mData) {
        super(context);
        this.mData = mData;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_dialog_select_bank_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvBankName.setText(mData.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        TextView tvBankName;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvBankName = (TextView) findViewById(R.id.tv_bank_name);
        }
    }
}
