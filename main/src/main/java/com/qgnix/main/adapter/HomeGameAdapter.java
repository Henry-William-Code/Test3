package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.glide.ImgLoader;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.HomeGameBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页游戏适配器
 */
public class HomeGameAdapter extends BaseRecyclerViewAdapter<HomeGameAdapter.ViewHolder> {

    private List<HomeGameBean> mData;


    public HomeGameAdapter(Context context, List<HomeGameBean> mData) {
        super(context);
        this.mData = mData;
        if (this.mData == null) {
            this.mData = new ArrayList<>();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_home_hot_game);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HomeGameBean bean = mData.get(position);
        ImgLoader.display(mContext, bean.getIcon(), holder.imgIcon);
        holder.tvName.setText(bean.getTitle());
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        ImageView imgIcon;
        TextView tvName;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            imgIcon = (ImageView) findViewById(com.qgnix.live.R.id.img_icon);
            tvName = (TextView) findViewById(com.qgnix.live.R.id.tv_name);
        }
    }
}
