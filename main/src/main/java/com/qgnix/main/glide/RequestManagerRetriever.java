package com.qgnix.main.glide;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


import com.qgnix.main.glide.binding.ApplicationLifecycle;

import com.qgnix.main.glide.binding.RequestManagerFragment;
import com.qgnix.main.glide.binding.SupportRequestManagerFragment;
import com.qgnix.main.glide.util.Util;

import java.util.HashMap;
import java.util.Map;

// 专门管理 RequestManager 的 大管家
public class RequestManagerRetriever implements Handler.Callback {

    @VisibleForTesting
    static final String FRAGMENT_TAG = "com.bumptech.glide.manager";

    @SuppressWarnings("deprecation")
    @VisibleForTesting
    final Map<FragmentManager, RequestManagerFragment> pendingRequestManagerFragments =
            new HashMap<>();

    @VisibleForTesting
    final Map<FragmentManager, SupportRequestManagerFragment> pendingSupportRequestManagerFragments =
            new HashMap<>();

    private final Handler handler;

    public RequestManagerRetriever() {
        // 主线程
        handler = new Handler(Looper.getMainLooper(), this);
    }

    private static final int ID_REMOVE_FRAGMENT_MANAGER = 1; // android.app Fragmetn 空白
    private static final int ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2; // androidx Fragmetn 空白

    private volatile RequestManager applicationManager;
    // 红色区域 Application 作用域

    private RequestManager getApplicationManager(Context context) {
        if (applicationManager == null) {
            synchronized (this) {
                if (applicationManager == null) {

                    Glide2 glide = Glide2.get(context.getApplicationContext());
                    applicationManager =  RequestManager.getInstance(new ApplicationLifecycle(), context.getApplicationContext());
                }
            }
        }
        return applicationManager;
    }

    // 参数 Context Activity FragmentActivity ... 调用了 此get函数【共用的】

    public RequestManager get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("你个货，传入的是 空的 context，要把你吊起来打...");
        } else if (Util.isOnMainThread() && !(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                return get((FragmentActivity) context); // 进入FragmentActivity的get函数
            } else if (context instanceof Activity) {
                return get((Activity) context); // 进入Activity的get函数
            } else if (context instanceof ContextWrapper && ((ContextWrapper) context).getBaseContext().getApplicationContext() != null) {
                return get(((ContextWrapper) context).getBaseContext()); // 继续递归寻找 匹配合适的
            }
        }

        // 若上面的判断都不满足，就会执行下面这句代码，同学们想知道Application作用域 就需要关心这句代码（红色区域）
        return getApplicationManager(context);
    }


    public RequestManager get(FragmentActivity activity) {
        if (Util.isOnBackgroundThread()) {
            return get(activity.getApplicationContext());
        } else {
            Util.assertNotDestroyed(activity);
            FragmentManager fm = activity.getSupportFragmentManager();
            return supportFragmentGet(activity, fm);
        }
    }


    public RequestManager get(Fragment fragment) { // androidx
        if (Util.isOnBackgroundThread()) {
            return get(fragment.getContext().getApplicationContext());
        } else {
            FragmentManager fm = fragment.getChildFragmentManager();
            return supportFragmentGet(fragment.getContext(), fm);
        }
    }

    @SuppressWarnings("deprecation")

    public RequestManager get(Activity activity) {
        if (Util.isOnBackgroundThread()) {
            return get(activity.getApplicationContext());
        } else {
            Util.assertNotDestroyed(activity);
            android.app.FragmentManager fm = activity.getFragmentManager();
            return fragmentGet(activity, fm);
        }
    }

    @Deprecated

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public RequestManager get(android.app.Fragment fragment) { // android.app
        if (fragment.getActivity() == null) {
            throw new IllegalArgumentException(
                    "You cannot start a load on a fragment before it is attached");
        }
        if (Util.isOnBackgroundThread() || Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return get(fragment.getActivity().getApplicationContext());
        } else {
            android.app.FragmentManager fm = fragment.getChildFragmentManager();
            return fragmentGet(fragment.getActivity(), fm);
        }
    }

    // TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 下面 管的是 androidx.fragment.app 兼容的

    private RequestManager supportFragmentGet(
            Context context,
            FragmentManager fm) {

        // 1、从 FragmentManager 中获取 SupportRequestManagerFragment(空白)
        SupportRequestManagerFragment current = getSupportRequestManagerFragment(fm);

        // 2、从该 空白Fragment 中获取 RequestManager
        RequestManager requestManager = current.getRequestManager();

        // 3、首次获取，则实例化 RequestManager
        if (requestManager == null) { // 【同学们注意：这样做的目的是为了  一个Activity或Fragment 只能有一个 RequestManager】

            // 3.1 实例化
            Glide2 glide = Glide2.get(context);
            requestManager =  RequestManager.getInstance( current.getGlideLifecycle(), context);

            // 3.2 设置 Fragment 对应的 RequestManager    空白的Fragment<--->requestManager
            current.setRequestManager(requestManager);
        }
        return requestManager;
    }

    // 1、从 FragmentManager 中获取 SupportRequestManagerFragment

    private SupportRequestManagerFragment getSupportRequestManagerFragment(
            final FragmentManager fm) {
        SupportRequestManagerFragment current =
                (SupportRequestManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);

        if (current == null) {

            //  1.2 尝试从【记录保存】中获取 Fragment
            current = pendingSupportRequestManagerFragments.get(fm);

            // 1.3 实例化 Fragment
            if (current == null) {

                // 1.3.1 创建对象 空白的Fragment
                current = new SupportRequestManagerFragment();  // 重复创建

                // 1.3.2 【记录保存】映射关系 进行保存   第一个保障
                // 一个MainActivity == 一个空白的SupportRequestManagerFragment == 一个RequestManager
                pendingSupportRequestManagerFragments.put(fm, current);

                // 1.3.3 提交 Fragment 事务
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();

                // 1.3.4 post 一个消息
                handler.obtainMessage(ID_REMOVE_SUPPORT_FRAGMENT_MANAGER, fm).sendToTarget();
            }
        }
        return current;
    }

    @Override
    public boolean handleMessage(Message message) {

        switch (message.what) {
            case ID_REMOVE_FRAGMENT_MANAGER:  // 移除 【记录保存】  1.3.5 post 一个消息
                android.app.FragmentManager fm = (android.app.FragmentManager) message.obj;
               pendingRequestManagerFragments.remove(fm); // 1.3.6 移除临时记录中的映射关系
                break;
            case ID_REMOVE_SUPPORT_FRAGMENT_MANAGER: // 移除 【记录保存】  1.3.5 post 一个消息
                FragmentManager supportFm = (FragmentManager) message.obj;
                pendingSupportRequestManagerFragments.remove(supportFm); // 1.3.6 移除临时记录中的映射关系
                break;
            default:
                break;
        }

        return false;
    }


    // TODO >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  下面 管的是 android.app.Fragment

    private RequestManager fragmentGet(
            Context context,
            android.app.FragmentManager fm) {

        // 1、从 FragmentManager 中获取 RequestManagerFragment
        RequestManagerFragment current = getRequestManagerFragment(fm);

        // 2、从该 Fragment 中获取 RequestManager
        RequestManager requestManager = current.getRequestManager();

        // 3、首次获取，则实例化 RequestManager
        if (requestManager == null) {  // 【同学们注意：这样做的目的是为了  一个Activity或Fragment 只能有一个 RequestManager】

            // 3.1 实例化
            Glide2 glide = Glide2.get(context);
            requestManager =  RequestManager.getInstance(current.getGlideLifecycle(), context);

            // 3.2 设置 Fragment 对应的 RequestManager
            current.setRequestManager(requestManager);
        }

        return requestManager;
    }

    // 1、从 FragmentManager 中获取 RequestManagerFragment
    private RequestManagerFragment getRequestManagerFragment(
            final android.app.FragmentManager fm) {

        // 第二次：就不会为null
        // 1.1 尝试获取 FRAGMENT_TAG 对应的 Fragment
        RequestManagerFragment current = (RequestManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);

        if (current == null) { // 【同学们注意：这样做的目的是为了  一个Activity或Fragment 只能有一个 空白的Fragment 对应】

            // 1.2 尝试从【记录保存】中获取 Fragment
            current = pendingRequestManagerFragments.get(fm);

            // 1.3 实例化 Fragment
            if (current == null) {

                // 1.3.1 创建对象 空白的Fragment
                current = new RequestManagerFragment();

                // 1.3.2 【记录保存】映射关系 进行保存
                //pendingRequestManagerFragments.put(fm, current);

                // 1.3.3 提交 Fragment 事务    同学们说：commitNow会抛出异常
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();

                // 1.3.4 post 一个消息
                handler.obtainMessage(ID_REMOVE_FRAGMENT_MANAGER, fm).sendToTarget();
            }
        }
        return current;
    }
}
