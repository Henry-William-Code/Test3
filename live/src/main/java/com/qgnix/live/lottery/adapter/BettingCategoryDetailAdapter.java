package com.qgnix.live.lottery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.common.glide.ImgLoader;
import com.qgnix.live.R;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.BettingDetailsBean;

import java.util.List;

/**
 * 彩票投注最后一级分类
 */
public class BettingCategoryDetailAdapter extends BaseRecyclerViewAdapter<BettingCategoryDetailAdapter.ViewHolder> {

    private final List<BettingDetailsBean> mData;

    public BettingCategoryDetailAdapter(Context context, List<BettingDetailsBean> oneChildList) {
        super(context);
        this.mData = oneChildList;
    }


    /**
     * 重置数据
     */
    public void resetData() {
        for (BettingDetailsBean bean : mData) {
            bean.setSelect(false);
            notifyDataSetChanged();
        }
    }

    @Override
    public BettingCategoryDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_child_betting);
    }

    @Override
    public void onBindViewHolder(BettingCategoryDetailAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BettingDetailsBean bean = mData.get(position);
        holder.tv_money.setText(bean.getOdds());
        holder.tv_title.setText(bean.getOdds_name());
        if (bean.isSelect()) {
            holder.tv_title.setBackgroundResource(R.drawable.shape_child_select_bg);
            holder.tv_title.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.tv_title.setTextColor(Color.parseColor("#000000"));
            String oddsImage = bean.getOdds_image();
            if (TextUtils.isEmpty(oddsImage)) {
                holder.tv_title.setBackgroundResource(R.drawable.shape_child_unselect_bg);
            } else {
                if (oddsImage.startsWith("#")) {
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.OVAL);
                    drawable.setStroke(2, Color.parseColor(oddsImage));
                    drawable.setColor(Color.parseColor(oddsImage));
                    holder.tv_title.setPadding(8,8,8,8);
                    holder.tv_title.setBackground(drawable);
                } else {
                    ImgLoader.displayDrawable(mContext, oddsImage, new ImgLoader.DrawableCallback() {

                        @Override
                        public void onLoadSuccess(Drawable drawable) {
                            holder.tv_title.setText("");
                            holder.tv_title.setBackground(drawable);
                        }

                        @Override
                        public void onLoadFailed() {
                            holder.tv_title.setBackgroundResource(R.drawable.shape_child_unselect_bg);
                        }
                    });
                }

            }


        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        private TextView tv_title, tv_money;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tv_title = (TextView) findViewById(R.id.tv_title);
            tv_money = (TextView) findViewById(R.id.tv_money);
        }
    }
}
