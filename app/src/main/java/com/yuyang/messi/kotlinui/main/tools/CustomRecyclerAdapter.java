package com.yuyang.messi.kotlinui.main.tools;

import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.room.entity.ModuleEntity;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ItemTouchHelper itemTouchHelper;
    public List<ModuleEntity> beanList = new ArrayList<>();
    private final List<ModuleEntity> startFixBeanList = new ArrayList<>();
    private final List<ModuleEntity> endFixBeanList = new ArrayList<>();
    private final boolean isSet;//是否在设置页面
    private boolean isEdit;

    public CustomRecyclerAdapter(Context context, ItemTouchHelper itemTouchHelper, boolean isSet) {
        this.context = context;
        this.itemTouchHelper = itemTouchHelper;
        this.isSet = isSet;
        if (!isSet) {
            addFixModule();
        }
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }

    public void setData(List<ModuleEntity> beanList) {
        this.beanList.clear();
        if (beanList != null) {
            this.beanList = beanList;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.activity_custem_set_item_child, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;

        if (isEdit) {
            myHolder.editView.setVisibility(View.VISIBLE);
            myHolder.editView.setBackgroundResource(R.drawable.custom_delete);
        } else {
            myHolder.editView.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myHolder.icon.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), PixelUtils.dp2px(5));

                }
            });
            myHolder.icon.setClipToOutline(true);
        }

        if (position < startFixBeanList.size()) {
            ModuleEntity moduleEntity = startFixBeanList.get(position);
            myHolder.icon.setImageResource(moduleEntity.getIconRes());
            myHolder.nameText.setText(moduleEntity.getName());

            if (moduleEntity.getBadgeCount() == 0) {
                myHolder.tipText.setVisibility(View.GONE);
            } else {
                myHolder.tipText.setVisibility(View.VISIBLE);
                int num = moduleEntity.getBadgeCount();
                myHolder.tipText.setText(String.valueOf(num > 99 ? "99+" : num));
            }
        } else if (position < startFixBeanList.size() + beanList.size()) {
            ModuleEntity moduleEntity = beanList.get(position - startFixBeanList.size());
            myHolder.icon.setImageResource(moduleEntity.getIconRes());
            myHolder.nameText.setText(moduleEntity.getName());

            if (moduleEntity.getBadgeCount() == 0) {
                myHolder.tipText.setVisibility(View.GONE);
            } else {
                myHolder.tipText.setVisibility(View.VISIBLE);
                int num = moduleEntity.getBadgeCount();
                myHolder.tipText.setText(String.valueOf(num > 99 ? "99+" : num));
            }
        } else if (position < startFixBeanList.size() + beanList.size() + endFixBeanList.size()) {
            ModuleEntity moduleEntity = endFixBeanList.get(position - startFixBeanList.size() - beanList.size());
            myHolder.icon.setImageResource(moduleEntity.getIconRes());
            myHolder.nameText.setText(moduleEntity.getName());

            if (moduleEntity.getBadgeCount() == 0) {
                myHolder.tipText.setVisibility(View.GONE);
            } else {
                myHolder.tipText.setVisibility(View.VISIBLE);
                int num = moduleEntity.getBadgeCount();
                myHolder.tipText.setText(String.valueOf(num > 99 ? "99+" : num));
            }
        } else if (position == startFixBeanList.size() + beanList.size() + endFixBeanList.size()) {
            myHolder.icon.setImageResource(R.mipmap.ic_launcher);
            myHolder.nameText.setText("更多");
            myHolder.tipText.setVisibility(View.GONE);
        } else {
            myHolder.icon.setImageDrawable(null);
            myHolder.nameText.setText(null);
            myHolder.tipText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (isSet) {
            return beanList.size();
        }
        int count = 0;
        count += startFixBeanList.size();
        count += beanList.size();
        count += endFixBeanList.size();
        count += 1;//更多
        if (count % 4 != 0) {//补齐4
            count += (4 - count % 4);
        }
        return count;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView nameText;
        private final TextView tipText;
        private final View editView;

        MyHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.ivIcon);
            nameText = itemView.findViewById(R.id.tvName);
            tipText = itemView.findViewById(R.id.tvTip);
            editView = itemView.findViewById(R.id.viewEdit);
            editView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    ModuleEntity moduleEntity = beanList.remove(index);
                    notifyItemRemoved(index);
                    if (listener != null) {
                        listener.onDelete(moduleEntity);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        ModuleEntity moduleEntity = null;
                        int adapterPosition = getAdapterPosition();
                        if (adapterPosition < 0) return;

                        if (adapterPosition < startFixBeanList.size()) {
                            moduleEntity = startFixBeanList.get(adapterPosition);
                            listener.onItemClick(v, moduleEntity);
                        } else if (adapterPosition < startFixBeanList.size() + beanList.size()) {
                            moduleEntity = beanList.get(adapterPosition - startFixBeanList.size());
                            listener.onItemClick(v, moduleEntity);
                        } else if (adapterPosition < startFixBeanList.size() + beanList.size() + endFixBeanList.size()) {
                            moduleEntity = endFixBeanList.get(adapterPosition - startFixBeanList.size() - beanList.size());
                            listener.onItemClick(v, moduleEntity);
                        } else if (adapterPosition == startFixBeanList.size() + beanList.size() + endFixBeanList.size()) {
                            listener.onItemClick(v, null);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (isSet && !isEdit) return false;
                    if (getAdapterPosition() < beanList.size() && itemTouchHelper != null) {
                        itemTouchHelper.startDrag(MyHolder.this);
                    }
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ModuleEntity moduleEntity);

        void onDelete(ModuleEntity moduleEntity);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private void addFixModule() {
        startFixBeanList.add(new ModuleEntity("固定头"));
        endFixBeanList.add(new ModuleEntity("固定尾"));
    }
}
