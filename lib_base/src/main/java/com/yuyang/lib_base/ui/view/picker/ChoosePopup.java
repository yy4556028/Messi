package com.yuyang.lib_base.ui.view.picker;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.R;
import com.yuyang.lib_base.bean.PopBean;
import com.yuyang.lib_base.recyclerview.item_decoration.LinearItemDecoration;

import java.util.List;

public class ChoosePopup {

    private Activity activity;
    private List<Drawable> drawableList;
    private List<PopBean> beanList;
    private Drawable bgDrawable;

    private PopupWindow popupWindow;

    private ChoosePopup(Activity activity) {
        this.activity = activity;
    }

    public static ChoosePopup with(Activity activity) {
        return new ChoosePopup(activity);
    }

    public ChoosePopup setData(List<Drawable> drawableList, List<PopBean> beanList) {
        if ((drawableList != null && drawableList.size() != beanList.size())) {
            drawableList = null;
        }
        this.drawableList = drawableList;
        this.beanList = beanList;
        return this;
    }

    public ChoosePopup setOnPopClickListener(OnItemChooseListener onItemChooseListener) {
        this.onItemChooseListener = onItemChooseListener;
        return this;
    }

    public ChoosePopup setBgDrawable(Drawable drawable) {
        this.bgDrawable = drawable;
        return this;
    }

    public ChoosePopup bindView(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.showAsDropDown(v);

//                    int[] location = new int[2];
//                    view.getLocationOnScreen(location);
//                    popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, location[1] + view.getHeight());
                }
            }
        });
        return this;
    }

    public PopupWindow build() {
        RecyclerView recyclerView = new RecyclerView(activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(new LinearItemDecoration(activity));
        recyclerView.setAdapter(new MyAdapter());

        popupWindow = new PopupWindow(
                recyclerView
                , LinearLayout.LayoutParams.WRAP_CONTENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);

        if (bgDrawable != null) {
            popupWindow.setBackgroundDrawable(bgDrawable);
        } else {
            popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
        }
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setClippingEnabled(false);
        return popupWindow;
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(View.inflate(activity, R.layout.view_choose_pop_item, null));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            if (drawableList != null) {
                viewHolder.icon.setImageDrawable(drawableList.get(position));
            }
            viewHolder.text.setText(beanList.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return beanList == null ? 0 : beanList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView icon;
            private TextView text;

            public ViewHolder(View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.view_choose_pop_item_image);
                text = itemView.findViewById(R.id.view_choose_pop_item_text);
                if (drawableList == null) {
                    icon.setVisibility(View.GONE);
                } else {
                    text.setVisibility(View.VISIBLE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemChooseListener != null) {
                            onItemChooseListener.onChoose(getAdapterPosition(), beanList.get(getAdapterPosition()));
                        }
                        popupWindow.dismiss();
                    }
                });
            }
        }
    }

    private OnItemChooseListener onItemChooseListener;

    public interface OnItemChooseListener {
        void onChoose(int pos, PopBean popBean);
    }
}