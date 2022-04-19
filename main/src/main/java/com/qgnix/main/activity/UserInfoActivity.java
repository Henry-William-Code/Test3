package com.qgnix.main.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.interfaces.ImageResultCallback;
import com.qgnix.common.utils.ClickUtil;
import com.qgnix.common.utils.CopyUtils;
import com.qgnix.common.utils.DialogUtil;
import com.qgnix.common.utils.ProcessImageUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.main.event.ModifyPhoneNum;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

/**
 * @author zhangj
 * @date 2020/7/15 23:55
 * 个人信息
 */
public class UserInfoActivity extends AbsActivity implements View.OnClickListener {
    /**
     * 头像
     */
    private RoundedImageView mAvatar;

    /**
     * 账户
     */
    private TextView mTvAccount;
    /**
     * 昵称
     */
    private TextView mTvNickname;
    /**
     * 真实名字
     */
    private TextView mTvRealname;

    /**
     * 联系电话
     */
    private TextView mTvContactNumber;

    /**
     * 最后一次登录时间
     */
    private TextView mTvLastLoginTime;

    /**
     * 注册时间
     */
    private TextView mTvRegisterTime;


    /**
     * 上传照片
     */
    private ProcessImageUtil mImageUtil;

    private View mRealnameLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.personal_information));

        EventBus.getDefault().register(this);


        mAvatar = findViewById(R.id.avatar);
        //修改头像
        findViewById(R.id.rl_avatar).setOnClickListener(this);
        //账户
        findViewById(R.id.ll_account).setOnClickListener(this);
        mTvAccount = findViewById(R.id.tv_account);
        //昵称
        findViewById(R.id.ll_nickname).setOnClickListener(this);
        mTvNickname = findViewById(R.id.tv_nickname);
        mTvRealname = findViewById(R.id.tv_realname);
        mRealnameLayout = findViewById(R.id.ll_realname);
        //联系电话
        findViewById(R.id.ll_contact_number).setOnClickListener(this);
        mTvContactNumber = findViewById(R.id.tv_contact_number);

        // 最后一次登录时间
        mTvLastLoginTime = findViewById(R.id.tv_last_login_time);
        // 注册时间
        mTvRegisterTime = findViewById(R.id.tv_register_time);

        if("IN".equals(CommonAppConfig.getInstance().getCountry())){
            mRealnameLayout.setVisibility(View.VISIBLE);
        }else{
            mRealnameLayout.setVisibility(View.GONE);
        }

        CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if (null == bean) {
                    return;
                }
                if (!TextUtils.isEmpty(bean.getAvatar())) {
                    ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
                }
                mTvNickname.setText(bean.getUserNiceName());
                mTvRealname.setText(bean.getRealName());
                mTvAccount.setText(bean.getId());
                mTvContactNumber.setText(bean.getMobile());
                mTvLastLoginTime.setText(bean.getLast_login_time());
                mTvRegisterTime.setText(bean.getCreate_time());
            }
        });

        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(mContext, file, mAvatar);
                    MainHttpUtil.updateAvatar(file, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, final String[] info) {
                            if (code != 0 || info.length == 0) {
                                ToastUtil.show(msg);
                                return;
                            }
                            ToastUtil.show(R.string.edit_profile_update_avatar_success);
                            CommonAppConfig.getInstance().clearUserInfo();
//                            CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
//                                @Override
//                                public void callback(UserBean bean) {
//                                    if (bean != null) {
//                                        JSONObject obj = JSON.parseObject(info[0]);
//                                        bean.setAvatar(obj.getString("avatar"));
//                                        bean.setAvatarThumb(obj.getString("avatarThumb"));
//                                    }
//                                }
//                            });
                        }
                    });
                }
            }

            @Override
            public void onFailure() {
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (!ClickUtil.canClick()) {
            return;
        }
        int vId = v.getId();
        if (vId == R.id.rl_avatar) {
            // 修改头像
            editAvatar();
        } else if (vId == R.id.ll_account) {
            // 复制账户
            CopyUtils.copy(mContext, mTvAccount.getText().toString().trim());
        } else if (vId == R.id.ll_nickname) {
            // 修改昵称
            Intent intent = new Intent(mContext, EditNicknameActivity.class);
            intent.putExtra(Constants.NICK_NAME, mTvNickname.getText());
            startActivityForResult(intent, 100);
        } else if (vId == R.id.ll_contact_number) {
            // 修改联系电话
            Intent intent = new Intent(mContext, ModifyPhoneNumActivity.class);
            intent.putExtra("phoneNum", mTvContactNumber.getText().toString().trim());
            startActivity(intent);
        }
    }

    /**
     * 修改头像
     */
    private void editAvatar() {
        requestPermission(new Runnable() {
            @Override
            public void run() {
                DialogUtil.showStringArrayDialog(mContext, new Integer[]{
                        R.string.camera, R.string.alumb}, new DialogUtil.StringArrayDialogCallback() {
                    @Override
                    public void onItemClick(String text, int tag) {
                        if (tag == R.string.camera) {
                            mImageUtil.getImageByCamera();
                        } else {
                            mImageUtil.getImageByAlumb();
                        }
                    }
                });
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == 100) {
            // 昵称
            mTvNickname.setText(data.getStringExtra(Constants.NICK_NAME));
        }

    }

    @Subscribe
    public void event(ModifyPhoneNum phoneNum) {
        mTvContactNumber.setText(phoneNum.getPhoneNum());
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_AVATAR);
        MainHttpUtil.cancel(CommonHttpConsts.GET_BASE_INFO);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}