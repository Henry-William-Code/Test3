package com.qgnix.live.lottery.entry;

import java.math.BigDecimal;

/**
 * 余额信息
 */
public class BalanceInfo {
    /**
     * coin : 17084
     * givecoin : 系统赠送金币/新扩展
     */

    private BigDecimal coin;
    /**
     * 赠送金币
     */
    private BigDecimal give_coin;
    /**
     * 可转换游戏币额度
     */
    private BigDecimal money;


    public BigDecimal getCoin() {
        return coin;
    }

    public void setCoin(BigDecimal coin) {
        this.coin = coin;
    }

    public BigDecimal getGive_coin() {
        return give_coin;
    }

    public void setGive_coin(BigDecimal give_coin) {
        this.give_coin = give_coin;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
