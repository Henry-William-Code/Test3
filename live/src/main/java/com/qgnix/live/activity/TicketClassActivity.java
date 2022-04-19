package com.qgnix.live.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.Constants;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.cache.CacheData;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.adapter.TicketClassAdapter;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.TicketData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by cxf on 2018/10/7.
 * 选择彩种
 */

public class TicketClassActivity extends AbsActivity {

    private final List<TicketData> mData = new ArrayList<>();

    private TicketClassAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_choose_class;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.please_choose_gamble));
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new TicketClassAdapter(this, mData);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                Intent intent = new Intent();
                intent.putExtra(Constants.TICKET_DATA, mData.get(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        rv.setAdapter(mAdapter);

        getData();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getData() {
        String ticketData = CacheData.getTicketData();
        if (!TextUtils.isEmpty(ticketData)) {
            L.e("===本地获取数据===");
            List<TicketData> lists = JSON.parseArray(ticketData, TicketData.class);
            mData.clear();
            lists= lists.stream().filter(v -> !v.getType().equalsIgnoreCase("wr")).collect(Collectors.toList());
            mData.addAll(lists);
            mAdapter.notifyDataSetChanged();
            return;
        }
        CommonHttpUtil.getBCTicket(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                L.e("===网络获取数据===");
                // 保存数据到本地
                CacheData.setTicketData(info[0]);
                List<TicketData> lists = JSON.parseArray(info[0], TicketData.class);
                mData.clear();
                lists= lists.stream().filter(v -> !v.getType().equalsIgnoreCase("wr")).collect(Collectors.toList());
                mData.addAll(lists);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        CommonHttpUtil.cancel(CommonHttpConsts.BC_TICKET_GET_TICKET);
        super.onDestroy();
    }
}
