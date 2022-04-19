package com.qgnix.live.lottery.entry;

import java.util.List;

public class ResultBean {


    /**
     * code : 0
     * msg : 投注成功
     * info : {"coin":"17106"}
     */

    private int code;
    private String msg;
   // private List<InfoBean> info;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

  /*  public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }*/

    public static class InfoBean {
        /**
         * coin : 17106
         */

        private String coin;

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }
    }
}
