package com.qgnix.phonelive.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class description
 * 崩溃处理类，用于捕获全局crash，生成日志，并上传服务器
 *
 * @author huburt
 * @date 2016-11-08
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    //crash日志的存放位置
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashTest/log/";
    //crash日志文件的前缀
    private static final String FILE_NAME = "crash";
    //crash日志文件的后缀，本质是txt，可以改成任意后缀使用户无法阅读
    private static final String FILE_NAME_SUFFIX = ".trace";
    //默认的crashHandler，如没有则为null
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    private Context mContext;

    private static CrashHandler sInstance = new CrashHandler();

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    /**
     * 当程序出现未被捕获的异常，系统将会自动调用此方法
     *
     * @param thread
     * @param throwable
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            dumpExceptionToSDCard(throwable);
            uploadExceptionToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throwable.printStackTrace();
        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则由自己结束
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, throwable);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * 将crash日志写入sd卡
     *
     * @param throwable
     */
    private void dumpExceptionToSDCard(Throwable throwable) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("tag", "sdcard unmounted");
            return;
        }
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(current));
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            printWriter.println(time);
            dumpPhoneInfo(printWriter);
            printWriter.println();
            throwable.printStackTrace(printWriter);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入手机环境信息
     *
     * @param printWriter
     * @throws PackageManager.NameNotFoundException
     */
    private void dumpPhoneInfo(PrintWriter printWriter) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        printWriter.print("App Version:");
        printWriter.print(packageInfo.versionName);
        printWriter.print("_");
        printWriter.println(packageInfo.versionCode);

        //android版本号
        printWriter.print("Os Version:");
        printWriter.print(Build.VERSION.RELEASE);
        printWriter.print("_");
        printWriter.println(Build.VERSION.SDK_INT);

        //手机制造商
        printWriter.print("Vendor:");
        printWriter.println(Build.MANUFACTURER);

        //手机型号
        printWriter.print("Model:");
        printWriter.println(Build.MODEL);

        //cpu架构
        printWriter.print("CPU ABI:");
        printWriter.println(Build.CPU_ABI);
    }

    /**
     * 将crash日志打包上传，此处省略
     */
    private void uploadExceptionToServer() {
        Log.d("tag", "upload");
    }
}