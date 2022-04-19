package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.BankCardBean;

import java.util.List;

/**
 * 选择银行账户适配器
 */
public class SelectBankCardAdapter extends BaseRecyclerViewAdapter<SelectBankCardAdapter.ViewHolder> {
    private final List<BankCardBean> mData;

    public SelectBankCardAdapter(Context context, List<BankCardBean> mData) {
        super(context);
        this.mData = mData;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_dialog_select_card_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BankCardBean cardBean = mData.get(position);
        holder.tvBankName.setText(cardBean.getBank_name());
        holder.tvCardAccount.setText(cardBean.getBank_number());
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        /**
         * 银行名称
         */
        TextView tvBankName;
        /**
         * 卡号
         */
        TextView tvCardAccount;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvBankName = (TextView) findViewById(R.id.tv_bank_name);
            tvCardAccount = (TextView) findViewById(R.id.tv_card_account_no);
        }
    }
}
