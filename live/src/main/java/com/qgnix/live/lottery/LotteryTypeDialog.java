package com.qgnix.live.lottery;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.bean.LoginBean;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.cache.CacheData;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.RouteUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.R;
import com.qgnix.live.adapter.TicketClassAdapter;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.TicketData;


import java.util.ArrayList;
import java.util.List;

/**
 * 彩票种类
 */
public class LotteryTypeDialog extends BaseCustomDialog {

    private final Context mContext;

    private final List<TicketData> mData = new ArrayList<>();
    private TicketClassAdapter mAdapter;

    public LotteryTypeDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_lottery_type;
    }

    @Override
    public void initView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        recyclerView.setLayoutManager(manager);
        mAdapter = new TicketClassAdapter(mContext, mData);
        mAdapter.setTvResColor(R.color.white);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                TicketData ticketData = mData.get(position);
                if (ticketData.getId().equals("0")) {
                    //游戏
                    loginGame(ticketData.getType(), ticketData.getType(), ticketData.getType());
                } else {
                    //彩票进入投注页
                    new BettingSelectDialog(mContext, ticketData).show();
                }
                dismiss();
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * 登录游戏
     */
    private void loginGame(String platType, String gameType, String gameCode) {
        CommonHttpUtil.ngLogin(platType, gameType, gameCode, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, final String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                CommonAppConfig.getInstance().getUserBean(new CommonCallback<UserBean>() {
                    @Override
                    public void callback(UserBean userBean) {
                        String coin = userBean == null ? "" : userBean.getCoin();
                        LoginBean loginBean = new Gson().fromJson(info[0], LoginBean.class);
                        RouteUtil.toGame(null, loginBean.getData(), platType, userBean.getCoin(), null);
                    }
                });
            }
        });
    }

    @Override
    public void initData() {
        String ticketData = CacheData.getTicketData();
        if (!TextUtils.isEmpty(ticketData)) {
            L.e("===本地获取数据===");
            handelData(ticketData);
            return;
        }
        CommonHttpUtil.getBCTicket(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                // 保存数据到本地
                CacheData.setTicketData(info[0]);

                handelData(info[0]);
            }
        });
    }

    /**
     * 处理数据
     *
     * @param data
     */
    private void handelData(String data) {
        List<TicketData> lists = JSON.parseArray(data, TicketData.class);
        mData.clear();
        mData.addAll(lists);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public int showGravity() {
        return Gravity.BOTTOM;
    }
}
