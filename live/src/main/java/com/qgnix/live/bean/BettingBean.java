package com.qgnix.live.bean;

public class BettingBean {

    /**
     * is_kj : 1
     * odds : 1.89
     * o_price : 2
     * tz_no : 202006041000
     * tz_zs : 1
     * tz_money : 2  投资
     * tz_time : 1591204960
     * names : 一分时时彩
     * fangshi : 第一球两面
     * is_zj : false
     */

    private String is_kj;
    private String odds;
    private String tz_no;
    private String tz_zs;
    private String tz_money; //这里作为  tz_zs * 单注投注额 的结果 服务端算好
    private String zj_money;  //中奖金额   中奖金额 = 赔率 * 投注数量 * 单注投注金额
    private long tz_time;
    private String name;
    private String fangshi;
    private Object is_zj;
    private String kj_number;
    private long kj_time;
    /**
     * 彩票图标
     */
    private String image;

    public String getIs_kj() {
        return is_kj;
    }

    public void setIs_kj(String is_kj) {
        this.is_kj = is_kj;
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds;
    }

    public String getZj_money() {
        return zj_money;
    }

    public void setZj_money(String zj_money) {
        this.zj_money = zj_money;
    }

    public String getTz_no() {
        return tz_no;
    }

    public void setTz_no(String tz_no) {
        this.tz_no = tz_no;
    }

    public String getTz_zs() {
        return tz_zs;
    }

    public void setTz_zs(String tz_zs) {
        this.tz_zs = tz_zs;
    }

    public String getTz_money() {
        return tz_money;
    }

    public void setTz_money(String tz_money) {
        this.tz_money = tz_money;
    }

    public long getTz_time() {
        return tz_time;
    }

    public void setTz_time(long tz_time) {
        this.tz_time = tz_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFangshi() {
        return fangshi;
    }

    public void setFangshi(String fangshi) {
        this.fangshi = fangshi;
    }

    public Object getIs_zj() {
        return is_zj;
    }

    public void setIs_zj(Object is_zj) {
        this.is_zj = is_zj;
    }

    public String getKj_number() {
        return kj_number;
    }

    public void setKj_number(String kj_number) {
        this.kj_number = kj_number;
    }

    public long getKj_time() {
        return kj_time;
    }

    public void setKj_time(long kj_time) {
        this.kj_time = kj_time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
