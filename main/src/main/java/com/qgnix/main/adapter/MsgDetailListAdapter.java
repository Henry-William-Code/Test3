package com.qgnix.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.MsgDetailBean;

import java.util.List;

/**
 * 消息详情列表
 */
public class MsgDetailListAdapter extends BaseRecyclerViewAdapter<MsgDetailListAdapter.ViewHolder> {
    private List<MsgDetailBean> mList;

    public MsgDetailListAdapter(Context context, List<MsgDetailBean> list) {
        super(context);
        this.mList = list;
    }

    public void onDataChange(List<MsgDetailBean> bean) {
        this.mList = bean;
        this.notifyDataSetChanged();
    }

    public int getMsgIdByPosition(int position) {
        return mList.get(position).getId();
    }

    public void removeItem(int pos) {
        mList.remove(pos);
//        notifyItemRemoved(pos);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_msg_detail);
    }

    public int position = -1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MsgDetailBean bean = mList.get(position);
        holder.tvMsgTime.setText(DateFormat.format("yyyy-MM-dd HH:mm:ss", bean.getAddTime() * 1000));
        holder.tvMsgContent.setText(bean.getContent());
        holder.tvMsgContent.setTag(position);
        holder.tvMsgContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Log.d("长按的位置",v.getTag()+"");
                MsgDetailListAdapter.this.position = (int) v.getTag();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        TextView tvMsgTime, tvMsgContent;//消息时间和内容

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvMsgTime = (TextView) findViewById(R.id.tv_msg_time);
            tvMsgContent = (TextView) findViewById(R.id.tv_msg_content);
        }
    }
}
