package com.qgnix.main.adapter;

public interface IViewHolderConvert<T> {
    void convert(ViewHolder holder, T t, int position);
}
