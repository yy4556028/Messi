package com.yuyang.messi.learn;

import android.os.Build;

import com.yuyang.lib_base.utils.ReflectionUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * https://www.jianshu.com/p/22b6a169e6bd?utm_campaign=maleskine&utm_content=note&utm_medium=seo_notes&utm_source=recommendation
 *
 * apkpatch.bat -f fix.apk -t oldApp.apk -o output -k yamap.jks -p yamashita521 -a yamap -e yamashita521
 *
 * dx --dex --output=bug-fix.dex xxxx.class
 */
public class DexHelper {

    public static void replaceDex(ClassLoader classLoader, List<File> outDexFiles, File optimizedDirectory) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                replaceDexV23(classLoader, outDexFiles, optimizedDirectory);
            } else if (Build.VERSION.SDK_INT >= 19) {
                replaceDexV19(classLoader, outDexFiles, optimizedDirectory);
            } else if (Build.VERSION.SDK_INT >= 14) {
                replaceDexV14(classLoader, outDexFiles, optimizedDirectory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //23和19的差别，就是 makeXXXElements 方法名和参数要求不同
    //后者是 makeDexElements(ArrayList<File> files, File optimizedDirectory,ArrayList<IOException> suppressedExceptions)
    //前者是 makePathElements(List<File> files, File optimizedDirectory,List<IOException> suppressedExceptions)
    private static void replaceDexV23(ClassLoader classLoader, List<File> outDexFiles, File optimizedDirectory) throws IllegalAccessException, InvocationTargetException {
        Field pathListField = ReflectionUtil.getDeclaredField(classLoader, "pathList");//1、获得DexPathList pathList 属性
        Object pathList = pathListField.get(classLoader); //2、获得DexPathList pathList对象
        Field dexElementsField = ReflectionUtil.getDeclaredField(pathList, "dexElements");//3、获得DexPathList的dexElements属性
        Object[] oldElements = (Object[]) dexElementsField.get(pathList);//4、获得pathList对象中 dexElements 的属性值

        List<IOException> suppressedExceptions = new ArrayList<>();
        Method makePathElementsMethod = ReflectionUtil.getDeclaredMethod(//获得 DexPathList 的 makePathElements 方法
                pathList, "makePathElements", List.class, File.class, List.class);
        assert makePathElementsMethod != null;
        Object[] newElements = (Object[]) makePathElementsMethod.invoke(//这个方法是静态方法，所以不需要传实例,直接invoke;这里取得的返回值就是 我们外部的dex文件构建成的 Element数组
                null, outDexFiles, optimizedDirectory, suppressedExceptions);//构建出一个新的Element数组
        //下面把新数组和旧数组合并，注意新数组放前面
        Object[] dexElements = null;
        if (newElements != null && newElements.length > 0) {
            dexElements = (Object[]) Array.newInstance(oldElements.getClass().getComponentType(), oldElements.length + newElements.length);//先建一个新容器
            //这5个参数解释一下, 如果是将A,B 你找AB的顺序组合成数组C，那么参数的含义，依次是 A对象，A数组开始复制的位置，C对象，C对象的开始存放的位置，数组A中要复制的元素个数
            System.arraycopy(
                    newElements, 0, dexElements, 0, newElements.length);//新来的数组放前面
            System.arraycopy(
                    oldElements, 0, dexElements, newElements.length, oldElements.length);
        }
        //最后把合并之后的数组设置到 dexElements里面
        dexElementsField.set(pathList, dexElements);
    }

    private static void replaceDexV19(ClassLoader classLoader, List<File> outDexFiles, File optimizedDirectory) throws IllegalAccessException, InvocationTargetException {
        Field pathListField = ReflectionUtil.getDeclaredField(classLoader, "pathList");//1、获得DexPathList pathList 属性
        Object pathList = pathListField.get(classLoader); //2、获得DexPathList pathList对象
        Field dexElementsField = ReflectionUtil.getDeclaredField(pathList, "dexElements");//3、获得DexPathList的dexElements属性
        Object[] oldElements = (Object[]) dexElementsField.get(pathList);//4、获得pathList对象中 dexElements 的属性值

        List<IOException> suppressedExceptions = new ArrayList<>();
        Method makePathElementsMethod = ReflectionUtil.getDeclaredMethod(//获得 DexPathList 的 makeDexElements 方法
                pathList, "makeDexElements", ArrayList.class, File.class, ArrayList.class);//别忘了后面的参数列表
        Object[] newElements = (Object[]) makePathElementsMethod.invoke(
                null, outDexFiles, optimizedDirectory, suppressedExceptions);//这个方法是静态方法，所以不需要传实例,直接invoke;这里取得的返回值就是 我们外部的dex文件构建成的 Element数组

        //下面把新数组和旧数组合并，注意新数组放前面
        Object[] dexElements = null;
        if (newElements != null && newElements.length > 0) {
            dexElements = (Object[]) Array.newInstance(oldElements.getClass().getComponentType(), oldElements.length + newElements.length);//先建一个新容器
            //这5个参数解释一下, 如果是将A,B 你找AB的顺序组合成数组C，那么参数的含义，依次是 A对象，A数组开始复制的位置，C对象，C对象的开始存放的位置，数组A中要复制的元素个数
            System.arraycopy(
                    newElements, 0, dexElements, 0, newElements.length);//新来的数组放前面
            System.arraycopy(
                    oldElements, 0, dexElements, newElements.length, oldElements.length);
        }

        //最后把合并之后的数组设置到 dexElements里面
        dexElementsField.set(pathList, dexElements);
    }

    //14和19的区别，是这个方法 makeDexElements(ArrayList<File> files,File optimizedDirectory)···它又少了一个参数
    private static void replaceDexV14(ClassLoader classLoader, List<File> outDexFiles, File optimizedDirectory) throws IllegalAccessException, InvocationTargetException {
        Field pathListField = ReflectionUtil.getDeclaredField(classLoader, "pathList");//1、获得DexPathList pathList 属性
        Object pathList = pathListField.get(classLoader); //2、获得DexPathList pathList对象
        Field dexElementsField = ReflectionUtil.getDeclaredField(pathList, "dexElements");//3、获得DexPathList的dexElements属性
        Object[] oldElements = (Object[]) dexElementsField.get(pathList);//4、获得pathList对象中 dexElements 的属性值

        List<IOException> suppressedExceptions = new ArrayList<>();
        Method makePathElementsMethod = ReflectionUtil.getDeclaredMethod(//获得 DexPathList 的 makeDexElements 方法
                pathList, "makeDexElements", ArrayList.class, File.class);//别忘了后面的参数列表
        Object[] newElements = (Object[]) makePathElementsMethod.invoke(
                null, outDexFiles, optimizedDirectory, suppressedExceptions);//这个方法是静态方法，所以不需要传实例,直接invoke;这里取得的返回值就是 我们外部的dex文件构建成的 Element数组

        //下面把新数组和旧数组合并，注意新数组放前面
        Object[] dexElements = null;
        if (newElements != null && newElements.length > 0) {
            dexElements = (Object[]) Array.newInstance(oldElements.getClass().getComponentType(), oldElements.length + newElements.length);//先建一个新容器
            //这5个参数解释一下, 如果是将A,B 你找AB的顺序组合成数组C，那么参数的含义，依次是 A对象，A数组开始复制的位置，C对象，C对象的开始存放的位置，数组A中要复制的元素个数
            System.arraycopy(
                    newElements, 0, dexElements, 0, newElements.length);//新来的数组放前面
            System.arraycopy(
                    oldElements, 0, dexElements, newElements.length, oldElements.length);
        }

        //最后把合并之后的数组设置到 dexElements里面
        dexElementsField.set(pathList, dexElements);
    }
}
