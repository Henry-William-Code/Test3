package com.qgnix.main.bean;

import java.util.List;

/**
 * @author zhangj
 * @version 1.0
 * @date 2020/7/19 16:42
 */
public class UserBankBean {


    private List<BankCardBean> data;

    // 余额信息
    private Profit profit;
    // 马币
    private Balance balance;
    // 需打的流水
    private GiveCoin give_coin;

    private String min;
    private String max;
    private int transaction_pass_status;

    public List<BankCardBean> getData() {
        return data;
    }

    public void setData(List<BankCardBean> data) {
        this.data = data;
    }

    public Profit getProfit() {
        return profit;
    }

    public void setProfit(Profit profit) {
        this.profit = profit;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public GiveCoin getGive_coin() {
        return give_coin;
    }

    public void setGive_coin(GiveCoin give_coin) {
        this.give_coin = give_coin;
    }

    public int getTransaction_pass_status() {
        return transaction_pass_status;
    }

    public void setTransaction_pass_status(int transaction_pass_status) {
        this.transaction_pass_status = transaction_pass_status;
    }

    /**
     * 门票
     */
    public static class Profit {

        private String votes;
        private String votestotal;
        private String total;
        private String cash_rate;
        private String tips;

        public String getVotes() {
            return votes;
        }

        public void setVotes(String votes) {
            this.votes = votes;
        }

        public String getVotestotal() {
            return votestotal;
        }

        public void setVotestotal(String votestotal) {
            this.votestotal = votestotal;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getCash_rate() {
            return cash_rate;
        }

        public void setCash_rate(String cash_rate) {
            this.cash_rate = cash_rate;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }
    }

    /**
     * 马币allcoin总，coin可提现
     */
    public static class Balance {
        private String coin;
        private String allcoin;

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public String getAllcoin() {
            return allcoin;
        }

        public void setAllcoin(String allcoin) {
            this.allcoin = allcoin;
        }
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public static class GiveCoin {
        private String give_coin;

        public String getGive_coin() {
            return give_coin;
        }

        public void setGive_coin(String give_coin) {
            this.give_coin = give_coin;
        }
    }

}
