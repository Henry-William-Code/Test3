package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.MyRecordBean;

import java.util.List;

/**
 * 我的报表
 */
public class MyRecordAdapter extends BaseRecyclerViewAdapter<MyRecordAdapter.ViewHolder> {
    /**
     * 数据
     */
    private List<MyRecordBean> mData;

    public MyRecordAdapter(Context context, List<MyRecordBean> mData) {
        super(context);
        this.mData = mData;
    }


    public void onDataChange(List<MyRecordBean> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_my_record_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyRecordBean bean = mData.get(position);
        holder.tvMember.setText(TextUtils.isEmpty(bean.getUser_nicename())?"":bean.getUser_nicename());
        holder.tvCategory.setText(TextUtils.isEmpty(bean.getType())?"":bean.getType());
        holder.tvConsumption.setText(TextUtils.isEmpty(bean.getTotal())?"":bean.getTotal());
        holder.tvRebate.setText(TextUtils.isEmpty(bean.getOne_profit())?"":bean.getOne_profit());

    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        /**
         * 会员
         */
        private TextView tvMember;
        /**
         * 类别
         */
        private TextView tvCategory;
        /**
         * 消费
         */
        private TextView tvConsumption;
        /**
         * 踢除
         */
        private TextView tvRebate;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvMember = (TextView) findViewById(R.id.tv_member);
            tvCategory = (TextView) findViewById(R.id.tv_category);
            tvConsumption = (TextView) findViewById(R.id.tv_consumption);
            tvRebate = (TextView) findViewById(R.id.tv_rebate);

        }
    }
}
