package com.qgnix.live.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.Constants;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.ActivityResultCallback;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.interfaces.ImageResultCallback;
import com.qgnix.common.utils.ClickUtil;
import com.qgnix.common.utils.DialogUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ProcessImageUtil;
import com.qgnix.common.utils.StringUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.common.views.AbsViewHolder;
import com.qgnix.live.R;
import com.qgnix.live.activity.LiveActivity;
import com.qgnix.live.activity.LiveAnchorActivity;
import com.qgnix.live.activity.TicketClassActivity;
import com.qgnix.live.bean.LiveRoomTypeBean;
import com.qgnix.live.dialog.LiveRoomTypeDialogFragment;
import com.qgnix.live.dialog.LiveTimeDialogFragment;
import com.qgnix.live.http.LiveHttpConsts;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.live.lottery.entry.TicketData;

import java.io.File;

/**
 * Created by cxf on 2018/10/7.
 * 开播前准备
 */

public class LiveReadyViewHolder extends AbsViewHolder implements View.OnClickListener {

    private ImageView mAvatar;
    private TextView mCoverText;
    private EditText mEditTitle;
    private RecyclerView mLiveShareRecyclerView;
    private ProcessImageUtil mImageUtil;
    private File mAvatarFile;
    private TextView mLiveClass;
    private TextView mLiveTypeTextView;//房间类型TextView
    private TextView btn_live_class;//直播类型
    private int mLiveClassID;//直播频道id
    private int mLiveType;//房间类型
    private int mLiveTypeVal;//房间密码，门票收费金额
    private int mLiveTimeCoin;//计时收费金额
    private ActivityResultCallback mActivityResultCallback;
    private CommonCallback<LiveRoomTypeBean> mLiveTypeCallback;
    /**
     * 彩票信息
     */
    private TicketData mTicketData;
    /**
     * 开启直播
     */
    private Button mBtnStartLive;

    public LiveReadyViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_ready;
    }

    @Override
    public void init() {
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mCoverText = (TextView) findViewById(R.id.cover_text);
        mEditTitle = (EditText) findViewById(R.id.edit_title);
        mLiveClass = (TextView) findViewById(R.id.live_class);
        mLiveTypeTextView = (TextView) findViewById(R.id.btn_room_type);
        mLiveShareRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLiveShareRecyclerView.setHasFixedSize(true);
        mLiveShareRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mImageUtil = ((LiveActivity) mContext).getProcessImageUtil();
        mImageUtil.setImageResultCallback(new ImageResultCallback() {

            @Override
            public void beforeCamera() {
                ((LiveAnchorActivity) mContext).beforeCamera();
            }

            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(mContext, file, mAvatar);
                    if (mAvatarFile == null) {
                        mCoverText.setText(WordUtil.getString(R.string.live_cover_2));
                        mCoverText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_live_cover));
                    }
                    mAvatarFile = file;
                }
            }

            @Override
            public void onFailure() {
            }
        });
        mLiveClass.setOnClickListener(this);
        findViewById(R.id.avatar_group).setOnClickListener(this);
        findViewById(R.id.btn_camera).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_beauty).setOnClickListener(this);
        mBtnStartLive = (Button) findViewById(R.id.btn_start_live);
        mBtnStartLive.setOnClickListener(this);
        mLiveTypeTextView.setOnClickListener(this);
        mActivityResultCallback = new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                mTicketData = intent.getParcelableExtra(Constants.TICKET_DATA);
                mLiveClass.setText(mTicketData.getTitle());
            }
        };
        mLiveTypeCallback = new CommonCallback<LiveRoomTypeBean>() {
            @Override
            public void callback(LiveRoomTypeBean bean) {
                switch (bean.getId()) {
                    case Constants.LIVE_TYPE_NORMAL:
                        onLiveTypeNormal(bean);
                        break;
                    case Constants.LIVE_TYPE_PWD:
                        onLiveTypePwd(bean);
                        break;
                    case Constants.LIVE_TYPE_PAY:
                        onLiveTypePay(bean);
                        break;
                    case Constants.LIVE_TYPE_TIME:
                        onLiveTypeTime(bean);
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        //直播功能比较耗时
        if (!ClickUtil.canClick(2000)) {
            return;
        }
        if (i == R.id.avatar_group) {
            setAvatar();

        } else if (i == R.id.btn_camera) {
            toggleCamera();
        } else if (i == R.id.btn_close) {
            close();

        } else if (i == R.id.live_class) {
            chooseLiveClass();

        } else if (i == R.id.btn_beauty) {
            beauty();
        } else if (i == R.id.btn_room_type) {
            chooseLiveType();

        } else if (i == R.id.btn_start_live) {
            mBtnStartLive.setClickable(false);
            startLive();
        }
    }

    /**
     * 设置头像
     */
    private void setAvatar() {
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

    /**
     * 切换镜头
     */
    private void toggleCamera() {
        ((LiveAnchorActivity) mContext).toggleCamera();
    }

    /**
     * 关闭
     */
    private void close() {
        ((LiveAnchorActivity) mContext).onBackPressed();
    }

    /**
     * 选择彩票
     */
    private void chooseLiveClass() {
        Intent intent = new Intent(mContext, TicketClassActivity.class);
        mImageUtil.startActivityForResult(intent, mActivityResultCallback);
    }

    /**
     * 设置美颜
     */
    private void beauty() {
        ((LiveAnchorActivity) mContext).beauty();
    }

    /**
     * 选择直播类型
     */
    private void chooseLiveType() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CHECKED_ID, mLiveType);
        LiveRoomTypeDialogFragment fragment = new LiveRoomTypeDialogFragment();
        fragment.setArguments(bundle);
        fragment.setCallback(mLiveTypeCallback);
        fragment.show(((LiveAnchorActivity) mContext).getSupportFragmentManager(), "LiveRoomTypeDialogFragment");
    }

    /**
     * 普通房间
     */
    private void onLiveTypeNormal(LiveRoomTypeBean bean) {
        mLiveType = bean.getId();
        mLiveTypeTextView.setText(bean.getName());
        mLiveTypeVal = 0;
        mLiveTimeCoin = 0;
    }

    /**
     * 密码房间
     */
    private void onLiveTypePwd(final LiveRoomTypeBean bean) {
        DialogUtil.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_pwd), DialogUtil.INPUT_TYPE_NUMBER_PASSWORD, 8, new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_pwd_empty);
                } else {
                    mLiveType = bean.getId();
                    mLiveTypeTextView.setText(bean.getName());
                    if (StringUtil.isInt(content)) {
                        mLiveTypeVal = Integer.parseInt(content);
                    }
                    mLiveTimeCoin = 0;
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 付费房间
     */
    private void onLiveTypePay(final LiveRoomTypeBean bean) {
        DialogUtil.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_fee), DialogUtil.INPUT_TYPE_NUMBER, 8, new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_fee_empty);
                } else {
                    mLiveType = bean.getId();
                    mLiveTypeTextView.setText(bean.getName());
                    if (StringUtil.isInt(content)) {
                        mLiveTypeVal = Integer.parseInt(content);
                    }
                    mLiveTimeCoin = 0;
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 计时房间
     */
    private void onLiveTypeTime(final LiveRoomTypeBean bean) {
        LiveTimeDialogFragment fragment = new LiveTimeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CHECKED_COIN, mLiveTimeCoin);
        fragment.setArguments(bundle);
        fragment.setCommonCallback(new CommonCallback<Integer>() {
            @Override
            public void callback(Integer coin) {
                mLiveType = bean.getId();
                mLiveTypeTextView.setText(bean.getName());
                mLiveTypeVal = coin;
                mLiveTimeCoin = coin;
            }
        });
        fragment.show(((LiveAnchorActivity) mContext).getSupportFragmentManager(), "LiveTimeDialogFragment");
    }

    public void hide() {
        if (mContentView != null && mContentView.getVisibility() == View.VISIBLE) {
            mContentView.setVisibility(View.INVISIBLE);
        }
    }


    public void show() {
        if (mContentView != null && mContentView.getVisibility() != View.VISIBLE) {
            mContentView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击开始直播按钮
     */
    private void startLive() {
        boolean startPreview = ((LiveAnchorActivity) mContext).isStartPreview();
        if (!startPreview) {
            ToastUtil.show(R.string.please_wait);
            mBtnStartLive.setClickable(true);
            return;
        }
        if (null == mTicketData) {
            ToastUtil.show(R.string.please_choose_gamble);
            mBtnStartLive.setClickable(true);
            return;
        }
        createRoom();
    }

    /**
     * 请求创建直播间接口，开始直播
     */
    private void createRoom() {
        String title = mEditTitle.getText().toString().trim();
        LiveHttpUtil.createRoom(title, 0, mLiveType, mLiveTypeVal, mTicketData.getId(), mAvatarFile, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    L.e("开播", "createRoom------->" + info[0]);
                    ((LiveAnchorActivity) mContext).startLiveSuccess(info[0], mLiveType, mLiveTypeVal, mTicketData);
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onFinish() {
                mBtnStartLive.setClickable(true);
            }
        });
    }

    public void release() {
        mImageUtil = null;
        mActivityResultCallback = null;
        mLiveTypeCallback = null;
    }

    @Override
    public void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.CREATE_ROOM);
    }
}
