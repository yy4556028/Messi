package com.yamap.lib_chat.keyboard.interfaces;

import android.view.ViewGroup;

import com.yamap.lib_chat.keyboard.adpater.EmoticonAdapter;

public interface EmoticonDisplayListener<T> {

    void onBindView(int position, ViewGroup parent, EmoticonAdapter.ViewHolder viewHolder, T t, boolean isDelBtn);
}
