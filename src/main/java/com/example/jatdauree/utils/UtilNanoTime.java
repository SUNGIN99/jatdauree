package com.example.jatdauree.utils;

public class UtilNanoTime {
    private final static long currentTimeNanosOffset = (System.currentTimeMillis() * 1000000) - System.nanoTime();
    public static long currentTimeNanos() {
        return System.nanoTime() + currentTimeNanosOffset;
    }
}
