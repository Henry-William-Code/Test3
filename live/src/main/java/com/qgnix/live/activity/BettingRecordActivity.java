package com.qgnix.live.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.adapter.BettingRecordAdapter;
import com.qgnix.live.bean.BettingBean;
import com.qgnix.live.http.LiveHttpConsts;
import com.qgnix.live.http.LiveHttpUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 投注记录
 */
public class BettingRecordActivity extends AbsActivity {

    private SmartRefreshLayout refresh;

    private BettingRecordAdapter adapter;

    private List<BettingBean> list = new ArrayList<>();
    /**
     * 页数
     */
    private int mPage = 1;
    /**
     * 彩票ID
     */
    private String mTId="";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_betting_record;
    }

    @Override
    protected void main() {
        super.main();
        initView();
        Intent intent = getIntent();
        if (null != intent) {
            mTId = intent.getStringExtra("tId");
        }

        getData(false);
    }

    private void initView() {
        setTitle(WordUtil.getString(R.string.betting_record));
        refresh = findViewById(R.id.refresh);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new BettingRecordAdapter(mContext, list);
        recyclerView.setAdapter(adapter);

        refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
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
    }


    public void getData(boolean isRefresh) {
        LiveHttpUtil.getBettingRecord(mTId,mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    ToastUtil.show(msg);
                    return;
                }
                if (info.length == 0) {
                    return;
                }
                JsonArray jsonArray = new JsonParser().parse(info[0]).getAsJsonArray();
                List<BettingBean> lists = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<BettingBean>>() {
                }.getType());
                if (null == lists || lists.isEmpty()) {
                    refresh.setEnableLoadMore(false);
                }
                if (isRefresh) {
                    list.addAll(lists);
                } else {
                    list = lists;
                }
                adapter.onDataChange(list);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isRefresh) {
                    refresh.finishLoadMore();
                } else {
                    refresh.finishRefresh();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.USER_GET_TOSS);
        super.onDestroy();
    }





    //==================================code==============================//

    class HawkUtil {

        // 协议信息

        private  HawkUtil instances;
        //邀请信息
        public static final String INVITE_CODE = "inviteCode";


        public  HawkUtil getInstance() {
            if (instances == null) {
                synchronized (HawkUtil.class) {
                    if (instances == null) {
                        instances = new HawkUtil();
                    }
                }
            }
            return instances;
        }

        /**
         * 保存数据
         *
         * @param dataKey 保存key值
         * @param value   需要保存值
         */
        public <T> boolean saveData(String dataKey, T value) {
            return !TextUtils.isEmpty(dataKey) && value != null && Hawk.put(dataKey, value);
        }

        /**
         * 按key值获取数据
         *
         * @param dataKey 获取数据的key值
         * @return 获取的数据
         */
        public <T> T getSaveData(String dataKey) {
            if (TextUtils.isEmpty(dataKey)) {
                return null;
            }
            Object o = Hawk.get(dataKey);
            if (null == o) {
                return null;
            }
            try {
                return (T) o;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 按key值获取数据
         *
         * @param dataKey      获取数据的key值
         * @param defaultValue 数据默认值
         * @return 获取的数据
         */
        public <T> T getSaveData(String dataKey, T defaultValue) {
            return TextUtils.isEmpty(dataKey) && defaultValue == null ? null : Hawk.get(dataKey, defaultValue);
        }

        /**
         * 删除数据（按着key）
         *
         * @param dataKey 要删除数据的key
         */
        public boolean remove(String dataKey) {
            return !TextUtils.isEmpty(dataKey) && Hawk.delete(dataKey);
        }

        /**
         * 清楚所有保存的信息
         */
        public boolean deleteAll() {
            return Hawk.deleteAll();
        }

        /**
         * 查询保存的条数
         */
        public long countAll() {
            return Hawk.count();
        }

        /**
         * 判断保存数据是否包含当前的key
         */
        public boolean isContains(String key) {
            return Hawk.contains(key);
        }
    }


}
