//package com.yuyang.messi.custom_annotation;
//
//import android.app.Activity;
//import android.view.View;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * https://blog.csdn.net/wuyuxing24/article/details/81139846
// */
//public class ButterKnifeProcess {
//
//    public static void bind(final Activity activity) {
//        Class annotationParent = activity.getClass();
//        Field[] fields = annotationParent.getDeclaredFields();
//        Method[] methods = annotationParent.getDeclaredMethods();
//        // OnClick
//        // 找到类里面所有的方法
//        for (final Method method : methods) {
//            //找到添加了OnClick注解的方法
//            BindView clickMethod = method.getAnnotation(BindView.class);
//            if (clickMethod != null && clickMethod.value().length != 0) {
//                for (int id : clickMethod.value()) {
//                    final View view = activity.findViewById(id);
//                    view.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            try {
//                                method.invoke(activity, view);
//                            } catch (IllegalAccessException e) {
//                                e.printStackTrace();
//                            } catch (InvocationTargetException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            }
//        }
//    }
//
//    private static Map<Class<?>, Method> classMethodMap = new HashMap<>();
//
//    public static void bind2(Activity target) {
//        if (target != null) {
//            Method method = classMethodMap.get(target.getClass());
//            try {
//                if (method == null) {
//                    //获取编译生成的注解类
//                    String bindClassName = target.getPackageName() + ".Bind" + target.getClass().getSimpleName();
//                    Class<?> bindClass = Class.forName(bindClassName);
//                    method = bindClass.getMethod("bindView", target.getClass());
//                    classMethodMap.put(target.getClass(), method);
//                }
//                method.invoke(null, target);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
