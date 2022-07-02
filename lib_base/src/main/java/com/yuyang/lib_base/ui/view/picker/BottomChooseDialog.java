package com.yuyang.lib_base.ui.view.picker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.yuyang.lib_base.R;
import com.yuyang.lib_base.bean.PopBean;
import com.yuyang.lib_base.ui.view.BottomDialog;

import java.util.List;

public class BottomChooseDialog {

    public static void bindSingle(final Activity activity,
                                  final View chickView,
                                  final String title,
                                  final List<PopBean> beanList,
                                  final SingleChoiceListener listener) {

        chickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingle(activity, title, beanList, listener);
            }
        });
    }

    public static void bindMulti(final Activity activity,
                                 final View chickView,
                                 final String title,
                                 final List<PopBean> beanList,
                                 final MultiChoiceListener listener) {

        chickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMulti(activity, title, beanList, listener);
            }
        });
    }

    public static void showSingle(final Activity activity,
                                  final String title,
                                  final List<PopBean> beanList,
                                  final SingleChoiceListener listener) {

        showSingle(activity, title, beanList, listener, false);
    }

    public static void showSingle(final Activity activity,
                                  final String title,
                                  final List<PopBean> beanList,
                                  final SingleChoiceListener listener,
                                  boolean showCheckBox) {

        final View view = activity.getLayoutInflater().inflate(R.layout.view_bottom_dialog, null);
        final BottomDialog bottomDialog = new BottomDialog(activity, view);

        view.findViewById(R.id.view_bottom_dialog_cancelText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });

        ListView listView = view.findViewById(R.id.view_bottom_dialog_listView);

        if (!TextUtils.isEmpty(title)) {
            View header = activity.getLayoutInflater().inflate(R.layout.view_bottom_dialog_header, null);
            TextView titleText = header.findViewById(R.id.view_bottom_dialog_header_titleText);
            titleText.setText(title);
            listView.addHeaderView(header);
        }

        listView.setAdapter(new MyAdapter(beanList, showCheckBox));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                position -= ((ListView) adapterView).getHeaderViewsCount();
                if (position >= 0) {
                    listener.onItemClick(position, beanList.get(position));
                    bottomDialog.dismiss();
                }
            }
        });
        bottomDialog.show();
    }

    public static void showMulti(final Activity activity,
                                 final String title,
                                 final List<PopBean> beanList,
                                 final MultiChoiceListener listener) {

        final View view = activity.getLayoutInflater().inflate(R.layout.view_bottom_dialog, null);
        final BottomDialog bottomDialog = new BottomDialog(activity, view);

        view.findViewById(R.id.view_bottom_dialog_cancelText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });
        view.findViewById(R.id.view_bottom_dialog_centerLine).setVisibility(View.VISIBLE);
        View confirmText = view.findViewById(R.id.view_bottom_dialog_confirmText);
        confirmText.setVisibility(View.VISIBLE);
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onConfirm(beanList);
                bottomDialog.dismiss();
            }
        });

        ListView listView = view.findViewById(R.id.view_bottom_dialog_listView);

        if (!TextUtils.isEmpty(title)) {
            View header = activity.getLayoutInflater().inflate(R.layout.view_bottom_dialog_header, null);
            TextView titleText = header.findViewById(R.id.view_bottom_dialog_header_titleText);
            titleText.setText(title);
            listView.addHeaderView(header);
        }
        final MyAdapter myAdapter = new MyAdapter(beanList, true);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                position -= ((ListView) adapterView).getHeaderViewsCount();
                if (position >= 0) {
                    beanList.get(position).setCheck(!beanList.get(position).isCheck());
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
        bottomDialog.show();
    }

    private static class MyAdapter extends BaseAdapter {

        private List<PopBean> beanList;
        private boolean showCheckBox;

        public MyAdapter(List<PopBean> beanList, boolean showCheckBox) {
            this.beanList = beanList;
            this.showCheckBox = showCheckBox;
        }

        @Override
        public int getCount() {
            return beanList == null ? 0 : beanList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") View view = View.inflate(parent.getContext(), R.layout.view_bottom_dialog_item, null);
            AppCompatCheckBox checkBox = view.findViewById(R.id.view_bottom_dialog_item_checkBox);
            TextView textView = view.findViewById(R.id.view_bottom_dialog_item_textView);
            checkBox.setVisibility(showCheckBox ? View.VISIBLE : View.GONE);
            checkBox.setChecked(beanList.get(position).isCheck());
            textView.setText(beanList.get(position).getName());
            return view;
        }
    }

    public interface SingleChoiceListener {
        void onItemClick(int index, PopBean popBean);
    }

    public interface MultiChoiceListener {
        void onConfirm(List<PopBean> allBeanList);
    }
}
