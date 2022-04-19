package com.qgnix.main.glide.request;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.qgnix.main.glide.engine.Engine;
import com.qgnix.main.glide.work.ImageViewTarget;

import java.lang.ref.SoftReference;

public class RequestBuilder {

    // 请求路径
    private String url;

    // 上下文
    private Context context;

    // 需要加载图片的控件
    private SoftReference<ImageView> imageView;

    // 占位图片
    private int resId;

    // 回调对象
    private RequestListener requestListener;

    // 图片的标识
    private String urlMd5;

    RequestOptions  requestOptions;




    public RequestBuilder(Context context) {
        this.context = context;
    }

    // 链式调度
    // 加载url
    public RequestBuilder load(String url) {
        this.url = url;
        Engine.getInstance().load(url,context);
        return this;
    }

    public RequestBuilder load(Uri uri) {
        this.url = url;
        Engine.getInstance().load(uri,context);
        return this;
    }

    public RequestBuilder load(int resId) {
        Engine.getInstance().load(resId,context);
        return this;
    }

    // 设置占位图片
    public RequestBuilder loadding(int resId) {
        this.resId = resId;
        return this;
    }

    // 设置监听器
    public RequestBuilder setListener(RequestListener requestListener) {
        this.requestListener = requestListener;
        return this;
    }


    public RequestBuilder apply(RequestOptions  requestOptions) {
        this.requestOptions = requestOptions;
        return this;
    }

    public RequestBuilder placeholder(int resId) {
        this.resId = resId;
        return this;
    }





    // 显示图片的控件
    public void into(ImageView imageView) {
        imageView.setTag(urlMd5);
        this.imageView = new SoftReference<>(imageView);
        ImageViewTarget imageViewTarget = new ImageViewTarget(imageView);
        //RequestManager.getInstance(new ApplicationLifecycle(),context).addBitmapRequest(this);
        Engine.getInstance().into(imageViewTarget,requestOptions,resId);
    }

    public String getUrl() {
        return url;
    }

    public Context getContext() {
        return context;
    }

    public ImageView getImageView() {
        return imageView.get();
    }

    public int getResId() {
        return resId;
    }

    public RequestListener getRequestListener() {
        return requestListener;
    }

    public String getUrlMd5() {
        return urlMd5;
    }




}
