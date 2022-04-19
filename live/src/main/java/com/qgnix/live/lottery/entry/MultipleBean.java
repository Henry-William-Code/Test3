package com.qgnix.live.lottery.entry;

// 倍数实体类
public class MultipleBean {

    private String id;

    private int multiple;

    private boolean isSelect;

    public MultipleBean(String id, int multiple, boolean isSelect) {
        this.id = id;
        this.multiple = multiple;
        this.isSelect = isSelect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
