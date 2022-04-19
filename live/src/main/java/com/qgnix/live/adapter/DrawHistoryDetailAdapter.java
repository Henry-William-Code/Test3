package com.qgnix.live.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.live.R;
import com.qgnix.live.bean.DrawHistoryBean;
import com.qgnix.live.lottery.adapter.KjNumberAdapter;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.utils.CpUtils;

import java.util.List;

/**
 * 开奖历史明细
 */
public class DrawHistoryDetailAdapter extends BaseRecyclerViewAdapter<DrawHistoryDetailAdapter.ViewHolder> {


    private List<DrawHistoryBean> mList;
    /**
     * 彩票类型
     */
    private String mType;
    /**
     * 开奖号码
     */
    private KjNumberAdapter kjNumberAdapter;

    private LinearLayoutManager manager;

    public DrawHistoryDetailAdapter(Context context, String type, List<DrawHistoryBean> list) {
        super(context);
        this.mType = type;
        this.mList = list;
    }


    public void onDataChange(List<DrawHistoryBean> bean) {
        this.mList = bean;
        this.notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_draw_history_detail);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DrawHistoryBean bean = mList.get(position);
        holder.tvDrawDateNo.setText(bean.getKj_no());

        // 上期开奖号码
        String[] kjNumber = bean.getKj_number().split(",");
        List<String> drawNum = CpUtils.formatDrawNum(mType, kjNumber);
        manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.rvDrawNumber.setLayoutManager(manager);
        kjNumberAdapter = new KjNumberAdapter(mContext, drawNum, mType);
        kjNumberAdapter.setTextColorBlank(true);
        kjNumberAdapter.setCircle(true);
        holder.rvDrawNumber.setAdapter(kjNumberAdapter);

        holder.root.setBackgroundColor(Color.parseColor(position % 2 == 0 ? "#ffffff" : "#ecf1f7"));

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        View root;
        /**
         * 开奖期号
         */
        TextView tvDrawDateNo;
        /**
         * 开奖号码
         */
        RecyclerView rvDrawNumber;


        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvDrawDateNo = (TextView) findViewById(R.id.tv_draw_date_no);
            rvDrawNumber = (RecyclerView) findViewById(R.id.rv_draw_number);
            root = findViewById(R.id.root);
        }
    }
}
