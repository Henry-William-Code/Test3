package com.qgnix.live.http;

import com.qgnix.common.http.HttpCallback;
import com.qgnix.live.music.LiveMusicBean;

/**
 * Created by cxf on 2018/10/20.
 */

public abstract class MusicUrlCallback extends HttpCallback {

    private LiveMusicBean mLiveMusicBean;

    public void setLiveMusicBean(LiveMusicBean bean) {
        mLiveMusicBean = bean;
    }

    public LiveMusicBean getLiveMusicBean() {
        return mLiveMusicBean;
    }
}
