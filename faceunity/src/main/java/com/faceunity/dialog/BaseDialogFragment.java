package com.faceunity.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.faceunity.R;


/**
 * @author Richie on 2018.09.19
 */
public abstract class BaseDialogFragment extends DialogFragment {


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View dialogView = createDialogView(inflater, container);
        initWindowParams();
        return dialogView;
    }

    /**
     * 创建 dialog view
     *
     * @param inflater
     * @param container
     * @return
     */
    protected abstract View createDialogView(LayoutInflater inflater,  ViewGroup container);

    protected int getDialogWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    protected int getDialogHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    private void initWindowParams() {
        Dialog dialog = getDialog();
        if (dialog != null) {

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = dialog.getWindow();
            if (window != null) {
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setBackgroundDrawableResource(R.color.transparent);
                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = Gravity.CENTER;
                windowAttributes.width = getDialogWidth();
                windowAttributes.height = getDialogHeight();
                window.setAttributes(windowAttributes);
            }
        }
    }

    public interface OnClickListener {
        /**
         * 确认
         */
        void onConfirm();

        /**
         * 取消
         */
        void onCancel();
    }

    public interface OnDismissListener {
        /**
         * 消失
         */
        void onDismiss();
    }
}
