package com.qgnix.main.glide.binding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;


import com.qgnix.main.glide.RequestManager;


// andrid x  的 空白的Fragment  监听生命周期变化的
public class SupportRequestManagerFragment extends Fragment {

    private static final String TAG = "SupportRMFragment";
    private final ActivityFragmentLifecycle lifecycle;

    private RequestManager requestManager;

    public SupportRequestManagerFragment() {
        this(new ActivityFragmentLifecycle());
    }

    @VisibleForTesting
    @SuppressLint("ValidFragment")
    public SupportRequestManagerFragment(ActivityFragmentLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void setRequestManager( RequestManager requestManager) {
        this.requestManager = requestManager;
    }


    public ActivityFragmentLifecycle getGlideLifecycle() {
        return lifecycle;
    }


    public RequestManager getRequestManager() {
        return requestManager;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // this.lifecycle.addListener(requestManager);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycle.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycle.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.onDestroy();
    }
}
