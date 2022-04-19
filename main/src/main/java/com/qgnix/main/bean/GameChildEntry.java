package com.qgnix.main.bean;

/**
 * 游戏信息
 */
public class GameChildEntry {

    /**
     * plat_type : AG
     * name : 神奇宝石
     * en_name : Mystic Gems
     * keyword :
     * ng_game_code : ng_ag_10001
     * category_id : 1
     * sec_category_id : 9
     * is_active : 1
     * is_hot : 1
     * is_new : 1
     * is_recommend : 0
     * image : https://storage.api-cdn.com/image/201805/1527239715898.jpg
     * is_mobile : 1
     * is_pc : 1
     * category_name : 老虎机
     * category_en_name : slot_games
     * sec_category_name : 多线老虎机
     * sec_category_en_name :
     */

    private String plat_type;
    private String name;
    private String en_name;
    private String keyword;
    private String game_code;
    private String game_type;
    private String image;
    private String id;
    private String title;
    private String icon;
    /**
     * 彩票类型
     */
    private String type;

    public String getPlat_type() {
        return plat_type;
    }

    public void setPlat_type(String plat_type) {
        this.plat_type = plat_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getGame_code() {
        return game_code;
    }

    public void setGame_code(String game_code) {
        this.game_code = game_code;
    }

    public String getGame_type() {
        return game_type;
    }

    public void setGame_type(String game_type) {
        this.game_type = game_type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
