package com.qgnix.common.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.qgnix.common.CommonAppContext;
import com.qgnix.common.R;
import com.qgnix.common.bean.ConfigBean;

/**
 * Created by cxf on 2017/10/9.
 */

public class VersionUtil {

    private static String sVersion;

    /**
     * 是否是最新版本
     */
    public static boolean isLatest(String version) {
        if (TextUtils.isEmpty(version)) {
            return true;
        }
        String curVersion = getVersion();
        if (TextUtils.isEmpty(curVersion)) {
            return true;
        }
        return compareVersion(curVersion, version) >= 0;
    }


    /**
     * 版本号比较
     *
     * @param v1
     * @param v2
     * @return 0代表相等，1代表左边大，-1代表右边大
     * Utils.compareVersion("1.0.358_20180820090554","1.0.358_20180820090553")=1
     */
    public static int compareVersion(String v1, String v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] version1Array = v1.split("[._]");
        String[] version2Array = v2.split("[._]");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        long diff = 0;

        while (index < minLen
                && (diff = Long.parseLong(version1Array[index])
                - Long.parseLong(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Long.parseLong(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Long.parseLong(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    public static void showDialog2(final Context context, ConfigBean configBean) {
        int isForce = configBean.getIsForce();
        String versionTip = configBean.getUpdateDes();
        String downloadUrl = configBean.getDownloadApkUrl();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
        ((TextView) view.findViewById(R.id.tv_content)).setText(versionTip);
        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setCancelable(isForce != 1);
        dialog.setContentView(view);
        View no = view.findViewById(R.id.btn_no);
        if (isForce != 1) {
            no.setVisibility(View.VISIBLE);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 当前版本已经取消升级
                    SpUtil.getInstance().setBooleanValue(SpUtil.IS_CANCEL_VERSION + configBean.getVersion(), true);
                    dialog.dismiss();
                }
            });
        }
        view.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(downloadUrl)) {
                    try {
                        // 清空本地缓存数据
                        SpUtil.getInstance().clear();

                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setData(Uri.parse(downloadUrl));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        ToastUtil.show(R.string.version_download_url_error);
                    }
                } else {
                    ToastUtil.show(R.string.version_download_url_error);
                }
                if (isForce != 1)
                    dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showDialog(final Context context, ConfigBean configBean) {
        int isForce = configBean.getIsForce();
        String versionTip = configBean.getUpdateDes();
        String downloadUrl = configBean.getDownloadApkUrl();

        DialogUtil.Builder builder = new DialogUtil.Builder(context);
        builder.setTitle(WordUtil.getString(R.string.version_update))
                .setContent(versionTip)
                .setConfrimString(WordUtil.getString(R.string.version_immediate_use))
                .setCancelString(WordUtil.getString(R.string.version_not_update))
                .setShowCancel(isForce != 1)
                .setCancelable(isForce != 1)
                .setClickCallback(new DialogUtil.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {
                        // 当前版本已经取消升级
                        SpUtil.getInstance().setBooleanValue(SpUtil.IS_CANCEL_VERSION + configBean.getVersion(), true);
                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        if (!TextUtils.isEmpty(downloadUrl)) {
                            try {
                                if (isForce == 1)
                                    dialog.show();

                                // 清空本地缓存数据
                                SpUtil.getInstance().clear();

                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                intent.setData(Uri.parse(downloadUrl));
                                context.startActivity(intent);
                            } catch (Exception e) {
                                ToastUtil.show(R.string.version_download_url_error);
                            }
                        } else {
                            ToastUtil.show(R.string.version_download_url_error);
                        }
                    }
                })
                .build()
                .show();
    }

    /**
     * 获取版本号
     */
    public static String getVersion() {
        if (TextUtils.isEmpty(sVersion)) {
            try {
                PackageManager manager = CommonAppContext.sInstance.getPackageManager();
                PackageInfo info = manager.getPackageInfo(CommonAppContext.sInstance.getPackageName(), 0);
                sVersion = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sVersion;
    }


    /**
     *
     * @return if version1 > version2, return 1, if equal, return 0, else return
     *         -1
     */
    public static int VersionComparison(String versionServer, String versionLocal) {
        String version1 = versionServer;
        String version2 = versionLocal;
        if (version1 == null || version1.length() == 0 || version2 == null || version2.length() == 0)
            throw new IllegalArgumentException("Invalid parameter!");

        int index1 = 0;
        int index2 = 0;
        while (index1 < version1.length() && index2 < version2.length()) {
            int[] number1 = getValue(version1, index1);
            int[] number2 = getValue(version2, index2);

            if (number1[0] < number2[0]){

                return -1;
            }
            else if (number1[0] > number2[0]){

                return 1;
            }
            else {
                index1 = number1[1] + 1;
                index2 = number2[1] + 1;
            }
        }
        if (index1 == version1.length() && index2 == version2.length())
            return 0;
        if (index1 < version1.length())
            return 1;
        else
            return -1;
    }


    /**
     *
     * @param version
     * @param index
     *            the starting point
     * @return the number between two dots, and the index of the dot
     */
    public static int[] getValue(String version, int index) {
        int[] value_index = new int[2];
        StringBuilder sb = new StringBuilder();
        while (index < version.length() && version.charAt(index) != '.') {
            sb.append(version.charAt(index));
            index++;
        }
        value_index[0] = Integer.parseInt(sb.toString());
        value_index[1] = index;

        return value_index;
    }

}
