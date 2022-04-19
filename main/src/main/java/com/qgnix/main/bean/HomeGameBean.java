package com.qgnix.main.bean;

// 首页游戏实体类
public class HomeGameBean {


    /**
     * icon : http://cdn2.luosifenn.cn/20200118/5e227b38a7af0.png
     * title  : 一分时时彩
     * url : http://zhibo.xuqkagj.cn/index.php?g=Appapi&m=Page&a=openLotteryhistory&t_id=31
     */

    private String icon;
    private String title;
    private String url;
    private String id;
    private String game_type;
    private String game_code;
    private String plat_type;
    private String type;

    public String getGame_type() {
        return game_type == null ? "-" : game_type;
    }

    public void setGame_type(String game_type) {
        this.game_type = game_type;
    }

    public String getGame_code() {
        return game_code == null ? "-" : game_code;
    }

    public void setGame_code(String game_code) {
        this.game_code = game_code;
    }

    public String getPlat_type() {
        return plat_type == null ? "-" : plat_type;
    }

    public void setPlat_type(String plat_type) {
        this.plat_type = plat_type;
    }

    public String getId() {
        return id == null ? "-" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
