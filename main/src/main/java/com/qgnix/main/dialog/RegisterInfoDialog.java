package com.qgnix.main.dialog;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.utils.L;
import com.qgnix.common.views.BigImageView;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.main.R;

import java.io.ByteArrayOutputStream;

/**
 * 新用户注册页弹框
 */
public class RegisterInfoDialog extends BaseCustomDialog implements View.OnClickListener {
    private final Context mContext;
    private IOnDialogClickListener mOnDialogClickListener;

    public RegisterInfoDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public void setOnDialogClickListener(IOnDialogClickListener listener){
        this.mOnDialogClickListener = listener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_register_skip;
    }

    @Override
    public void initView() {
        TextView reg_sure = findViewById(R.id.reg_sure);
        TextView reg_cancle = findViewById(R.id.reg_cancle);
        TextView coin_tip = findViewById(R.id.coin_tip);
        reg_sure.setOnClickListener(this);
        reg_cancle.setOnClickListener(this);
        coin_tip.setText(mContext.getString(R.string.reg_dialog_content_tip,CommonAppConfig.getInstance().getCoinAward()));
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
        if (v.getId() == R.id.reg_sure) {
            if(mOnDialogClickListener!=null){
                mOnDialogClickListener.onOkListener();
            }
            dismiss();
        }
        if(v.getId() == R.id.reg_cancle){
            if(mOnDialogClickListener!=null){
                mOnDialogClickListener.onCancleListener();
            }
            dismiss();
        }
    }

    public interface IOnDialogClickListener{
       void onOkListener();
       void onCancleListener();
    }
}


