package com.qgnix.live.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.R;
import com.qgnix.live.bean.VideoIntroductionBean;
import com.qgnix.live.http.LiveHttpUtil;

/**
 * 视频简介
 */
public class VideoIntroductionDialog extends BaseCustomDialog {
    private final String mLiveId;
    private TextView mTvContent;

    public VideoIntroductionDialog(Context context, String mLiveId) {
        super(context);
        this.mLiveId = mLiveId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_video_introduction;
    }

    @Override
    public void initView() {
        mTvContent = findViewById(R.id.tv_content);
    }

    @Override
    public void initData() {
        LiveHttpUtil.getVideoIntroduction(mLiveId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                VideoIntroductionBean bean = JSON.parseObject(info[0], VideoIntroductionBean.class);
                if (bean==null|| TextUtils.isEmpty(bean.getContent())){
                    mTvContent.setText(R.string.no_data);
                    return;
                }
                String content = bean.getContent();
                mTvContent.setText(content);
            }
        });
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
