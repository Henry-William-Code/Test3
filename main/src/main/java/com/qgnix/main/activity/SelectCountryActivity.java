package com.qgnix.main.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.custom.ItemDecoration;
import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.adapter.SelectCountryAdapter;
import com.qgnix.main.bean.PhoneRuleBean;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 选择归属地
 */
public class SelectCountryActivity extends AbsActivity {

    private SelectCountryAdapter mAdapter;

    private final List<PhoneRuleBean> mList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_country;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.input_country));

        RecyclerView rv = findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        rv.addItemDecoration(new ItemDecoration(mContext));
        rv.setLayoutManager(manager);
        mAdapter = new SelectCountryAdapter(mContext, mList);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                PhoneRuleBean ruleBean = mList.get(position);
                if (ruleBean.getStatus() == 1) {
                    Intent intent = new Intent();
                    intent.putExtra("countryName", ruleBean.getName());
                    intent.putExtra("countryCode", ruleBean.getE164());
                    intent.putExtra("phoneRule", ruleBean.getRule());
                    intent.putExtra("country", ruleBean.getCode());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    ToastUtil.show(R.string.not_yet_opened);
                }
            }
        });
        rv.setAdapter(mAdapter);

        getData();

    }

    // 获取数据
    private void getData() {
        MainHttpUtil.getPhoneRuleList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                List<PhoneRuleBean> list = new Gson().fromJson(Arrays.toString(info), new TypeToken<List<PhoneRuleBean>>() {
                }.getType());
                mList.addAll(list);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GetPhoneRules);
        super.onDestroy();
    }
}