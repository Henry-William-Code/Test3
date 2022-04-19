package com.qgnix.main.adapter;

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
import com.qgnix.common.custom.MyRadioButton;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.http.CommonHttpUtil;
import com.qgnix.common.utils.CommonIconUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.main.R;
import com.qgnix.live.bean.SearchUserBean;

import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 */

public class SearchAdapter extends RefreshAdapter<SearchUserBean> {

    private View.OnClickListener mFollowClickListener;
    private View.OnClickListener mClickListener;
    private int mFrom;
    private String mUid;

    public SearchAdapter(Context context, int from) {
        super(context);
        mFrom = from;
        mFollowClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                SearchUserBean bean = (SearchUserBean) tag;
                CommonHttpUtil.setAttention(bean.getId(), null);
            }
        };
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                SearchUserBean bean = (SearchUserBean) tag;
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, 0);
                }
            }
        };
        mUid = CommonAppConfig.getInstance().getUid();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_search, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position, List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    public void updateItem(String id, int attention) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        for (int i = 0, size = mList.size(); i < size; i++) {
            SearchUserBean bean = mList.get(i);
            if (bean != null && id.equals(bean.getId())) {
                bean.setAttention(attention);
                notifyItemChanged(i, Constants.PAYLOAD);
                break;
            }
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mSign;
        TextView tvLiveFlag;
        ImageView mSex;
        ImageView mLevelAnchor;
        ImageView mLevel;
        MyRadioButton mBtnFollow;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSign = itemView.findViewById(R.id.sign);
            tvLiveFlag = itemView.findViewById(R.id.tv_live_flag);
            mSex = itemView.findViewById(R.id.sex);
            mLevelAnchor = itemView.findViewById(R.id.level_anchor);
            mLevel = itemView.findViewById(R.id.level);
            mBtnFollow = itemView.findViewById(R.id.btn_follow);
            itemView.setOnClickListener(mClickListener);
            mBtnFollow.setOnClickListener(mFollowClickListener);
        }

        void setData(SearchUserBean bean, int position, Object payload) {
            itemView.setTag(bean);
            mBtnFollow.setTag(bean);
            tvLiveFlag.setVisibility(bean.getIslive()==1?View.VISIBLE:View.GONE);
            if (payload == null) {
                ImgLoader.displayAvatar(mContext,bean.getAvatar(), mAvatar);
                mName.setText(bean.getUserNiceName());
                mSign.setText(bean.getSignature());
                mSex.setImageResource(CommonIconUtil.getSexIcon(bean.getSex()));
                LevelBean anchorLevelBean = CommonAppConfig.getInstance().getAnchorLevel(bean.getLevelAnchor());
                if (anchorLevelBean != null) {
                    ImgLoader.display(mContext,anchorLevelBean.getThumb(), mLevelAnchor);
                }
                LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
                if (levelBean != null) {
                    ImgLoader.display(mContext,levelBean.getThumb(), mLevel);
                }
            }
            if (mUid.equals(bean.getId())) {
                if (mBtnFollow.getVisibility() == View.VISIBLE) {
                    mBtnFollow.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mBtnFollow.getVisibility() != View.VISIBLE) {
                    mBtnFollow.setVisibility(View.VISIBLE);
                }
                if (bean.getAttention() == 1) {
                    mBtnFollow.doChecked(true);
                    mBtnFollow.setText(R.string.following);
                } else {
                    mBtnFollow.doChecked(false);
                    mBtnFollow.setText(R.string.follow);
                }
            }
        }

    }
}
