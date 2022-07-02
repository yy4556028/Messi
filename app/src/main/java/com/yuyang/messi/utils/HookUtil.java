package com.yuyang.messi.utils;

import android.view.View;

import com.yuyang.lib_base.utils.ToastUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookUtil implements InvocationHandler {

    private Object delegate;

    public Object bind(Object delegate) {
        this.delegate = delegate;
        return Proxy.newProxyInstance(
                this.delegate.getClass().getClassLoader(),
                this.delegate.getClass().getInterfaces(),
                this);
    }

    /**
     * @param o
     * @param method
     * @param objects
     */
    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        return null;
    }

    public static void hookViewClickTest(View originView) throws Exception {
        //第一步 拿到 View实例内部的ListenerInfo 对象
        Method getListenerInfoMethod = View.class.getDeclaredMethod("getListenerInfo");
        getListenerInfoMethod.setAccessible(true);
        Object mListenerInfo = getListenerInfoMethod.invoke(originView);
        //第二步 得到原始的 onClickListener
        Class listenerInfoClass = Class.forName("android.view.View$ListenerInfo");
        Field mOnClickListener = listenerInfoClass.getDeclaredField("mOnClickListener");
        final View.OnClickListener originOnClickListener = (View.OnClickListener) mOnClickListener.get(mListenerInfo);

//        mOnClickListener.set(mListenerInfo, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ToastUtil.showToast("hook");
//                originOnClickListener.onClick(view);
//            }
//        });

        Object obj = Proxy.newProxyInstance(
                originView.getClass().getClassLoader(),//参数1：指定产生代理对象的类加载器，需要将其指定为和目标对象同一个类加载器
                new Class[]{View.OnClickListener.class},//参数2：指定目标对象的实现接口
                new InvocationHandler() {//指定InvocationHandler对象。即动态代理对象在调用方法时，会关联到哪个InvocationHandler对象
                    /**
                     * @param proxy     当前的代理对象Object(不是被代理对象)
                     * @param method    当前执行的方法
                     * @param args      当前执行方法的参数
                     */
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        ToastUtil.showToast("hook " + proxy.getClass().getSimpleName());
                        return method.invoke(originOnClickListener, args);
                    }
                });

//        ToastUtil.showToast((obj instanceof View.OnClickListener) + "");
        mOnClickListener.set(mListenerInfo, obj);
    }
}
