package com.yuyang.messi.kotlinui.main.tools;

import android.graphics.Outline;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.room.entity.ModuleEntity;

import java.util.List;

public class CustomSetChildRecyclerAdapter extends BaseQuickAdapter<ModuleEntity, BaseViewHolder> {

    private final List<ModuleEntity> customBeanList;
    private boolean isEdit = false;

    public CustomSetChildRecyclerAdapter(List<ModuleEntity> customBeanList) {
        super(R.layout.activity_custem_set_item_child);
        this.customBeanList = customBeanList;
    }

    public void setEdit(boolean edit, boolean notify) {
        isEdit = edit;
        if (notify) {
            notifyDataSetChanged();
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ModuleEntity moduleEntity) {
        helper.setImageResource(R.id.ivIcon, moduleEntity.getIconRes())
                .setText(R.id.tvName, moduleEntity.getName())
                .setGone(R.id.tvTip, false)
                .addOnClickListener(R.id.viewEdit);

        if (isEdit) {
            helper.setGone(R.id.viewEdit, true)
                    .setBackgroundRes(R.id.viewEdit, customBeanList.contains(moduleEntity) ? R.drawable.custom_delete : R.drawable.custom_add);
        } else {
            helper.setGone(R.id.viewEdit, false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View icon = helper.getView(R.id.ivIcon);
            icon.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), PixelUtils.dp2px(5));
                }
            });
            icon.setClipToOutline(true);
        }
    }
}
