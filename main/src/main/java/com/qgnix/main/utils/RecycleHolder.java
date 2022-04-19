package com.qgnix.main.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qgnix.common.glide.ImgLoader;
import com.qgnix.main.adapter.NoMoreClickListener;


/**
 * @author sameal
 * @date 2016/9/7
 */

public class RecycleHolder extends RecyclerView.ViewHolder {

    /**
     * 用于存储当前item当中的View
     */
    private SparseArray<View> mViews;

    public RecycleHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<View>();
    }

    public <T extends View> T findView(int ViewId) {
        View view = mViews.get(ViewId);
        //集合中没有，则从item当中获取，并存入集合当中
        if (view == null) {
            view = itemView.findViewById(ViewId);
            mViews.put(ViewId, view);
        }
        return (T) view;
    }


    public RecycleHolder setSlideClickListener(int menuId, NoMoreClickListener listener) {
        View view = findView(menuId);
        view.setOnClickListener(listener);
        return this;
    }

    public RecycleHolder setSlideLongClickListener(int viewId, View.OnLongClickListener longClickListener) {
        View view = findView(viewId);
        view.setOnLongClickListener(longClickListener);
        return this;
    }

    public RecycleHolder setTextViewFlags(int id) {
        TextView textView = findView(id);
        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        return this;
    }


    public RecycleHolder setText(int viewId, String text) {
        TextView tv = findView(viewId);
        if (text == null) {
            text = "";
        }
        tv.setText(text);
        return this;
    }

    public RecycleHolder setText(int viewId, Spanned text) {
        TextView tv = findView(viewId);
        if (text == null) {
            tv.setText("");
        } else {
            tv.setText(text);
        }
        return this;
    }

    public RecycleHolder setEnable(int viewId, boolean enable) {
        View view = findView(viewId);
        view.setEnabled(enable);
        return this;
    }

    public RecycleHolder setRotation(int viewId, int rotation) {
        ImageView iv = findView(viewId);
        iv.setRotation(rotation);
        return this;
    }

    public RecycleHolder setEms(int viewId, int ems) {
        TextView tv = findView(viewId);
        tv.setMaxEms(ems);
        return this;
    }

    public String getText(int viewId) {
        TextView textView = findView(viewId);
        return textView.getText().toString();
    }

    public RecycleHolder setTextSize(int viewId, int size) {
        TextView tv = findView(viewId);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        return this;
    }

    public RecycleHolder setText(int viewId, int text) {
        TextView tv = findView(viewId);
        tv.setText(text);
        return this;
    }

    public RecycleHolder setImageResource(int viewId, int ImageId) {
        ImageView image = findView(viewId);
        image.setImageResource(ImageId);
        return this;
    }

    public RecycleHolder setImageResource(int viewId, int ImageId, int radius) {
        ImageView view = findView(viewId);
        ImgLoader.displayRounded(view.getContext(), radius, ImageId, view);
        return this;
    }

    public Drawable.ConstantState getImgResource(int id) {
        ImageView imageView = findView(id);
        return imageView.getDrawable().getCurrent().getConstantState();
    }

    public RecycleHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView image = findView(viewId);
        image.setImageBitmap(bitmap);
        return this;
    }

    public RecycleHolder setImageNet(int viewId, String url) {
        ImageView image = findView(viewId);
        ImgLoader.display(image.getContext(), url, image);
        return this;
    }

    public RecycleHolder setImageParms(int viewId, int hight, int weight) {
        ImageView imageView = findView(viewId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(weight, hight);
        imageView.setLayoutParams(params);
        return this;
    }

    public RecycleHolder setCirImage(int viewId, String url) {
        ImageView imageView = findView(viewId);
        ImgLoader.display(imageView.getContext(), url, imageView);
        return this;
    }

    /**
     * 设置圆角图片
     *
     * @param viewId
     * @param url
     * @param radius
     * @return
     */
    public RecycleHolder setRoundImageByUrl(int viewId, String url, int radius) {
        ImageView view = findView(viewId);
        ImgLoader.displayRounded(view.getContext(), radius, url, view);
        return this;
    }

    public RecycleHolder setVisibility(int viewId) {
        View view = findView(viewId);
        view.setVisibility(View.VISIBLE);
        return this;
    }

    public RecycleHolder setVisibility(int viewId, int t) {
        View view = findView(viewId);
        view.setVisibility(t);
        return this;
    }

    public RecycleHolder setTextColor(int id, String color) {
        TextView view = findView(id);
        view.setTextColor(Color.parseColor(color));
        return this;
    }

    public RecycleHolder setTextColor(int id, int color) {
        TextView view = findView(id);
        view.setTextColor(color);
        return this;
    }

    public RecycleHolder setTextMaxEms(int id, int max) {
        TextView view = findView(id);
        view.setMaxEms(max);
        return this;
    }

    public RecycleHolder setBackColor(int id, int color) {
        View view = findView(id);
        view.setBackgroundColor(color);
        return this;
    }

    public RecycleHolder setBackgroup(int id, Drawable drawable) {
        View view = findView(id);
        view.setBackground(drawable);
        return this;
    }

    public RecycleHolder setBackgroup(int id, GradientDrawable drawable) {
        View view = findView(id);
        view.setBackground(drawable);
        return this;
    }

    public RecycleHolder setBackgroup(int id, int drawable) {
        View view = findView(id);
        view.setBackgroundResource(drawable);
        return this;
    }

    public RecycleHolder setParms(int id, RecyclerView.LayoutParams params) {
        View view = findView(id);
        view.setLayoutParams(params);
        return this;
    }

}
