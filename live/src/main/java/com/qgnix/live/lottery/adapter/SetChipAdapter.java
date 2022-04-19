package com.qgnix.live.lottery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.live.R;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.SetChipsBean;

import java.util.List;

/**
 * 筹码 设置适配器
 */
public class SetChipAdapter extends BaseRecyclerViewAdapter<SetChipAdapter.ViewHolder> {

    private final List<SetChipsBean> list;


    public SetChipAdapter(Context context, List<SetChipsBean> list) {
        super(context);
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_set_chip);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SetChipsBean bean = list.get(position);
        holder.tvMoney.setText(String.valueOf(bean.getMoney()));
        holder.tvMoney.setSelected(bean.isSelect());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        TextView tvMoney;


        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvMoney = (TextView) findViewById(R.id.tv_money);
        }
    }
}
