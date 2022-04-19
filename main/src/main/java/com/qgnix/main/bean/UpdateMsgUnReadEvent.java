package com.qgnix.main.bean;

public class UpdateMsgUnReadEvent {
    private int mUnReadCount;
    public UpdateMsgUnReadEvent(int count){
        this.mUnReadCount=count;
    }

    public int getmUnReadCount() {
        return mUnReadCount;
    }

    public void setmUnReadCount(int mUnReadCount) {
        this.mUnReadCount = mUnReadCount;
    }
}