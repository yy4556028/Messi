package com.example.aidldemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

public class AidlService extends Service {

    private final List<AidlBean> aidlBeanList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationUtil.showNotification(this, MainActivity.class, "heheda", false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends IMyAidlInterface.Stub {

        @Override
        public List<AidlBean> getAidlBeanList() throws RemoteException {
            synchronized (this) {
                return aidlBeanList;
            }
        }

        @Override
        public int getAidlBeanCount() throws RemoteException {
            synchronized (this) {
                return aidlBeanList.size();
            }
        }

        @Override
        public void addAidlBeanIn(AidlBean aidlBean) throws RemoteException {
            synchronized (this) {
                aidlBean.setTag("In");
                aidlBeanList.add(aidlBean);
            }
        }

        @Override
        public void addAidlBeanOut(AidlBean aidlBean) throws RemoteException {
            synchronized (this) {
                aidlBean.setTag("Out");
                aidlBeanList.add(aidlBean);
            }
        }

        @Override
        public void addAidlBeanInOut(AidlBean aidlBean) throws RemoteException {
            synchronized (this) {
                aidlBean.setTag("InOut");
                aidlBeanList.add(aidlBean);
            }
        }
    }
}