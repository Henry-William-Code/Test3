package com.qgnix.live.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.qgnix.common.utils.DpUtil;
import com.qgnix.common.utils.HtmlUtils;
import com.qgnix.live.R;

import java.net.URLEncoder;


/**
 * 彩票规则dialog
 */

public class TicketHelpDialog implements View.OnClickListener {

    private Context mContext;
    private Dialog mDialog;
    /**
     * 标题
     */
    private String mTitle;
    /**
     * 内容
     */
    private String mContent;

    public TicketHelpDialog(Context context, String title, String content) {
        this.mContext = context;
        this.mTitle = title;
        this.mContent = content;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public TicketHelpDialog builder() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_ticket_help, null);
        //标题
        TextView tvTitle = view.findViewById(R.id.tv_title);
        //内容
        TextView tvContent = view.findViewById(R.id.tv_content);
        tvTitle.setText(mTitle);
        HtmlUtils.setTxtData(mContent, tvContent);
        //该语句在设置后必加，不然没有任何效果
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        mDialog = new Dialog(mContext, R.style.dialog);
        mDialog.setContentView(view);
        Window window = mDialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(280);
        params.height = DpUtil.dp2px(480);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        //关闭
        view.findViewById(R.id.btn_i_know).setOnClickListener(this);
        view.findViewById(R.id.iv_close).setOnClickListener(this);
        return this;
    }

    public void show() {
        mDialog.show();
    }

    public boolean isShow() {
        return mDialog.isShowing();
    }

    public void dismiss() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        mDialog.dismiss();
    }
}
