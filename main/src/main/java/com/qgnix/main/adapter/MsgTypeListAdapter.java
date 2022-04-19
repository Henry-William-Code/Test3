package com.qgnix.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.live.bean.DrawHistoryBean;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.MsgTypeBean;

import java.util.List;

/**
 * 开奖历史
 */
public class MsgTypeListAdapter extends BaseRecyclerViewAdapter<MsgTypeListAdapter.ViewHolder> {


    private List<MsgTypeBean> mList;
    /**
     * 是否显示下一步箭头
     */
    private boolean mNext;

    public MsgTypeListAdapter(Context context, List<MsgTypeBean> list) {
        super(context);
        this.mList = list;
        mNext = true;
    }

    public MsgTypeListAdapter(Context context, List<MsgTypeBean> mList, boolean mNext) {
        super(context);
        this.mList = mList;
        this.mNext = mNext;
    }

    public void onDataChange(List<MsgTypeBean> bean) {
        this.mList = bean;
        this.notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_msg_type);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MsgTypeBean bean = mList.get(position);
        holder.tvMsgType.setText(bean.getN() + (bean.getUnReadCount() > 0 ? " (" + bean.getUnReadCount() + ")" : ""));
        holder.ivAvatar.setImageResource(bean.getIcon());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        TextView tvMsgType;
        ImageView ivAvatar;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
            tvMsgType = (TextView) findViewById(R.id.tv_msg_type);
        }
    }
}
