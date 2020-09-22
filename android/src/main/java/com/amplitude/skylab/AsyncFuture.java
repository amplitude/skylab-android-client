package com.amplitude.skylab;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncFuture<T> implements Future<T> {
    private volatile T value = null;
    private volatile boolean completed = false;
    private volatile Throwable throwable = null;
    private final Object lock = new Object();

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return completed;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        synchronized (lock) {
            while (!completed) {
                lock.wait();
            }
        }
        if (throwable != null) {
            throw new ExecutionException(throwable);
        }
        return value;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
            TimeoutException {
        long nanosRemaining = unit.toNanos(timeout);
        long end = System.nanoTime() + nanosRemaining;
        synchronized (lock) {
            while (!completed && nanosRemaining > 0) {
                TimeUnit.NANOSECONDS.timedWait(lock, nanosRemaining);
                nanosRemaining = end - System.nanoTime();
            }
        }
        if (!completed) {
            throw new TimeoutException();
        }

        if (throwable != null) {
            throw new ExecutionException(throwable);
        }

        return value;
    }

    synchronized void complete(T value) {
        if (!completed) {
            this.value = value;
            synchronized (lock) {
                completed = true;
                lock.notifyAll();
            }
        }
    }

    synchronized void completeExceptionally(Throwable ex) {
        if (!completed) {
            throwable = ex;
            synchronized (lock) {
                completed = true;
                lock.notifyAll();
            }
        }
    }
}
