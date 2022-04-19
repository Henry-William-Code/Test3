package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.glide.ImgLoader;
import com.qgnix.live.bean.LiveBean;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;

import java.util.List;

/**
 * 直播adapter
 */
public class LiveAdapter extends BaseRecyclerViewAdapter<LiveAdapter.ViewHolder> {

    private List<LiveBean> mList;

    public LiveAdapter(Context context, List<LiveBean> mList) {
        super(context);
        this.mList = mList;
    }


    public void onDataChange(List<LiveBean> bean) {
        this.mList = bean;
        this.notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        return new LiveAdapter.ViewHolder(parent, R.layout.item_main_live);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        LiveBean bean = mList.get(position);
        ImgLoader.display(mContext, bean.getThumb(), viewHolder.ivCover);
        viewHolder.tvTitle.setText(bean.getTitle());
        if (!bean.getUserNiceName().equals(bean.getTitle())) {
            viewHolder.tvNickName.setVisibility(View.VISIBLE);
            viewHolder.tvNickName.setText(bean.getUserNiceName());
            if (bean.getIsHot().equals("1")) {
                viewHolder.ivHot.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.tvNickName.setVisibility(View.GONE);
            viewHolder.ivHot.setVisibility(View.GONE);
        }


        if (bean.getTicket_tag() == null) {
            viewHolder.tvTicketName.setVisibility(View.GONE);
        } else {
            viewHolder.tvTicketName.setVisibility(View.VISIBLE);
            viewHolder.tvTicketName.setText(bean.getTicket_tag().getTitle());
        }
        viewHolder.tvCity.setText(Html.fromHtml(bean.getCity()));
        viewHolder.tvViewers.setText(bean.getNums());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        /**
         * 彩票名称
         */
        TextView tvTicketName;
        /**
         * 观看人数
         */
        TextView tvViewers;
        /**
         * 城市
         */
        TextView tvCity;
        /**
         * 标题
         */
        TextView tvTitle;
        /**
         * 昵称
         */
        TextView tvNickName;
        ImageView ivHot;

        /**
         * 封面图
         */
        ImageView ivCover;


        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvTicketName = (TextView) findViewById(R.id.tv_ticket_name);
            tvCity = (TextView) findViewById(R.id.tv_city);
            tvViewers = (TextView) findViewById(R.id.tv_viewers);
            tvTitle = (TextView) findViewById(R.id.tv_title);
            tvNickName = (TextView) findViewById(R.id.tv_nick_name);
            ivHot = (ImageView) findViewById(R.id.iv_hot);
            ivCover = (ImageView) findViewById(R.id.iv_cover);
        }
    }
}
