package com.qgnix.main.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.CopyUtils;
import com.qgnix.common.utils.ImgUtils;
import com.qgnix.common.utils.QRCodeUtils;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.main.bean.ShareInfoBean;
import com.qgnix.main.http.MainHttpUtil;

/**
 * @author sameal
 * @date 2020/7/12 14:36
 * 推广页
 */
@Deprecated
public class PromoteActivity extends AbsActivity implements View.OnClickListener {
    /**
     * 邀请码
     */
    private TextView mTvInviteCode;
    /**
     * 已邀请好友
     */
    private TextView mTvBuddyNum;
    /**
     * 有效好友人数
     */
    private TextView mTvEffectiveBuddyNum;
    /**
     * 昨日收益
     */
    private TextView mTvLastAmount;
    /**
     * 累计收益
     */
    private TextView mTvAllAmount;
    /**
     * 邀请奖励信息
     */
    private TextView mtvTitlesub;
    /**
     * 邀请奖励
     */
    private TextView mtvInvitationReward;
    private ShareInfoBean mBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_promote;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.promotion_to_make_money));
        mTvInviteCode = findViewById(R.id.tvInviteCode);
        mTvBuddyNum = findViewById(R.id.tvBuddyNum);
        mTvEffectiveBuddyNum = findViewById(R.id.tvEffectiveBuddyNum);
        mTvLastAmount = findViewById(R.id.tvLastAmount);
        mTvAllAmount = findViewById(R.id.tvAllAmount);
        mtvTitlesub = findViewById(R.id.tvTitlesub);
        //邀请奖励
        mtvInvitationReward = findViewById(R.id.tv_invitation_reward);
        findViewById(R.id.btnCopy).setOnClickListener(this);
        findViewById(R.id.btCopy).setOnClickListener(this);
        findViewById(R.id.btnSaveImage).setOnClickListener(this);

        getShareInfo();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btCopy) {
            if (null != mBean && !TextUtils.isEmpty(mBean.getShare_code())) {
                CopyUtils.copy(mContext, mBean.getShare_code());
            }
        } else if (i == R.id.btnCopy) {
            if (null != mBean && !TextUtils.isEmpty(mBean.getLink())) {
                CopyUtils.copy(mContext, mBean.getLink());
            }
        } else if (i == R.id.btnSaveImage) {
            if (null == mBean) {
                return;
            }
            String url = mBean.getLink();
            String www = mBean.getLinkbase();
            String sharecode = WordUtil.getString(com.qgnix.common.R.string.invite_code) + ":" + mBean.getShare_code();
            String if_scan_code_no_opened = WordUtil.getString(com.qgnix.common.R.string.if_scan_code_no_opened);

            Bitmap bitmap = QRCodeUtils.createImage(url, "UTF-8", sharecode, www, if_scan_code_no_opened, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            ImgUtils.saveImageToGallery(mContext, bitmap);
        }
    }


    // 获取推广信息
    public void getShareInfo() {
        MainHttpUtil.getShareInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 && info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                mBean = JSON.parseObject(info[0], ShareInfoBean.class);
                if (null == mBean) {
                    return;
                }
                showDataToView(mBean);
            }
        });

    }

    private void showDataToView(ShareInfoBean bean) {
        mTvInviteCode.setText(WordUtil.getString(R.string.Invitation_code,
                TextUtils.isEmpty(bean.getShare_code()) ? "-" : bean.getShare_code()));
        mTvBuddyNum.setText(TextUtils.isEmpty(bean.getFenxiaos()) ? "0" : bean.getFenxiaos());
        mTvEffectiveBuddyNum.setText(TextUtils.isEmpty(bean.getEffectivenumber()) ? "0" : bean.getEffectivenumber());
        mTvLastAmount.setText(TextUtils.isEmpty(bean.getYesterdayprofit()) ? "0.00" : bean.getYesterdayprofit());
        mTvAllAmount.setText(TextUtils.isEmpty(bean.getProfit()) ? "0.00" : bean.getProfit());
        mtvTitlesub.setText(WordUtil.getString(R.string.Successful_promotion, TextUtils.isEmpty(bean.getFenxiaos()) ? "0" : bean.getFenxiaos(), TextUtils.isEmpty(bean.getGetcoin()) ? "0.00" : bean.getGetcoin()));
        mtvInvitationReward.setText(WordUtil.getString(R.string.promote_text, TextUtils.isEmpty(bean.getInviteprofit()) ? "0" : bean.getInviteprofit()));

    }


}