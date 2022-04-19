package com.qgnix.live.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qgnix.live.view.RecycleHolder;

import java.util.List;

/**
 * Created by Simple on 2016/9/7.
 */

public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<RecycleHolder> {

    private List<T> mDatas;
    private int mLayoutId;
    private LayoutInflater mInflater;

    private OnItemClickListener onItemClickListener;

    public RecyclerAdapter(Context mContext, List<T> mDatas, int mLayoutId) {
        this.mDatas = mDatas;
        this.mLayoutId = mLayoutId;
        if (mContext != null) {
            mInflater = LayoutInflater.from(mContext);
        }
    }

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //这里是创建ViewHolder的地方，RecyclerAdapter内部已经实现了ViewHolder的重用
        //这里我们直接new就好了
        View view = mInflater.inflate(mLayoutId, parent, false);

        return new RecycleHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener i) {
        this.onItemClickListener = i;
    }

    @Override
    public void onBindViewHolder(final RecycleHolder holder, int position) {
        if (onItemClickListener != null) {
            //设置背景
            holder.itemView.setOnClickListener(new NoMoreClickListener() {
                @Override
                protected void onMoreClick(View view) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱
                    onItemClickListener.onItemClickListener(holder.itemView, holder.getLayoutPosition());
                }
            });
        }
        convert(holder, mDatas.get(position), position);
    }

    public abstract void convert(RecycleHolder holder, T data, int position);

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    /**
     * 自定义RecyclerView item的点击事件的点击事件
     */
    public interface OnItemClickListener {

        /**
         * 点击事件
         *
         * @param view
         * @param position
         */
        void onItemClickListener(View view, int position);
    }

    public void move(int fromPosition, int toPosition) {
        T prev = mDatas.remove(fromPosition);
        mDatas.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

}
