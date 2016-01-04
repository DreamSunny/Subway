package com.infrastructure.net;

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by user on 2016/1/4.
 */
public class DefaultThreadPool {
    // 阻塞队列最大任务数量
    static final int BLOCKING_QUEUE_SIZE = 20;

    static final int THREAD_POOL_CORE_SIZE = 6;
    static final int THREAD_POOL_MAX_SIZE = 10;

    // 缓冲BaseRequest任务队列
    static ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);

    private static DefaultThreadPool instance = null;

    // 线程池，目前是十个线程
    static AbstractExecutorService pool = new ThreadPoolExecutor(
            THREAD_POOL_CORE_SIZE, THREAD_POOL_MAX_SIZE, 15L, TimeUnit.SECONDS, blockingQueue, new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    public static synchronized DefaultThreadPool getInstance() {
        if (instance == null) {
            instance = new DefaultThreadPool();
        }
        return instance;
    }

    public static void removeAllTask() {
        blockingQueue.clear();
    }

    public static void removeTaskFromQueue(final Object obj) {
        blockingQueue.remove(obj);
    }

    /**
     * 关闭，并等待任务执行完成，不接受新任务
     */
    public static void shutdown() {
        if (pool != null) {
            pool.shutdown();
        }
    }

    /**
     * 立即关闭，并挂起所有正在执行的线程，不接受新任务
     */
    public static void shutdownRightnow() {
        if (pool != null) {
            pool.shutdown();
            try {
                // 设置超时极短，强制关闭所有任务
                pool.awaitTermination(1, TimeUnit.MICROSECONDS);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行任务
     */
    public void execute(final Runnable r) {
        if (r != null) {
            try {
                pool.execute(r);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
