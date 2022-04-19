package com.qgnix.live.lottery.entry;

// time 时间
public class TimeBean {

    /**
     * id : 295433
     * t_id : 32
     * kj_time : 1590508681
     * kj_number : 30,44,38,9,33,43,3
     * kj_no : 202005261924
     * kj_begin : 1590477841
     * kj_end : 1590477901
     * is_kj : 1
     * title : 一分六合彩
     * last_kj_number : 5,1,3,2,0
     *
     *
     */

    private String id;
    private String t_id;
    private String kj_time;
    private String kj_number;
    private String kj_no;
    private String kj_begin;
    private String kj_end;
    private String is_kj;
    private String last_kj_number;
    private String last_kj_no;
    private String title;
    private int now_time;
    /**
     * 是否显示加号
     */
    private int sfxj;

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

    public String getKj_time() {
        return kj_time;
    }

    public void setKj_time(String kj_time) {
        this.kj_time = kj_time;
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

    public String getKj_begin() {
        return kj_begin;
    }

    public void setKj_begin(String kj_begin) {
        this.kj_begin = kj_begin;
    }

    public String getKj_end() {
        return kj_end;
    }

    public void setKj_end(String kj_end) {
        this.kj_end = kj_end;
    }

    public String getIs_kj() {
        return is_kj;
    }

    public void setIs_kj(String is_kj) {
        this.is_kj = is_kj;
    }

    public String getLast_kj_number() {
        return last_kj_number;
    }

    public void setLast_kj_number(String last_kj_number) {
        this.last_kj_number = last_kj_number;
    }

    public String getLast_kj_no() {
        return last_kj_no;
    }

    public void setLast_kj_no(String last_kj_no) {
        this.last_kj_no = last_kj_no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNow_time() {
        return now_time;
    }

    public void setNow_time(int now_time) {
        this.now_time = now_time;
    }

    public int getSfxj() {
        return sfxj;
    }

    public void setSfxj(int sfxj) {
        this.sfxj = sfxj;
    }
}
