package com.yuyang.messi.ui.study;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yuyang.messi.R;
import com.yuyang.messi.room.entity.ModuleEntity;

import java.util.List;

public class EnglishAdapter extends BaseQuickAdapter<ModuleEntity, BaseViewHolder> {


    public EnglishAdapter(@Nullable List<ModuleEntity> data) {
        super(R.layout.activity_english_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ModuleEntity item) {
        helper.setText(R.id.tvText, item.getName());
    }
}
