package com.qgnix.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qgnix.common.CommonAppConfig;
import com.qgnix.common.Constants;
import com.qgnix.common.interfaces.UserNumChangeCallback;
import com.qgnix.live.R;
import com.qgnix.common.bean.LevelBean;
import com.qgnix.live.bean.LiveUserGiftBean;
import com.qgnix.common.bean.UserBean;
import com.qgnix.common.glide.ImgLoader;
import com.qgnix.common.interfaces.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/10.
 */

public class LiveUserAdapter extends RecyclerView.Adapter<LiveUserAdapter.Vh> {

    private Context mContext;
    private List<LiveUserGiftBean> mList;
    private Integer visitorNum = 0; //关注数 有虚假观众 所以不能用 mList.size
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<UserBean> mOnItemClickListener;
    private UserNumChangeCallback userNumChangeCallback;


    public void setVisitorNum(Integer visitorNum) {
        this.visitorNum = visitorNum;
    }

    public LiveUserAdapter(Context context, UserNumChangeCallback userNumChangeCallback) {
        mContext = context;
        this.userNumChangeCallback = userNumChangeCallback;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
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

    public void setOnItemClickListener(OnItemClickListener<UserBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_user, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, int position) {

    }

    @Override
    public void onBindViewHolder(Vh vh, int position, List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        ImageView mIcon;
        ImageView mGuardIcon;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mIcon = itemView.findViewById(R.id.icon);
            mGuardIcon = itemView.findViewById(R.id.guard_icon);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveUserGiftBean userBean, int position, Object payload) {
            itemView.setTag(position);
            if (payload == null) {
                ImgLoader.displayAvatar(mContext, userBean.getAvatar(), mAvatar);
                LevelBean levelBean = CommonAppConfig.getInstance().getLevel(userBean.getLevel());
                if (levelBean != null) {
                    ImgLoader.display(mContext, levelBean.getThumb(), mIcon);
                }
            }
            int guardType = userBean.getGuardType();
            if (guardType == Constants.GUARD_TYPE_NONE) {
                if (mIcon.getVisibility() != View.VISIBLE) {
                    mIcon.setVisibility(View.VISIBLE);
                }
                if (mGuardIcon.getVisibility() == View.VISIBLE) {
                    mGuardIcon.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mIcon.getVisibility() == View.VISIBLE) {
                    mIcon.setVisibility(View.INVISIBLE);
                }
                if (mGuardIcon.getVisibility() != View.VISIBLE) {
                    mGuardIcon.setVisibility(View.VISIBLE);
                }
                if (guardType == Constants.GUARD_TYPE_MONTH) {
                    ImgLoader.display(mContext, R.mipmap.icon_guard_type_0, mGuardIcon);
                } else if (guardType == Constants.GUARD_TYPE_YEAR) {
                    ImgLoader.display(mContext, R.mipmap.icon_guard_type_1, mGuardIcon);
                }
            }
        }
    }

    public void refreshList(List<LiveUserGiftBean> list) {
        if (mList != null && list != null && list.size() > 0) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
            userNumChange();
        }
    }

    private int findItemPosition(String uid) {
        if (!TextUtils.isEmpty(uid)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                if (uid.equals(mList.get(i).getId())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void removeItem(String uid) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        int position = findItemPosition(uid);
        if (position >= 0) {
            mList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mList.size(), Constants.PAYLOAD);
            visitorNum --;
            userNumChange();
        }
    }

    public void insertItem(LiveUserGiftBean userBean) {
        if (userBean == null) {
            return;
        }
        int position = findItemPosition(userBean.getId());
        if (position >= 0) {
            return;
        }
        int size = mList.size();
        mList.add(userBean);

        notifyItemInserted(size);
        visitorNum ++;
        userNumChange();
    }

    public void insertList(List<LiveUserGiftBean> list) {
        if (mList != null && list != null && list.size() > 0) {
            int position = mList.size();
            mList.addAll(list);
            notifyItemRangeInserted(position, mList.size());
            visitorNum = visitorNum+list.size();
            userNumChange();
        }
    }

    /**
     * 守护信息发生变化
     */
    public void onGuardChanged(String uid, int guardType) {
        if (!TextUtils.isEmpty(uid)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                LiveUserGiftBean bean = mList.get(i);
                if (uid.equals(bean.getId())) {
                    if (bean.getGuardType() != guardType) {
                        bean.setGuardType(guardType);
                        notifyItemChanged(i, Constants.PAYLOAD);
                    }
                    break;
                }
            }
        }
    }

    public void clear() {
        if (mList != null) {
            mList.clear();
        }
        visitorNum = 0;
        userNumChange();
        notifyDataSetChanged();
    }

    public void userNumChange() {
        userNumChangeCallback.onChange(visitorNum);
    }
}
