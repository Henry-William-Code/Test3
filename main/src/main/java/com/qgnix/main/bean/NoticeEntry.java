package com.qgnix.main.bean;

public class NoticeEntry {
    /**
     * id : 1
     * title : 测试
     * content : 测试公告已发布
     * addtime :
     */

    private String id;
    private String title;
    private String content;
    private String addtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }
}
