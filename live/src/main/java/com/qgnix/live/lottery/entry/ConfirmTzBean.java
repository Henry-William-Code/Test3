package com.qgnix.live.lottery.entry;

/**
 * 确认投注参数
 */
public class ConfirmTzBean {

    /**
     * 投注金额
     */
    private String tz_money;
    /**
     * odds表的id
     */
    private String o_id;
    /**
     * 投注数量
     */
    private String tz_zs;
    /**
     * 二级分类id
     */
    private String b_id;

    public String getTz_money() {
        return tz_money;
    }

    public void setTz_money(String tz_money) {
        this.tz_money = tz_money;
    }

    public String getO_id() {
        return o_id;
    }

    public void setO_id(String o_id) {
        this.o_id = o_id;
    }

    public String getTz_zs() {
        return tz_zs;
    }

    public void setTz_zs(String tz_zs) {
        this.tz_zs = tz_zs;
    }

    public String getB_id() {
        return b_id;
    }

    public void setB_id(String b_id) {
        this.b_id = b_id;
    }
}
