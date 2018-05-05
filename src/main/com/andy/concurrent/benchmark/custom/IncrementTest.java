package com.andy.concurrent.benchmark.custom;

public class IncrementTest {

    private long counter;

    private void printMessage(String name, long estimatedTime) {
        System.out.println(name);
        System.out.println("i="+counter);
        System.out.println("est.time ="+estimatedTime);
    }

    public void runTests() throws InterruptedException {

        long startTime = System.nanoTime();
        counter = (new MultiThreadRunnable()).runTest();
        long estimatedTime = (System.nanoTime() - startTime);
        printMessage("classicSynchronizedRunnable", estimatedTime);

        startTime = System.nanoTime();
        counter = (new SingleThread()).runTest();
        estimatedTime = (System.nanoTime() - startTime);
        printMessage("singleThread", estimatedTime);

        startTime = System.nanoTime();
        counter = (new MultiThreadExtendsThread()).runTest();
        estimatedTime = (System.nanoTime() - startTime);
        printMessage("classicSynchronized", estimatedTime);

      }

}

