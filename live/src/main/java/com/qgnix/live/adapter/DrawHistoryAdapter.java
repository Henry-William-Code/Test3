package com.qgnix.live.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.glide.ImgLoader;
import com.qgnix.live.R;
import com.qgnix.live.bean.DrawHistoryBean;
import com.qgnix.live.lottery.adapter.KjNumberAdapter;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.TicketData;
import com.qgnix.live.utils.CpUtils;

import java.util.List;

/**
 * 开奖历史
 */
public class DrawHistoryAdapter extends BaseRecyclerViewAdapter<DrawHistoryAdapter.ViewHolder> {


    private List<DrawHistoryBean> mList;
    private List<TicketData> mIconData;
    /**
     * 是否显示下一步箭头
     */
    private boolean mNext;

    public DrawHistoryAdapter(Context context, List<DrawHistoryBean> list, List<TicketData> iconData) {
        super(context);
        this.mList = list;
        this.mIconData = iconData;
        mNext = true;
    }

    public DrawHistoryAdapter(Context context, List<DrawHistoryBean> mList, boolean mNext) {
        super(context);
        this.mList = mList;
        this.mNext = mNext;
    }

    public void onDataChange(List<DrawHistoryBean> bean) {
        this.mList = bean;
        this.notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_draw_history);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        DrawHistoryBean bean = mList.get(position);
        holder.tvTicketName.setText(bean.getName());
        holder.tvDrawDateNo.setText(bean.getKj_no());
        holder.ivNext.setVisibility(mNext ? View.VISIBLE : View.GONE);

        // 上期开奖号码
        String[] kjNumber = bean.getKj_number().split(",");
        List<String> drawNum = CpUtils.formatDrawNum(bean.getType(), kjNumber);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.rvDrawNumber.setLayoutManager(manager);
        KjNumberAdapter kjNumberAdapter = new KjNumberAdapter(mContext, drawNum, bean.getType());
        kjNumberAdapter.setTextColorBlank(true);
        kjNumberAdapter.setCircle(true);
        holder.rvDrawNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return holder.itemView.onTouchEvent(event);
            }
        });
        holder.rvDrawNumber.setAdapter(kjNumberAdapter);

        String icon = getIcon(bean.getType());
        ImgLoader.display(mContext, icon, holder.iv_icon);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        ImageView iv_icon;
        /**
         * 彩票名称
         */
        TextView tvTicketName;
        /**
         * 开奖期号
         */
        TextView tvDrawDateNo;
        /**
         * 开奖号码
         */
        RecyclerView rvDrawNumber;

        /**
         * 下一步
         */
        ImageView ivNext;


        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            iv_icon = (ImageView) findViewById(R.id.iv_icon);
            tvTicketName = (TextView) findViewById(R.id.tv_ticket_name);
            tvDrawDateNo = (TextView) findViewById(R.id.tv_draw_date_no);
            rvDrawNumber = (RecyclerView) findViewById(R.id.rv_draw_number);
            ivNext = (ImageView) findViewById(R.id.iv_next);
        }
    }

    private String getIcon(String type) {
        for (int i = 0; i < mIconData.size(); i++) {
            if (type.equals(mIconData.get(i).getType())) {
                return mIconData.get(i).getImage();
            }
        }
        return "";
    }
}
