package com.qgnix.main.bean;

/**
 * 首页tag
 */
public class HomeTagBean {
    /**
     * 图标
     */
    private int iconResId;
    /**
     * 内容
     */
    private String content;
    /**
     * 是否显示下级箭头
     */
    private boolean next;

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }
}
