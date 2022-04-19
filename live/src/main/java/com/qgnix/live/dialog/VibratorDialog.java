package com.qgnix.live.dialog;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.lovense.sdklibrary.Lovense;
import com.lovense.sdklibrary.LovenseToy;
import com.lovense.sdklibrary.callBack.LovenseError;
import com.lovense.sdklibrary.callBack.OnErrorListener;
import com.lovense.sdklibrary.callBack.OnSearchToyListener;
import com.qgnix.common.custom.ItemDecoration;
import com.qgnix.common.utils.L;
import com.qgnix.common.utils.ToastUtil;
import com.qgnix.live.R;
import com.qgnix.live.adapter.VibratorAdapter;
import com.qgnix.live.interfaces.OnVibratorClickListener;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 跳蛋adapter
 */
public class VibratorDialog extends BaseCustomDialog implements View.OnClickListener {

    /**
     * 跳蛋设备
     */
    private final List<LovenseToy> mLovenseToys = new ArrayList<>();

    private final Application mApplication;
    private final Context mContext;


    private VibratorAdapter mAdapter;
    /**
     * 暂无数据
     */
    private TextView mEmptyData;
    private RecyclerView mRvVibrator;

    private OnVibratorClickListener mOnVibratorClickListener;

    public void setOnVibratorClickListener(OnVibratorClickListener onVibratorClickListener) {
        this.mOnVibratorClickListener = onVibratorClickListener;
    }

    public VibratorDialog(Context context, Application application) {
        super(context);
        this.mApplication = application;
        this.mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_vibrator;
    }

    @Override
    public void initView() {
        findViewById(R.id.start_scan).setOnClickListener(this);
        findViewById(R.id.stop_scan).setOnClickListener(this);

        mRvVibrator = findViewById(R.id.rv_vibrator);
        mEmptyData = findViewById(R.id.tv_empty_data);

        mRvVibrator.addItemDecoration(new ItemDecoration(mContext));
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mRvVibrator.setLayoutManager(manager);
        mAdapter = new VibratorAdapter(mContext, mLovenseToys);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                LovenseToy top = mLovenseToys.get(position);
                mOnVibratorClickListener.onVibratorClick(top);

            }
        });
        mRvVibrator.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        // 本地设备
        List<LovenseToy> localLovenseToys = Lovense.getInstance(mApplication).listToys(new OnErrorListener() {
            @Override
            public void onError(LovenseError error) {
                L.e("==查找保存本地已保存设备失败==" + error.toString());
            }
        });
        if (null != localLovenseToys && !localLovenseToys.isEmpty()) {
            mLovenseToys.addAll(localLovenseToys);
            mEmptyData.setVisibility(View.GONE);
            mRvVibrator.setVisibility(View.VISIBLE);
            //刷新适配器
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public int showGravity() {
        return Gravity.BOTTOM;
    }


    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.start_scan) {
            //查找
            scanToys();
        } else if (vId == R.id.stop_scan) {
            // 停止查找
            Lovense.getInstance(mApplication).stopSearching();
        }
    }

    /**
     * 查找跳蛋设备
     */
    public void scanToys() {
        Lovense.getInstance(mApplication).searchToys(new OnSearchToyListener() {
            @Override
            public void onSearchToy(LovenseToy lovenseToy) {
                if (null == lovenseToy) {
                    return;
                }
                if (isTopAdded(lovenseToy)) {
                    return;
                }
                mLovenseToys.add(lovenseToy);
                if (mRvVibrator.getVisibility() == View.GONE) {
                    mEmptyData.setVisibility(View.GONE);
                    mRvVibrator.setVisibility(View.VISIBLE);
                }
                // 刷新adapter
                mAdapter.notifyDataSetChanged();
            } // Find toys

            @Override
            public void finishSearch() {
                // 设备保存本地
                Lovense.getInstance(mApplication).saveToys(mLovenseToys, new OnErrorListener() {
                    @Override
                    public void onError(LovenseError error) {
                        L.e("==设备保存本地失败==" + error.toString());
                    }
                });
            }  // Scan finish

            @Override
            public void onError(LovenseError msg) {
                L.e("==查找设备失败==" + msg.toString());
                ToastUtil.show(msg.getMessage());
            } // error

        });
    }


    /**
     * 判断是否已添加
     *
     * @param lovenseToy
     * @return
     */
    private boolean isTopAdded(LovenseToy lovenseToy) {
        for (LovenseToy t : mLovenseToys) {
            String id = t.getToyId();
            if (!TextUtils.isEmpty(id) && id.equals(lovenseToy.getToyId())) {
                return true;
            }
        }
        return false;
    }
}
