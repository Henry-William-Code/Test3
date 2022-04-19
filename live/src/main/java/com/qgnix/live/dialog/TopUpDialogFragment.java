package com.qgnix.live.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.utils.DpUtil;
import com.qgnix.common.utils.HtmlUtils;
import com.qgnix.live.R;

/**
 * Created by cxf on 2018/11/21.
 * 充值说明
 */

public class TopUpDialogFragment {

    private Context context;
    private Dialog dialog;
    private Button btnIKnow;
    private ImageView imgClose;
    private TextView tvTitle, tvContent;
    private String title, content;

    public TopUpDialogFragment(Context context, String title, String content) {
        this.context = context;
        this.title = title;
        this.content = content;
    }

    public TopUpDialogFragment builder() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_top_up, null);
        imgClose = view.findViewById(R.id.img_close);
        btnIKnow = view.findViewById(R.id.btn_I_know);
        tvTitle = view.findViewById(R.id.tv_title);
        tvContent = view.findViewById(R.id.tv_content);
        tvTitle.setText(title);
        HtmlUtils.setTxtData(content, tvContent);
        //该语句在设置后必加，不然没有任何效果
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(300);
        params.height = DpUtil.dp2px(400);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        btnIKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return this;
    }

    public void show() {
        dialog.show();
    }

    public boolean isShow() {
        return dialog.isShowing();
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
