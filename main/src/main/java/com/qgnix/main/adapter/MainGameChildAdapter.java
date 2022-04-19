package com.qgnix.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.common.glide.ImgLoader;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.GameChildEntry;

import java.util.List;

/**
 * 游戏子类适配器
 */
public class MainGameChildAdapter extends BaseRecyclerViewAdapter<MainGameChildAdapter.ViewHolder> {

    private final List<GameChildEntry> list;

    public MainGameChildAdapter(Context context, List<GameChildEntry> list) {
        super(context);
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_game_child);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GameChildEntry entry = list.get(position);
        ImgLoader.display(mContext, TextUtils.isEmpty(entry.getImage()) ? entry.getIcon() : entry.getImage(), R.mipmap.ic_logo, holder.imageLogo);
        if (!TextUtils.isEmpty(entry.getName())) {
            holder.tvTitle.setText(entry.getName());
        } else {
            holder.tvTitle.setText(entry.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        ImageView imageLogo;
        TextView tvTitle;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            imageLogo = (ImageView) findViewById(R.id.image_logo);
            tvTitle = (TextView) findViewById(R.id.t_title);
        }
    }
}
