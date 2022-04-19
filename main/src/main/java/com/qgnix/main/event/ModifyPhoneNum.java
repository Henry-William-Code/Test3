package com.qgnix.main.event;

public class ModifyPhoneNum {
    /**
     * 手机号
     */
    private String phoneNum;

    public ModifyPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
