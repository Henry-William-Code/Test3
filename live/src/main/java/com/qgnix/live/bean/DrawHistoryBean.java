package com.qgnix.live.bean;

/**
 * 开奖历史bean
 */
public class DrawHistoryBean {
    /**
     *
     */
    private String id;
    /**
     * 彩票id
     */
    private String t_id;
    /**
     * 开奖号码
     */
    private String kj_number;
    /**
     * 开奖期号
     */
    private String kj_no;
    /**
     * 彩票名称
     */
    private String name;
    /**
     * 彩票名称
     */
    private String name_en;

    /**
     * 彩票类型
     */
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getKj_number() {
        return kj_number;
    }

    public void setKj_number(String kj_number) {
        this.kj_number = kj_number;
    }

    public String getKj_no() {
        return kj_no;
    }

    public void setKj_no(String kj_no) {
        this.kj_no = kj_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
