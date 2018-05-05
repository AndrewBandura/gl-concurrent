package com.andy.concurrent.benchmark.custom;

public class MultiThreadRunnable {

    private final int MAX_ITERATION = 1_000_000;
    private final int THREAD_COUNT = 4;
    private volatile long a = 0;

    public long runTest() throws InterruptedException {
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> increment(), "Thread_" + i);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        return a;
    }

    private long increment() {
        while (a < MAX_ITERATION) {
            synchronized (this) {
                if (a < MAX_ITERATION)
                    a++;
            }
        }
        return a;
    }
}

