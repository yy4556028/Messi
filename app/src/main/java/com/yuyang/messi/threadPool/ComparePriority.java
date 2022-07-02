package com.yuyang.messi.threadPool;

import java.util.Comparator;

public class ComparePriority<T extends PriorityRunnable> implements Comparator<T> {

    @Override
    public int compare(T lhs, T rhs) {
        return rhs.getPriority() - lhs.getPriority();
    }
}
