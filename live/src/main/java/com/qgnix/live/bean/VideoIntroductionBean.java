package com.qgnix.live.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 视频简介bean
 */
public class VideoIntroductionBean {

    /**
     * title
     */ /**
     * title : dfgsdfh
     * content : 想起了兜里没钱dfghgfh
     */

    @JSONField(name = "title")
    private String title;
    /**
     * content
     */
    @JSONField(name = "content")
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
