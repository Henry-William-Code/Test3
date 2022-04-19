package com.qgnix.main.bean;

/**
 * 我的分销
 */
public class MyDistributionBean {
    /**
     * 分享链接
     */
    private String link;

    /**
     * 分销信息
     */
    private Summary summary;
    /**
     * 用户信息
     */
    private UserInfo userinfo;
    /**
     * 推荐人
     */
    private Referrer referrer;

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public UserInfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserInfo userinfo) {
        this.userinfo = userinfo;
    }

    public Referrer getReferrer() {
        return referrer;
    }

    public void setReferrer(Referrer referrer) {
        this.referrer = referrer;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    /**
     * 分销信息
     */
    public static class Summary {
        /**
         * 下线总人数
         */
        private String xxzrs;
        /**
         * 注册人数
         */
        private String cx_xxrs;
        /**
         * 投注总金额
         */
        private String tzzje;
        /**
         * 投注人数
         */
        private String cx_tzrs;
        /**
         * 送礼总金额
         */
        private String slzje;
        /**
         * 送礼金额
         */
        private String cx_slje;
        /**
         * 投注金额
         */
        private String cx_tzje;
        /**
         * 返点总额
         */
        private String fdzje;
        /**
         * 可提现金额
         */
        private String ktjje;
        /**
         * 返点总额
         */
        private String cx_fdje;


        public String getXxzrs() {
            return xxzrs;
        }

        public void setXxzrs(String xxzrs) {
            this.xxzrs = xxzrs;
        }

        public String getCx_xxrs() {
            return cx_xxrs;
        }

        public void setCx_xxrs(String cx_xxrs) {
            this.cx_xxrs = cx_xxrs;
        }

        public String getTzzje() {
            return tzzje;
        }

        public void setTzzje(String tzzje) {
            this.tzzje = tzzje;
        }

        public String getCx_tzrs() {
            return cx_tzrs;
        }

        public void setCx_tzrs(String cx_tzrs) {
            this.cx_tzrs = cx_tzrs;
        }

        public String getSlzje() {
            return slzje;
        }

        public void setSlzje(String slzje) {
            this.slzje = slzje;
        }

        public String getCx_slje() {
            return cx_slje;
        }

        public void setCx_slje(String cx_slje) {
            this.cx_slje = cx_slje;
        }

        public String getCx_tzje() {
            return cx_tzje;
        }

        public void setCx_tzje(String cx_tzje) {
            this.cx_tzje = cx_tzje;
        }

        public String getFdzje() {
            return fdzje;
        }

        public void setFdzje(String fdzje) {
            this.fdzje = fdzje;
        }

        public String getKtjje() {
            return ktjje;
        }

        public void setKtjje(String ktjje) {
            this.ktjje = ktjje;
        }

        public String getCx_fdje() {
            return cx_fdje;
        }

        public void setCx_fdje(String cx_fdje) {
            this.cx_fdje = cx_fdje;
        }
    }

    /**
     * 用户信息
     */
    public static class UserInfo {
        /**
         * id
         */
        private String id;
        /**
         * 用户名
         */
        private String user_nicename;
        /**
         * 头像
         */
        private String avatar;
        /**
         * 头像缩略图
         */
        private String avatar_thumb;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_nicename() {
            return user_nicename;
        }

        public void setUser_nicename(String user_nicename) {
            this.user_nicename = user_nicename;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAvatar_thumb() {
            return avatar_thumb;
        }

        public void setAvatar_thumb(String avatar_thumb) {
            this.avatar_thumb = avatar_thumb;
        }
    }

    /**
     * 推荐人信息
     */
    public static class Referrer {
        /**
         * id
         */
        private String id;
        /**
         * 用户名
         */
        private String user_nicename;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_nicename() {
            return user_nicename;
        }

        public void setUser_nicename(String user_nicename) {
            this.user_nicename = user_nicename;
        }
    }


}
