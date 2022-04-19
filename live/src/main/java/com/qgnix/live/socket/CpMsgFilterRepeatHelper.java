package com.qgnix.live.socket;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CpMsgFilterRepeatHelper {
    private int mMaxSize = 0;
    private List<String> cache = new ArrayList<>();

    public CpMsgFilterRepeatHelper(int maxSize) {
        this.mMaxSize = maxSize;
    }

    /**
     * 过滤重复的
     * @param obj
     * @return 过滤掉了重复的就返回true,不重复就返回false
     */
    public boolean filterRepeat(JSONObject obj){
        String uuid = obj.getString("uuid");
        if(cache.contains(uuid)){//根据uuid进行判断是否已存在，已存在就过滤掉
            return true;
        }
        if (cache.size() == mMaxSize) {
            cache.remove(0);
        }
        cache.add(uuid);//将不存在的消息加入去重过滤器
        return false;
    }
}
