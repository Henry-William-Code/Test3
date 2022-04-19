package com.qgnix.main.bean;

/**
 * 充值金额
 */
public class RechargeAmountBean {
    /**
     * 金额
     */
    private String amount;
    /**
     * 是否选中
     */
    private boolean select;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
