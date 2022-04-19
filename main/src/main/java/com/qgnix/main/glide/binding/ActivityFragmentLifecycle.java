package com.qgnix.main.glide.binding;



import com.qgnix.main.glide.binding.inter.Lifecycle;
import com.qgnix.main.glide.binding.inter.LifecycleListener;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

// 非Applicatoin作用域 绿色色领域  只有他有资格 搞一个空白的Fragment来监听
public class ActivityFragmentLifecycle implements Lifecycle {

    // 容器
    private final Set<LifecycleListener> lifecycleListeners =
            Collections.newSetFromMap(new WeakHashMap<LifecycleListener, Boolean>());
    private boolean isStarted;  // 启动的标记
    private boolean isDestroyed; // 销毁的标记

    @Override
    public void addListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);

        if (isDestroyed) {
            listener.onDestroy();
        } else if (isStarted) {
            listener.onStart();
        } else {
            listener.onStop();  // 首次启动：会默认 onStop 先停止   然后再onStart
        }
    }

    @Override
    public void removeListener(LifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }

    void onStart() {
        isStarted = true;
        for (LifecycleListener lifecycleListener : lifecycleListeners) {
            lifecycleListener.onStart();
        }
    }

    void onStop() {
        isStarted = false;
        for (LifecycleListener lifecycleListener : lifecycleListeners) {
            lifecycleListener.onStop();
        }
    }

    void onDestroy() {
        isDestroyed = true;
        for (LifecycleListener lifecycleListener : lifecycleListeners) {
            lifecycleListener.onDestroy();
        }
    }
}
