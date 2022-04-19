package com.qgnix.common.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 截图工具类
 */
public class ShotScreenManager {

    private static final String TAG = "===" + ShotScreenManager.class.getSimpleName() + "::";

    private static ShotScreenManager sInstance;

    public static ShotScreenManager getInstance() {
        if (null == sInstance) {
            synchronized (ShotScreenManager.class) {
                if (null == sInstance) {
                    sInstance = new ShotScreenManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 截屏保存到相册
     *
     * @param view
     * @param saveFilePath
     */
    public void viewShotScreenToGallery(View view, String saveFilePath) {
        if (view == null) {
            Log.e(TAG, "screenShot--->view is null");
            return;
        }
        //允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        saveImageToGallery(view.getContext(), bitmap, saveFilePath);
    }


    /**
     * 保存图片 刷新相册
     *
     * @param context
     * @param bmp
     * @param path
     */
    private void saveImageToGallery(Context context, Bitmap bmp, String path) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            L.e("没有SD卡！");
            return;
        }

        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File defaultShotDir = new File(dcim, path);
        if (!defaultShotDir.exists()) {
            boolean mkdirs = defaultShotDir.mkdirs();
            if (!mkdirs) {
                L.e("创建图片文件夹失败！");
                return;
            }
        }
        // 将要保存图片的路径
        File imgFile = new File(defaultShotDir, "super_" + System.currentTimeMillis() + ".jpg");
        Log.d(TAG, "--->图片保存地址：" + imgFile);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(
                    new FileOutputStream(imgFile));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            L.e(TAG + "保存图片成功！" + imgFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            L.e(TAG + "保存图片失败！");
        } finally {
            if (null != bos) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        refreshGallery(context, imgFile);
    }

    /**
     * 刷新相册
     *
     * @param ctx     上下文
     * @param imgFile 图片文件
     */
    private void refreshGallery(Context ctx, File imgFile) {
        // 最后通知图库更新
        ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Uri contentUri = Uri.fromFile(imgFile.getAbsoluteFile());
            intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        } else {
            //4.4开始不允许发送"Intent.ACTION_MEDIA_MOUNTED"广播,
            // 否则会出现: Permission Denial: not allowed to send broadcast android.intent.action.MEDIA_MOUNTED from pid=15410, uid=10135
            intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
        }
        ctx.sendBroadcast(intent);
    }
}