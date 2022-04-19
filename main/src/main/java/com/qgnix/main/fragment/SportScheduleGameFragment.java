package com.qgnix.main.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qgnix.common.fragment.BaseFragment;
import com.qgnix.main.R;
import com.qgnix.main.bean.ScheduleGameBean;
import com.qgnix.main.constant.Constants;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Sport --> Schedule --> game  fragment
 */
public class SportScheduleGameFragment extends BaseFragment {


    //---------------------------------------- 视图控件相关开始位置 ----------------------------------


    /**
     * 刷新布局
     */
    SmartRefreshLayout fssg_srfl_refresh_layout;

    /**
     * 数据列表
     */
    RecyclerView fssg_rv_game_list;


    //---------------------------------------- 视图控件相关结束位置 ----------------------------------
    //---------------------------------------- 其它变量相关开始位置 ----------------------------------

    /**
     * fragment类型
     */
    private int type;

    /**
     * 列表适配器
     */
    private BaseQuickAdapter adapter;


    /**
     * 数据源列表
     */
    private List<ScheduleGameBean> scheduleGameBeans = new ArrayList<>();


    //---------------------------------------- 其它变量相关结束位置 ----------------------------------


    public static SportScheduleGameFragment newInstance(int type) {
        SportScheduleGameFragment fragment = new SportScheduleGameFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LayoutParamsKey.KEY_FRAGMENT_TYPE_1, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initData(Bundle bundle) {
        type = getArguments().getInt(Constants.LayoutParamsKey.KEY_FRAGMENT_TYPE_1);
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_sport_schedule_game;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {

        fssg_srfl_refresh_layout = view.findViewById(R.id.fssg_srfl_refresh_layout);
        fssg_rv_game_list = view.findViewById(R.id.fssg_rv_game_list);
    }

    @Override
    public void doBusiness() {

        initAdapter();
        initEvent();
        getNetworkData(true);
    }


    /**
     * 初始化列表适配器
     */
    private void initAdapter() {


        scheduleGameBeans.add(new ScheduleGameBean());
        scheduleGameBeans.add(new ScheduleGameBean());
        scheduleGameBeans.add(new ScheduleGameBean());
        scheduleGameBeans.add(new ScheduleGameBean());
        scheduleGameBeans.add(new ScheduleGameBean());


        adapter = new BaseQuickAdapter<ScheduleGameBean, BaseViewHolder>(R.layout.adapter_schedule_game_list, scheduleGameBeans) {
            @Override
            protected void convert(@NonNull BaseViewHolder helper, ScheduleGameBean item) {
                ImageView asgl_iv_in_the_game = helper.getView(R.id.asgl_iv_in_the_game);
                Glide.with(mContext).load(R.drawable.ic_sport_schedule_game).into(asgl_iv_in_the_game);

                //Glide.with(mContext).load(R.drawable.ic_sport_schedule_game).listener(new RequestListener() {
                //    @Override
                //    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                //        return false;
                //    }
                //
                //    @Override
                //    public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                //        if (resource instanceof GifDrawable) {
                //            //加载一次
                //            ((GifDrawable)resource).setLoopCount(1);
                //        }
                //        return false;
                //    }
                //}).into(asgl_iv_in_the_game);
            }

        };
        fssg_rv_game_list.setLayoutManager(new LinearLayoutManager(mContext));
        fssg_rv_game_list.setAdapter(adapter);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {

        //重新加载
        //cve_tv_empty_button.setOnClickListener(v -> getNetworkData(true));

        //下拉刷新
        fssg_srfl_refresh_layout.setOnRefreshListener(refreshLayout -> getNetworkData(true));

        //上拉加载
        fssg_srfl_refresh_layout.setOnLoadMoreListener(refreshLayout -> getNetworkData(false));

        ////列表点击事件
        //adapter.setOnItemClickListener((adapter, view, position) -> {
        //    //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), adapter.getViewByPosition(wfpcl_rv_activity_list, position, R.id.winl_tv_title), getString(R.string.transition_animation_1));
        //    //startActivity(new Intent(mContext, NewsInfoDetailsActivity.class)
        //    //        .putExtra(Constants.LayoutParamsKey.KEY_INTENT_CODE_INT_KEY, type == Constants.FragmentTypeKey.fragmentTypePartyConstruction.KEY_TYPE_DEMOCRATIC_ORGANIZATIONS ?
        //    //                Constants.LayoutParamsKey.KEY_NEWS_TYPE_DEMOCRATIC_ORGANIZATIONS : Constants.LayoutParamsKey.KEY_NEWS_TYPE_DEMOCRATIC_LIFE)
        //    //        .putExtra(Constants.LayoutParamsKey.KEY_INTENT_CODE_OTHER_KEY, partyActivityInfoEntityList.get(position).getId())
        //    //        .putExtra(Constants.LayoutParamsKey.KEY_INTENT_CODE_OTHER_KEY_2, partyActivityInfoEntityList.get(position).getTitle()), options.toBundle());
        //});
    }

    /**
     * 初始化数据
     */
    private void getNetworkData(boolean isRefresh) {

        if (isRefresh) {
            fssg_srfl_refresh_layout.finishRefresh(3000);
        } else {
            fssg_srfl_refresh_layout.finishLoadMore(3000);
        }
    }

}
