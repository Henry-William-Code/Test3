package com.qgnix.main.fragment;

import android.content.Context;
import android.graphics.Outline;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.fragment.BaseFragment;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.main.R;
import com.qgnix.main.bean.BannerBean;
import com.qgnix.main.common.BannerJumpHelper;
import com.qgnix.main.http.MainHttpUtil;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.List;

public class SportProgressFragment extends BaseFragment {

    //---------------------------------------- 视图控件相关开始位置 ----------------------------------

    /**
     * Banner滑动栏控件
     */

    private Banner fso_banner_progress;



    //---------------------------------------- 视图控件相关结束位置 ----------------------------------
    //---------------------------------------- 其它变量相关开始位置 ----------------------------------

    /**
     * 轮播数据
     */
    private List<BannerBean> mBannerBeans;

    //---------------------------------------- 其它变量相关结束位置 ----------------------------------


    public static SportProgressFragment newInstance() {
        return new SportProgressFragment();
    }



    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_sport_progress;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        fso_banner_progress = view.findViewById(R.id.fso_banner_progress);

    }

    @Override
    public void doBusiness() {



        // 轮播图
        fso_banner_progress.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                ImgLoader.display(mContext, ((BannerBean) path).getSlide_pic(), imageView);
            }
        });

        fso_banner_progress.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
//                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 15);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 0);
            }
        });

        fso_banner_progress.setClipToOutline(true);
        fso_banner_progress.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int p) {
                BannerBean bean = mBannerBeans.get(p);
                new BannerJumpHelper().jump(mContext, bean);
            }
        });



        // 轮播图
        MainHttpUtil.getBannerInfo(new CommonCallback<String>() {
            @Override
            public void callback(String info) {
                mBannerBeans = JSON.parseArray(info, BannerBean.class);
                fso_banner_progress.setImages(mBannerBeans);
                fso_banner_progress.start();
            }
        });
    }
}
