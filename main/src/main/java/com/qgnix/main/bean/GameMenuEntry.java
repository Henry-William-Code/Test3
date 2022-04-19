package com.qgnix.main.bean;

/**
 * 游戏菜单
 */
public class GameMenuEntry {

    /**
     * plat_type : vr
     * title : 彩票游戏
     * game_type : Lottery
     */

    private String plat_type;
    private String title;
    private String game_type;
    private String game_code;
    private String provider_type;
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getPlat_type() {
        return plat_type;
    }

    public void setPlat_type(String plat_type) {
        this.plat_type = plat_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGame_type() {
        return game_type;
    }

    public void setGame_type(String game_type) {
        this.game_type = game_type;
    }

    public String getGame_code() {
        return game_code;
    }

    public void setGame_code(String game_code) {
        this.game_code = game_code;
    }

    public String getProvider_type() {
        return provider_type;
    }

    public void setProvider_type(String provider_type) {
        this.provider_type = provider_type;
    }
}
