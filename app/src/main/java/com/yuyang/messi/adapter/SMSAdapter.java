package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuyang.messi.R;
import com.yuyang.messi.bean.SMSBean;

import java.util.List;

/**
 * 短信适配器
 * @author yuyang
 */
public class SMSAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<SMSBean> list;

	public SMSAdapter(Context context, List<SMSBean> list) {

		inflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_sms_list_item, parent,
					false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(list.get(position).getContact());
		holder.count.setText("(" + list.get(position).getMsgCount() + ")");

		holder.date.setText(list.get(position).getLastDate());

		holder.content.setText(list.get(position).getLastMsg());

		return convertView;
	}

	public final class ViewHolder {
		public TextView name;
		public TextView count;
		public TextView date;
		public TextView content;

		public ViewHolder(View convertView) {
			name = (TextView) convertView.findViewById(R.id.activity_sms_list_item_name);
			count = (TextView) convertView.findViewById(R.id.activity_sms_list_item_count);
			date = (TextView) convertView.findViewById(R.id.activity_sms_list_item_date);
			content = (TextView) convertView.findViewById(R.id.activity_sms_list_item_content);
		}
	}
}
