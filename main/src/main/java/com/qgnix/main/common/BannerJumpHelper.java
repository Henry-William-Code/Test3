package com.qgnix.main.common;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.qgnix.main.activity.BigImgActivity;
import com.qgnix.main.activity.WebViewsActivity;
import com.qgnix.main.bean.BannerBean;

public class BannerJumpHelper {
    public void jump(Context context, BannerBean bean) {
        String link = bean.getSlide_url();
        if (!TextUtils.isEmpty(link)) {
            if (link.contains("&amp;")) {
                link = link.replace("&amp;", "&");
            }
            WebViewsActivity.forward(context, link, link.contains("is_token=true"), "");
        } else {
            Intent intent = new Intent(context, BigImgActivity.class);
            intent.putExtra("imgUrl", bean.getSlide_pic());
            context.startActivity(intent);
        }
    }
}
