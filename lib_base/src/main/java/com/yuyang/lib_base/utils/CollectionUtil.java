package com.yuyang.lib_base.utils;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class CollectionUtil {

    public static <T> List<T> asList(T... array) {
        return new ArrayList<>(Arrays.asList(array));
    }

    public static boolean isEmpty(Collection<?> collection) {

        return null == collection || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {

        return null == map || map.isEmpty();
    }

    public static boolean isEmpty(SparseArray<?> sa) {

        return null == sa || sa.size() == 0;
    }

    public static <T> boolean isEmpty(T[] array) {

        return null == array || array.length == 0;
    }

    public static boolean isEmpty(char[] array) {

        return null == array || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {

        return null == array || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {

        return null == array || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {

        return null == array || array.length == 0;
    }
}
