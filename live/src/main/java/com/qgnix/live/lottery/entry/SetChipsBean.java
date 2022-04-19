package com.qgnix.live.lottery.entry;

/**
 * 选择筹码
 */
public class SetChipsBean {

    private int money;

    private boolean select;

    public SetChipsBean(int money) {
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
