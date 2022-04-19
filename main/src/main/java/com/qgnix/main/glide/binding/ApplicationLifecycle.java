package com.qgnix.main.glide.binding;



import com.qgnix.main.glide.binding.inter.Lifecycle;
import com.qgnix.main.glide.binding.inter.LifecycleListener;


// Applicatoin作用域 红色领域
public class ApplicationLifecycle implements Lifecycle {

    @Override
    public void addListener(LifecycleListener listener) {
        /**
         * APP起来了，APP起来的时候，执行onStart
         */
        /**
         * 子线程中，在实例化 RequestManager的时候，会执行 this.lifecycle.addListener(this);
         * 此时，由于是子线程，无法搞一个空白的Fragment覆盖上去，也意味着无法监听 Activity/Fragment
         * 所以 只能是在addListener时，手动的onStart了
         */
        listener.onStart();
    }

    @Override
    public void removeListener(LifecycleListener listener) {
        // 我也没法做事情，APP死亡  我就跟随

        // Do nothing.

        /**
         * 子线程中，由于无法搞一个空白的Fragment覆盖上去，也意味着无法监听 Activity/Fragment
         *  那么只能是属于 全局大范围的跟随App进程的灭亡，而灭亡了，所以啥事也不干了...
         */
    }
}
