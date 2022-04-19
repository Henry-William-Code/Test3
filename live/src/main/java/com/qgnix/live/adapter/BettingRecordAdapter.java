package com.qgnix.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.bean.BettingBean;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

// 记录
public class BettingRecordAdapter extends BaseRecyclerViewAdapter<BettingRecordAdapter.ViewHolder> {

    private List<BettingBean> list;

    public BettingRecordAdapter(Context context, List<BettingBean> list) {
        super(context);
        this.list = list;
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
    }

    public void onDataChange(List<BettingBean> bean) {
        this.list = bean;
        this.notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_betting_record);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BettingBean bean = list.get(position);
        if (!TextUtils.isEmpty(bean.getImage())) {
            ImgLoader.display(mContext, bean.getImage(), holder.ivTicketIcon);
        }
        holder.tv_bet_no.setText(bean.getTz_no());
        holder.tv_content.setText(bean.getFangshi());
        holder.tv_title.setText(bean.getName());
        holder.tv_bet_time.setText(DateFormat.format("MM-dd HH:mm:ss", bean.getTz_time() * 1000));
        holder.tv_kj_time.setText(DateFormat.format("MM-dd HH:mm:ss", bean.getKj_time() * 1000));

        holder.tv_tou_money.setText(bean.getTz_money() + WordUtil.getString(R.string.coin));
        holder.tv_odd.setText(bean.getOdds());
        holder.tv_kj_no.setText(bean.getKj_number());
        //先初始下
        if (TextUtils.isEmpty(bean.getKj_number())) {
            holder.tv_bet_status.setText(R.string.wait_draw);
            holder.tv_jiang_money.setText("0.00" + WordUtil.getString(R.string.coin));
        } else {
            Object object = bean.getIs_zj();
            if ("0".equals(object.toString())) {
                holder.tv_bet_status.setText(R.string.not_winning);
                holder.tv_jiang_money.setText("0.00" + WordUtil.getString(R.string.coin));
            } else {
                holder.tv_bet_status.setText(R.string.has_won);
                //开奖情况下数据更新
                holder.tv_jiang_money.setText(bean.getZj_money() + WordUtil.getString(R.string.coin));
            }
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        TextView tv_bet_no, tv_bet_status, tv_bet_time, tv_kj_time,
                tv_title, tv_content, tv_tou_money, tv_jiang_money, tv_odd, tv_kj_no;

        /**
         * 彩票图标
         */
        ImageView ivTicketIcon;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tv_bet_no = (TextView) findViewById(R.id.tv_bet_no);
            tv_bet_time = (TextView) findViewById(R.id.tv_bet_time);
            tv_kj_time = (TextView) findViewById(R.id.tv_kj_time);
            tv_bet_status = (TextView) findViewById(R.id.tv_bet_status);
            tv_title = (TextView) findViewById(R.id.tv_title);
            tv_content = (TextView) findViewById(R.id.tv_content);
            tv_tou_money = (TextView) findViewById(R.id.tv_tou_money);
            tv_jiang_money = (TextView) findViewById(R.id.tv_jiang_money);
            tv_kj_no = (TextView) findViewById(R.id.tv_kj_no);
            tv_odd = (TextView) findViewById(R.id.tv_odd);
            ivTicketIcon = (ImageView) findViewById(R.id.iv_ticket_icon);
        }
    }
}
