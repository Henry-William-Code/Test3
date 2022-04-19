package com.qgnix.main.dialog;


import android.content.Context;
import android.graphics.Bitmap;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.glide.ImgLoader;

import com.qgnix.common.utils.L;
import com.qgnix.common.views.BigImageView;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.main.R;

import java.io.ByteArrayOutputStream;

/**
 * 新用户注册送彩金
 */
public class ActivityInfoDialog extends BaseCustomDialog implements View.OnClickListener {
    private final Context mContext;

    public ActivityInfoDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_activity_info_layout;
    }

    @Override
    public void initView() {
        final BigImageView tvNewRegBonus = findViewById(R.id.iv_new_reg_bonus);
        findViewById(R.id.btn_close).setOnClickListener(this);
        // 图片地址
        String bonusImgUrl = CommonAppConfig.getInstance().getNewRegBonus()!=null &&  CommonAppConfig.getInstance().getNewRegBonus().startsWith("http")? CommonAppConfig.getInstance().getNewRegBonus() : CommonAppConfig.getInstance().getImgHost() + CommonAppConfig.getInstance().getNewRegBonus();
        L.e("===bonusImgUrl==", bonusImgUrl);
        ImgLoader.displayBitmap(mContext, bonusImgUrl, new ImgLoader.BitmapCallback() {
            @Override
            public void onLoadSuccess(Bitmap imgBitmap) {
                if (null == imgBitmap) return;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                tvNewRegBonus.setImage(baos.toByteArray());
            }

            @Override
            public void onLoadFailed() {
                Log.e("===HtmlImgGetter=失败==", "onLoadFailed ");
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    public int showGravity() {
        return Gravity.CENTER;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_close) {
            dismiss();
        }
    }
}


