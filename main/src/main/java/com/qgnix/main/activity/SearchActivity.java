package com.qgnix.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.adapter.RefreshAdapter;
import com.qgnix.common.custom.CommonRefreshView;
import com.qgnix.common.event.FollowEvent;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.OnItemClickListener;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.bean.LiveBean;
import com.qgnix.live.bean.SearchUserBean;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.adapter.LiveAdapter;
import com.qgnix.main.adapter.SearchAdapter;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;
import com.qgnix.main.presenter.CheckLivePresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2018/10/25.
 * 首页直播搜索
 */

public class SearchActivity extends AbsActivity implements OnItemClickListener<SearchUserBean> {

    private EditText mEditText;

    private InputMethodManager imm;
    private String mKey;
    private MyHandler mHandler;
    /**
     * 刷新view
     */
    private SmartRefreshLayout mRefresh;
    /**
     * rv
     */
    private RecyclerView mRecyclerView;

    /**
     * 页数
     */
    private int mPage = 1;
    private long searchTime = 0;
    /**
     * 直播数据适配器
     */
    private LiveAdapter mLiveAdapter;
    /**
     * 直播数据
     */
    private final List<LiveBean> mLiveBeans = new ArrayList<>();

    public static void forward(Context context,int selectIndex) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("selectIndex",selectIndex);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }
    /**
     * item 是否已点击
     */
    private boolean misItemClick;
    @Override
    protected void main() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mEditText = findViewById(R.id.edit);
        mLiveAdapter = new LiveAdapter(mContext, mLiveBeans);
        mRefresh = findViewById(R.id.refresh);
        mRecyclerView = findViewById(R.id.recyclerView);

        mLiveAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                if (misItemClick) {
                    return;
                }
                misItemClick = true;
                // 观看直播
//                ((MainActivity) mContext).watchLive(mLiveBeans.get(position), Constants.LIVE_HOME, position);
                //((MainActivity) mContext).watchLive(mLiveBeans, mLiveBeans.get(position), Constants.LIVE_HOME, position);

                CheckLivePresenter checkLivePresenter = new CheckLivePresenter(mContext);
                checkLivePresenter.watchLive(mLiveBeans, mLiveBeans.get(position), Constants.LIVE_HOME, position);
                misItemClick = false;
            }
        });
        mRecyclerView.setAdapter(mLiveAdapter);

        mRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                ++mPage;
                getData(true);
            }


            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mPage = 1;
                getData(false);
            }
        });
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainHttpUtil.cancel(MainHttpConsts.SEARCH);
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (!TextUtils.isEmpty(s)) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    }
                } else {
                    mKey = null;
                    mLiveBeans.clear();
                    mLiveAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mHandler = new MyHandler(this);
        EventBus.getDefault().register(this);
    }





    /**
     * @param isLoadMore 是否加载更多
     */
    private void getData(final boolean isLoadMore) {
        if(mPage<=1 || searchTime==0) {
            searchTime = System.currentTimeMillis() / 1000;
        }
        if (!isLoadMore) {
            mPage = 1;
        }
        String liveclassid = getIntent().getIntExtra("selectIndex",1)+"";
        Map<String, String> map = new HashMap<>();
        map.put("p", String.valueOf(mPage));
        map.put("pnum", "20");
        map.put("key", mKey);
        map.put("liveclassid",liveclassid);
        map.put("uid", CommonAppConfig.getInstance().getUid());
        map.put("token", CommonAppConfig.getInstance().getToken());
        map.put("isAdult", String.valueOf(CommonAppConfig.ADULT_MODE));
        map.put("searchTime",""+searchTime);
        MainHttpUtil.getClassLive(map, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                List<LiveBean> lists = JSON.parseArray(Arrays.toString(info), LiveBean.class);

                if (!isLoadMore) {
                    mLiveBeans.clear();
                }
                if (!lists.isEmpty()){
                    for (LiveBean bean : lists) {
                        if (mLiveBeans.contains(bean)){
                            continue;
                        }

                        if(CommonAppConfig.getInstance().getSwitchStatu()) {
                            if(2==bean.getIsvideo()||1==bean.getIsvideo()){
                                mLiveBeans.add(bean);
                            }
                        }else{
                            mLiveBeans.add(bean);
                        }
                    }
                }
                mLiveAdapter.onDataChange(mLiveBeans);
            }


            @Override
            public void onFinish() {
                super.onFinish();
                if (isLoadMore) {
                    mRefresh.finishLoadMore();
                } else {
                    mRefresh.finishRefresh();
                }
            }
        });
    }


    private void search() {
        String key = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(key)) {
            ToastUtil.show(R.string.content_empty);
            return;
        }
        MainHttpUtil.cancel(MainHttpConsts.SEARCH);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mKey = key;
        getData(true);
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        MainHttpUtil.cancel(MainHttpConsts.SEARCH);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.release();
        }
        mHandler = null;
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {

    }

    @Override
    public void onItemClick(SearchUserBean bean, int position) {
        if (imm != null && mEditText != null) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
    }


    private static class MyHandler extends Handler {

        private SearchActivity mActivity;

        public MyHandler(SearchActivity activity) {
            mActivity = new WeakReference<>(activity).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity != null) {
                mActivity.search();
            }
        }

        public void release() {
            mActivity = null;
        }
    }


}
