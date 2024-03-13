package com.yuyang.messi.ui.category.adapter;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.utils.AppInfoUtil;
import com.yuyang.lib_base.utils.security.MD5Util;
import com.yuyang.messi.R;

import java.util.List;

public class SwipeMenuListViewAdapter extends BaseAdapter {

    public List<ApplicationInfo> appList;

    public SwipeMenuListViewAdapter(List<ApplicationInfo> appList) {
        this.appList = appList;
    }

    public void updateData(List<ApplicationInfo> appList) {
        this.appList = appList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return appList == null ? 0 : appList.size();
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        return position % 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(BaseApp.getInstance(), R.layout.activity_swipe_menu_list_view_item, null);
            new ViewHolder(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        ApplicationInfo item = getItem(position);

        holder.image.setImageDrawable(item.loadIcon(BaseApp.getInstance().getPackageManager()));
        holder.name.setText(item.loadLabel(BaseApp.getInstance().getPackageManager()));
        holder.version.setText(String.format("版本：%s", AppInfoUtil.getAppVersionName(item.packageName)));
        holder.packageName.setText(item.packageName);

        Signature signature = AppInfoUtil.getAppSignature(item.packageName);
        holder.textView1.setText(String.format("签名信息:%s", MD5Util.md5(signature.toByteArray())));
        try {
            holder.textView2.setText(String.format("TargetSdk:%s", BaseApp.getInstance().getPackageManager().getPackageInfo(item.packageName, 0).applicationInfo.targetSdkVersion));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView name;
        TextView version;
        TextView packageName;
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;

        public ViewHolder(View view) {
            image = view.findViewById(R.id.activity_swipe_menu_list_view_image);
            name = view.findViewById(R.id.activity_swipe_menu_list_view_name);
            version = view.findViewById(R.id.activity_swipe_menu_list_view_version);
            packageName = view.findViewById(R.id.activity_swipe_menu_list_view_packageName);
            textView1 = view.findViewById(R.id.activity_swipe_menu_list_view_textView1);
            textView2 = view.findViewById(R.id.activity_swipe_menu_list_view_textView2);
            textView3 = view.findViewById(R.id.activity_swipe_menu_list_view_textView3);
            textView4 = view.findViewById(R.id.activity_swipe_menu_list_view_textView4);
            view.setTag(this);
        }
    }
}
