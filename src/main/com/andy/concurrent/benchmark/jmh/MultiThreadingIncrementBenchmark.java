package com.andy.concurrent.benchmark.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.*;

@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class MultiThreadingIncrementBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        private BenchUnit benchUnit  = new BenchUnit();
        private AtomicLong atomicBenchUnit = new AtomicLong(0);
        private static final int MAX_ITERATION = 1_000_000;
        private static final int NUM_THREADS = 4;
    }

    @Benchmark
    @Group("incrementWithSingleThread")
    public void timeOfIncrementBySingleThread(BenchmarkState state, Blackhole bh) {
        incrementWithSingleThread(state);
        bh.consume(state.benchUnit.value);
    }

    @Benchmark
    @Group("incrementWithSynchronizedExtendsThread")
    public void timeOfIncrementBySynchronizedExtendsThread(BenchmarkState state, Blackhole bh) {

        Thread[] threads = new Thread[BenchmarkState.NUM_THREADS];
        for(int i=0;i<threads.length;i++){
            threads[i] = new ThreadExtendsThread(state);}

        for(int i=0; i<threads.length;i++){
            threads[i].start();
        }

     bh.consume(state.benchUnit.value);
    }

    @Benchmark
    @Group("incrementWithSynchronizedImplementsRunnable")
    public void timeOfIncrementBySynchronizedImplementsRunnable(BenchmarkState state, Blackhole bh) {

        Thread[] threads = new Thread[state.NUM_THREADS];
        for(int i=0;i<threads.length;i++){
            threads[i] = new Thread(() -> incrementWithSynchronized(state));}

        for(int i=0; i<threads.length;i++){
            threads[i].start();
        }

        bh.consume(state.benchUnit.value);
    }

    @Benchmark
    @Group("incrementWithLock")
    public void timeOfIncrementByFixedThreadPoolReentrantLock(BenchmarkState state, Blackhole bh) throws ExecutionException, InterruptedException {
        ReentrantLock locker = new ReentrantLock();
        ExecutorService executorService = Executors.newFixedThreadPool(state.NUM_THREADS);
        for (int i = 0; i < BenchmarkState.NUM_THREADS; i++) {
            Future<BenchUnit> future = executorService.submit(() -> incrementWithReentrantLock(state , locker));
            bh.consume(future.get().value);
        }
        executorService.shutdown();
    }

    @Benchmark
    @Group("incrementWithAtomicLong")
    public void timeOfIncrementByFixedThreadPoolAtomicLong(BenchmarkState state, Blackhole bh) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(state.NUM_THREADS);
        for (int i = 0; i < BenchmarkState.NUM_THREADS; i++) {
            Future<AtomicLong> future = executorService.submit(() -> incrementWithAtomic(state));
            bh.consume(future.get());
        }
        executorService.shutdown();
    }

    @Benchmark
    @Group("incrementWithReadWriteLock")
    public void timeOfIncrementByFixedThreadPoolReadWriteLock(BenchmarkState state, Blackhole bh) throws ExecutionException, InterruptedException {
        ReadWriteLock locker = new ReentrantReadWriteLock();
        ExecutorService executorService = Executors.newFixedThreadPool(state.NUM_THREADS);
        for (int i = 0; i < BenchmarkState.NUM_THREADS; i++) {
            Future<BenchUnit> future = executorService.submit(() -> incrementWithReadWriteLock(state, locker));
            bh.consume(future.get().value);
        }
        executorService.shutdown();
    }

    @Benchmark
    @Group("incrementWithStampedLock")
    public void timeOfIncrementByFixedThreadPoolStampedLock(BenchmarkState state, Blackhole bh) throws ExecutionException, InterruptedException {
        StampedLock locker = new StampedLock();
        ExecutorService executorService = Executors.newFixedThreadPool(state.NUM_THREADS);
        for (int i = 0; i < BenchmarkState.NUM_THREADS; i++) {
            Future<BenchUnit> future = executorService.submit(() -> incrementWithStampedLock(state, locker));
            bh.consume(future.get().value);
        }
        executorService.shutdown();
    }


    private void incrementWithSingleThread(BenchmarkState state) {
        BenchUnit benchUnit = state.benchUnit;
        while (benchUnit.value < BenchmarkState.MAX_ITERATION){{
                state.benchUnit.value++;
            }
        }
    }

    private void incrementWithSynchronized(BenchmarkState state) {
        BenchUnit benchUnit = state.benchUnit;
        while (benchUnit.value < BenchmarkState.MAX_ITERATION){
            synchronized (this) {
                benchUnit.value++;
            }
           // System.out.println(Thread.currentThread().getName()+":"+state.benchUnit.value);
        }
    }

    private BenchUnit incrementWithReentrantLock(BenchmarkState state , Lock locker) {
        BenchUnit benchUnit = state.benchUnit;
        while (benchUnit.value < BenchmarkState.MAX_ITERATION) {
            locker.lock();
            try {
                if (benchUnit.value < BenchmarkState.MAX_ITERATION)
                    benchUnit.value++;
            } finally {
                locker.unlock();
            }
        }
       // System.out.println(Thread.currentThread().getName() + ": "+state.benchUnit.value);
        return benchUnit;
    }

    private BenchUnit incrementWithReadWriteLock(BenchmarkState state , ReadWriteLock locker) {
        BenchUnit benchUnit = state.benchUnit;
        while (benchUnit.value < BenchmarkState.MAX_ITERATION) {
            locker.writeLock().lock();
            try {
                if (benchUnit.value < BenchmarkState.MAX_ITERATION)
                    benchUnit.value++;
            } finally {
                locker.writeLock().unlock();
            }
        }
        return benchUnit;
    }

    private BenchUnit incrementWithStampedLock(BenchmarkState state , StampedLock locker) {
        BenchUnit benchUnit = state.benchUnit;
        while (benchUnit.value < BenchmarkState.MAX_ITERATION) {
            long stamp = locker.writeLock();
            try {
                if (benchUnit.value < BenchmarkState.MAX_ITERATION)
                    benchUnit.value++;
            } finally {
                locker.unlockWrite(stamp);
            }
        }
        return benchUnit;
    }


    private AtomicLong incrementWithAtomic(BenchmarkState state) {
        AtomicLong atomicBenchUnit  = state.atomicBenchUnit;
        while (atomicBenchUnit.get() < state.MAX_ITERATION) {
            atomicBenchUnit.incrementAndGet();
        }
        return atomicBenchUnit;
    }


    public static void runTests() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + MultiThreadingIncrementBenchmark.class.getSimpleName() + ".*")
                .forks(1)
               // .addProfiler(LinuxPerfProfiler.class)
                //.addProfiler(LinuxPerfNormProfiler.class)
                //.addProfiler(LinuxPerfAsmProfiler.class)
                //.shouldDoGC(true)
                //.addProfiler(WinPerfAsmProfiler.class)
                //.jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
                //.syncIterations(true)
                .build();
        new Runner(opt).run();
    }


    class  ThreadExtendsThread extends Thread{

        BenchmarkState state;

        public ThreadExtendsThread(BenchmarkState state) {
            this.state = state;
        }

        @Override
        public void run() {
            incrementWithSynchronized(state);
        }
    }

}

class BenchUnit{
     volatile long value;
}
