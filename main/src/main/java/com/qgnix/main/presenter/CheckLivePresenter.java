package com.qgnix.main.presenter;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.DialogUtil;
import com.qgnix.common.utils.MD5Util;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.activity.LiveAudienceActivity;
import com.qgnix.live.bean.LiveBean;
import com.qgnix.live.http.LiveHttpConsts;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.main.R;

import java.util.List;

/**
 * Created by cxf on 2017/9/29.
 * 直播详情
 */

public class CheckLivePresenter {

    private Context mContext;
    private LiveBean mLiveBean;//选中的直播间信息
    private String mKey;
    private int mPosition;
    private int mLiveType;//直播间的类型  普通 密码 门票 计时等
    private int mLiveTypeVal;//收费价格等
    private String mLiveTypeMsg;//直播间提示信息或房间密码
    private int mLiveSdk;
    private HttpCallback mCheckLiveCallback;
    private List<LiveBean> mListList;

    public CheckLivePresenter(Context context) {
        mContext = context;
    }

    /**
     * 观众 观看直播
     */
    public void watchLive(LiveBean bean, String key, int position) {
        mLiveBean = bean;
        mKey = key;
        mPosition = position;
        if (mCheckLiveCallback == null) {
            mCheckLiveCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        if (info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            mLiveType = obj.getIntValue("type");
                            mLiveTypeVal = obj.getIntValue("type_val");
                            mLiveTypeMsg = obj.getString("type_msg");
                            if (CommonAppConfig.LIVE_SDK_CHANGED) {
                                mLiveSdk = obj.getIntValue("live_sdk");
                            } else {
                                mLiveSdk = CommonAppConfig.LIVE_SDK_USED;
                            }
                            switch (mLiveType) {
                                case Constants.LIVE_TYPE_NORMAL:
                                    forwardNormalRoom();
                                    break;
                                case Constants.LIVE_TYPE_PWD:
                                    forwardPwdRoom();
                                    break;
                                case Constants.LIVE_TYPE_PAY:
                                case Constants.LIVE_TYPE_TIME:
                                    forwardPayRoom();
                                    break;
                            }
                        }
                    } else {
                        ToastUtil.show(msg);
                    }
                }

                @Override
                public boolean showLoadingDialog() {
                    return true;
                }

                @Override
                public Dialog createLoadingDialog() {
                    return DialogUtil.loadingDialog(mContext);
                }
            };
        }
        LiveHttpUtil.checkLive(bean.getUid(), bean.getStream(), mCheckLiveCallback);
    }


    /**
     * 观众 观看直播
     */
    public void watchLive(List<LiveBean> listList, LiveBean bean, String key, int position) {
        mLiveBean = bean;
        mKey = key;
        mPosition = position;
        mListList = listList;
        if (mCheckLiveCallback == null) {
            mCheckLiveCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        if (info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            mLiveType = obj.getIntValue("type");
                            mLiveTypeVal = obj.getIntValue("type_val");
                            mLiveTypeMsg = obj.getString("type_msg");
                            if (CommonAppConfig.LIVE_SDK_CHANGED) {
                                mLiveSdk = obj.getIntValue("live_sdk");
                            } else {
                                mLiveSdk = CommonAppConfig.LIVE_SDK_USED;
                            }
                            switch (mLiveType) {
                                case Constants.LIVE_TYPE_NORMAL:
//                                    forwardNormalRoom();
                                    forwardNormalRoom(mListList);
                                    break;
                                case Constants.LIVE_TYPE_PWD:
                                    forwardPwdRoom(mListList);
                                    break;
                                case Constants.LIVE_TYPE_PAY:
                                case Constants.LIVE_TYPE_TIME:
                                    forwardPayRoom(mListList);
                                    break;
                            }
                        }
                    } else {
                        ToastUtil.show(msg);
                    }
                }

                @Override
                public boolean showLoadingDialog() {
                    return true;
                }

                @Override
                public Dialog createLoadingDialog() {
                    return DialogUtil.loadingDialog(mContext);
                }
            };
        }
        LiveHttpUtil.checkLive(bean.getUid(), bean.getStream(), mCheckLiveCallback);
    }


    /**
     * 前往普通房间
     */
    private void forwardNormalRoom() {
        forwardLiveAudienceActivity();
    }

    /**
     * 前往普通房间
     */
    private void forwardNormalRoom(List<LiveBean> listList) {
        forwardLiveAudienceActivity(listList);
    }

    /**
     * 前往密码房间
     */
    private void forwardPwdRoom() {
        forwardPwdRoom(null);
    }

    /**
     * 前往密码房间
     */
    private void forwardPwdRoom(final List<LiveBean> listList) {
        DialogUtil.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_input_password), DialogUtil.INPUT_TYPE_NUMBER_PASSWORD, new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(WordUtil.getString(R.string.live_input_password));
                    return;
                }
                String password = MD5Util.getMD5(content);
                if (mLiveTypeMsg.equalsIgnoreCase(password)) {
                    dialog.dismiss();
                    if (listList == null) {
                        forwardLiveAudienceActivity();
                    } else {
                        forwardLiveAudienceActivity(listList);
                    }
                } else {
                    ToastUtil.show(WordUtil.getString(R.string.live_password_error));
                }
            }
        });
    }

    /**
     * 前往付费房间
     */
    private void forwardPayRoom() {
        forwardPayRoom(null);
    }

    /**
     * 前往付费房间
     */
    private void forwardPayRoom(final List<LiveBean> listList) {
        DialogUtil.showSimpleDialog(mContext, mLiveTypeMsg, new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                roomCharge(listList);
            }
        });
    }

    public void roomCharge(final List<LiveBean> listList) {
        LiveHttpUtil.roomCharge(mLiveBean.getUid(), mLiveBean.getStream(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (listList == null) {
                        forwardLiveAudienceActivity();
                    } else {
                        forwardLiveAudienceActivity(listList);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUtil.loadingDialog(mContext);
            }
        });
    }

    public void cancel() {
        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
    }

    /**
     * 跳转到直播间
     */
    private void forwardLiveAudienceActivity() {
        LiveAudienceActivity.forward(mContext, mLiveBean, mLiveType, mLiveTypeVal, mKey, mPosition, mLiveSdk);
    }

    /**
     * 跳转到直播间
     */
    private void forwardLiveAudienceActivity(List<LiveBean> listList) {
        LiveAudienceActivity.forward(mContext, mLiveBean, mLiveType, mLiveTypeVal, mKey, mPosition, mLiveSdk, listList);
    }
}
