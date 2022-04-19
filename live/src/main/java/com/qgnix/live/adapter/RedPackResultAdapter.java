package com.qgnix.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.glide.ImgLoader;
import com.qgnix.live.R;
import com.qgnix.live.bean.RedPackResultBean;

import java.util.List;

/**
 * Created by cxf on 2018/11/21.
 */

public class RedPackResultAdapter extends RecyclerView.Adapter<RedPackResultAdapter.Vh> {

    private Context mContext;
    private List<RedPackResultBean> mList;
    private LayoutInflater mInflater;

    public RedPackResultAdapter(Context context, List<RedPackResultBean> list) {
        mContext=context;
        mList = list;
        mInflater = LayoutInflater.from(context);
    }



    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_red_pack_result, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mTime;
        TextView mWinCoin;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mTime = itemView.findViewById(R.id.time);
            mWinCoin = itemView.findViewById(R.id.win_coin);
        }

        void setData(RedPackResultBean bean) {
            ImgLoader.displayAvatar(mContext,bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            mTime.setText(bean.getTime());
            mWinCoin.setText(bean.getWinCoin());
        }
    }
}
