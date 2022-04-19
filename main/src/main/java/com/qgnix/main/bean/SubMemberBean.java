package com.qgnix.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 下级会员信息
 */
public class SubMemberBean {

    /**
     * id
     */ /**
     * id : 43084
     * user_nicename : kevin测试
     * avatar : /default.png
     * avatar_thumb : /default_thumb.png
     * first_recharge : 724.00
     * tzcion : 2500.00
     */

    @JSONField(name = "id")
    private String id;
    /**
     * userNicename
     */
    @JSONField(name = "user_nicename")
    private String userNicename;
    /**
     * avatar
     */
    @JSONField(name = "avatar")
    private String avatar;
    /**
     * avatarThumb
     */
    @JSONField(name = "avatar_thumb")
    private String avatarThumb;
    /**
     * firstRecharge
     */
    @JSONField(name = "first_recharge")
    private String firstRecharge;
    /**
     * tzcion
     */
    @JSONField(name = "tzcion")
    private String tzcion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserNicename() {
        return userNicename;
    }

    public void setUserNicename(String userNicename) {
        this.userNicename = userNicename;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarThumb() {
        return avatarThumb;
    }

    public void setAvatarThumb(String avatarThumb) {
        this.avatarThumb = avatarThumb;
    }

    public String getFirstRecharge() {
        return firstRecharge;
    }

    public void setFirstRecharge(String firstRecharge) {
        this.firstRecharge = firstRecharge;
    }

    public String getTzcion() {
        return tzcion;
    }

    public void setTzcion(String tzcion) {
        this.tzcion = tzcion;
    }
}
