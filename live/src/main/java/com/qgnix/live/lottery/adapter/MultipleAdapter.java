package com.qgnix.live.lottery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.MultipleBean;

import java.util.ArrayList;
import java.util.List;

// 倍数适配器
public class MultipleAdapter extends BaseRecyclerViewAdapter<MultipleAdapter.ViewHolder> {

    private List<MultipleBean> list;

    private OnItemClickListener onItemClickListener;

    public MultipleAdapter(Context context, List<MultipleBean> list) {
        super(context);
        this.list = list;
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_multiple);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MultipleBean bean = list.get(position);
        holder.tv_multiple.setText(bean.getMultiple() + WordUtil.getString(R.string.Times));
        if (bean.isSelect()) {
            holder.tv_multiple.setBackgroundResource(R.drawable.shape_confirm_bg);
            holder.tv_multiple.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.tv_multiple.setBackgroundResource(R.drawable.shape_confirm_unselect_bg);
            holder.tv_multiple.setTextColor(Color.parseColor("#000000"));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        private TextView tv_multiple;


        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tv_multiple = (TextView) findViewById(R.id.tv_multiple);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
