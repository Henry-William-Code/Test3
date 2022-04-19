package com.qgnix.live.interfaces;

import com.qgnix.live.lottery.entry.BettingDetailsBean;

/**
 * 投注分类明细回调
 */
public interface OnBettingCategoryDetailListener {
    void onSelectedDataListener(BettingDetailsBean selectedData);

    /**
     * 取消选中
     *
     * @param detailsBean
     */
    void onUnSelectedDataListener(BettingDetailsBean detailsBean);
}
