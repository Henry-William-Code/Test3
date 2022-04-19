package com.qgnix.live.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.glide.ImgLoader;
import com.qgnix.live.R;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.TicketData;

import java.util.List;

/**
 * 彩票信息适配器
 */
public class TicketClassAdapter extends BaseRecyclerViewAdapter<TicketClassAdapter.ViewHolder> {

    private final List<TicketData> mData;
    /**
     * 字体颜色
     */
    @ColorRes
    private int mTvResColor;

    public void setTvResColor(int tvResColor) {
        this.mTvResColor = tvResColor;
    }

    public TicketClassAdapter(Context context, List<TicketData> mData) {
        super(context);
        this.mData = mData;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_ticket_class);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ImgLoader.display(mContext, mData.get(position).getImage(), holder.imgIcon);
        if (mTvResColor != 0) {
            holder.tvName.setTextColor(mContext.getResources().getColor(mTvResColor));
        }
        holder.tvName.setText(mData.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        ImageView imgIcon;
        TextView tvName;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            imgIcon = (ImageView) findViewById(R.id.img_icon);
            tvName = (TextView) findViewById(R.id.tv_name);
        }
    }
}
