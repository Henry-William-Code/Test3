package com.qgnix.live.bean;

import com.qgnix.common.Constants;
import com.qgnix.common.utils.WordUtil;
import com.qgnix.live.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/8.
 * 直播房间类型
 */

public class LiveRoomTypeBean {

    private int mId;
    private String mName;
    private int nameResId;
    private int mCheckedIcon;
    private int mUnCheckedIcon;
    private boolean mChecked;

    public LiveRoomTypeBean() {
    }

    public LiveRoomTypeBean(int id, String name) {
        mId = id;
        mName = name;
    }

    public LiveRoomTypeBean(int id, String name, int checkedIcon, int unCheckedIcon) {
        mId = id;
        mName = name;
        mCheckedIcon = checkedIcon;
        mUnCheckedIcon = unCheckedIcon;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getNameResId() {
        return nameResId;
    }

    public void setNameResId(int nameResId) {
        this.nameResId = nameResId;
    }

    public int getCheckedIcon() {
        return mCheckedIcon;
    }

    public void setCheckedIcon(int checkedIcon) {
        mCheckedIcon = checkedIcon;
    }

    public int getUnCheckedIcon() {
        return mUnCheckedIcon;
    }

    public void setUnCheckedIcon(int unCheckedIcon) {
        mUnCheckedIcon = unCheckedIcon;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public static List<LiveRoomTypeBean> getLiveTypeList(String[][] liveTypes) {
        List<LiveRoomTypeBean> list = new ArrayList<>();
        if (liveTypes != null) {
            for (String[] arr : liveTypes) {
                LiveRoomTypeBean bean = new LiveRoomTypeBean(Integer.parseInt(arr[0]), arr[1]);
                switch (bean.getId()) {
                    case Constants.LIVE_TYPE_NORMAL:
                        bean.setCheckedIcon(R.mipmap.icon_live_type_normal_1);
                        bean.setUnCheckedIcon(R.mipmap.icon_live_type_normal_2);
                        bean.setNameResId(R.string.main_live_type_normal);
                        bean.setName(WordUtil.getString(R.string.main_live_type_normal));
                        break;
                    case Constants.LIVE_TYPE_PWD:
                        bean.setCheckedIcon(R.mipmap.icon_live_type_pwd_1);
                        bean.setUnCheckedIcon(R.mipmap.icon_live_type_pwd_2);
                        bean.setNameResId(R.string.main_live_type_pwd);
                        bean.setName(WordUtil.getString(R.string.main_live_type_pwd));
                        break;
                    case Constants.LIVE_TYPE_PAY:
                        bean.setCheckedIcon(R.mipmap.icon_live_type_pay_1);
                        bean.setUnCheckedIcon(R.mipmap.icon_live_type_pay_2);
                        bean.setNameResId(R.string.main_live_type_pay);
                        bean.setName(WordUtil.getString(R.string.main_live_type_pay));
                        break;
                    case Constants.LIVE_TYPE_TIME:
                        bean.setCheckedIcon(R.mipmap.icon_live_type_time_1);
                        bean.setUnCheckedIcon(R.mipmap.icon_live_type_time_2);
                        bean.setNameResId(R.string.main_live_type_time);
                        bean.setName(WordUtil.getString(R.string.main_live_type_time));
                        break;
                }
                list.add(bean);
            }
        }
        return list;
    }
}
