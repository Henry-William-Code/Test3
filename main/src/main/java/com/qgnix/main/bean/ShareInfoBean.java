package com.qgnix.main.bean;


public class ShareInfoBean {
    /**
     * fenxiao: "0",
     * fenxiaos: "22",
     * link: "https://live.yudlan.com:443?inviteCode=RMU4E0",
     * profit: "470.40",
     * share_code: "RMU4E0",
     * yesterdayprofit: 0,
     * effectivenumber: 2,
     * getcoin: "1.00"
     * inviteprofit: "1.00"
     */

    /**
     * 今日新增人数
     */
    private String fenxiao;
    /**
     * 己邀请好友/推广人数
     */
    private String fenxiaos;
    /**
     * 推广链接
     */
    private String link;
    /**
     * 网站基础链接
     */
    private String linkbase;
    /**
     * 总收益
     */
    private String profit;
    /**
     * 推广码
     */
    private String share_code;
    /**
     * 昨天收益
     */
    private String yesterdayprofit;
    /**
     * 有效人数
     */
    private String effectivenumber;
    /**
     * 获得推广马币
     */
    private String getcoin;
    /**
     * 每推广一个获取的金币
     */
    private String inviteprofit;

    public String getFenxiao() {
        return fenxiao;
    }

    public void setFenxiao(String fenxiao) {
        this.fenxiao = fenxiao;
    }

    public String getFenxiaos() {
        return fenxiaos;
    }

    public void setFenxiaos(String fenxiaos) {
        this.fenxiaos = fenxiaos;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getProfit() {
        return profit;
    }

    public void setLinkbase(String linkbase) {
        this.linkbase = linkbase;
    }

    public String getLinkbase() {
        return linkbase;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getShare_code() {
        return share_code;
    }

    public void setShare_code(String share_code) {
        this.share_code = share_code;
    }

    public String getYesterdayprofit() {
        return yesterdayprofit;
    }

    public void setYesterdayprofit(String yesterdayprofit) {
        this.yesterdayprofit = yesterdayprofit;
    }

    public String getEffectivenumber() {
        return effectivenumber;
    }

    public void setEffectivenumber(String effectivenumber) {
        this.effectivenumber = effectivenumber;
    }

    public String getGetcoin() {
        return getcoin;
    }

    public void setGetcoin(String getcoin) {
        this.getcoin = getcoin;
    }

    public String getInviteprofit() {
        return inviteprofit;
    }

    public void setInviteprofit(String inviteprofit) {
        this.inviteprofit = inviteprofit;
    }
}
