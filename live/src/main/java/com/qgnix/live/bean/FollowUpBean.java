package com.qgnix.live.bean;

import com.qgnix.live.lottery.entry.ConfirmTzBean;

import java.util.List;

/**
 * 跟投bean
 */
public class FollowUpBean {

    /**
     * 下注信息
     */
    private List<ConfirmTzBean> tz;
    /**
     * 下注人
     */
    private String username;
    /**
     * 下注金额
     */
    private String money;


    public List<ConfirmTzBean> getTz() {
        return tz;
    }

    public void setTz(List<ConfirmTzBean> tz) {
        this.tz = tz;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
