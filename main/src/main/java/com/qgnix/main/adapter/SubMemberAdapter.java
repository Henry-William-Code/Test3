package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.SubMemberBean;

import java.util.List;

public class SubMemberAdapter extends BaseRecyclerViewAdapter<SubMemberAdapter.ViewHolder> {


    private List<SubMemberBean> mList;

    public SubMemberAdapter(Context context, List<SubMemberBean> list) {
        super(context);
        this.mList = list;
    }

    public void onDataChange(List<SubMemberBean> list) {
        this.mList = list;
        this.notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup, R.layout.item_sub_member_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        SubMemberBean bean = mList.get(i);
        holder.tvMemberId.setText(bean.getId());
        holder.tvFirstDepositAmount.setText(TextUtils.isEmpty(bean.getFirstRecharge()) ? "0.00" : bean.getFirstRecharge());
        holder.tvTotalBet.setText(TextUtils.isEmpty(bean.getTzcion()) ? "0.00" : bean.getTzcion());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        /**
         * 会员ID
         */
        TextView tvMemberId;
        /**
         * 首存金额
         */
        TextView tvFirstDepositAmount;
        /**
         * 投注总额
         */
        TextView tvTotalBet;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvMemberId = (TextView) findViewById(R.id.tv_member_id);
            tvFirstDepositAmount = (TextView) findViewById(R.id.tv_first_deposit_amount);
            tvTotalBet = (TextView) findViewById(R.id.tv_total_bet);
        }
    }
}
