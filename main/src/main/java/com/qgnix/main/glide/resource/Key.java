package com.qgnix.main.glide.resource;


import com.qgnix.main.glide.util.Tool;

public class Key {

    private String key; // 合格：唯一 加密的  ac037ea49e34257dc5577d1796bb137dbaddc0e42a9dff051beee8ea457a4668  磁盘缓存用的
    // https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg 不合格 磁盘缓存 直接报错

    public Key(String path) { // https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg
        this.key = Tool.getSHA256StrJava(path);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
