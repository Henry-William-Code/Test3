package com.qgnix.live.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.faceunity.interfaces.BeautyEffectListener;
import com.qgnix.common.utils.L;
import com.qgnix.common.views.AbsViewHolder;
import com.qgnix.live.R;
import com.qgnix.live.interfaces.ILivePushViewHolder;
import com.qgnix.live.interfaces.LivePushListener;

/**
 * Created by cxf on 2018/12/22.
 */

public abstract class AbsLivePushViewHolder extends AbsViewHolder implements ILivePushViewHolder {

    protected final String TAG = getClass().getSimpleName();
    protected LivePushListener mLivePushListener;
    protected boolean mCameraFront;//是否是前置摄像头
    protected boolean mFlashOpen;//闪光灯是否开启了
    protected boolean mPaused;
    protected boolean mStartPush;
    protected ViewGroup mBigContainer;
    protected ViewGroup mSmallContainer;
    protected ViewGroup mLeftContainer;
    protected ViewGroup mRightContainer;
    protected ViewGroup mPkContainer;
    protected View mPreView;
    protected boolean mOpenCamera;//是否选择了相机
    protected BeautyEffectListener mEffectListener;//萌颜的效果监听

    //倒计时
    protected TextView mCountDownText;
    protected int mCountDownCount = 3;

    public AbsLivePushViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    public void init() {
        mBigContainer = (ViewGroup) findViewById(R.id.big_container);
        mSmallContainer = (ViewGroup) findViewById(R.id.small_container);
        mLeftContainer = (ViewGroup) findViewById(R.id.left_container);
        mRightContainer = (ViewGroup) findViewById(R.id.right_container);
        mPkContainer = (ViewGroup) findViewById(R.id.pk_container);
        mCameraFront = true;

    }

    /**
     * 开播的时候 3 2 1倒计时
     */
    protected void startCountDown() {
        ViewGroup parent = (ViewGroup) mContentView;
        mCountDownText = (TextView) LayoutInflater.from(mContext).inflate(R.layout.view_count_down, parent, false);
        parent.addView(mCountDownText);
        mCountDownText.setText(String.valueOf(mCountDownCount));
        ScaleAnimation animation = new ScaleAnimation(3, 1, 3, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setRepeatCount(2);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mCountDownText != null) {
                    ViewGroup parent = (ViewGroup) mCountDownText.getParent();
                    if (parent != null) {
                        parent.removeView(mCountDownText);
                        mCountDownText = null;
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (mCountDownText != null) {
                    mCountDownCount--;
                    mCountDownText.setText(String.valueOf(mCountDownCount));
                }
            }
        });
        mCountDownText.startAnimation(animation);
    }


    @Override
    public ViewGroup getSmallContainer() {
        return mSmallContainer;
    }


    @Override
    public ViewGroup getRightContainer() {
        return mRightContainer;
    }

    @Override
    public ViewGroup getPkContainer() {
        return mPkContainer;
    }


    @Override
    public void setOpenCamera(boolean openCamera) {
        mOpenCamera = openCamera;
    }

    @Override
    public void setLivePushListener(LivePushListener livePushListener) {
        mLivePushListener = livePushListener;
    }

    @Override
    public BeautyEffectListener getEffectListener() {
        return mEffectListener;
    }

    protected abstract void onCameraRestart();

    @Override
    public void release() {
        if (mCountDownText != null) {
            mCountDownText.clearAnimation();
        }
        mLivePushListener = null;
    }

    @Override
    public void onReStart() {
        if (mOpenCamera) {
            mOpenCamera = false;
            onCameraRestart();
        }
    }

    @Override
    public void onDestroy() {
        L.e(TAG, "LifeCycle------>onDestroy");
    }

}
