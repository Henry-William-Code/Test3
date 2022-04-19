package com.qgnix.main.bean;

/**
 * Created by cxf on 2019/3/30.
 * 轮播图bean
 */
public class BannerBean {
    private String slide_pic;
    private String slide_url;

    public String getSlide_pic() {
        return slide_pic == null ? "-" : slide_pic;
    }

    public void setSlide_pic(String slide_pic) {
        this.slide_pic = slide_pic;
    }

    public String getSlide_url() {
        return slide_url == null ? "-" : slide_url;
    }

    public void setSlide_url(String slide_url) {
        this.slide_url = slide_url;
    }
}
