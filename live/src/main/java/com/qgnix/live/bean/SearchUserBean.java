package com.qgnix.live.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.qgnix.common.bean.UserBean;
import com.qgnix.live.lottery.entry.TicketData;

/**
 * Created by cxf on 2018/9/29.
 */

public class SearchUserBean extends UserBean {

    private int attention;
    /**
     * 主播是否在线 1 在线 0 不在线
     */
    private int islive;
    /**
     * 彩票信息
     */
    private TicketData ticket_tag;
    /**
     * 是否开启翻译
     */
    private int istranslate;

    private String stream;
    private String pull;
    private String nums;

    @JSONField(name = "isattention")
    public int getAttention() {
        return attention;
    }

    @JSONField(name = "isattention")
    public void setAttention(int attention) {
        this.attention = attention;
    }

    public SearchUserBean() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.attention);
        dest.writeInt(this.islive);
        dest.writeInt(this.istranslate);
        dest.writeString(this.stream);
        dest.writeString(this.pull);
        dest.writeString(this.nums);
        dest.writeParcelable(this.ticket_tag, flags);
    }

    public SearchUserBean(Parcel in) {
        super(in);
        this.attention = in.readInt();
        this.islive = in.readInt();
        this.istranslate = in.readInt();
        this.stream = in.readString();
        this.pull = in.readString();
        this.nums = in.readString();
        this.ticket_tag = in.readParcelable(TicketData.class.getClassLoader());
    }

    public static final Parcelable.Creator<SearchUserBean> CREATOR = new Parcelable.Creator<SearchUserBean>() {
        @Override
        public SearchUserBean[] newArray(int size) {
            return new SearchUserBean[size];
        }

        @Override
        public SearchUserBean createFromParcel(Parcel in) {
            return new SearchUserBean(in);
        }
    };

    public int getIslive() {
        return islive;
    }

    public void setIslive(int islive) {
        this.islive = islive;
    }

    public TicketData getTicket_tag() {
        return ticket_tag;
    }

    public void setTicket_tag(TicketData ticket_tag) {
        this.ticket_tag = ticket_tag;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getPull() {
        return pull;
    }

    public void setPull(String pull) {
        this.pull = pull;
    }

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public int getIstranslate() {
        return istranslate;
    }

    public void setIstranslate(int istranslate) {
        this.istranslate = istranslate;
    }
}
