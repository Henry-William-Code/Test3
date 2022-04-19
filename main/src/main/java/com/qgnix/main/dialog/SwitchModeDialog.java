package com.qgnix.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.main.R;
import com.qgnix.main.interfaces.OnSwitchModelListener;

/**
 * 切换模式
 */
public class SwitchModeDialog extends BaseCustomDialog implements View.OnClickListener {

    private TextView mTvAdult;
    private TextView mTvNonAdult;
    private final Context mContext;
    private final OnSwitchModelListener mOnSwitchModelListener;

    public SwitchModeDialog(Context context, OnSwitchModelListener mOnSwitchModelListener) {
        super(context);
        this.mContext = context;
        this.mOnSwitchModelListener = mOnSwitchModelListener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_switch_model_layout;
    }

    @Override
    public void initView() {
        mTvAdult = findViewById(R.id.tv_adult);
        mTvNonAdult = findViewById(R.id.tv_non_adult);
        int isAdult = CommonAppConfig.ADULT_MODE;
        int adultColor = isAdult==1 ? R.color.global:  R.color.black2;
        int nonAdultColor = isAdult==0 ? R.color.global: R.color.black2;

        setAdultColor(adultColor,nonAdultColor);


        mTvAdult.setOnClickListener(this);
        mTvNonAdult.setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public int showGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.tv_cancel) {
            dismiss();
        } else if (vId == R.id.tv_adult) {
            CommonAppConfig.ADULT_MODE=1;
            setAdultColor(R.color.global,R.color.black2);
            dismiss();
            mOnSwitchModelListener.success();
        } else if (vId == R.id.tv_non_adult) {
            CommonAppConfig.ADULT_MODE=0;
            setAdultColor(R.color.black2,R.color.global);
            dismiss();
            mOnSwitchModelListener.success();
        }
    }

    private void setAdultColor(int adultColor,int nonAdultColor){
        mTvAdult.setTextColor( mContext.getResources().getColor(adultColor));
        mTvNonAdult.setTextColor( mContext.getResources().getColor(nonAdultColor));
    }
}
