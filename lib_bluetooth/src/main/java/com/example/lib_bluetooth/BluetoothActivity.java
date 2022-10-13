package com.example.lib_bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.yuyang.lib_base.ui.base.BaseActivity;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.header.HeaderRightBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//                https://github.com/AltBeacon/android-beacon-library
//                https://github.com/Jasonchenlijian/FastBle
public class BluetoothActivity extends BaseActivity {

    private BluetoothRecyclerAdapter mRecyclerAdapter;
    private StickyRecyclerHeadersDecoration headersDecor;

    private TextView startScanText;
    private TextView stopScanText;

    private BluetoothAdapter mBluetoothAdapter;

    private boolean isBlueToothOpen;//记录进入页面时蓝牙是否开启，如未开启，退出页面时将蓝牙关闭

    private final ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                List<String> deniedAskList = new ArrayList<>();
                List<String> deniedNoAskList = new ArrayList<>();
                for (Map.Entry<String, Boolean> stringBooleanEntry : result.entrySet()) {
                    if (!stringBooleanEntry.getValue()) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), stringBooleanEntry.getKey())) {
                            deniedAskList.add(stringBooleanEntry.getKey());
                        } else {
                            deniedNoAskList.add(stringBooleanEntry.getKey());
                        }
                    }
                }

                if (deniedAskList.size() == 0 && deniedNoAskList.size() == 0) {//全通过

                    initView();
                    initReceiver();

                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled()) {
                        isBlueToothOpen = false;
                        mBluetoothAdapter.enable();
                    } else {
                        isBlueToothOpen = true;
                        startScanText.performClick();
                    }

                } else if (deniedNoAskList.size() > 0) {
                    finish();
                } else {
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        permissionsLauncher.launch(new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN});
    }

    @Override
    public void onResume() {
        super.onResume();
        initBoundDevices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        if (!isBlueToothOpen) {
            BluetoothAdapter.getDefaultAdapter().disable();
        }
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("蓝牙");
        List<HeaderRightBean> rightBeanList = new ArrayList<>();
        rightBeanList.add(new HeaderRightBean("系统蓝牙", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }));
        headerLayout.setRight(rightBeanList);

        RecyclerView mRecyclerView = findViewById(R.id.activity_bluetooth_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new BluetoothRecyclerAdapter(getActivity());

        headersDecor = new StickyRecyclerHeadersDecoration(mRecyclerAdapter);
        mRecyclerView.addItemDecoration(headersDecor);
        mRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
        mRecyclerView.setAdapter(mRecyclerAdapter);

        startScanText = findViewById(R.id.activity_bluetooth_startScanText);
        stopScanText = findViewById(R.id.activity_bluetooth_stopScanText);

        startScanText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerAdapter.unbondBeanList.clear();
                initBoundDevices();
                startScanText.setEnabled(false);
                stopScanText.setEnabled(true);
                mBluetoothAdapter.startDiscovery();
            }
        });
        stopScanText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanText.setEnabled(true);
                stopScanText.setEnabled(false);
                mBluetoothAdapter.cancelDiscovery();
            }
        });
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);

        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void initBoundDevices() {
        if (mRecyclerAdapter == null) return;
        mRecyclerAdapter.bondBeanList.clear();
        mRecyclerAdapter.bondBeanList.addAll(mBluetoothAdapter.getBondedDevices());
        mRecyclerAdapter.notifyDataSetChanged();
    }

    // 接收设备的接收器
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED: {
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
                case BluetoothDevice.ACTION_FOUND: {    // 获得已经搜索到的蓝牙设备
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // 搜索到的不是已经绑定的蓝牙设备
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED &&
                        !mRecyclerAdapter.unbondBeanList.contains(device) &&
                        !TextUtils.isEmpty(device.getName())) {
                        int pos = mRecyclerAdapter.getItemCount();
                        mRecyclerAdapter.unbondBeanList.add(device);
                        mRecyclerAdapter.notifyItemInserted(pos);
                        headersDecor.invalidateHeaders();
                    }
                    break;
                }
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED: {   //扫描开始
                    startScanText.setEnabled(false);
                    stopScanText.setEnabled(true);
                    break;
                }
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED: {  //扫描完成
                    startScanText.setEnabled(true);
                    stopScanText.setEnabled(false);
                    break;
                }
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED: {   //配对状态变更
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_BONDING://正在配对
                            break;
                        case BluetoothDevice.BOND_BONDED://完成配对
                            mRecyclerAdapter.changeItem(device);
                            //自动连接该设备
                            break;
                        case BluetoothDevice.BOND_NONE://取消配对
                            break;
                        default:
                            break;
                    }
                    break;
                }
                case BluetoothDevice.ACTION_PAIRING_REQUEST: {//其它设备蓝牙配对请求
                    BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    break;
                }
            }
        }
    };
}
