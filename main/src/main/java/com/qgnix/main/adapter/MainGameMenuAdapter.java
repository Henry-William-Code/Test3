package com.qgnix.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.main.R;
import com.qgnix.main.bean.GameMenuEntry;

import java.util.List;

/**
 * 首页游戏菜单
 */
public class MainGameMenuAdapter extends BaseRecyclerViewAdapter<MainGameMenuAdapter.ViewHolder> {

    private final List<GameMenuEntry> lists;

    public MainGameMenuAdapter(Context context, List<GameMenuEntry> lists) {
        super(context);
        this.lists = lists;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_main_game_menu);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        GameMenuEntry entry = lists.get(position);
        holder.tv_menu_title.setText(entry.getTitle());
        holder.tv_menu_title.setSelected(entry.isSelect());
        holder.view_line.setVisibility(entry.isSelect() ? View.VISIBLE : View.GONE);
        holder.tv_menu_title.setTextColor(entry.isSelect() ? Color.parseColor("#dec352") : Color.parseColor("#727480"));
    }

    @Override
    public int getItemCount() {
        return lists == null ? 0 : lists.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        TextView tv_menu_title;
        View view_line;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tv_menu_title = (TextView) findViewById(R.id.tv_menu_title);
            view_line = findViewById(R.id.view_line);
        }
    }

}