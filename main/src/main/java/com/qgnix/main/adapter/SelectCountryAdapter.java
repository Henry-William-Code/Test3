package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.PhoneRuleBean;

import java.util.List;

/**
 * 选择国家适配器
 */
public class SelectCountryAdapter extends BaseRecyclerViewAdapter<SelectCountryAdapter.ViewHolder> {

    private List<PhoneRuleBean> mData;

    public SelectCountryAdapter(Context context, List<PhoneRuleBean> data) {
        super(context);
        this.mData = data;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_select_country_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PhoneRuleBean bean = mData.get(position);
        holder.tvCountryName.setText(bean.getName());
        holder.tvCountryCode.setText(bean.getE164());
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        TextView tvCountryName;
        TextView tvCountryCode;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvCountryName = (TextView) findViewById(R.id.tv_country_name);
            tvCountryCode = (TextView) findViewById(R.id.tv_country_code);
        }
    }
}
