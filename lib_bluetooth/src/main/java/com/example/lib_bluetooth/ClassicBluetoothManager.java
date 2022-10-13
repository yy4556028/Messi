package com.example.lib_bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;

import com.yuyang.lib_base.utils.LogUtil;

import java.lang.reflect.Method;
import java.util.Set;

/** Created by andy 2019/1/23. Email: 1239604859@qq.com */
@SuppressLint("MissingPermission")
public class ClassicBluetoothManager {
  private String TAG = "ClassicBluetoothManager";
  public static final int ACL = Integer.MAX_VALUE;

  public abstract static class BluetoothProfileCallback {
    public void stateConnecting(int profile, @NonNull BluetoothDevice device) {}

    public void stateConnected(int profile, @NonNull BluetoothDevice device) {}

    public void stateDisconnecting(int profile, @NonNull BluetoothDevice device) {}

    public void stateDisconnected(int profile, @NonNull BluetoothDevice device) {}

    public void bondNone(@NonNull BluetoothDevice device) {}

    public void bondBonded(@NonNull BluetoothDevice device) {}

    public void onBluetoothClosed() {}
  }

  private Context mContext;
  private BluetoothProfileCallback mBluetoothProfileCallback;

  public ClassicBluetoothManager(Context context) {
    mContext = context;
    HFP.getInstance().initHFP(context);
    A2DP.getInstance().initA2DP(context);
  }

  public void setBluetoothProfileCallback(BluetoothProfileCallback bluetoothProfileCallback) {
    mBluetoothProfileCallback = bluetoothProfileCallback;
  }

  public void setTAG(String tag) {
    TAG = tag;
  }

  public void register() {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
    intentFilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
    intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
    intentFilter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
    intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
    mContext.registerReceiver(btStatusReceiver, intentFilter);
  }

  public void unregister() {
    mBluetoothProfileCallback = null;
    mContext.unregisterReceiver(btStatusReceiver);
  }

  private BroadcastReceiver btStatusReceiver =
      new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          handleReceiver(context, intent);
        }
      };

  private void handleReceiver(Context context, Intent intent) {
    if (intent.getAction() == null) {
      return;
    }
    int state;
    BluetoothDevice device;
    switch (intent.getAction()) {
      case BluetoothAdapter.ACTION_STATE_CHANGED:
        state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        if (state == BluetoothAdapter.STATE_OFF) {
          if (mBluetoothProfileCallback != null) {
            mBluetoothProfileCallback.onBluetoothClosed();
          }
        }
        break;
        //            //ACL
        //            case BluetoothManager.ACTION_ACL_CONNECTED:
        //                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        //                LogUtil.d(TAG, "ACL: ACTION_CONNECTED: " + device);
        //                if (mBluetoothProfileCallback != null && device != null) {
        //                    mBluetoothProfileCallback.stateConnected(ACL, device);
        //                }
        //                break;
        //
        //            case BluetoothManager.ACTION_ACL_DISCONNECTED:
        //                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        //                LogUtil.d(TAG, "ACL: ACTION_DISCONNECTED: " + device);
        //                if (mBluetoothProfileCallback != null && device != null) {
        //                    mBluetoothProfileCallback.stateDisconnected(ACL, device);
        //                }
        //                break;

        // 1
      case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
        state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1);
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        switch (state) {
          case BluetoothA2dp.STATE_CONNECTING:
            LogUtil.d(TAG, "BluetoothA2dp connecting " + device);
            if (mBluetoothProfileCallback != null && device != null) {
              mBluetoothProfileCallback.stateConnecting(BluetoothProfile.A2DP, device);
            }
            break;
          case BluetoothA2dp.STATE_CONNECTED:
            LogUtil.d(TAG, "BluetoothA2dp connected " + device);
            if (mBluetoothProfileCallback != null && device != null) {
              mBluetoothProfileCallback.stateConnected(BluetoothProfile.A2DP, device);
            }
            break;
          case BluetoothA2dp.STATE_DISCONNECTING:
            LogUtil.d(TAG, "BluetoothA2dp disconnecting " + device);
            if (mBluetoothProfileCallback != null && device != null) {
              mBluetoothProfileCallback.stateDisconnecting(BluetoothProfile.A2DP, device);
            }
            break;
          case BluetoothA2dp.STATE_DISCONNECTED:
            LogUtil.d(TAG, "BluetoothA2dp disconnected " + device);
            if (mBluetoothProfileCallback != null && device != null) {
              mBluetoothProfileCallback.stateDisconnected(BluetoothProfile.A2DP, device);
            }
            break;
        }
        break;
        // 2
      case BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED:
        state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1);
        switch (state) {
          case BluetoothA2dp.STATE_PLAYING:
            LogUtil.d(TAG, "BluetoothA2dp state: playing ");
            break;
          case BluetoothA2dp.STATE_NOT_PLAYING:
            LogUtil.d(TAG, "BluetoothA2dp state: not playing");
            break;
        }
        break;
        // 3
      case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
        state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        switch (state) {
          case BluetoothHeadset.STATE_CONNECTED:
            LogUtil.d(TAG, "BluetoothHeadset.STATE_CONNECTED " + device);
            if (mBluetoothProfileCallback != null && device != null) {
              mBluetoothProfileCallback.stateConnected(BluetoothProfile.HEADSET, device);
            }
            break;
          case BluetoothHeadset.STATE_CONNECTING:
            LogUtil.d(TAG, "BluetoothHeadset.STATE_CONNECTING " + device);
            if (mBluetoothProfileCallback != null && device != null) {
              mBluetoothProfileCallback.stateConnecting(BluetoothProfile.HEADSET, device);
            }
            break;
          case BluetoothHeadset.STATE_DISCONNECTED:
            LogUtil.d(TAG, "BluetoothHeadset.STATE_DISCONNECTED " + device);
            if (mBluetoothProfileCallback != null && device != null) {
              mBluetoothProfileCallback.stateDisconnected(BluetoothProfile.HEADSET, device);
            }
            break;
          case BluetoothHeadset.STATE_DISCONNECTING:
            LogUtil.d(TAG, "BluetoothHeadset.STATE_DISCONNECTING " + device);
            if (mBluetoothProfileCallback != null && device != null) {
              mBluetoothProfileCallback.stateDisconnecting(BluetoothProfile.HEADSET, device);
            }
            break;
        }
        break;
        // 4
      case BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED:
        break;
        // 5
      case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);

        switch (state) {
          case BluetoothDevice.BOND_NONE:
            LogUtil.d(TAG, "ACTION_BOND_STATE_CHANGED: BOND_NONE, " + device);
            if (mBluetoothProfileCallback != null && device != null) {
              mBluetoothProfileCallback.bondNone(device);
            }
            break;
          case BluetoothDevice.BOND_BONDING:
            LogUtil.d(TAG, "ACTION_BOND_STATE_CHANGED: BOND_BONDING, " + device);
            break;
          case BluetoothDevice.BOND_BONDED:
            LogUtil.d(TAG, "ACTION_BOND_STATE_CHANGED: BOND_BONDED, " + device);
            if (mBluetoothProfileCallback != null && device != null) {
              mBluetoothProfileCallback.bondBonded(device);
            }
            break;
        }

        break;
    }
  }

  public static class A2DP {
    public static final String TAG = "ClassicBluetooth_A2DP";
    private static A2DP INSTANCE;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothA2dp a2dpProfile;

    private A2DP() {}

    public static synchronized A2DP getInstance() {
      if (INSTANCE == null) {
        INSTANCE = new A2DP();
      }
      return INSTANCE;
    }

    public void initA2DP(Context context) {
      if (a2dpProfile != null) {
        return;
      }
      mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      mBluetoothAdapter.getProfileProxy(
          context.getApplicationContext(), new ProfileListener(), BluetoothProfile.A2DP);
    }

    public boolean a2dpConnect(BluetoothDevice device) {
      boolean result = false;
      if (a2dpProfile != null) {
        try {
          Method connect =
              a2dpProfile.getClass().getDeclaredMethod("connect", BluetoothDevice.class);
          connect.setAccessible(true);
          result = (boolean) connect.invoke(a2dpProfile, device);
          LogUtil.i(TAG, "a2dpConnect: use reflect to connect a2dp --> " + result);
        } catch (Exception e) {
          LogUtil.e(TAG, "a2dpConnect: " + e.toString());
        }
      }
      return result;
    }

    public void a2dpDisconnect(BluetoothDevice device) {
      if (a2dpProfile != null) {
        try {
          Method disconnect =
              a2dpProfile.getClass().getDeclaredMethod("disconnect", BluetoothDevice.class);
          disconnect.setAccessible(true);
          boolean result = (boolean) disconnect.invoke(a2dpProfile, device);
          LogUtil.i(TAG, "a2dpDisconnect: use reflect to disconnect a2dp --> " + result);
        } catch (Exception e) {
          LogUtil.e(TAG, "a2dpDisconnect: " + e.toString());
        }
      }
    }

    /**
     * PRIORITY_OFF 0 PRIORITY_ON 100 PRIORITY_AUTO_CONNECT 1000 PRIORITY_UNDEFINED -1
     *
     * @param device device
     * @param priority priority
     */
    public void setPriority(BluetoothDevice device, int priority) {
      if (a2dpProfile != null) {
        try {
          Method connectMethod =
              BluetoothA2dp.class.getMethod("setPriority", BluetoothDevice.class, int.class);
          connectMethod.invoke(a2dpProfile, device, priority);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    public void close() {
      mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, a2dpProfile);
    }

    public int getA2DPState() {
      if (mBluetoothAdapter != null) {
        return mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
      } else {
        return -1;
      }
    }

    /** 判断与哪一个远程设备处于连接 */
    public BluetoothDevice getSysA2dpConnected() {
      Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
      for (BluetoothDevice device : bondedDevices) {
        if (getA2dpState(device)) {
          return device;
        }
      }
      return null;
    }

    public boolean getA2dpState(BluetoothDevice device) {
      return a2dpProfile != null
          && a2dpProfile.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED;
    }

    private class ProfileListener implements BluetoothProfile.ServiceListener {
      @Override
      public void onServiceConnected(int profile, BluetoothProfile proxy) {
        LogUtil.d(TAG, "onServiceConnected: profile -- " + profile + "  proxy -- " + proxy);
        if (profile == BluetoothProfile.A2DP) {
          LogUtil.i(TAG, "connect to a2dp server");
          a2dpProfile = (BluetoothA2dp) proxy;
        }
      }

      @Override
      public void onServiceDisconnected(int profile) {
        LogUtil.d(TAG, "onServiceDisconnected: profile -- " + profile);
        if (profile == BluetoothProfile.A2DP) {
          LogUtil.i(TAG, "disconnect to a2dp server");
          a2dpProfile = null;
        }
      }
    }
  }

  public static class HFP {
    public static final String TAG = "ClassicBluetooth_HFP";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadset mBluetoothHeadset;
    private static HFP INSTANCE;
    private boolean enableHfp = false;

    private BluetoothDevice connectedDevice;

    private HFP() {}

    public boolean isEnableHfp() {
      return enableHfp;
    }

    public static synchronized HFP getInstance() {
      if (INSTANCE == null) {
        INSTANCE = new HFP();
      }
      return INSTANCE;
    }

    public void initHFP(Context context) {
      if (mBluetoothHeadset != null) {
        return;
      }
      mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      enableHfp =
          mBluetoothAdapter.getProfileProxy(
              context.getApplicationContext(), new ProfileListener(), BluetoothProfile.HEADSET);
    }

    private class ProfileListener implements BluetoothProfile.ServiceListener {
      @Override
      public void onServiceConnected(int profile, BluetoothProfile proxy) {
        LogUtil.d(TAG, "onServiceConnected: profile -- " + profile + "  proxy -- " + proxy);
        if (profile == BluetoothProfile.HEADSET) {
          LogUtil.i(TAG, "connect to hfp server");
          mBluetoothHeadset = (BluetoothHeadset) proxy;
        }
      }

      @Override
      public void onServiceDisconnected(int profile) {
        LogUtil.d(TAG, "onServiceDisconnected: profile -- " + profile);
        if (profile == BluetoothProfile.HEADSET) {
          LogUtil.i(TAG, "disconnect to hfp server");
          mBluetoothHeadset = null;
        }
      }
    }

    public boolean hfpConnect(BluetoothDevice bluetoothDevice) {
      boolean result = false;
      try {
        Method connect =
            mBluetoothHeadset.getClass().getDeclaredMethod("connect", BluetoothDevice.class);
        connect.setAccessible(true);
        result = (boolean) connect.invoke(mBluetoothHeadset, bluetoothDevice);
        LogUtil.i(TAG, "hfpConnect: use reflect to connect hfp --> " + result);

      } catch (Exception e) {
        LogUtil.e(TAG, "hfpConnect: " + e.toString());
      }
      return result;
    }

    /**
     * CONNECTION_POLICY_FORBIDDEN 0 CONNECTION_POLICY_ALLOWED 100
     *
     * @param device device
     * @param priority priority
     */
    public void setPriority(BluetoothDevice device, int priority) {
      if (mBluetoothHeadset != null) {
        try {
          Method connectMethod =
              BluetoothHeadset.class.getMethod("setPriority", BluetoothDevice.class, int.class);
          boolean result = (boolean) connectMethod.invoke(mBluetoothHeadset, device, priority);
          LogUtil.i(TAG, "setPriority: use reflect to setPriority hfp --> " + result);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    public void hfpDisconnect(BluetoothDevice bluetoothDevice) {
      try {
        Method disconnect =
            mBluetoothHeadset.getClass().getDeclaredMethod("disconnect", BluetoothDevice.class);
        disconnect.setAccessible(true);
        boolean result = (boolean) disconnect.invoke(mBluetoothHeadset, bluetoothDevice);
        LogUtil.i(TAG, "hfpDisconnect: use reflect to disconnect hfp --> " + result);

      } catch (Exception e) {
        LogUtil.e(TAG, "hfpDisconnect: " + e.toString());
      }
    }

    public void close() {
      if (connectedDevice != null) {
        hfpDisconnect(connectedDevice);
      }
      mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
    }

    public int getHfpState() {
      if (mBluetoothAdapter != null) {
        return mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
      } else {
        return -1;
      }
    }

    /** 判断与哪一个远程设备处于连接 */
    public BluetoothDevice getSysHfpConnected() {
      Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
      for (BluetoothDevice device : bondedDevices) {
        if (getHfpState(device)) {
          return device;
        }
      }
      return null;
    }

    /** 判断与远程设备的连接状态 */
    public boolean getHfpState(BluetoothDevice device) {
      LogUtil.d(TAG, "hfpProfile_-->" + mBluetoothHeadset);
      return mBluetoothHeadset != null
          && mBluetoothHeadset.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED;
    }

    public BluetoothHeadset getBluetoothHeadset() {
      return mBluetoothHeadset;
    }
  }
}
