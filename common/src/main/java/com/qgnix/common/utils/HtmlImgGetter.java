package com.qgnix.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.qgnix.common.glide.ImgLoader;

/**
 * 加载html图片
 */
public class HtmlImgGetter implements Html.ImageGetter {
    private final Context mContext;
    private final TextView mTextView;
    private final int mWidth;

    public HtmlImgGetter(TextView txt) {
        this.mTextView = txt;
        this.mContext = txt.getContext();
        mWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        Log.e("===HtmlImgGetter=", "screenWidth: " + mWidth);
    }


    @Override
    public Drawable getDrawable(String source) {
        Log.e("===HtmlImgGetter=", "getDrawable: " + source);
        final UrlDrawable urlDrawable = new UrlDrawable();
        ImgLoader.displayBitmap(mContext, source, new ImgLoader.BitmapCallback() {
            @Override
            public void onLoadSuccess(Bitmap imgBitmap) {
                if (null == imgBitmap) return;
                Log.e("===HtmlImgGetter=", "imgBitmap: " + imgBitmap.getWidth() + "::" + imgBitmap.getHeight());
                float scale = (float) mWidth / (float) imgBitmap.getWidth();
                Log.e("===HtmlImgGetter=", "scale: " + scale);
                int bw = Math.round(imgBitmap.getWidth() * scale);
                urlDrawable.bitmap = imgBitmap;
                urlDrawable.setBounds(0, 0, bw, imgBitmap.getHeight());
                mTextView.invalidate();
                mTextView.setText(mTextView.getText()); // 解决图文重叠
            }

            @Override
            public void onLoadFailed() {
                Log.e("===HtmlImgGetter=失败==", "onLoadFailed ");
            }
        });
        return urlDrawable;
    }

    public static class UrlDrawable extends BitmapDrawable {
        protected Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) { // override the draw to facilitate refresh function later
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, getPaint());
            }
        }
    }

    /**
     * 返回bitmap的接口
     */
    public interface OnResultListener {
        void onResult(Bitmap bitmap);
    }
}