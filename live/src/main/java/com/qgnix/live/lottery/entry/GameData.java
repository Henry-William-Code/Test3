package com.qgnix.live.lottery.entry;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 彩票信息
 */
public class GameData implements Parcelable {

    private String plat_type;
    private String name;
    private String game_code;
    private String ng_game_code;
    private String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGame_code() {
        return game_code;
    }

    public void setGame_code(String game_code) {
        this.game_code = game_code;
    }

    public String getNg_game_code() {
        return ng_game_code;
    }

    public void setNg_game_code(String ng_game_code) {
        this.ng_game_code = ng_game_code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPlat_type() {
        return plat_type;
    }

    public void setPlat_type(String plat_type) {
        this.plat_type = plat_type;
    }
    public GameData() {
    }
    protected GameData(Parcel in) {
        plat_type = in.readString();
        name = in.readString();
        game_code = in.readString();
        ng_game_code = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(plat_type);
        dest.writeString(name);
        dest.writeString(game_code);
        dest.writeString(ng_game_code);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GameData> CREATOR = new Creator<GameData>() {
        @Override
        public GameData createFromParcel(Parcel in) {
            return new GameData(in);
        }

        @Override
        public GameData[] newArray(int size) {
            return new GameData[size];
        }
    };
}
