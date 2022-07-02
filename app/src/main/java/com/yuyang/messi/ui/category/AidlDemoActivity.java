package com.yuyang.messi.ui.category;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.example.aidldemo.AidlBean;
import com.example.aidldemo.IMyAidlInterface;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.util.List;

public class AidlDemoActivity extends AppBaseActivity {

    private boolean aidlConnect = false;

    private IMyAidlInterface iMyAidlInterface;
    private final AidlBean aidlBean_in = new AidlBean();
    private final AidlBean aidlBean_out = new AidlBean();
    private final AidlBean aidlBean_inout = new AidlBean();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_aidl_demo;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAidl();
    }

    private Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

    private void initAidl() {

        findViewById(R.id.BeanList).setOnClickListener(view -> {
            if (!aidlConnect) {
                ToastUtil.showToast("服务未连接");
                return;
            }
            try {
                List<AidlBean> aidlBeanList = iMyAidlInterface.getAidlBeanList();
                ToastUtil.showToast(aidlBeanList.size() + "");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.addBeanIn).setOnClickListener(view -> {
            if (!aidlConnect) {
                ToastUtil.showToast("服务未连接");
                return;
            }
            if (TextUtils.isEmpty(aidlBean_in.getName())) {
                aidlBean_in.setName("aidlBean_in");
                try {
                    iMyAidlInterface.addAidlBeanIn(aidlBean_in);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                aidlBean_in.setAge(aidlBean_in.getAge() + 1);
            }
        });
        findViewById(R.id.addBeanOut).setOnClickListener(view -> {
            if (!aidlConnect) {
                ToastUtil.showToast("服务未连接");
                return;
            }
            if (TextUtils.isEmpty(aidlBean_out.getName())) {
                aidlBean_out.setName("aidlBean_out");
                try {
                    iMyAidlInterface.addAidlBeanOut(aidlBean_out);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                aidlBean_out.setAge(aidlBean_out.getAge() + 1);
            }
        });
        findViewById(R.id.addBeanInOut).setOnClickListener(view -> {
            if (!aidlConnect) {
                ToastUtil.showToast("服务未连接");
                return;
            }
            if (TextUtils.isEmpty(aidlBean_inout.getName())) {
                aidlBean_inout.setName("aidlBean_inout");
                try {
                    iMyAidlInterface.addAidlBeanInOut(aidlBean_inout);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                aidlBean_inout.setAge(aidlBean_inout.getAge() + 1);
            }
        });
        Intent explicitIntent = createExplicitFromImplicitIntent(this, new Intent("com.example.aidldemo.server"));

        if (explicitIntent != null) {
            bindService(explicitIntent, new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
                    aidlConnect = true;
                    ToastUtil.showToast("服务已连接");
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    aidlConnect = false;
                    ToastUtil.showToast("服务已断开");
                }
            }, BIND_AUTO_CREATE);
        } else {
            ToastUtil.showToast("找不到服务");
        }
    }
}
