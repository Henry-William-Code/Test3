package com.qgnix.live.lottery.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.interfaces.OnBettingCategoryDetailListener;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.BettingCategoryBean;
import com.qgnix.live.lottery.entry.BettingDetailsBean;

import java.util.List;

/**
 * 二级分类
 */
public class BettingTwoCategoryAdapter extends BaseRecyclerViewAdapter<BettingTwoCategoryAdapter.ViewHolder> {

    private final List<BettingCategoryBean> mData;
    /**
     * 是否直播间
     */
    private boolean mIsLive;


    public void setIsLive(boolean isLive) {
        this.mIsLive = isLive;
    }


    private OnBettingCategoryDetailListener mOnBettingCategoryDetailListener;

    /**
     * 选中回调
     */
    public void setOnBettingCategoryDetailListener(OnBettingCategoryDetailListener onBettingCategoryDetailListener) {
        this.mOnBettingCategoryDetailListener = onBettingCategoryDetailListener;
    }

    public BettingTwoCategoryAdapter(Context context, List<BettingCategoryBean> mData) {
        super(context);
        this.mData = mData;
    }

    /**
     * 重置数据
     */
    public void resetData() {
        for (BettingCategoryBean bean : mData) {
            bean.setSelect(false);
            bean.setSelectedCount(0);
            for (BettingDetailsBean item : bean.getItems()) {
                item.setSelect(false);
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_betting_two_category_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BettingCategoryBean bean = mData.get(position);
        holder.tvTwoCategoryName.setText(bean.getName());
        holder.tvTwoCategoryName.setTextColor(mContext.getResources().getColor(mIsLive ? R.color.white : R.color.black3));
        List<BettingDetailsBean> beanItems = bean.getItems();
        if (null == beanItems || beanItems.isEmpty()) {
            return;
        }
        int maxZs = bean.getZs();
        GridLayoutManager manager = new GridLayoutManager(mContext, 6);
        holder.rvCategory.setLayoutManager(manager);
        // 投注明细
        BettingCategoryDetailAdapter childSelectAdapter = new BettingCategoryDetailAdapter(mContext, beanItems);
        childSelectAdapter.setOnItemClickListener(new BettingCategoryDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                //true 不能全选 false 可全选
                boolean unSelectALl = false;
                //
                final StringBuilder sb = new StringBuilder();
                BettingDetailsBean detailsBean = beanItems.get(position);
                if (detailsBean.isSelect()) {
                    detailsBean.setSelect(false);
                    if (bean.getSelectedCount() > 0) {
                        bean.setSelectedCount(bean.getSelectedCount() - 1);
                    }
                    mOnBettingCategoryDetailListener.onUnSelectedDataListener(detailsBean);
                } else {
                    //  注数
                    if (maxZs > 0) {
                        if (bean.getSelectedCount() >= maxZs) {
                            ToastUtil.show(com.qgnix.live.R.string.can_only_choose_three, maxZs);
                            return;
                        }
                        bean.setSelectedCount(bean.getSelectedCount() + 1);
                    }
                    if (detailsBean.getGroup() != 0) {
                        // 总数
                        int totalCount = 0;
                        //选中总数
                        int selectedCount = 0;
                        for (BettingDetailsBean b1 : beanItems) {
                            if (detailsBean.getGroup() == b1.getGroup()) {
                                totalCount++;
                                sb.append(b1.getOdds_name()).append(",");
                                if (b1.isSelect()) {
                                    selectedCount++;
                                }
                            }
                        }
                        unSelectALl = selectedCount + 1 == totalCount;
                    }

                    if (!unSelectALl) {
                        detailsBean.setB_id(bean.getId());
                        detailsBean.setChildTitle(bean.getName());
                        detailsBean.setSelect(true);
                        mOnBettingCategoryDetailListener.onSelectedDataListener(detailsBean);
                    }
                }
                if (unSelectALl) {
                    ToastUtil.show(WordUtil.getString(R.string.not_allow_bet_at_the_same_time, bean.getName(), sb.deleteCharAt(sb.length() - 1).toString()));
                    return;
                }
                childSelectAdapter.notifyDataSetChanged();
            }
        });
        holder.rvCategory.setAdapter(childSelectAdapter);
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }


    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        /**
         * 二级分类名称
         */
        private final TextView tvTwoCategoryName;
        private final RecyclerView rvCategory;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvTwoCategoryName = (TextView) findViewById(R.id.tv_two_category_name);
            rvCategory = (RecyclerView) findViewById(R.id.rv_category);
        }
    }
}
