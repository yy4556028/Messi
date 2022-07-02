package com.yuyang.messi.ui.interview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.flexbox.FlexboxLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.room.entity.ModuleEntity;

import java.util.List;

/**
 * @see FlexboxLayout
 */
public class InterviewAdapter extends BaseQuickAdapter<ModuleEntity, BaseViewHolder> {


    public InterviewAdapter(@Nullable List<ModuleEntity> data) {
        super(R.layout.activity_interview_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ModuleEntity item) {
        helper.setText(R.id.tvText, item.getName());
    }
}
