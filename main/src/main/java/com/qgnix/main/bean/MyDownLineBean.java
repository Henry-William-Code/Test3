package com.qgnix.main.bean;

/**
 * 我的下线
 */
public class MyDownLineBean {
    /**
     * 会员名称
     */
    private String user_nicename;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 头像缩略图
     */
    private String avatar_thumb;
    /**
     * 获得返点
     */
    private String profit;

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }
}
