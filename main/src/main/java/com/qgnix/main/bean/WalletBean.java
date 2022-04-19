package com.qgnix.main.bean;

import java.io.Serializable;

public class WalletBean implements Serializable {
    private String avatar;
    private String avatarThumb;
    private double coin;
    private double dayRates;
    private int id;
    private String levelName;
    private int levelid;
    private String minTransInAmount;
    private String realName;
    private String userLogin;
    private String userNicename;
    private String walletBalance;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarThumb() {
        return avatarThumb;
    }

    public void setAvatarThumb(String avatarThumb) {
        this.avatarThumb = avatarThumb;
    }

    public double getCoin() {
        return coin;
    }

    public void setCoin(double coin) {
        this.coin = coin;
    }

    public double getDayRates() {
        return dayRates;
    }

    public void setDayRates(double dayRates) {
        this.dayRates = dayRates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getLevelid() {
        return levelid;
    }

    public void setLevelid(int levelid) {
        this.levelid = levelid;
    }

    public String getMinTransInAmount() {
        return minTransInAmount;
    }

    public void setMinTransInAmount(String minTransInAmount) {
        this.minTransInAmount = minTransInAmount;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserNicename() {
        return userNicename;
    }

    public void setUserNicename(String userNicename) {
        this.userNicename = userNicename;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }
}
