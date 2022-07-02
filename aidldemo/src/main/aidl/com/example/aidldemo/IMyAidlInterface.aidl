// IMyAidlInterface.aidl
package com.example.aidldemo;
import com.example.aidldemo.AidlBean;
// Declare any non-default types here with import statements

interface IMyAidlInterface {

    List<AidlBean> getAidlBeanList();
    int getAidlBeanCount();

    void addAidlBeanIn(in AidlBean aidlBean);

    void addAidlBeanOut(out AidlBean aidlBean);

    void addAidlBeanInOut(inout AidlBean aidlBean);
}