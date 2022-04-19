package com.qgnix.main.bean;

public class CurrencyBean {

    /**
     * key : MYR
     * value : 马来西亚林吉特
     * rate : 1.000000
     */
    private String key;
    private String value;
    private String rate;
    private String reverserate;//反汇率
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getReverserate() {
        return reverserate;
    }

    public void setReverserate(String reverserate) {
        this.reverserate = reverserate;
    }
}
