package com.yamap.lib_chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.yamap.lib_chat.events.ChatKeyboardFuncClickEvent;
import com.yamap.lib_chat.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class FuncGridView extends GridView {

    public FuncGridView(Context context) {
        this(context, null);
    }

    public FuncGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setNumColumns(4);
        setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        setGravity(Gravity.CENTER_VERTICAL);
        setVerticalSpacing((int) CommonUtil.dp2px(30));
        setPadding(0, (int) CommonUtil.dp2px(40), 0, 0);

        ArrayList<FuncBean> funcBeanList = new ArrayList<>();
        funcBeanList.add(new FuncBean(R.mipmap.func_photo, getContext().getString(R.string.func_photo)));
        funcBeanList.add(new FuncBean(R.mipmap.func_camera, getContext().getString(R.string.func_capture)));
        funcBeanList.add(new FuncBean(R.mipmap.func_audio, getContext().getString(R.string.func_video)));
        funcBeanList.add(new FuncBean(R.mipmap.func_contact, getContext().getString(R.string.func_contact)));
        funcBeanList.add(new FuncBean(R.mipmap.func_file, getContext().getString(R.string.func_file)));
        funcBeanList.add(new FuncBean(R.mipmap.func_loaction, getContext().getString(R.string.func_location)));
        FuncAdapter funcAdapter = new FuncAdapter(getContext(), funcBeanList);
        setAdapter(funcAdapter);
    }

    class FuncAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Context mContext;
        private ArrayList<FuncBean> funcBeanList = new ArrayList<>();

        public FuncAdapter(Context context, ArrayList<FuncBean> data) {
            this.mContext = context;
            this.inflater = LayoutInflater.from(context);
            if (data != null) {
                this.funcBeanList = data;
            }
        }

        @Override
        public int getCount() {
            return funcBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return funcBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.func_grid_item, null);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.func_grid_item_icon);
                viewHolder.name = (TextView) convertView.findViewById(R.id.func_grid_item_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final FuncBean appBean = funcBeanList.get(position);
            if (appBean != null) {
                viewHolder.icon.setBackgroundResource(appBean.getIcon());
                viewHolder.name.setText(appBean.getFuncName());
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new ChatKeyboardFuncClickEvent(position));
                    }
                });
            }
            return convertView;
        }

        final class ViewHolder {
            private ImageView icon;
            private TextView name;
        }
    }

    class FuncBean {

        private int icon;
        private String funcName;

        public int getIcon() {
            return icon;
        }

        public String getFuncName() {
            return funcName;
        }

        public FuncBean(int icon, String funcName) {
            this.icon = icon;
            this.funcName = funcName;
        }
    }
}
