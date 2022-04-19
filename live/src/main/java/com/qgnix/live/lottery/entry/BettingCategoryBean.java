package com.qgnix.live.lottery.entry;

import java.util.List;

/**
 * 彩票投注分类
 */
public class BettingCategoryBean {

    /**
     * id : 5
     * name : 特码生肖
     * zs : 0
     * sort : 0
     * isLast : 1
     * items:[]
     */

    /**
     * 游戏id
     */
    private String id;
    /**
     * 投注名称
     */
    private String name;
    /**
     * 最大注数
     */
    private int zs;
    /**
     * 排序
     */
    private int sort;
    /**
     * 1 最后一级，非1 不是最后一级
     */
    private int isLast;


    private List<BettingCategoryBean> bet;

    private List<BettingDetailsBean> items;

    private boolean isSelect;
    /**
     * 选中注数
     */
    private int selectedCount;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getZs() {
        return zs;
    }

    public void setZs(int zs) {
        this.zs = zs;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getIsLast() {
        return isLast;
    }

    public void setIsLast(int isLast) {
        this.isLast = isLast;
    }

    public List<BettingCategoryBean> getBet() {
        return bet;
    }

    public void setBet(List<BettingCategoryBean> bet) {
        this.bet = bet;
    }

    public List<BettingDetailsBean> getItems() {
        return items;
    }

    public void setItems(List<BettingDetailsBean> items) {
        this.items = items;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(int selectedCount) {
        this.selectedCount = selectedCount;
    }
}
