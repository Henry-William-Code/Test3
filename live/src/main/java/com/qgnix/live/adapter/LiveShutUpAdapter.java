package com.qgnix.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.adapter.RefreshAdapter;
import com.qgnix.common.bean.LevelBean;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.utils.CommonIconUtil;
import com.qgnix.live.R;
import com.qgnix.live.bean.LiveShutUpBean;

import java.util.List;

/**
 * Created by cxf on 2019/4/27.
 */

public class LiveShutUpAdapter extends RefreshAdapter<LiveShutUpBean> {

    private View.OnClickListener mOnClickListener;

    public LiveShutUpAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_shut_up, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position, List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    public void removeItem(String uid) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        int position = -1;
        for (int i = 0, size = mList.size(); i < size; i++) {
            if (uid.equals(mList.get(i).getUid())) {
                position = i;
                break;
            }
        }
        if (position >= 0) {
            mList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mList.size(), Constants.PAYLOAD);
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mSex;
        ImageView mLevel;
        View mBtnDel;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSex = itemView.findViewById(R.id.sex);
            mLevel = itemView.findViewById(R.id.level);
            mBtnDel = itemView.findViewById(R.id.btn_delete);
            mBtnDel.setOnClickListener(mOnClickListener);
        }

        void setData(LiveShutUpBean bean, int position, Object payload) {
            mBtnDel.setTag(position);
            if (payload == null) {
                ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
                mName.setText(bean.getUserNiceName());
                mSex.setImageResource(CommonIconUtil.getSexIcon(bean.getSex()));
                LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
                if (levelBean != null) {
                    ImgLoader.display(mContext, levelBean.getThumb(), mLevel);
                }
            }
        }
    }
}
