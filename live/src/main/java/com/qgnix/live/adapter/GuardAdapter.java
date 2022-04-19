package com.qgnix.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.adapter.RefreshAdapter;
import com.qgnix.common.bean.LevelBean;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.utils.CommonIconUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.bean.GuardUserBean;

/**
 * Created by cxf on 2018/11/6.
 */

public class GuardAdapter extends RefreshAdapter<GuardUserBean> {

    private static final int HEAD = 1;
    private static final int NORMAL = 0;
    private String mVotesName;
    private String mWeekContributeString;//本周贡献
    private boolean mDialog;

    public GuardAdapter(Context context, boolean dialog) {
        super(context);
        mDialog = dialog;
        mVotesName = CommonAppConfig.getInstance().getVotesName();
        mWeekContributeString = WordUtil.getString(R.string.guard_week_con);
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        }
        return NORMAL;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            return new HeadVh(mInflater.inflate(mDialog ? R.layout.guard_list_head : R.layout.guard_list_head_2, parent, false));
        }
        return new Vh(mInflater.inflate(mDialog ? R.layout.guard_list : R.layout.guard_list_2, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof HeadVh) {
            ((HeadVh) vh).setData(mList.get(position));
        } else {
            ((Vh) vh).setData(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class HeadVh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mSex;
        ImageView mLevel;
        TextView mVotes;


        public HeadVh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSex = itemView.findViewById(R.id.sex);
            mLevel = itemView.findViewById(R.id.level);
            mVotes = itemView.findViewById(R.id.votes);
        }

        void setData(GuardUserBean bean) {
            ImgLoader.displayAvatar(mContext,bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            mSex.setImageResource(CommonIconUtil.getSexIcon(bean.getSex()));
            LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
            if (levelBean != null) {
                ImgLoader.display(mContext,levelBean.getThumb(), mLevel);
            }
            mVotes.setText(Html.fromHtml(mWeekContributeString + "  <font color='#ffdd00'>" + bean.getContribute() + "</font>  " + mVotesName));
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mIcon;
        ImageView mAvatar;
        TextView mName;
        ImageView mSex;
        ImageView mLevel;
        TextView mVotes;


        public Vh(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.icon);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSex = itemView.findViewById(R.id.sex);
            mLevel = itemView.findViewById(R.id.level);
            mVotes = itemView.findViewById(R.id.votes);
        }

        void setData(GuardUserBean bean) {
            ImgLoader.displayAvatar(mContext,bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            mSex.setImageResource(CommonIconUtil.getSexIcon(bean.getSex()));
            LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
            if (levelBean != null) {
                ImgLoader.display(mContext,levelBean.getThumb(), mLevel);
            }
            mVotes.setText(bean.getContribute() + " " + mVotesName);
            if (bean.getType() == 1) {
                mIcon.setImageResource(R.mipmap.icon_guard_type_0);
            } else {
                mIcon.setImageResource(R.mipmap.icon_guard_type_1);
            }
        }
    }
}
