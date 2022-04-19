package com.qgnix.main.glide.work;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.qgnix.main.glide.binding.inter.LifecycleListener;


public class ImageViewTarget implements LifecycleListener {

    private static final String TAG = "ImageViewTarget.class";

    private ImageView view;

    @Override
    public void onStart() {
        Log.i(TAG, "onStart: ImageViewTarget 做自己的具体业务逻辑处理 ....");
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop: ImageViewTarget 做自己的具体业务逻辑处理 ....");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ImageViewTarget 做自己的具体业务逻辑处理 ....");
    }

    public ImageViewTarget(ImageView view) {
        this.view = view;
    }

    public void setResource(Bitmap resource) {
        view.setImageBitmap(resource);
    }

    public void setImageResource(int resId) {
        view.setImageResource(resId);
    }
}
