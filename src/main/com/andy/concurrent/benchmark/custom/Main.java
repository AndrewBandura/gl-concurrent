package com.andy.concurrent.benchmark.custom;

import org.openjdk.jmh.runner.RunnerException;

public class Main {

    public static void main(String[] args) throws RunnerException, InterruptedException {
        IncrementTest incrementTest = new IncrementTest();
        incrementTest.runTests();
    }
}
