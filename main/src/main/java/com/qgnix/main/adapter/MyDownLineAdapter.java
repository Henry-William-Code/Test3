package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.MyDownLineBean;

import java.util.List;

/**
 * 我的下线适配器
 */
public class MyDownLineAdapter extends BaseRecyclerViewAdapter<MyDownLineAdapter.ViewHolder> {
    /**
     * 踢人回调
     */
    private OnWeedOutListener mOnWeedOutListener;

    /**
     * 数据
     */
    private List<MyDownLineBean> mData;

    public MyDownLineAdapter(Context context, List<MyDownLineBean> mData) {
        super(context);
        this.mData = mData;
    }

    public void setOnWeedOutListener(OnWeedOutListener onWeedOutListener) {
        this.mOnWeedOutListener = onWeedOutListener;
    }


    public void onDataChange( List<MyDownLineBean> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_down_line_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyDownLineBean bean = mData.get(position);
        if (!TextUtils.isEmpty(bean.getAvatar_thumb())) {
            ImgLoader.display(mContext, bean.getAvatar_thumb(), holder.ivAvatar);
        }
        holder.tvMemberName.setText(WordUtil.getString(R.string.member_name, TextUtils.isEmpty(bean.getUser_nicename()) ? "" : bean.getUser_nicename()));
        holder.tvGetRebate.setText(WordUtil.getString(R.string.get_rebate, TextUtils.isEmpty(bean.getProfit()) ? "0" : bean.getProfit()));
        holder.btnWeedOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnWeedOutListener.onWeedOut();
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }


    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        /**
         * 头像
         */
        private ImageView ivAvatar;
        /**
         * 会员名称
         */
        private TextView tvGetRebate;
        /**
         * 获得返点
         */
        private TextView tvMemberName;
        /**
         * 踢除
         */
        private Button btnWeedOut;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
            tvMemberName = (TextView) findViewById(R.id.tv_member_name);
            tvGetRebate = (TextView) findViewById(R.id.tv_get_rebate);
            btnWeedOut = (Button) findViewById(R.id.btn_weed_out);
        }
    }

    /**
     * 剔除回调
     */
    public interface OnWeedOutListener {
        void onWeedOut();
    }
}
