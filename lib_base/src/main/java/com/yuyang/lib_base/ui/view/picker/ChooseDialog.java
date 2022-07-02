package com.yuyang.lib_base.ui.view.picker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.R;
import com.yuyang.lib_base.bean.PopBean;
import com.yuyang.lib_base.utils.PixelUtils;

import java.util.List;

public class ChooseDialog extends Dialog {

    private RecyclerView recyclerView;

    public ChooseDialog(Context context) {
        super(context, R.style.Theme_AppCompat_Dialog);
        init();
    }

    private void init() {
        recyclerView = new RecyclerView(getContext());
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setContentView(recyclerView);
    }

    public static void show(View anchor, List<PopBean> beanList, OnItemChooseListener listener) {
        ChooseDialog.show(anchor, null, beanList, listener);
    }

    public static void show(View anchor, List<Drawable> drawableList, List<PopBean> beanList, OnItemChooseListener listener) {
        ChooseDialog chooseDialog = new ChooseDialog(anchor.getContext());
        chooseDialog.initDataAndShow(anchor, drawableList, beanList, listener);
    }

    public static void bind(final View anchor, final List<Drawable> drawableList, final List<PopBean> beanList, final OnItemChooseListener listener) {
        anchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(anchor, drawableList, beanList, listener);
            }
        });
    }

    private void initDataAndShow(View anchor, List<Drawable> drawableList, List<PopBean> beanList, OnItemChooseListener listener) {
        onItemChooseListener = listener;
        recyclerView.setAdapter(new MyAdapter(drawableList, beanList));

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.START | Gravity.TOP);

        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        lp.x = location[0];
        lp.y = location[1] - PixelUtils.dp2px(24) + anchor.getHeight();

        dialogWindow.setAttributes(lp);
        show();
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Drawable> drawableList;
        private List<PopBean> beanList;

        MyAdapter(List<Drawable> drawableList, List<PopBean> beanList) {
            this.drawableList = drawableList;
            this.beanList = beanList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(getContext()).inflate(R.layout.view_choose_dialog_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            MyHolder myHolder = (MyHolder) holder;

            if (drawableList != null && position < drawableList.size()) {
                myHolder.imageView.setImageDrawable(drawableList.get(position));
            }
            myHolder.textView.setText(beanList.get(position).getName());

            if (position == beanList.size() - 1) {
                myHolder.line.setVisibility(View.GONE);
            } else {
                myHolder.line.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return beanList == null ? 0 : beanList.size();
        }

        private class MyHolder extends RecyclerView.ViewHolder {

            private ImageView imageView;
            private TextView textView;
            private View line;

            public MyHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.view_choose_dialog_item_imageView);
                textView = itemView.findViewById(R.id.view_choose_dialog_item_textView);
                line = itemView.findViewById(R.id.view_choose_dialog_item_line);

                if (drawableList == null) {
                    imageView.setVisibility(View.GONE);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemChooseListener != null) {
                            onItemChooseListener.onChoose(getAdapterPosition(), beanList.get(getAdapterPosition()));
                        }
                        hide();
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
