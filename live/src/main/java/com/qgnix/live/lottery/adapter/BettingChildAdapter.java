package com.qgnix.live.lottery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.BettingDetailsBean;

import java.util.ArrayList;
import java.util.List;

// 确认投注子选项适配器
public class BettingChildAdapter extends BaseRecyclerViewAdapter<BettingChildAdapter.ViewHolder> {

    private List<BettingDetailsBean> list;

    private OnItemClickListener onItemClickListener;

    public BettingChildAdapter(Context context, List<BettingDetailsBean> list) {
        super(context);
        this.list = list;
        if (this.list == null) {
            this.list = new ArrayList<>();
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_notes);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BettingDetailsBean bean = list.get(position);
        holder.tv_type.setText(bean.getOdds_name());
        holder.tv_title.setText(bean.getTicketName() + "-" + bean.getChildTitle());
        String strOne =WordUtil.getRedHtmlStr(1)+WordUtil.getBlankHtmlStrFromRes(R.string.note)+ WordUtil.getRedHtmlStr(bean.getMultiple())+WordUtil.getBlankHtmlStrFromRes(R.string.Times);
        holder.tv_betting.setText(Html.fromHtml(strOne));
        holder.tv_number_notes.setText(bean.getPrice() + "×" + bean.getMultiple());
        holder.img_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClickListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        TextView tv_type, tv_title, tv_betting, tv_number_notes;
        ImageView img_del;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tv_type = (TextView) findViewById(R.id.tv_type);
            tv_title = (TextView) findViewById(R.id.tv_title);
            tv_betting = (TextView) findViewById(R.id.tv_betting);
            tv_number_notes = (TextView) findViewById(R.id.tv_number_notes);
            img_del = (ImageView) findViewById(R.id.img_del);
        }
    }

    public interface OnItemClickListener {
        void onClickListener(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
