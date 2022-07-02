package com.example.lib_bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.lib_base.utils.ToastUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BluetoothRecyclerAdapter extends RecyclerView.Adapter<BluetoothRecyclerAdapter.MyHolder> implements
    StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;

    public List<BluetoothDevice> bondBeanList = new ArrayList<>();
    public List<BluetoothDevice> unbondBeanList = new ArrayList<>();

    public BluetoothRecyclerAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void clearData() {
        bondBeanList.clear();
        unbondBeanList.clear();
        notifyDataSetChanged();
    }

    public void changeItem(BluetoothDevice bluetoothDevice) {
        bondBeanList.remove(bluetoothDevice);
        unbondBeanList.remove(bluetoothDevice);

        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            unbondBeanList.remove(bluetoothDevice);
            if (!bondBeanList.contains(bluetoothDevice)) {
                bondBeanList.add(bluetoothDevice);
            }
        } else {
            bondBeanList.remove(bluetoothDevice);
            if (!unbondBeanList.contains(bluetoothDevice)) {
                unbondBeanList.add(bluetoothDevice);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(inflater.inflate(R.layout.activity_bluetooth_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        if (position < bondBeanList.size()) {
            final BluetoothDevice bluetoothDevice = bondBeanList.get(position);
            holder.nameText.setText(bluetoothDevice.getName());
            holder.addressText.setText(bluetoothDevice.getAddress());
            holder.actionText.setText("连接");
        } else {
            final BluetoothDevice bluetoothDevice = unbondBeanList.get(position - bondBeanList.size());
            holder.nameText.setText(bluetoothDevice.getName());
            holder.addressText.setText(bluetoothDevice.getAddress());
            holder.actionText.setText("配对");
        }
    }

    @Override
    public int getItemCount() {
        return bondBeanList.size() + unbondBeanList.size();
    }

    @Override
    public int getItemRealCount() {
        return getItemCount();
    }

    @Override
    public long getHeaderId(int position) {
        if (position < bondBeanList.size()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        TextView textView = new TextView(viewGroup.getContext());
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setPadding(PixelUtils.dp2px(15), PixelUtils.dp2px(15), PixelUtils.dp2px(15), PixelUtils.dp2px(15));
        textView.setTextSize(16);
        textView.setTextColor(ContextCompat.getColor(viewGroup.getContext(), com.yuyang.lib_base.R.color.base_text_b1));
        textView.setBackgroundColor(Color.parseColor("#EEEEEE"));
        return new RecyclerView.ViewHolder(textView) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder viewHolder, int i) {
        TextView textView = (TextView) viewHolder.itemView;
        if (i == 0) {
            textView.setText(String.format("已配对的设备(%s)", bondBeanList.size()));
        } else {
            textView.setText(String.format("可用设备(%s)", unbondBeanList.size()));
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private TextView nameText;
        private TextView addressText;
        private TextView actionText;

        public MyHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.activity_bluetooth_item_nameText);
            addressText = itemView.findViewById(R.id.activity_bluetooth_item_addressText);
            actionText = itemView.findViewById(R.id.activity_bluetooth_item_actionText);

            actionText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (actionText.getText().toString()) {
                        case "连接": {
                            BluetoothDevice bluetoothDevice = bondBeanList.get(getAdapterPosition());
                            BluetoothReadActivity.runActivity(v.getContext(), bluetoothDevice);
                            break;
                        }
                        case "配对": {
                            BluetoothDevice bluetoothDevice = unbondBeanList.get(getAdapterPosition() - bondBeanList.size());

                            try {
                                Method createBond = BluetoothDevice.class.getMethod("createBond");
                                Method removeBond = BluetoothDevice.class.getMethod("removeBond");
                                createBond.invoke(bluetoothDevice);
                            } catch (Exception e) {
                                e.printStackTrace();
                                ToastUtil.showToast("无法执行配对");
                            }
                            break;
                        }
                    }
                }
            });
        }
    }
}