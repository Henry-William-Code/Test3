package com.qgnix.live.lottery.entry;

/**
 * 投注分类详情
 */
public class BettingDetailsBean {
    /**
     * id : 5
     * odds_name : 单
     * odds : 2.00
     * odds_image :
     * price : 1
     */

    private String id;
    private String odds_name;
    private String odds;
    private String odds_image;
    private String price;
    //非0 不能同时选择
    private  int group;

    /**
     * 上一级名称
     */
    private String childTitle;
    /**
     * 彩票名称
     */
    private String ticketName;
    /**
     * 是否选中
     */
    private boolean isSelect;
    /**
     * 倍数
     */
    private int multiple;
    /**
     * 二级分类id
     */
    private String b_id;


    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public String getChildTitle() {
        return childTitle;
    }

    public void setChildTitle(String childTitle) {
        this.childTitle = childTitle;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getOdds_name() {
        return odds_name;
    }

    public void setOdds_name(String odds_name) {
        this.odds_name = odds_name;
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds;
    }

    public String getOdds_image() {
        return odds_image;
    }

    public void setOdds_image(String odds_image) {
        this.odds_image = odds_image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getB_id() {
        return b_id;
    }

    public void setB_id(String b_id) {
        this.b_id = b_id;
    }
}
