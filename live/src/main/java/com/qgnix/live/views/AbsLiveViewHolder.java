package com.qgnix.live.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.views.AbsViewHolder;
import com.qgnix.live.R;

/**
 * Created by cxf on 2018/10/9.
 */

public abstract class AbsLiveViewHolder extends AbsViewHolder implements View.OnClickListener {

    private TextView mRedPoint;
    //消息，只有主播显示
    private ImageView mBtnMsg;

    public AbsLiveViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public AbsLiveViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    @Override
    public void init() {
        findViewById(R.id.btn_chat).setOnClickListener(this);
        mBtnMsg = (ImageView) findViewById(R.id.btn_msg);
        mBtnMsg.setOnClickListener(this);
        mRedPoint = (TextView) findViewById(R.id.red_point);
    }

  /*  @Override
    public void onClick(View v) {
        int i = v.getId();
        if (null != ((LiveActivity) mContext).mKeyBoardHeightUtil) {
            ((LiveActivity) mContext).mKeyBoardHeightUtil.release();
        }
        if (i == R.id.btn_msg) {
            // TODO: 2020/9/24  主播消息待完善
            ToastUtil.show("主播消息待完善");
            // ((LiveActivity) mContext).openChatListWindow();
        } else if (i == R.id.btn_chat) {
            if (CommonAppConfig.getInstance().getUserBean().getLevel() >= 1) {
                ((LiveActivity) mContext).openChatWindow();
            } else {
                ToastUtil.show("您需要达到VIP1才能发言");
            }
        }
    }*/

    public void setUnReadCount(String unReadCount) {
        if (mRedPoint != null) {
            if ("0".equals(unReadCount)) {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mRedPoint.getVisibility() != View.VISIBLE) {
                    // mRedPoint.setVisibility(View.VISIBLE);
                }
            }
            mRedPoint.setText(unReadCount);
        }
    }


}
