package com.qgnix.main.bean;

/**
 * 首页多布局数据类型bean
 */
public class HomeHotTypeBean {
    /**
     * 轮播图
     */
    public static final int DATA_ONE_BANNER = 0;
    /**
     * 提示信息
     */
    public static final int DATA_TWO_TAG = 1;
    /**
     * 游戏
     */
    public static final int DATA_THREE_GAME = 2;
    /**
     * 直播
     */
    public static final int DATA_FOUR_LIVE = 3;

    /**
     * 数据类型
     */
    private int dataType;

    private Object data;

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
