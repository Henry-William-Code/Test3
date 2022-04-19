package com.qgnix.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.HtmlConfig;
import com.qgnix.common.activity.AbsActivity;
import com.qgnix.common.bean.ConfigBean;
import com.qgnix.common.http.CommonHttpConsts;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.interfaces.CommonCallback;
import com.qgnix.common.utils.DialogUtil;
import com.qgnix.common.utils.GlideCatchUtil;
import com.qgnix.common.utils.SpUtil;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.common.utils.VersionUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.main.http.MainHttpConsts;
import com.qgnix.main.http.MainHttpUtil;

import java.io.File;

/**
 * Created by cxf on 2018/9/
 * <p>
 * 系统设置页面
 */

public class SettingActivity extends AbsActivity implements View.OnClickListener {

    private Handler mHandler;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.setting));


        // 退出登录
        findViewById(R.id.btn_logout).setOnClickListener(this);

        // 修改密码
        findViewById(R.id.rela_up_pass).setOnClickListener(this);
        //设置交易密码
        findViewById(R.id.rela_up_pay).setOnClickListener(this);
        // 清除缓存
        findViewById(R.id.rela_cache).setOnClickListener(this);
        // 检测版本
        findViewById(R.id.rela_version).setOnClickListener(this);
        // 隐私协议
        findViewById(R.id.rela_yinsi).setOnClickListener(this);
        // 关于我们
        findViewById(R.id.rela_about).setOnClickListener(this);

        // 语言切换
        findViewById(R.id.rela_language).setOnClickListener(this);

        //当前版本号
        TextView tvCurVersion = findViewById(R.id.tv_cur_version);
        tvCurVersion.setText(WordUtil.getString(R.string.current_version, VersionUtil.getVersion()));

    }

    /**
     * 检查更新
     */
    private void checkVersion() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (VersionUtil.isLatest(configBean.getVersion())) {
                        ToastUtil.show(R.string.version_latest);
                    } else {
                        VersionUtil.showDialog(mContext, configBean);
                    }
                }
            }
        });

    }

    /**
     * 退出登录
     */
    private void logout() {
        String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
        if (!android.text.TextUtils.isEmpty(configString)) {
            JSONObject obj = JSON.parseObject(configString);
            MainHttpUtil.cb = obj.getString("cd");
        }
        CommonAppConfig.getInstance().clearLoginInfo();
        LoginActivity.forward();
    }
    /**
     * 获取缓存
     */
    private String getCacheSize() {
        return GlideCatchUtil.getInstance().getCacheSize();
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        final Dialog dialog = DialogUtil.loadingDialog(mContext, getString(R.string.setting_clear_cache_ing));
        dialog.show();
        GlideCatchUtil.getInstance().clearImageAllCache();
        File gifGiftDir = new File(CommonAppConfig.GIF_PATH);
        if (gifGiftDir.exists() && gifGiftDir.length() > 0) {
            gifGiftDir.delete();
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
                ToastUtil.show(R.string.setting_clear_cache);
            }
        }, 2000);
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        MainHttpUtil.cancel(MainHttpConsts.GET_SETTING_LIST);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_logout) {
            logout();
        } else if (id == R.id.rela_up_pass) {
            startActivity(new Intent(mContext, ModifyPwdActivity.class));
        } else if (id == R.id.rela_up_pay) {
            startActivity(new Intent(mContext, SetTransactionPwdAct.class));
        } else if (id == R.id.rela_cache) {
            clearCache();
        } else if (id == R.id.rela_version) {
            checkVersion();
        } else if (id == R.id.rela_yinsi) {
            WebViewsActivity.forward(mContext, HtmlConfig.PRIVACY_AGREEMENT);
        } else if (id == R.id.rela_about) {
            WebViewsActivity.forward(mContext, HtmlConfig.ABOUT_US);
        } else if (id == R.id.rela_language) {
            LanSwitchActivity.toForward(mContext);
        }
    }
}
