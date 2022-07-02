package com.yuyang.messi.threadPool;

public class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {

    private final Runnable runnable;
    private final int priority;

    public PriorityRunnable(Runnable runnable, int priority) {
        this.runnable = runnable;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public void run() {
        runnable.run();
    }

    @Override
    public int compareTo(PriorityRunnable another) {
        return another.getPriority() - getPriority();
    }
}
