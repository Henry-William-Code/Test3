package com.qgnix.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 银行卡信息
 */
public class BankCardBean {
    /**
     * id : 4
     * uid : 27480
     * name : 好了老婆后悔了
     * bank_name : 凌云
     * bank_address : 公民
     * bank_number : 5467975436454246469464
     * addtime : 1595147512
     */

    private String id;
    private String uid;
    private String name;
    private String bank_name;
    private String bank_address;
    private String bank_number;
    private String addtime;
    /**
     * 币种
     */
    private String currency;
    /**
     * 汇率
     */
    private String rate;
    /**
     * 货币名称
     */
    @JSONField(name = "currency_name")
    private String currencyName;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_address() {
        return bank_address;
    }

    public void setBank_address(String bank_address) {
        this.bank_address = bank_address;
    }

    public String getBank_number() {
        return bank_number;
    }

    public void setBank_number(String bank_number) {
        this.bank_number = bank_number;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
}
