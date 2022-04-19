package com.qgnix.live.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.event.CommonSuccessEvent;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.adapter.NoMoreClickListener;
import com.qgnix.live.adapter.RecyclerAdapter;
import com.qgnix.live.bean.TransferBean;
import com.qgnix.live.dialog.QutotaDialog;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.live.lottery.entry.BalanceInfo;
import com.qgnix.live.view.RecycleHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * 额度转换
 */
public class QutotaConversionActivity extends AbsActivity implements View.OnClickListener {

    private TextView tv_account_balance;
    //private TextView tv_alert_msg;

    private Button btn_yjzr;
    private RecyclerView recyclerView;

    /**
     * 可转换游戏币额度
     */
    //private TextView mTvGameCurrency;

    private RecyclerAdapter<TransferBean> adapter;
    private final List<TransferBean> list = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_qutota_conversion;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.Quota_conversion));
        initView();
        loadData();
    }

    private void initView() {
        tv_account_balance = findViewById(R.id.tv_account_balance);
        //mTvGameCurrency = findViewById(R.id.tv_game_currency);
        //tv_alert_msg = findViewById(R.id.tv_alert_msg);

        btn_yjzr = findViewById(R.id.btn_yjzr);
        recyclerView = findViewById(R.id.recyclerView);


        btn_yjzr.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setFocusable(false);

        initAdapter();
    }

    private void initAdapter() {
        adapter = new RecyclerAdapter<TransferBean>(this, list, R.layout.item_qutota_record) {
            @Override
            public void convert(RecycleHolder holder, TransferBean data, int position) {
                holder.setText(R.id.tv_title, data.getTitle())
                        .setText(R.id.tv_money, TextUtils.isEmpty(data.getCoin()) ? "0" : data.getCoin());

                holder.setSlideClickListener(R.id.bt_zc, new NoMoreClickListener() {
                    @Override
                    protected void onMoreClick(View view) {
                        if (TextUtils.isEmpty(data.getCoin())) {
                            ToastUtil.show(R.string.the_transferable_balance_is_zero);
                            return;
                        }
                        new QutotaDialog(QutotaConversionActivity.this, data, 0, new QutotaDialog.DialogListener() {
                            @Override
                            public void dialogCallback(int code) {
                                if (code == 1) {
                                    EventBus.getDefault().post(new CommonSuccessEvent());
                                    loadData();
                                }

                            }
                        }).show();
                    }
                })
                        .setSlideClickListener(R.id.bt_zr, new NoMoreClickListener() {
                            @Override
                            protected void onMoreClick(View view) {
                                new QutotaDialog(QutotaConversionActivity.this, data, 1, new QutotaDialog.DialogListener() {
                                    @Override
                                    public void dialogCallback(int code) {
                                        if (code == 1) {
                                            EventBus.getDefault().post(new CommonSuccessEvent());
                                            loadData();
                                        }
                                    }
                                }).show();
                            }
                        });
            }
        };
        recyclerView.setAdapter(adapter);
    }


    // 一键转出全部平台额度
    public void setAllTrans() {
        String accountBalance = tv_account_balance.getText().toString().trim();
        if (TextUtils.isEmpty(accountBalance) || "0".equals(accountBalance)) {
            ToastUtil.show(R.string.Insufficient_balance);
            return;
        }

        new AlertDialog.Builder(this).setTitle(R.string.dialog_tip).setMessage(R.string.whether_to_switch_back_at_once)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    boolean isClick = false;

                    public void onClick(DialogInterface dialog, int which) {
                        if (isClick) {
                            return;
                        }
                        isClick = true;
                        LiveHttpUtil.nGAllTrans(new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                isClick = false;
                                if (code != 0) {
                                    ToastUtil.show(msg);
                                    return;
                                }
                                if (null != dialog) {
                                    dialog.dismiss();
                                }

                                EventBus.getDefault().post(new CommonSuccessEvent());
                                loadData();
                            }

                            @Override
                            public void onError() {
                                super.onError();
                                isClick = false;
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_yjzr) { // 一键转出tv_one_key
            setAllTrans();
        }
    }

    private void loadData() {
        //获取余额
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) {
                    ToastUtil.show(msg);
                    return;
                }
                if (info.length == 0) {
                    return;
                }
                BalanceInfo balanceInfo = JSON.parseObject(info[0], BalanceInfo.class);
                // 余额
                tv_account_balance.setText(String.valueOf(balanceInfo.getCoin().subtract(balanceInfo.getGive_coin()))+ " ("+ WordUtil.getString(R.string.coin)+")");
                // 可转游戏币
                //mTvGameCurrency.setText(String.valueOf(balanceInfo.getMoney()));
                //提示消息
                //tv_alert_msg.setText(String.format(WordUtil.getString(R.string.convert_game_currency_msg), balanceInfo.getGive_coin()));
            }
        });

        getCategory();
    }

    // 获取类别
    private void getCategory() {
        LiveHttpUtil.nGAllBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                List<TransferBean> data = JSON.parseArray(info[0], TransferBean.class);
                list.clear();
                list.addAll(data);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
