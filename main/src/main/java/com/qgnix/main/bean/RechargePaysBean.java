package com.qgnix.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 充值bean
 */
public class RechargePaysBean {

    /**
     * amounts
     */ /**
     * online : [{"url":"/index.php?g=Appapi&m=Awepay","bonus":"0","trait":"2","currency":"MYR"},{"url":"/index.php?g=Appapi&m=Eeziepay","bonus":"0","trait":"2","currency":"MYR,IDR,THB,VND"},{"url":"/index.php?g=Appapi&m=Fastspay","bonus":"0","trait":"2","currency":"MYR,IDR,THB,VND"},{"url":"/index.php?g=Appapi&m=Gspay","bonus":"0","trait":"2","currency":"MYR"}]
     * offline : [{"url":"24","bonus":"20","trait":"1","currency":"MYR"}]
     * amounts : 100,200,500,1000,2000,5000,10000,20000
     * rate : 18.100000
     */

    /**
     * 金额
     */
    @JSONField(name = "amounts")
    private String amounts;
    /**
     * 汇率
     */
    @JSONField(name = "rate")
    private String rate;
    /**
     * 线上
     */
    @JSONField(name = "online")
    private List<PayChannelsBean> online;
    /**
     * 线下
     */
    @JSONField(name = "offline")
    private List<PayChannelsBean> offline;
    @JSONField(name = "crypto")
    private List<PayChannelsBean> crypto;

    /**
     * 公告
     */
    @JSONField(name = "declare")
    private String declare;

    public String getAmounts() {
        return amounts;
    }

    public void setAmounts(String amounts) {
        this.amounts = amounts;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public List<PayChannelsBean> getOnline() {
        return online;
    }

    public void setOnline(List<PayChannelsBean> online) {
        this.online = online;
    }

    public List<PayChannelsBean> getOffline() {
        return offline;
    }

    public void setOffline(List<PayChannelsBean> offline) {
        this.offline = offline;
    }

    public String getDeclare() {
        return declare;
    }

    public void setDeclare(String declare) {
        this.declare = declare;
    }

    public List<PayChannelsBean> getCrypto() {
        return crypto;
    }

    public void setCrypto(List<PayChannelsBean> crypto) {
        this.crypto = crypto;
    }

    /**
     * 支付渠道
     */
    public static class PayChannelsBean {
        /**
         * url : /index.php?g=Appapi&m=Awepay
         * bonus : 0
         * trait : 2
         * currency : MYR
         * min : 50
         * max : 1000
         */
        /**
         * 渠道名称
         */
        private String channelName;
        /**
         * 地址
         */
        @JSONField(name = "url")
        private String url;
        /**
         * 奖励
         */
        @JSONField(name = "bonus")
        private String bonus;
        /**
         * trait
         */
        @JSONField(name = "trait")
        private String trait;
        /**
         * 币种
         */
        @JSONField(name = "currency")
        private String currency;

        /**
         * 最小值
         */
        @JSONField(name = "min")
        private int min;
        /**
         * 最大值
         */
        @JSONField(name = "max")
        private int max;

        /**
         * 是否选中
         */
        private boolean select;

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getBonus() {
            return bonus;
        }

        public void setBonus(String bonus) {
            this.bonus = bonus;
        }

        public String getTrait() {
            return trait;
        }

        public void setTrait(String trait) {
            this.trait = trait;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public boolean isSelect() {
            return select;
        }

        public void setSelect(boolean select) {
            this.select = select;
        }
    }
}
