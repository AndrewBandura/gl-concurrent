package com.andy.concurrent.benchmark.custom;

public class SingleThread {
    private final int MAX_ITERATION = 1_000_000;
    private long a = 0;

    public long runTest() {
        return increment();
    }

    private  long increment() {
        for (int i=0; i<MAX_ITERATION; i++){
            a = a+1;
        }
        return a;

    }
}
