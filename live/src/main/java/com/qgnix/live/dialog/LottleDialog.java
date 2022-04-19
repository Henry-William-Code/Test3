package com.qgnix.live.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qgnix.live.R;

// 开奖、投注
public class LottleDialog extends BaseCustomDialog {

    private final Context mContext;

    private final String mTId;

    public LottleDialog(Context context, String tId) {
        super(context);
        this.mContext = context;
        this.mTId = tId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_lottle;
    }

    @Override
    public void initView() {
        ImageView  imgClose = findViewById(R.id.img_close);
        RelativeLayout relaKaijiang = findViewById(R.id.rela_kaijiang);
        RelativeLayout   relaTouzhu = findViewById(R.id.rela_touzhu);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        relaTouzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                new BettingRecordDialog(mContext, mTId).show();

            }
        });
        relaKaijiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                new DrawHistoryDialog(mContext, mTId).show();
            }
        });
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

}
