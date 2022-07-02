package com.example.lib_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.yuyang.lib_base.ui.base.BaseActivity;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.ToastUtil;

public class BluetoothReadActivity extends BaseActivity {

    private static final String KEY_BEAN = "key_bean";

    private TextView connectText;
    private TextView disconnectText;

    private BluetoothDevice mBluetoothDevice;

    public static void runActivity(Context context, BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(context, BluetoothReadActivity.class);
        intent.putExtra(KEY_BEAN, bluetoothDevice);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_read);
        mBluetoothDevice = getIntent().getParcelableExtra(KEY_BEAN);
        initView();
        initReceiver();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(mBluetoothDevice.getName());

        connectText = findViewById(R.id.activity_bluetooth_connectText);
        disconnectText = findViewById(R.id.activity_bluetooth_disconnectText);

        connectText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast("呵呵");
            }
        });
        disconnectText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast("呵呵");
            }
        });
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    // 接收设备的接收器
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED: {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:// 开启中
                            break;
                        case BluetoothAdapter.STATE_ON://开启
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:// 关闭中
                            break;
                        case BluetoothAdapter.STATE_OFF: // 关闭
                            break;
                    }
                    break;
                }
            }
        }
    };
}
