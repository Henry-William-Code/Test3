package com.qgnix.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.hjq.language.MultiLanguages;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.utils.DynamicTimeFormatUtil;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.LanSwitchUtil;
import com.qgnix.common.views.CustomTopBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.jaaksi.pickerview.picker.BasePicker;
import org.jaaksi.pickerview.util.Util;
import org.jaaksi.pickerview.widget.DefaultCenterDecoration;
import org.jaaksi.pickerview.widget.PickerView;

import java.util.HashMap;


/**
 * Created by cxf on 2017/8/3.
 */

public class CommonAppContext extends MultiDexApplication {

    public static CommonAppContext sInstance;
    private int mCount;
    private boolean mFront;//是否前台

    public static HashMap<String,Object> map = new HashMap<>(); //放一些全局对象, 避免从服务端不必要的数据获取

    @Override
    public void onCreate() {
        super.onCreate();
        // 在 Application 中初始化
        LanSwitchUtil.init(this);
        sInstance = this;
        //初始化Http
        CommonHttpUtil.init();

        registerActivityLifecycleCallbacks();


        //初始化下拉上拉加载
        initSmartRefreshLayout();


        //初始化时间日期样式
        initTimeStyle(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        // 国际化适配（绑定语种）
        super.attachBaseContext(MultiLanguages.attach(base));
    }

    private void registerActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mCount++;
                if (!mFront) {
                    mFront = true;
                    L.e("AppContext------->处于前台");
                    CommonAppConfig.getInstance().setFrontGround(true);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mCount--;
                if (mCount == 0) {
                    mFront = false;
                    L.e("AppContext------->处于后台");
                    CommonAppConfig.getInstance().setFrontGround(false);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }


    /**
     * 默认配置下拉刷新上拉加载视图控件
     */
    private void initSmartRefreshLayout() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) ->{
            layout.setPrimaryColorsId(R.color.main_background_color, android.R.color.black);//全局设置主题颜色
            return new ClassicsHeader(context).setTimeFormat(new DynamicTimeFormatUtil("更新于 %s"));//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }


    /**
     * 自定义时间样式
     */
    private void initTimeStyle(Application application) {
        // 利用修改静态默认属性值，快速定制一套满足自己app样式需求的Picker.
        // BasePickerView
        PickerView.sDefaultVisibleItemCount = 5;
        PickerView.sDefaultItemSize = 50;
        PickerView.sDefaultIsCirculation = false;
        //PickerView.sDefaultDrawIndicator = false;

        // PickerView
        PickerView.sOutTextSize = 18;
        PickerView.sCenterTextSize = 18;
        PickerView.sCenterColor = Color.BLACK;
        PickerView.sOutColor = Color.GRAY;
        //PickerView.sShadowColors = null;

        // BasePicker
        int padding = Util.dip2px(application, 20);
        BasePicker.sDefaultPaddingRect = new Rect(padding, padding, padding, padding);
        BasePicker.sDefaultPickerBackgroundColor = Color.WHITE;
        BasePicker.sDefaultCanceledOnTouchOutside = false;
        // 自定义 TopBar
        BasePicker.sDefaultTopBarCreator = parent -> new CustomTopBar(parent);

        // DefaultCenterDecoration
        DefaultCenterDecoration.sDefaultLineWidth = 1;
        DefaultCenterDecoration.sDefaultLineColor = Color.parseColor("#ddf0ff");
        //DefaultCenterDecoration.sDefaultDrawable = new ColorDrawable(Color.WHITE);
        int leftMargin = Util.dip2px(application, 10);
        int topMargin = Util.dip2px(application, 2);
        DefaultCenterDecoration.sDefaultMarginRect =
                new Rect(leftMargin, -topMargin, leftMargin, -topMargin);
    }

}
