package com.qgnix.main.bean;

public class MsgTypeBean {

    private int t;
    private int unReadCount = 0;
    private String n;
    private int icon = -1;

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
