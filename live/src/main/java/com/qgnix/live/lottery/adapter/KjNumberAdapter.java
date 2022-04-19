package com.qgnix.live.lottery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qgnix.live.R;
import com.qgnix.live.bean.CpTypeEnum;
import com.qgnix.live.lottery.base.BaseRecyclerViewAdapter;
import com.qgnix.live.utils.CpUtils;

import java.util.List;

/**
 * 开奖号码适配器
 */
public class KjNumberAdapter extends BaseRecyclerViewAdapter<KjNumberAdapter.ViewHolder> {

    private final List<String> list;
    /**
     * 彩票来袭
     */
    private String mCpType;
    /**
     * 是否文本显示黑色
     */
    private boolean textColorBlank;
    /**
     * 是否圆形
     */
    private boolean isCircle;
    /**
     * 是否显示4d  a b c d
     */
    private boolean isShow4DTag;

    public KjNumberAdapter(Context context, List<String> list, String cpType) {
        super(context);
        this.list = list;
        mCpType = cpType;
    }

    /**
     * 设置彩票类型
     *
     * @param cpType
     */
    public void setCpType(String cpType) {
        this.mCpType = cpType;
    }

    public void setCircle(boolean circle) {
        isCircle = circle;
    }

    public void setTextColorBlank(boolean textColorBlank) {
        this.textColorBlank = textColorBlank;
    }

    public void setShow4DTag(boolean show4DTag) {
        isShow4DTag = show4DTag;
    }


    @Override
    public KjNumberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_kj_number);
    }

    @Override
    public void onBindViewHolder(KjNumberAdapter.ViewHolder holder, int position) {
        String str = list.get(position);
        // 快三
        if (CpTypeEnum.KS.name().equals(mCpType)) {
            holder.tvNumber.setVisibility(View.GONE);
            holder.imgSieve.setVisibility(View.VISIBLE);
            if ("+".equals(str) || "=".equals(str)) {
                holder.tvNumber.setVisibility(View.VISIBLE);
                holder.imgSieve.setVisibility(View.GONE);

                holder.tvNumber.setText(str);
                holder.tvNumber.setTextColor(getContext().getResources().getColor(textColorBlank ? R.color.black : R.color.white));
            } else if (position == list.size() - 1) {
                holder.tvNumber.setVisibility(View.VISIBLE);
                holder.imgSieve.setVisibility(View.GONE);
                ViewGroup.LayoutParams params= holder.tvNumber.getLayoutParams();
                params.width=ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.tvNumber.setLayoutParams(params);
                holder.tvNumber.setText(str);
                holder.tvNumber.setTextColor(getContext().getResources().getColor(textColorBlank ? R.color.black : R.color.white));
            } else {
                holder.tvNumber.setVisibility(View.GONE);
                holder.imgSieve.setVisibility(View.VISIBLE);

                holder.imgSieve.setImageResource(getImgSieve(str));
            }
        } else {
            if (isShow4DTag && "4D".equals(mCpType)) {
                holder.tvTag.setVisibility(View.VISIBLE);
                holder.tvTag.setText(String.valueOf((char) (65 + position)));
            }
            holder.tvNumber.setVisibility(View.VISIBLE);
            holder.imgSieve.setVisibility(View.GONE);

            GradientDrawable drawable = new GradientDrawable();
            if (isCircle) {
                int wh = (int) mContext.getResources().getDimension(R.dimen.x55);
                holder.tvNumber.getLayoutParams().width = wh;
                holder.tvNumber.getLayoutParams().height = wh;
                drawable.setShape(GradientDrawable.OVAL);
                int size = (int) mContext.getResources().getDimension(R.dimen.x40);
                drawable.setSize(size, size);
            } else {
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setCornerRadius((int) mContext.getResources().getDimension(R.dimen.x10));
            }

            if ("+".equals(str) || "=".equals(str)) {
                holder.tvNumber.setText(str);
                holder.tvNumber.setTextColor(getContext().getResources().getColor(textColorBlank ? R.color.black : R.color.white));
                drawable.setColor(Color.parseColor("#00000000"));
            } else {
                holder.tvNumber.setTextColor(getContext().getResources().getColor(R.color.white));
                drawable.setColor(Color.parseColor(CpUtils.getDrawNoBgColor(mCpType, str)));
                holder.tvNumber.setText(CpUtils.formatZeroDrawNum(mCpType, str));
            }
            holder.tvNumber.setBackground(drawable);
        }


    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        TextView tvTag;
        TextView tvNumber;
        ImageView imgSieve;

        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            tvTag = (TextView) findViewById(R.id.tv_tag);
            tvNumber = (TextView) findViewById(R.id.tv_number);
            imgSieve = (ImageView) findViewById(R.id.img_sieve);
        }
    }

    public int getImgSieve(String number) {
        int resourse = 0;
        switch (number) {
            case "1":
                resourse = R.mipmap.his_ks_01;
                break;
            case "2":
                resourse = R.mipmap.his_ks_02;
                break;
            case "3":
                resourse = R.mipmap.his_ks_03;
                break;
            case "4":
                resourse = R.mipmap.his_ks_04;
                break;
            case "5":
                resourse = R.mipmap.his_ks_05;
                break;
            case "6":
                resourse = R.mipmap.his_ks_06;
                break;
        }
        return resourse;
    }
}
