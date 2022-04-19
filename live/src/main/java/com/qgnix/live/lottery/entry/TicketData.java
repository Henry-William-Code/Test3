package com.qgnix.live.lottery.entry;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 彩票信息
 */
public class TicketData implements Parcelable {

    /**
     * image: "/img/yflh_en.png",
     * title: "One min mark six",
     * id: "32",
     * type: "LHC"
     */

    private String id;
    /**
     * 图片
     */
    private String image;
    /**
     * 标题
     */
    private String title;
    /**
     * 彩票类型
     */
    private String type;

    public TicketData() {
    }

    public TicketData(Parcel in) {
        this.id = in.readString();
        this.image = in.readString();
        this.title = in.readString();
        this.type = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.image);
        dest.writeString(this.title);
        dest.writeString(this.type);
    }

    public static final Creator<TicketData> CREATOR = new Creator<TicketData>() {
        @Override
        public TicketData[] newArray(int size) {
            return new TicketData[size];
        }

        @Override
        public TicketData createFromParcel(Parcel in) {
            return new TicketData(in);
        }
    };
}
