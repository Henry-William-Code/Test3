package com.qgnix.live.lottery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qgnix.common.custom.ItemDecoration;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.dialog.BaseCustomDialog;
import com.qgnix.live.http.LiveHttpUtil;
import com.qgnix.live.lottery.adapter.BettingChildAdapter;
import com.qgnix.live.lottery.adapter.MultipleAdapter;
import com.qgnix.live.lottery.entry.BalanceInfo;
import com.qgnix.live.lottery.entry.BettingDetailsBean;
import com.qgnix.live.lottery.entry.ConfirmTzBean;
import com.qgnix.live.lottery.entry.MultipleBean;
import com.qgnix.live.lottery.entry.ResultBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 确认投注dialog
 */
public class ConfirmBettingDialog extends BaseCustomDialog {

    /**
     * 投注总额
     */
    private TextView mTvBettingTotalMoney;
    /**
     * 投注注数
     */
    private TextView mTvBettingCount;
    /**
     * 账户余额
     */
    private TextView mTvBalance;

    /**
     * 倍数
     */
    private RecyclerView mRvMultiple;


    private final Context mContext;

    private BettingChildAdapter adapter;


    // 默认选中的倍数
    private int checkMultiplePosition = 0;

    // 总价
    private int sum = 0;

    /**
     * 默认倍数
     */
    private int multiple;

    private final List<BettingDetailsBean> newList = new ArrayList<>();

    private final List<ConfirmTzBean> commitList = new ArrayList<>();

    private final OnSuccessListener onSuccessListener;

    public ConfirmBettingDialog(Context context, int chip, List<BettingDetailsBean> list, OnSuccessListener onSuccessListener) {
        this(context, 1, chip, list, onSuccessListener);
    }

    public ConfirmBettingDialog(Context context, int multiple, int chip, List<BettingDetailsBean> list, OnSuccessListener onSuccessListener) {
        super(context);
        this.mContext = context;
        this.multiple = multiple;
        this.onSuccessListener = onSuccessListener;

        newList.clear();
        for (BettingDetailsBean bean : list) {
            bean.setPrice(String.valueOf(chip));
            bean.setMultiple(multiple);
            newList.add(bean);
            sum += multiple * Integer.parseInt(bean.getPrice());
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_confim_betting;
    }

    @Override
    public void initView() {
        mTvBettingCount = findViewById(R.id.tv_betting_count);
        mTvBettingTotalMoney = findViewById(R.id.tv_betting_total_money);
        mTvBalance = findViewById(R.id.tv_balance);

        RecyclerView rvNotes = findViewById(R.id.recyclerView_notes);
        mRvMultiple = findViewById(R.id.rv_multiple);

        findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onSuccessListener) {
                    onSuccessListener.onFail();
                }
                dismiss();
            }
        });


        //投注信息
        rvNotes.setLayoutManager(new LinearLayoutManager(mContext));
        rvNotes.addItemDecoration(new ItemDecoration(mContext));
        adapter = new BettingChildAdapter(mContext, newList);
        rvNotes.setAdapter(adapter);

        // 删除
        adapter.setOnItemClickListener(new BettingChildAdapter.OnItemClickListener() {
            @Override
            public void onClickListener(int position) {
                if (newList.size() == 1) {
                    ToastUtil.show(R.string.select_at_least_one);
                    return;
                }
                newList.remove(position);
                adapter.notifyItemRemoved(position);
                sum = 0;
                for (int i = 0; i < newList.size(); i++) {
                    sum += multiple * Integer.parseInt(newList.get(i).getPrice());
                    newList.get(i).setMultiple(multiple);
                }
                adapter.notifyDataSetChanged();

                handleTotalBettingInfo(true);
            }
        });

        //确认投注
        TextView btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConfirm.setClickable(false);
                if (newList.isEmpty()) {
                    ToastUtil.show(R.string.betting_number_cannot_be_empty);
                    btnConfirm.setClickable(true);
                    return;
                }
                commitList.clear();
                for (BettingDetailsBean detailsBean : newList) {
                    ConfirmTzBean bean = new ConfirmTzBean();
                    bean.setTz_money(detailsBean.getPrice());
                    bean.setB_id(detailsBean.getB_id());
                    bean.setO_id(detailsBean.getId());
                    bean.setTz_zs(String.valueOf(detailsBean.getMultiple()));
                    commitList.add(bean);
                }
                String tz = new Gson().toJson(commitList);
                L.d("==投注参数==", tz);
                LiveHttpUtil.saveTicketZt(tz, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        btnConfirm.setClickable(true);
                        if (code != 0) {
                            ToastUtil.show(msg);
                            return;
                        }
                        if (info.length == 0) {
                            return;
                        }
                        JsonObject data = new JsonParser().parse(info[0]).getAsJsonObject();
                        JSONObject jsonObject = JSONObject.parseObject(info[0]);
                        ResultBean resultBean = new Gson().fromJson(data.toString(), ResultBean.class);
                        if (resultBean.getCode() == 0) {
                            dismiss();
                            ToastUtil.showSuccess(mContext);
                            onSuccessListener.onSuccess(200, sum, jsonObject.getString("coin"));
                        } else {
                            onSuccessListener.onSuccess(100, 0, null);
                            ToastUtil.show(resultBean.getMsg());
                        }
                    }

                    @Override
                    public void onError() {
                        super.onError();
                        btnConfirm.setClickable(true);
                        onSuccessListener.onFail();
                    }
                });
            }
        });
    }

    @Override
    public void initData() {
        getMultiple();
        handleTotalBettingInfo(true);
        loadData();
    }

    /**
     * 处理总的投注信息
     *
     * @param isZs true 注释变 false 注数不变
     */
    private void handleTotalBettingInfo(boolean isZs) {
        // 注数
        if (isZs) {
            mTvBettingCount.setText(Html.fromHtml(WordUtil.getRedHtmlStr(newList.size()) + WordUtil.getBlankHtmlStrFromRes(R.string.note)));
        }
        // 投注总额
        String allMoney = Html.fromHtml(WordUtil.getBlankHtmlStrFromRes(R.string.Total)) + WordUtil.getRedHtmlStr(sum) + WordUtil.getBlankHtmlStrFromRes(R.string.yuan);
        mTvBettingTotalMoney.setText(Html.fromHtml(allMoney));
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    public int showGravity() {
        return Gravity.BOTTOM;
    }

    // 获取倍数
    private void getMultiple() {
        List<MultipleBean> multipleBeans = new ArrayList<>();

        multipleBeans.add(new MultipleBean("1", 1, true));
        multipleBeans.add(new MultipleBean("2", 2, false));
        multipleBeans.add(new MultipleBean("3", 5, false));
        multipleBeans.add(new MultipleBean("4", 10, false));
        multipleBeans.add(new MultipleBean("5", 20, false));

        // 倍数列表
        mRvMultiple.setLayoutManager(new GridLayoutManager(mContext, 5));
        MultipleAdapter multipleAdapter = new MultipleAdapter(mContext, multipleBeans);
        mRvMultiple.setAdapter(multipleAdapter);
        multipleAdapter.setOnItemClickListener(new MultipleAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                for (int i = 0; i < multipleBeans.size(); i++) {
                    if (position == i) {
                        checkMultiplePosition = position;
                        multiple = multipleBeans.get(checkMultiplePosition).getMultiple();
                        multipleBeans.get(i).setSelect(true);
                    } else {
                        multipleBeans.get(i).setSelect(false);
                    }
                }
                multipleAdapter.notifyDataSetChanged();
                // 同时修改列表中的数据
                sum = 0;
                for (int i = 0; i < newList.size(); i++) {
                    sum += multiple * Integer.parseInt(newList.get(i).getPrice());
                    newList.get(i).setMultiple(multiple);
                }
                adapter.notifyDataSetChanged();
                handleTotalBettingInfo(false);
            }
        });

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
                BalanceInfo balanceInfo = new Gson().fromJson(info[0], BalanceInfo.class);
                // 余额20201120去掉多的一个COIN
                String balance = WordUtil.getBlankHtmlStrFromRes(R.string.total_balance) + "." + WordUtil.getRedHtmlStr(String.valueOf(balanceInfo.getCoin()));// + WordUtil.getBlankHtmlStrFromRes(R.string.yuan);
                mTvBalance.setText(Html.fromHtml(balance));
            }

        });
    }

    public interface OnSuccessListener {
        void onSuccess(int code, int totalPrice, String balance);

        void onFail();
    }
}
