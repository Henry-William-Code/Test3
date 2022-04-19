package com.qgnix.main.glide.request;

public class RequestOptions {
    private int imageWidth;
    private int imageHeight;

    public int getImageWidth() {
        return imageWidth==0?800:imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight==0?800:imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public RequestOptions override(int width, int height){
        this.imageWidth = width;
        this.imageHeight = height;
        return this;
    }
}
