package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.bean.LanBean;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;

import java.util.List;

/**
 * 语言切换适配器
 */
public class LanSwitchAdapter extends BaseRecyclerViewAdapter<LanSwitchAdapter.ViewHolder> {
    private final List<LanBean> mData;


    public LanSwitchAdapter(Context context, List<LanBean> mData) {
        super(context);
        this.mData = mData;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new LanSwitchAdapter.ViewHolder(parent, R.layout.item_lan_switch_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final LanBean lanBean = mData.get(i);
        viewHolder.tvLan.setText(lanBean.getLanName());
        viewHolder.tvLan.setSelected(lanBean.isSelect());
        viewHolder.tvCountryIcon.setImageResource(lanBean.getIconId());
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        ImageView tvCountryIcon;
        TextView tvLan;


        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvCountryIcon = (ImageView) findViewById(R.id.iv_country_icon);
            tvLan = (TextView) findViewById(R.id.tv_lan);
        }
    }
}
