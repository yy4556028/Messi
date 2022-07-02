package com.yuyang.messi.kotlinui.main.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.yuyang.lib_base.ui.view.CommonDialog;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.ModuleGroupBean;
import com.yuyang.messi.room.database.ModuleDatabase;
import com.yuyang.messi.room.entity.ModuleEntity;

import java.util.List;

public class CustomSetBottomRecyclerAdapter extends BaseQuickAdapter<ModuleGroupBean, BaseViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private List<ModuleEntity> customBeanList;
    private int recyclerViewHeight;
    private int headerHeight;

    private boolean isEdit;

    public CustomSetBottomRecyclerAdapter(List<ModuleGroupBean> groupBeanList) {
        super(groupBeanList);
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (recyclerViewHeight == 0) recyclerViewHeight = parent.getMeasuredHeight();
        return new BaseViewHolder(new RecyclerView(mContext));
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }

    public void setData(List<ModuleEntity> customBeanList, List<ModuleGroupBean> groupBeanList) {
        this.customBeanList = customBeanList;
        setNewData(groupBeanList);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ModuleGroupBean groupBean) {
        /**
         * 为了最后一组 滑倒底部时，header顶到顶部
         */
        if (helper.getAdapterPosition() == getItemCount() - 1) {
            helper.itemView.setMinimumHeight(recyclerViewHeight - headerHeight);
        } else {
            helper.itemView.setMinimumHeight(0);
        }

        RecyclerView recyclerView = (RecyclerView) helper.itemView;
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        CustomSetChildRecyclerAdapter recyclerAdapter;
        if (recyclerView.getTag() == null) {
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
            recyclerView.setAdapter(recyclerAdapter = new CustomSetChildRecyclerAdapter(customBeanList));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ModuleTool.dealModuleClick(mContext, recyclerAdapter.getItem(position));
                }
            });
            recyclerAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                    if ("网址".equals(groupBean.getGroupName())) {
                        CommonDialog commonDialog = new CommonDialog(mContext);
                        commonDialog.show();
                        commonDialog.setTitle(null);
                        commonDialog.setSubtitle("确定删除该网址?\n" + recyclerAdapter.getItem(position).getNetUrl());
                        commonDialog.setBtnText("取消", "确定");
                        commonDialog.setOnBtnRightClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                commonDialog.dismiss();
                                ModuleDatabase.getInstance().getModuleDao().deleteModule(recyclerAdapter.getItem(position));
                            }
                        });
                    }
                    return true;
                }
            });
            recyclerAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    if (view.getId() == R.id.viewEdit) {
                        ModuleEntity moduleEntity = recyclerAdapter.getItem(position);

                        if (itemListener != null) {
                            if (customBeanList.contains(moduleEntity)) {
                                itemListener.onDelete(moduleEntity);
                            } else {
                                itemListener.onAdd(moduleEntity);
                            }
                        }
                        recyclerAdapter.notifyItemChanged(position);
                    }
                }
            });
            recyclerView.setTag(recyclerAdapter);
        } else {
            recyclerAdapter = (CustomSetChildRecyclerAdapter) recyclerView.getTag();
        }
        recyclerAdapter.setEdit(isEdit, false);
        recyclerAdapter.setNewData(getItem(helper.getAdapterPosition()).getItemList());
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).getGroupName().hashCode();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_custom_set_item_title, viewGroup, false);
        if (headerHeight == 0) {
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            headerHeight = view.getMeasuredHeight();
        }
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        TextView textView = viewHolder.itemView.findViewById(R.id.activity_custom_set_item_title_titleText);
        View line = viewHolder.itemView.findViewById(R.id.activity_custom_set_item_title_line);
        textView.setText(getItem(position).getGroupName());
        if (position == 0) {
            line.setVisibility(View.GONE);
        } else {
            line.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemRealCount() {
        return getItemCount();
    }

    public interface OnItemListener {

        void onAdd(ModuleEntity moduleEntity);

        void onDelete(ModuleEntity moduleEntity);
    }

    private OnItemListener itemListener;

    public void setOnItemListener(OnItemListener itemListener) {
        this.itemListener = itemListener;
    }
}
