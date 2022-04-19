package com.faceunity.interfaces;

/**
 * Created by cxf on 2018/10/8.
 * 萌颜美颜回调
 */

public interface TiBeautyEffectListener extends BeautyEffectListener {


    void onMeiBaiChanged(int progress);

    void onMoPiChanged(int progress);

    void onBaoHeChanged(int progress);

    void onFengNenChanged(int progress);

    void onBigEyeChanged(int progress);

    void onFaceChanged(int progress);

    void onTieZhiChanged(String tieZhiName);

}
