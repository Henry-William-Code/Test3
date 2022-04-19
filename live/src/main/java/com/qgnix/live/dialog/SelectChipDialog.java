package com.qgnix.live.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.utils.KeyboardUtils;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.interfaces.OnSelectChipListener;
import com.qgnix.live.lottery.adapter.SetChipAdapter;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.lottery.entry.SetChipsBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 选择筹码
 */
public class SelectChipDialog extends BaseCustomDialog {


    private RecyclerView mRvChip;
    private SetChipAdapter mChipAdapter;

    private EditText mEtChipAmount;

    private final Context mContext;

    private OnSelectChipListener mOnSelectChipListener;

    public void setOnSelectChipListener(OnSelectChipListener onSelectChipListener) {
        this.mOnSelectChipListener = onSelectChipListener;
    }

    public SelectChipDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_select_chip_layout;
    }

    @Override
    public void initView() {
        mEtChipAmount = findViewById(R.id.et_chip_amount);
        //回车事件关闭键盘
        mEtChipAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    KeyboardUtils.hideSoftInput(v);
                    return true;
                }
                return false;
            }
        });

        mRvChip = findViewById(R.id.rv_chip);
        findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = mEtChipAmount.getText().toString().trim();
                String[] chipArr = CommonAppConfig.getInstance().getChips();
                if (null != chipArr && chipArr.length > 0) {
                    Arrays.sort(chipArr);
                    int minChips = Integer.parseInt(chipArr[0]);
                    if (Integer.parseInt(TextUtils.isEmpty(amount) ? "0" : amount) < minChips) {
                        ToastUtil.show(WordUtil.getString(R.string.min_chip, minChips));
                        return;
                    }
                    mOnSelectChipListener.onSelectChip(amount);
                }
                dismiss();
            }
        });
    }

    @Override
    public void initData() {
        final List<SetChipsBean> chips = new ArrayList<>();
        String[] chipArr = CommonAppConfig.getInstance().getChips();
        SetChipsBean setChipsBean;
        for (String chip : chipArr) {
            setChipsBean = new SetChipsBean(Integer.parseInt(chip));
            chips.add(setChipsBean);
        }
        SetChipsBean chipsBean = chips.get(0);
        chipsBean.setSelect(true);
        String money = String.valueOf(chipsBean.getMoney());
        mEtChipAmount.setText(money);
        mEtChipAmount.setSelection(money.length());
        mEtChipAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                for (SetChipsBean chip : chips) {
                    chip.setSelect(s.toString().equals(String.valueOf(chip.getMoney())));
                }
                mChipAdapter.notifyDataSetChanged();
            }
        });

        mRvChip.setLayoutManager(new GridLayoutManager(mContext, 4));
        mChipAdapter = new SetChipAdapter(mContext, chips);
        mChipAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                for (SetChipsBean chip : chips) {
                    chip.setSelect(false);
                }
                SetChipsBean bean = chips.get(position);
                bean.setSelect(true);
                String tempMoney = String.valueOf(bean.getMoney());
                mEtChipAmount.setText(tempMoney);
                mEtChipAmount.setSelection(tempMoney.length());
                mChipAdapter.notifyDataSetChanged();
            }
        });
        mRvChip.setAdapter(mChipAdapter);
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
