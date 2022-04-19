package com.qgnix.main.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.broadcom.bt.util.mime4j.field.datetime.DateTime;
import com.faceunity.utils.Constant;
import com.qgnix.common.fragment.BaseDateTimeSelectFragment;
import com.qgnix.common.fragment.BaseFragment;
import com.qgnix.common.utils.DateTimeSelectUtil;
import com.qgnix.common.utils.TimeUtil;
import com.qgnix.main.R;
import com.qgnix.main.adapter.FragmentAdapter;
import com.qgnix.main.constant.Constants;

import org.jaaksi.pickerview.picker.TimePicker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 运行计划fragment
 */
public class SportScheduleFragment extends BaseDateTimeSelectFragment {


    //---------------------------------------- 视图控件相关开始位置 ----------------------------------

    /**
     * ViewPager页面对象
     */
    ViewPager fss_vp_content;

    /**
     * 顶部订单导航栏
     */
    TabLayout fss_tl_tab;


    /**
     * 筛选
     */
    ImageView fss_iv_screening;

    /**
     * 今天日历
     */
    TextView fss_tv_today;


    /**
     * 标题
     */
    String[] party_applying_type;


    //---------------------------------------- 视图控件相关结束位置 ----------------------------------
    //---------------------------------------- 其它变量相关开始位置 ----------------------------------


    /**
     * fragment集合
     */
    private List<Fragment> mBaseFragment;


    //---------------------------------------- 其它变量相关结束位置 ----------------------------------


    public static SportScheduleFragment newInstance() {
        return new SportScheduleFragment();
    }

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_sport_schedule;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        fss_vp_content = view.findViewById(R.id.fss_vp_content);
        fss_tl_tab = view.findViewById(R.id.fss_tl_tab);
        fss_iv_screening = view.findViewById(R.id.fss_iv_screening);
        fss_tv_today = view.findViewById(R.id.fss_tv_today);

        party_applying_type = getResources().getStringArray(R.array.sport_schedule_game_tab);

    }

    @Override
    public void doBusiness() {



        initFragment();
        initEvent();

    }

    /**
     * 初始化fragment子页面
     */
    private void initFragment() {
        mBaseFragment = new ArrayList<>();
        mBaseFragment.add(SportScheduleGameFragment.newInstance(Constants.FragmentTypeKey.SportScheduleGameFragment.KEY_INDEX_TODAY));
        mBaseFragment.add(SportScheduleGameFragment.newInstance(Constants.FragmentTypeKey.SportScheduleGameFragment.KEY_INDEX_FOOTBALL));
        mBaseFragment.add(SportScheduleGameFragment.newInstance(Constants.FragmentTypeKey.SportScheduleGameFragment.KEY_INDEX_TENNIS));
        mBaseFragment.add(SportScheduleGameFragment.newInstance(Constants.FragmentTypeKey.SportScheduleGameFragment.KEY_INDEX_VOLLEYBALL));

        fss_vp_content.setOffscreenPageLimit(mBaseFragment.size());
        fss_vp_content.setAdapter(new FragmentAdapter(getFragmentManager(), mBaseFragment, party_applying_type));
        fss_tl_tab.setupWithViewPager(fss_vp_content);

    }


    /**
     * 初始化监听事件
     */
    private void initEvent() {


        //今天日历单击事件
        fss_tv_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "今天日历单击事件");
                new DateTimeSelectUtil(SportScheduleFragment.this).selectTime(mContext, 0, DateTimeSelectUtil.TIME_FORMAT_1);

            }
        });


        //筛选 单击事件
        fss_iv_screening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "筛选 单击事件");
            }
        });

    }


    @Override
    public void onTimeSelect(TimePicker picker, Date date) {
        Log.e(TAG, String.valueOf(date.getTime()));
        Log.e(TAG, TimeUtil.getFormatTime("yyyy-MM-dd", date.getTime()));
    }
}
