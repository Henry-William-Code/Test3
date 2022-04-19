package com.qgnix.main.activity;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.custom.ItemDecoration;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.http.LiveHttpConsts;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.adapter.MsgTypeListAdapter;
import com.qgnix.main.bean.MsgTypeBean;
import com.qgnix.main.bean.MsgUnReadNumBean;
import com.qgnix.main.bean.UpdateMsgUnReadEvent;
import com.qgnix.main.http.MainHttpUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息列表
 */
public class MsgTypeListActivity extends AbsActivity {
    private final int REQUEST_CODE = 0;

    private SmartRefreshLayout mRefresh;
    private MsgTypeListAdapter mAdapter;
    private List<MsgTypeBean> mList = new ArrayList<>();
    private int mUnReadCount = 0;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_msg_list;
    }

    @Override
    protected void main() {
        super.main();
        initView();
        getData();
    }

    private void initView() {
        setTitle(WordUtil.getString(R.string.msg_list_title));
        mRefresh = findViewById(com.qgnix.live.R.id.refresh);
        mRefresh.setEnableLoadMore(false);
        mRefresh.setEnableRefresh(false);
        RecyclerView recyclerView = findViewById(com.qgnix.live.R.id.recyclerView);
        recyclerView.addItemDecoration(new ItemDecoration(mContext));
        mAdapter = new MsgTypeListAdapter(mContext, mList);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                MsgTypeBean bean = mList.get(position);
                Intent intent = new Intent(mContext, MsgDetailListActivity.class);
                intent.putExtra("type", bean.getT());
                intent.putExtra("title", bean.getN());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    public void getData() {
        MainHttpUtil.getCountUnread(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                mUnReadCount = 0;
                mList = concatListData(info);
                EventBus.getDefault().post(new UpdateMsgUnReadEvent(mUnReadCount));
                mAdapter.onDataChange(mList);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    /**
     * 拼接数据（未读数量和图标）
     * @param info
     * @return
     */
    private List<MsgTypeBean> concatListData(String[] info) {
        List<MsgUnReadNumBean> lists0 = JSON.parseArray(info[0], MsgUnReadNumBean.class);
        List<MsgTypeBean> lists = JSON.parseArray(info[1], MsgTypeBean.class);
        for (int i = 0; i < lists0.size(); i++) {
            MsgUnReadNumBean msgUnReadNumBean = lists0.get(i);
            mUnReadCount += msgUnReadNumBean.getC();
            for (int j = 0; j < lists.size(); j++) {
                if (lists.get(j).getT() == msgUnReadNumBean.getT()) {
                    lists.get(j).setUnReadCount(msgUnReadNumBean.getC());
                    break;
                }
            }
        }
        for (int j = 0; j < lists.size(); j++) {
            switch (lists.get(j).getT()){
                case 1:
                    lists.get(j).setIcon(R.mipmap.icon_customer);
                    break;
                case 2:
                    lists.get(j).setIcon(R.mipmap.icon_recharge);
                    break;
                case 3:
                    lists.get(j).setIcon(R.mipmap.icon_reward);
                    break;
            }
        }
        return lists;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                getData();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.OPEN_LOTTERY_API);
        super.onDestroy();
    }
}