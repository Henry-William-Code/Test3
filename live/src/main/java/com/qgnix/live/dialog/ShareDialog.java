package com.qgnix.live.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ShotScreenManager;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;

public class ShareDialog extends BaseCustomDialog {
    private final Context mContext;

    /**
     * 头像地址
     */
    private final String mAvatarUrl;
    private String mInviteCode; //当前用户的邀请码
    private String mSite;      //分享的站点


    public ShareDialog(Context context, String avatarUrl, String inviteCode, String site) {
        super(context);
        this.mContext = context;
        this.mAvatarUrl = avatarUrl;
        this.mInviteCode = inviteCode;
        this.mSite = site;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_share_layout;
    }

    @Override
    public void initView() {
        LinearLayout llRoot = findViewById(R.id.ll_root);
        ImageView ivAvatar = findViewById(R.id.iv_avatar);
        if (!TextUtils.isEmpty(mAvatarUrl)) {
            ImgLoader.display(mContext, mAvatarUrl, ivAvatar);
        }

        // 邀请码
        TextView tvInvitationCode = findViewById(R.id.tv_invitation_code);
        if(mInviteCode==null)
            mInviteCode = CommonAppConfig.LOCAL_INVITATION_CODE;
        tvInvitationCode.setText(WordUtil.getString(R.string.invitation_code, mInviteCode));
        if(mSite!=null)
            ((TextView)findViewById(R.id.tv_site) ).setText(mSite);

        //关闭
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // 保存
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShotScreenManager.getInstance().viewShotScreenToGallery(llRoot, "cherry");
                dismiss();
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
}
