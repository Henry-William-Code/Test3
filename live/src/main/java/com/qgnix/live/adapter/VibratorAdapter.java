package com.qgnix.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lovense.sdklibrary.LovenseToy;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * 跳蛋设备daapter
 */
public class VibratorAdapter extends BaseRecyclerViewAdapter<VibratorAdapter.ViewHolder> {
    private List<LovenseToy> mData;


    public VibratorAdapter(Context context, List<LovenseToy> mData) {
        super(context);
        this.mData = mData;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_vibrator_layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LovenseToy top = mData.get(position);
        holder.name.setText(WordUtil.getString(R.string.device_name, top.getDeviceName()));
        holder.id.setText(WordUtil.getString(R.string.device_no, top.getUuid()));
        holder.rssi.setText(WordUtil.getString(R.string.device_rssi, top.getRssi()));
        holder.status.setText(top.getStatus() == 1 ? R.string.device_status_connected : R.string.device_status_not_connected);

    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        private TextView id;
        private TextView name;
        private TextView rssi;
        private TextView status;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            id = itemView.findViewById(R.id.tv_id);
            name = itemView.findViewById(R.id.tv_name);
            rssi = itemView.findViewById(R.id.tv_rssi);
            status = itemView.findViewById(R.id.tv_status);
        }
    }
}
