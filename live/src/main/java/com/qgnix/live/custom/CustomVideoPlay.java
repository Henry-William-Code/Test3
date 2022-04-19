package com.qgnix.live.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qgnix.live.R;
import com.qgnix.live.event.VideoActionEvent;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge;

import org.greenrobot.eventbus.EventBus;

/**
 * 视频播放组件
 */

public class CustomVideoPlay extends StandardGSYVideoPlayer {

    public static boolean isFirst = true;
    private ImageView tvPause;

    public CustomVideoPlay(Context context) {
        super(context);
    }

    public CustomVideoPlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private static final String TAG = "CustomVideoPlay";

    @Override
    public int getLayoutId() {
        return R.layout.sample_video;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        ImageView imgStart = (ImageView)findViewById(R.id.tv_start);
        imgStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GSYVideoViewBridge gsyVideoManager = getGSYVideoManager();
                boolean playing = gsyVideoManager.isPlaying();
                if (playing) {
                    imgStart.setImageResource(R.mipmap.zanting);
                } else {
                    imgStart.setImageResource(R.mipmap.bofang);
                }
                clickStartIcon();

            }
        });

    }

    @Override
    protected void onClickUiToggle() {
        super.onClickUiToggle();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: ");
        return super.onTouchEvent(event);
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG, "onTouch: ");
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouch: ACTION_DOWN");
                EventBus.getDefault().post(new VideoActionEvent(action));
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouch: ACTION_UP");
                EventBus.getDefault().post(new VideoActionEvent(action));
                break;
        }
        return super.onTouch(v, event);
    }



    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        if (isFirst) {
            setViewShowState(mStartButton,GONE);
            isFirst = false;
        }
    }



    @Override
    public void onCompletion() {
        super.onCompletion();
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        //视频播放完毕
        EventBus.getDefault().post(new VideoActionEvent(true));
    }

    /**
     * 需要在尺寸发生变化的时候重新处理
     */
    @Override
    public void onSurfaceSizeChanged(Surface surface, int width, int height) {
        super.onSurfaceSizeChanged(surface, width, height);
    }

    @Override
    public void onSurfaceAvailable(Surface surface) {
        super.onSurfaceAvailable(surface);
    }







}
