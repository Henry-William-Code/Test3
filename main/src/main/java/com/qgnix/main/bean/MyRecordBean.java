package com.qgnix.main.bean;

/**
 * 我的报表
 */
public class MyRecordBean {
    private String uid;
    /**
     * 消费
     */
    private String total;
    /**
     * 返点数,
     */
    private String one_profit;
    private String addtime;
    /**
     * 用户
     */
    private String user_nicename;
    /**
     * 类型
     */
    private String type;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOne_profit() {
        return one_profit;
    }

    public void setOne_profit(String one_profit) {
        this.one_profit = one_profit;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
