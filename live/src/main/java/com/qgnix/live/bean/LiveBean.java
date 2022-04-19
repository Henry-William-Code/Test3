package com.qgnix.live.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.lottery.entry.GameData;
import com.qgnix.live.lottery.entry.TicketData;

import java.util.Objects;

/**
 * Created by cxf on 2017/8/9.
 */

public class LiveBean implements Parcelable {
    private String uid;
    private String avatar;
    private String avatarThumb;
    private String userNiceName;
    private String title;
    private String city;
    private String stream;
    private String pull;
    private String thumb;
    private String nums;
    private int sex;
    private String distance;
    private int levelAnchor;
    private int type;
    private String typeVal;
    private String goodNum;//主播的靓号
    private int gameAction;//正在进行的游戏的标识
    private String game;
    private String islive;
    private String isHot = "0";
    private String ismic = "-10";//1表示 主播开启了 这个功能 那么 观众就可以点击连麦
    private GameData gameData;
    /**
     * 是否开启翻译
     */
    private int istranslate;
    /**
     * 彩票信息
     */
    private TicketData ticket_tag;
    /**
     * 1 视频 非1不是视频
     */
    private int isvideo;
    /**
     * 1 横屏 0 竖屏 默认1
     */
    private int anyway;

    public int getAnyway() {
        return anyway;
    }

    public void setAnyway(int anyway) {
        this.anyway = anyway;
    }

    public int getIsvideo() {
        return isvideo;
    }

    public void setIsvideo(int isvideo) {
        this.isvideo = isvideo;
    }

    public TicketData getTicket_tag() {
        return ticket_tag;
    }

    public void setTicket_tag(TicketData ticket_tag) {
        this.ticket_tag = ticket_tag;
    }

    public GameData getGameData() {
        return gameData;
    }

    public void setGameData(GameData gameData) {
        this.gameData = gameData;
    }

    public String getIslive() {
        return islive;
    }

    public void setIslive(String islive) {
        this.islive = islive;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @JSONField(name = "avatar_thumb")
    public String getAvatarThumb() {
        return avatarThumb;
    }

    @JSONField(name = "avatar_thumb")
    public void setAvatarThumb(String avatarThumb) {
        this.avatarThumb = avatarThumb;
    }

    @JSONField(name = "user_nicename")
    public String getUserNiceName() {
        return userNiceName;
    }

    @JSONField(name = "user_nicename")
    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }


    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    @JSONField(name = "level_anchor")
    public int getLevelAnchor() {
        return levelAnchor;
    }

    @JSONField(name = "level_anchor")
    public void setLevelAnchor(int levelAnchor) {
        this.levelAnchor = levelAnchor;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @JSONField(name = "type_val")
    public String getTypeVal() {
        return typeVal;
    }

    @JSONField(name = "type_val")
    public void setTypeVal(String typeVal) {
        this.typeVal = typeVal;
    }

    @JSONField(name = "goodnum")
    public String getGoodNum() {
        return goodNum;
    }

    @JSONField(name = "goodnum")
    public void setGoodNum(String goodNum) {
        this.goodNum = goodNum;
    }

    @JSONField(name = "game_action")
    public int getGameAction() {
        return gameAction;
    }

    @JSONField(name = "game_action")
    public void setGameAction(int gameAction) {
        this.gameAction = gameAction;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getIstranslate() {
        return istranslate;
    }

    public void setIstranslate(int istranslate) {
        this.istranslate = istranslate;
    }

    public String getIsHot() {
        return isHot;
    }

    public void setIsHot(String isHot) {
        this.isHot = isHot;
    }

    public String getIsmic() {
        return ismic;
    }

    public void setIsmic(String ismic) {
        this.ismic = ismic;
    }

    /**
     * 显示靓号
     */
    public String getLiangNameTip() {
        if (!TextUtils.isEmpty(this.goodNum) && !"0".equals(this.goodNum)) {
            return WordUtil.getString(R.string.live_liang) + ":" + this.goodNum;
        }
        return "ID:" + this.uid;
    }

    public LiveBean() {

    }

    private LiveBean(Parcel in) {
        this.uid = in.readString();
        this.avatar = in.readString();
        this.avatarThumb = in.readString();
        this.userNiceName = in.readString();
        this.sex = in.readInt();
        this.title = in.readString();
        this.city = in.readString();
        this.stream = in.readString();
        this.pull = in.readString();
        this.thumb = in.readString();
        this.nums = in.readString();
        this.distance = in.readString();
        this.levelAnchor = in.readInt();
        this.type = in.readInt();
        this.typeVal = in.readString();
        this.goodNum = in.readString();
        this.gameAction = in.readInt();
        this.game = in.readString();
        this.islive = in.readString();
        this.istranslate = in.readInt();
        this.isvideo = in.readInt();
        this.anyway = in.readInt();
        this.ticket_tag = in.readParcelable(TicketData.class.getClassLoader());
        this.gameData = in.readParcelable(GameData.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.avatar);
        dest.writeString(this.avatarThumb);
        dest.writeString(this.userNiceName);
        dest.writeInt(this.sex);
        dest.writeString(this.title);
        dest.writeString(this.city);
        dest.writeString(this.stream);
        dest.writeString(this.pull);
        dest.writeString(this.thumb);
        dest.writeString(this.nums);
        dest.writeString(this.distance);
        dest.writeInt(this.levelAnchor);
        dest.writeInt(this.type);
        dest.writeString(this.typeVal);
        dest.writeString(this.goodNum);
        dest.writeInt(this.gameAction);
        dest.writeString(this.game);
        dest.writeString(this.islive);
        dest.writeInt(this.istranslate);
        dest.writeInt(this.isvideo);
        dest.writeInt(this.anyway);
        dest.writeParcelable(this.ticket_tag, flags);
        dest.writeParcelable(this.gameData, flags);
    }

    public static final Creator<LiveBean> CREATOR = new Creator<LiveBean>() {
        @Override
        public LiveBean[] newArray(int size) {
            return new LiveBean[size];
        }

        @Override
        public LiveBean createFromParcel(Parcel in) {
            return new LiveBean(in);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiveBean liveBean = (LiveBean) o;
        return uid.equals(liveBean.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    @Override
    public String toString() {
        return "uid: " + uid + " , userNiceName: " + userNiceName + " ,playUrl: " + pull;
    }
}
