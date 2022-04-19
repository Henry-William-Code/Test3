package com.qgnix.common.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.R;
import com.qgnix.common.utils.L;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by cxf on 2017/8/9.
 */

public class ImgLoader {
    private static final boolean SKIP_MEMORY_CACHE = false;

    private static BlurTransformation sBlurTransformation;

    static {
        sBlurTransformation = new BlurTransformation(25);
    }


    public static void displayRounded(Context context, int radius, int resId, ImageView imageView) {
        if (context == null) {
            return;
        }
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(context).load(resId).apply(options).into(imageView);
    }

    public static void displayRounded(Context context, int radius, String url, ImageView imageView) {
        if (context == null||TextUtils.isEmpty(url)) {
            return;
        }
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(context).load(handleUrl(url)).apply(options).into(imageView);
    }

    public static void display(Context context, String url, int placeHolderRes, ImageView imageView) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }
        Glide.with(context).load(handleUrl(url)).placeholder(placeHolderRes).into(imageView);
    }

    public static void display(Context context, String url, ImageView imageView) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }
        Glide.with(context).asDrawable().load(handleUrl(url)).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayWithError(Context context, String url, ImageView imageView, int errorRes) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }
        Glide.with(context).asDrawable().load(handleUrl(url)).placeholder(errorRes).error(errorRes).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayAvatar(Context context, String url, ImageView imageView) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }
        displayWithError(context, handleUrl(url), imageView, R.mipmap.icon_avatar_placeholder);
    }

    public static void display(Context context, File file, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(file).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void display(Context context, int res, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(res).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    /**
     * 显示视频封面缩略图
     */
    public static void displayVideoThumb(Context context, String videoPath, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(Uri.fromFile(new File(videoPath))).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayDrawable(Context context, String url, final DrawableCallback callback) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }
        Glide.with(context).asDrawable().load(handleUrl(url)).skipMemoryCache(SKIP_MEMORY_CACHE).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource,  Transition<? super Drawable> transition) {
                if (callback != null) {
                    callback.onLoadSuccess(resource);
                }
            }

            @Override
            public void onLoadFailed( Drawable errorDrawable) {
                if (callback != null) {
                    callback.onLoadFailed();
                }
            }

        });
    }
    public static void displayBitmap(Context context, String url, final BitmapCallback callback) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }
        Glide.with(context).asBitmap().load(handleUrl(url)).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource,  Transition<? super Bitmap> transition) {
                callback.onLoadSuccess(resource);
            }

            @Override
            public void onLoadCleared( Drawable placeholder) {
                callback.onLoadFailed();
            }
        });
    }

    public static void clear(Context context, ImageView imageView) {
        Glide.with(context).clear(imageView);
    }


    /**
     * 显示模糊的毛玻璃图片
     */
    public static void displayBlur(Context context, String url, ImageView imageView) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }
        Glide.with(context).asDrawable().load(handleUrl(url))
                .skipMemoryCache(SKIP_MEMORY_CACHE)
                .apply(RequestOptions.bitmapTransform(sBlurTransformation))
                .into(imageView);
    }


    public interface DrawableCallback {
        void onLoadSuccess(Drawable drawable);

        void onLoadFailed();
    }
    public interface BitmapCallback {
        void onLoadSuccess(Bitmap bitmap);

        void onLoadFailed();
    }



    /**
     * 处理图片地址
     *
     * @param url 原始图片地址
     * @return 处理后的图片地址
     */
    private static String handleUrl(String url) {
        if (url.startsWith("http") || url.startsWith("https") || url.startsWith("file")) {
            return url;
        }
        L.e("===handleImgUrl===>",url);
        return CommonAppConfig.getInstance().getImgHost() + url;
    }

}
