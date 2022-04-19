package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.RechargeAmountBean;

import java.util.List;

/**
 * 充值金额适配器
 */
public class RechargeAmountAdapter extends BaseRecyclerViewAdapter<RechargeAmountAdapter.ViewHolder> {

    private final List<RechargeAmountBean> mData;

    public RechargeAmountAdapter(Context context, List<RechargeAmountBean> data) {
        super(context);
        this.mData = data;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup, R.layout.item_recharge_amount_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        RechargeAmountBean bean = mData.get(position);
        viewHolder.tvPrice.setText(bean.getAmount());
        viewHolder.tvPrice.setSelected(bean.isSelect());

    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        TextView tvPrice;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvPrice = (TextView) findViewById(R.id.tv_price);
        }
    }
}
