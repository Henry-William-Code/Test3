package com.qgnix.live.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qgnix.common.interfaces.OnItemClickListener;
import com.qgnix.common.utils.ClickUtil;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;
import com.qgnix.live.bean.FollowUpBean;
import com.qgnix.live.bean.LiveChatBean;
import com.qgnix.live.lottery.entry.ConfirmTzBean;
import com.qgnix.live.utils.LiveTextRender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/10.
 */

public class LiveChatAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<LiveChatBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<LiveChatBean> mOnItemClickListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    /**
     * 彩票名称
     */
    private String ticketName;


    // 跟投回调
    private OnFollowUpListener mOnFollowUpListener;

    public void setOnFollowUpListener(OnFollowUpListener onFollowUpListener) {
        this.mOnFollowUpListener = onFollowUpListener;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public LiveChatAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    LiveChatBean bean = (LiveChatBean) tag;
                    if (bean.getType() != LiveChatBean.SYSTEM && mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean, 0);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<LiveChatBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LiveChatBean.RED_PACK) {
            return new RedPackVh(mInflater.inflate(R.layout.item_live_chat_red_pack, parent, false));
        } else if (viewType == LiveChatBean.FOLLOW_UP) {
            // 跟投
            return new FollowUpVh(mInflater.inflate(R.layout.item_live_follow_up, parent, false));
        } else if (viewType == LiveChatBean.WINNING) {
            // 中奖
            return new WiningVh(mInflater.inflate(R.layout.item_live_winning, parent, false));
        } else {
            return new Vh(mInflater.inflate(R.layout.item_live_chat, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        LiveChatBean liveChatBean = mList.get(position);
        if (vh instanceof Vh) {
            ((Vh) vh).setData(liveChatBean);
        } else if (vh instanceof RedPackVh) {
            ((RedPackVh) vh).setData(liveChatBean);
        } else if (vh instanceof FollowUpVh) {
            ((FollowUpVh) vh).setData(liveChatBean);
            ((FollowUpVh) vh).tvFollowUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnFollowUpListener && ClickUtil.canClick()) {
                        mOnFollowUpListener.onFollowUp(liveChatBean.getFollowUpBean().getTz());
                    }
                }
            });
        } else if (vh instanceof WiningVh) {
            ((WiningVh) vh).setData(liveChatBean);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    }

    class RedPackVh extends RecyclerView.ViewHolder {

        TextView mTextView;

        public RedPackVh(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }

        void setData(LiveChatBean bean) {
            mTextView.setText(bean.getContent());
        }
    }

    // 跟投
    class FollowUpVh extends RecyclerView.ViewHolder {

        TextView tvFollowUpMsg;
        TextView tvFollowUp;

        public FollowUpVh(View itemView) {
            super(itemView);
            tvFollowUpMsg = itemView.findViewById(R.id.tv_follow_up_msg);
            tvFollowUp = itemView.findViewById(R.id.tv_follow_up);
        }

        void setData(LiveChatBean bean) {
            FollowUpBean followUpBean = bean.getFollowUpBean();
            if (null == followUpBean) {
                tvFollowUpMsg.setText("");
            } else {
                tvFollowUpMsg.setText(WordUtil.getString(R.string.follow_up_msg_flag, followUpBean.getUsername(), ticketName, followUpBean.getMoney()));
            }
        }
    }

    // 中奖
    class WiningVh extends RecyclerView.ViewHolder {

        TextView tvWiningMsg;

        public WiningVh(View itemView) {
            super(itemView);
            tvWiningMsg = itemView.findViewById(R.id.tv_winning_msg);
        }

        void setData(LiveChatBean bean) {
            tvWiningMsg.setText(WordUtil.getString(R.string.winning_msg_flag, bean.getContent(), ticketName));
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        TextView mTextView;

        public Vh(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveChatBean bean) {
            itemView.setTag(bean);
            if (bean.getType() == LiveChatBean.SYSTEM) {
                mTextView.setTextColor(0xffffdd00);
                mTextView.setText(bean.getContent());
            } else {
                if (bean.getType() == LiveChatBean.ENTER_ROOM) {
                    mTextView.setTextColor(0xffc8c8c8);
                } else {
                    mTextView.setTextColor(0xffffffff);
                }
                LiveTextRender.render(mContext, mTextView, bean);
            }
        }
    }

    public void insertItem(LiveChatBean bean) {
        if (bean == null) {
            return;
        }
        int size = mList.size();
        mList.add(bean);
        notifyItemInserted(size);
        int lastItemPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
        if (lastItemPosition != size - 1) {
            mRecyclerView.smoothScrollToPosition(size);
        } else {
            mRecyclerView.scrollToPosition(size);
        }
    }

    public void scrollToBottom() {
        if (mList.size() > 0) {
            mRecyclerView.smoothScrollToPosition(mList.size() - 1);
        }
    }

    public void clear() {
        if (mList != null) {
            mList.clear();
        }
        notifyDataSetChanged();
    }


    /**
     * 跟投回调
     */
    public interface OnFollowUpListener {
        void onFollowUp(List<ConfirmTzBean> tz);
    }
}
