package com.qgnix.live.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.faceunity.FURenderer;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.utils.DpUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.R;
import com.qgnix.live.activity.LiveActivity;
import com.qgnix.live.http.LiveHttpConsts;
import com.qgnix.live.http.LiveHttpUtil;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by cxf on 2018/10/7.
 * 腾讯云直播推流
 */

public class LivePushTxViewHolder extends AbsLivePushViewHolder implements ITXLivePushListener {

    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;
    private String mBgmPath;//背景音乐路径

    private FURenderer mFURenderer;

    private boolean mOnFirstCreate = true;

    public LivePushTxViewHolder(Context context, ViewGroup parentView, FURenderer fuRenderer) {
        super(context, parentView);
        this.mFURenderer = fuRenderer;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_push_tx;
    }

    @Override
    public void init() {
        super.init();
        mLivePusher = new TXLivePusher(mContext);
        mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.setVideoFPS(15);//视频帧率
        mLivePushConfig.setVideoEncodeGop(1);//GOP大小
        mLivePushConfig.setVideoBitrate(1800);
        mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_720_1280);
        mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_HARDWARE);//硬件加速
        Bitmap bitmap = decodeResource(mContext.getResources(), R.mipmap.bg_live_tx_pause);
        mLivePushConfig.setPauseImg(bitmap);
        mLivePushConfig.setTouchFocus(false);//自动对焦
        mLivePushConfig.setANS(true);//噪声抑制
        mLivePushConfig.enableAEC(true);//开启回声消除：连麦时必须开启，非连麦时不要开启
        mLivePushConfig.setAudioSampleRate(48000);
        mLivePushConfig.setAudioChannels(1);//声道数量
        mLivePusher.setConfig(mLivePushConfig);
        mLivePusher.setMirror(true);
        mLivePusher.setPushListener(this);
        mLivePusher.setBGMVolume(1f);
        mLivePusher.setMicVolume(4f);
        mLivePusher.setBGMNofify(new TXLivePusher.OnBGMNotify() {
            @Override
            public void onBGMStart() {

            }

            @Override
            public void onBGMProgress(long l, long l1) {

            }

            @Override
            public void onBGMComplete(int i) {
                if (!TextUtils.isEmpty(mBgmPath) && mLivePusher != null) {
                    mLivePusher.playBGM(mBgmPath);
                }
            }
        });
        mLivePusher.setVideoProcessListener(new TXLivePusher.VideoCustomProcessListener() {
            /**
             * 在OpenGL线程中回调，在这里可以进行采集图像的二次处理
             * @param i  纹理ID
             * @param i1      纹理的宽度
             * @param i2     纹理的高度
             * @return 返回给SDK的纹理
             * 说明：SDK回调出来的纹理类型是GLES20.GL_TEXTURE_2D，接口返回给SDK的纹理类型也必须是GLES20.GL_TEXTURE_2D
             */
            @Override
            public int onTextureCustomProcess(int i, int i1, int i2) {
                if (mOnFirstCreate) {
                    Log.d(TAG, "onTextureCustomProcess: create");
                    mFURenderer.onSurfaceCreated();
                    mOnFirstCreate = false;
                }
                int texId = mFURenderer.onDrawFrameSingleInputTex(i, i1, i2);
                return texId;
            }

            /**
             * 增值版回调人脸坐标
             * @param floats   归一化人脸坐标，每两个值表示某点P的X,Y值。值域[0.f, 1.f]
             */
            @Override
            public void onDetectFacePoints(float[] floats) {

            }

            /**
             * 在OpenGL线程中回调，可以在这里释放创建的OpenGL资源
             */
            @Override
            public void onTextureDestoryed() {
                Log.d(TAG, "onTextureDestoryed: t:" + Thread.currentThread().getId());
                mFURenderer.onSurfaceDestroyed();
                mOnFirstCreate = true;
            }
        });
        mPreView = findViewById(R.id.camera_preview);
        mLivePusher.startCameraPreview((TXCloudVideoView) mPreView);
    }

    @Override
    public void changeToLeft() {
        if (mPreView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreView.getLayoutParams();
            params.width = mPreView.getWidth() / 2;
            params.height = DpUtil.dp2px(250);
            params.topMargin = DpUtil.dp2px(130);
            mPreView.setLayoutParams(params);
        }
    }

    @Override
    public void changeToBig() {
        if (mPreView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            mPreView.setLayoutParams(params);
        }
    }


    /**
     * 切换镜头
     */
    @Override
    public void toggleCamera() {
        if (mLivePusher != null) {
            if (mFlashOpen) {
                toggleFlash();
            }
            mLivePusher.switchCamera();
            mCameraFront = !mCameraFront;
            mLivePusher.setMirror(mCameraFront);
        }
    }

    /**
     * 打开关闭闪光灯
     */
    @Override
    public void toggleFlash() {
        if (mCameraFront) {
            ToastUtil.show(R.string.live_open_flash);
            return;
        }
        if (mLivePusher != null) {
            boolean open = !mFlashOpen;
            if (mLivePusher.turnOnFlashLight(open)) {
                mFlashOpen = open;
            }
        }
    }

    /**
     * 开始推流
     *
     * @param pushUrl 推流地址
     */
    @Override
    public void startPush(String pushUrl) {
        if (mLivePusher != null) {
            mLivePusher.startPusher(pushUrl);
        }
        startCountDown();
    }


    @Override
    public void onPause() {
        mPaused = true;
        if (mStartPush && mLivePusher != null) {
            mLivePusher.pauseBGM();
            mLivePusher.pausePusher();
        }
    }

    @Override
    public void onResume() {
        if (mPaused && mStartPush && mLivePusher != null) {
            mLivePusher.resumePusher();
            mLivePusher.resumeBGM();
        }
        mPaused = false;
    }

    @Override
    public void startBgm(String path) {
        if (mLivePusher != null) {
            boolean result = mLivePusher.playBGM(path);
            if (result) {
                mBgmPath = path;
            }
        }
    }

    @Override
    public void pauseBgm() {
        if (mLivePusher != null) {
            mLivePusher.pauseBGM();
        }
    }

    @Override
    public void resumeBgm() {
        if (mLivePusher != null) {
            mLivePusher.resumeBGM();
        }
    }

    @Override
    public void stopBgm() {
        if (mLivePusher != null) {
            mLivePusher.stopBGM();
        }
        mBgmPath = null;
    }

    @Override
    protected void onCameraRestart() {
        if (mLivePusher != null && mPreView != null) {
            mLivePusher.startCameraPreview((TXCloudVideoView) mPreView);
        }
    }

    @Override
    public void release() {
        super.release();
        LiveHttpUtil.cancel(LiveHttpConsts.LINK_MIC_TX_MIX_STREAM);
        if (mLivePusher != null) {
            mLivePusher.stopBGM();
            mLivePusher.stopPusher();
            mLivePusher.stopScreenCapture();
            mLivePusher.stopCameraPreview(false);
            mLivePusher.setVideoProcessListener(null);
            mLivePusher.setBGMNofify(null);
            mLivePusher.setPushListener(null);
        }
        mLivePusher = null;
        if (mLivePushConfig != null) {
            mLivePushConfig.setPauseImg(null);
        }
        mLivePushConfig = null;
    }

    @Override
    public void onPushEvent(int e, Bundle bundle) {
        if (e == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {
            ToastUtil.show(R.string.live_push_failed_1);

        } else if (e == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) {
            ToastUtil.show(R.string.live_push_failed_1);

        } else if (e == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || e == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS) {
            L.e(TAG, "网络断开，推流失败------>");

        } else if (e == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            L.e(TAG, "不支持硬件加速------>");
            if (mLivePushConfig != null && mLivePusher != null) {
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
                mLivePusher.setConfig(mLivePushConfig);
            }
        } else if (e == TXLiveConstants.PUSH_EVT_FIRST_FRAME_AVAILABLE) {//预览成功
            L.e(TAG, "mStearm--->初始化完毕");
            if (mLivePushListener != null) {
                mLivePushListener.onPreviewStart();
            }
        } else if (e == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {//推流成功
            L.e(TAG, "mStearm--->推流成功");
            if (!mStartPush) {
                mStartPush = true;
                if (mLivePushListener != null) {
                    mLivePushListener.onPushStart();
                }
            }
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    /**
     * 腾讯sdk连麦时候主播混流
     *
     * @param linkMicType 混流类型 1主播与主播连麦  0 用户与主播连麦
     * @param toStream    对方的stream
     */
    public void onLinkMicTxMixStreamEvent(int linkMicType, String toStream) {
        String txAppId = getTxAppId();
        String selfAnchorStream = getLiveStream();
        if (TextUtils.isEmpty(txAppId) || TextUtils.isEmpty(selfAnchorStream)) {
            return;
        }
        String mixParams = null;
        if (linkMicType == Constants.LINK_MIC_TYPE_NORMAL) {
            mixParams = createMixParams(txAppId, selfAnchorStream, toStream);
        } else if (linkMicType == Constants.LINK_MIC_TYPE_ANCHOR) {
            mixParams = createMixParams2(txAppId, selfAnchorStream, toStream);
        }
        if (TextUtils.isEmpty(mixParams)) {
            return;
        }
        LiveHttpUtil.linkMicTxMixStream(mixParams);
    }


    /**
     * 计算混流参数 观众与主播连麦
     *
     * @param txAppId          腾讯云appId
     * @param selfAnchorStream 自己主播的stream
     * @param toStream         对方的stream
     * @return
     */
    private String createMixParams(String txAppId, String selfAnchorStream, String toStream) {
        JSONObject obj = new JSONObject();
        long timestamp = System.currentTimeMillis() / 1000;
        obj.put("timestamp", timestamp);
        obj.put("eventId", timestamp);
        JSONObject interFace = new JSONObject();
        interFace.put("interfaceName", "Mix_StreamV2");
        JSONObject para = new JSONObject();
        para.put("app_id", txAppId);
        para.put("interface", "mix_streamv2.start_mix_stream_advanced");
        para.put("mix_stream_session_id", selfAnchorStream);
        para.put("output_stream_id", selfAnchorStream);
        JSONArray array = new JSONArray();
        JSONObject mainAnchor = new JSONObject();//大主播
        mainAnchor.put("input_stream_id", selfAnchorStream);
        JSONObject mainLayoutParams = new JSONObject();
        mainLayoutParams.put("image_layer", 1);
        mainAnchor.put("layout_params", mainLayoutParams);
        array.add(mainAnchor);

        if (!TextUtils.isEmpty(toStream)) {
            JSONObject smallAnchor = new JSONObject();//小主播
            smallAnchor.put("input_stream_id", toStream);
            JSONObject smallLayoutParams = new JSONObject();
            smallLayoutParams.put("image_layer", 2);
            smallLayoutParams.put("image_width", 0.25);
            smallLayoutParams.put("image_height", 0.21);
            smallLayoutParams.put("location_x", 0.75);
            smallLayoutParams.put("location_y", 0.6);
            smallAnchor.put("layout_params", smallLayoutParams);
            array.add(smallAnchor);
        }

        para.put("input_stream_list", array);
        interFace.put("para", para);
        obj.put("interface", interFace);
        return obj.toString();
    }


    /**
     * 计算混流参数 主播与主播连麦
     *
     * @param txAppId          腾讯云appId
     * @param selfAnchorStream 自己主播的stream
     * @param toStream         对方的stream
     * @return
     */
    private String createMixParams2(String txAppId, String selfAnchorStream, String toStream) {
        JSONObject obj = new JSONObject();
        long timestamp = System.currentTimeMillis() / 1000;
        obj.put("timestamp", timestamp);
        obj.put("eventId", timestamp);
        JSONObject interFace = new JSONObject();
        interFace.put("interfaceName", "Mix_StreamV2");
        JSONObject para = new JSONObject();
        para.put("app_id", txAppId);
        para.put("interface", "mix_streamv2.start_mix_stream_advanced");
        para.put("mix_stream_session_id", selfAnchorStream);
        para.put("output_stream_id", selfAnchorStream);
        JSONArray array = new JSONArray();


        if (!TextUtils.isEmpty(toStream)) {

            JSONObject bg = new JSONObject();//背景
            bg.put("input_stream_id", "canvas1");//背景的id,这个字符串随便写
            JSONObject bgLayoutParams = new JSONObject();
            bgLayoutParams.put("image_layer", 1);
            bgLayoutParams.put("input_type", 3);
            bg.put("layout_params", bgLayoutParams);
            array.add(bg);

            JSONObject selfAnchor = new JSONObject();//自己主播
            selfAnchor.put("input_stream_id", selfAnchorStream);
            JSONObject selfLayoutParams = new JSONObject();
            selfLayoutParams.put("image_layer", 2);
            selfLayoutParams.put("image_width", 0.5);
            selfLayoutParams.put("image_height", 0.5);
            selfLayoutParams.put("location_x", 0);
            selfLayoutParams.put("location_y", 0.25);
            selfAnchor.put("layout_params", selfLayoutParams);
            array.add(selfAnchor);

            JSONObject toAnchor = new JSONObject();//对方主播
            toAnchor.put("input_stream_id", toStream);
            JSONObject toLayoutParams = new JSONObject();
            toLayoutParams.put("image_layer", 3);
            toLayoutParams.put("image_width", 0.5);
            toLayoutParams.put("image_height", 0.5);
            toLayoutParams.put("location_x", 0.5);
            toLayoutParams.put("location_y", 0.25);
            toAnchor.put("layout_params", toLayoutParams);
            array.add(toAnchor);
        } else {
            JSONObject mainAnchor = new JSONObject();//大主播
            mainAnchor.put("input_stream_id", selfAnchorStream);
            JSONObject mainLayoutParams = new JSONObject();
            mainLayoutParams.put("image_layer", 1);
            mainAnchor.put("layout_params", mainLayoutParams);
            array.add(mainAnchor);
        }

        para.put("input_stream_list", array);
        interFace.put("para", para);
        obj.put("interface", interFace);
        return obj.toString();
    }

    private String getLiveStream() {
        return ((LiveActivity) mContext).getStream();
    }

    private String getTxAppId() {
        return ((LiveActivity) mContext).getTxAppId();
    }

}
