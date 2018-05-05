package com.andy.concurrent.benchmark.jmh;

import org.openjdk.jmh.runner.RunnerException;

public class Main {

    public static void main(String[] args) throws RunnerException {
        //IncrementBenchmark.runTests();
        MultiThreadingIncrementBenchmark multiThreadingIncrementBenchmark = new MultiThreadingIncrementBenchmark();
        multiThreadingIncrementBenchmark.runTests();
    }
}
