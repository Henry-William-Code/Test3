package com.qgnix.live.bean;


import com.contrarywind.interfaces.IPickerViewData;

import java.io.Serializable;

public class TransferBean implements IPickerViewData, Serializable {

    private String plat_type;
    private String title;
    private String game_type;
    private String coin;
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
    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }
    @Override
    public String getPickerViewText() {
        return title;
    }
}
