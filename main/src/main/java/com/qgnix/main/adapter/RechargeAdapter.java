package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.RechargePaysBean;

import java.util.List;

/**
 * 充值渠道适配器
 */
public class RechargeAdapter extends BaseRecyclerViewAdapter<RechargeAdapter.ViewHolder> {
    private final List<RechargePaysBean.PayChannelsBean> mData;

    public RechargeAdapter(Context context, List<RechargePaysBean.PayChannelsBean> data) {
        super(context);
        this.mData = data;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_recharge_channel_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RechargePaysBean.PayChannelsBean bean = mData.get(position);
        String country = CommonAppConfig.getInstance().getCountry();
        if(bean.getUrl()!=null && bean.getUrl().toLowerCase().contains("direpay")){
            holder.tvChannelName.setText(WordUtil.getString(R.string.channelCrypto, country, (position + 1)));
            bean.setChannelName(WordUtil.getString(R.string.channelCrypto, country, (position + 1)));
        }else{
            holder.tvChannelName.setText(WordUtil.getString(R.string.channel, country, (position + 1)));
            bean.setChannelName(WordUtil.getString(R.string.channel, country, (position + 1)));
        }

        holder.tvLimit.setText(WordUtil.getString(R.string.limit, bean.getMin(), bean.getMax()));
        //
        holder.rLRoot.setBackgroundColor(bean.isSelect() ? mContext.getResources().getColor(R.color.global) : mContext.getResources().getColor(R.color.white));
        holder.tvChannelName.setTextColor(bean.isSelect() ? mContext.getResources().getColor(R.color.white) : mContext.getResources().getColor(R.color.black3));
        holder.tvLimit.setTextColor(bean.isSelect() ? mContext.getResources().getColor(R.color.white) : mContext.getResources().getColor(R.color.black4));

    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        RelativeLayout rLRoot;
        TextView tvChannelName;
        TextView tvLimit;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            rLRoot = (RelativeLayout) findViewById(R.id.rL_root);
            tvChannelName = (TextView) findViewById(R.id.tv_channel_name);
            tvLimit = (TextView) findViewById(R.id.tv_limit);
        }
    }
}
