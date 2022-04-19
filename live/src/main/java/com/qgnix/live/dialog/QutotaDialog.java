package com.qgnix.live.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qgnix.common.http.HttpCallback;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.R;
import com.qgnix.live.bean.TransferBean;
import com.qgnix.live.http.LiveHttpUtil;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by cxf on 2018/11/19.
 * 额度转入转出
 */

public class QutotaDialog extends BaseCustomDialog {

    private TransferBean mQutotaConversionBean;
    private int type = 0;//1转入  0转出
    private EditText etAmount;
    private TextView btnCancel, btnSubmit;

    /**
     * 回调
     */
    private final DialogListener mListener;

    public QutotaDialog(Context context, TransferBean qutotaConversionBean, int type, DialogListener listener) {
        super(context);
        this.mQutotaConversionBean = qutotaConversionBean;
        this.type = type;
        this.mListener = listener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_qutota;
    }

    @Override
    public void initView() {
        etAmount = findViewById(R.id.et_amount);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSubmit = findViewById(R.id.btn_submit);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.dialogCallback(0);
                dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubmit.setEnabled(false);
                submit();
            }
        });
    }

    private void submit() {
        if (TextUtils.isEmpty(etAmount.getText().toString())) {
            ToastUtil.show(R.string.red_pack_7_2);
            return;
        }
        String amount = etAmount.getText().toString().trim();
        if (type == 0) {
            String coin = mQutotaConversionBean.getCoin();
            if (coin.contains(",")) {
                coin = coin.replace(",", "");
            }
            if (new BigDecimal(amount).compareTo(new BigDecimal(coin)) > 0) {
                ToastUtil.show(R.string.the_conversion_amount_cannot_be_greater_than_the_balance);
                return;
            }
        }
        LiveHttpUtil.nGetTrans(amount, mQutotaConversionBean.getPlat_type(), type, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                L.e("===Trans_v2=", Arrays.toString(info));
                etAmount.setText("");
                btnSubmit.setEnabled(true);
                if (code != 0) {
                    return;
                }
                ToastUtil.show(msg);
                mListener.dialogCallback(1);
                dismiss();
            }

            @Override
            public void onError() {
                super.onError();
                btnSubmit.setEnabled(true);
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public int showGravity() {
        return Gravity.CENTER;
    }

    /**
     * dialog回调
     */
    public interface DialogListener {
        /**
         * 回调
         *
         * @param code 1 成功
         */
        void dialogCallback(int code);
    }

}
