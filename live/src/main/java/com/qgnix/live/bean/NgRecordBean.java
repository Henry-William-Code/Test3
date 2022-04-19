package com.qgnix.live.bean;

public class NgRecordBean {

    private String id;    //平台全局唯一ID 该字段需要商户保存	int(11)
    private String betId;    //	注单id	varchar(255)
    private String username;    //	玩家名称	varchar(60)
    private String platType;    //	平台类型plat_type	varchar(20)
    private String gameType;    //	游戏类型game_type	tinyint(2)
    private String betAmount;    //	下注金额	decimal(12,4)
    private String validAmount;    //	有效投注金额	decimal(12,4)
    private String winLoss;    //	输赢金额	decimal(12,4)
    private String gameName;    //	游戏名称	varchar(500)
    private String betContent;    //	下注内容 数据库字段长度建议为text，部分游戏字段投注内容会比较长	text
    private String awardResult;    //	开奖结果	varchar(1000)
    private String betTime;    //	下注（北京）时间	date
    private String lastUpdateTime;    //	最后更新（北京）时间	date
    private String status;    //	状态 1已结算 2未结算 0无效注单	tinyint(1)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlatType() {
        return platType;
    }

    public void setPlatType(String platType) {
        this.platType = platType;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public String getValidAmount() {
        return validAmount;
    }

    public void setValidAmount(String validAmount) {
        this.validAmount = validAmount;
    }

    public String getWinLoss() {
        return winLoss;
    }

    public void setWinLoss(String winLoss) {
        this.winLoss = winLoss;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getBetContent() {
        return betContent;
    }

    public void setBetContent(String betContent) {
        this.betContent = betContent;
    }

    public String getAwardResult() {
        return awardResult;
    }

    public void setAwardResult(String awardResult) {
        this.awardResult = awardResult;
    }

    public String getBetTime() {
        return betTime;
    }

    public void setBetTime(String betTime) {
        this.betTime = betTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
