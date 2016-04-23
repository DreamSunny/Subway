package com.dsunny.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 公共线程池
 */
public class ThreadPoolUtil {
    // CPU核心数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // 核心线程数
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    // 最大线程数量
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    // 非核心线程闲置时间为1秒
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread #" + mCount.getAndIncrement());
        }
    };

    // 线程池任务队列容量为128
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>(128);

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    private ThreadPoolUtil() {
    }

    private static class ThreadPoolUtilsHolder {
        private static final ThreadPoolUtil INSTANCE = new ThreadPoolUtil();
    }

    /**
     * 获取请求线程池的实例
     * @return 请求线程池的实例
     */
    public static ThreadPoolUtil getInstance() {
        return ThreadPoolUtilsHolder.INSTANCE;
    }

    /**
     * 清空线程池
     */
    public static void removeAllTask() {
        sPoolWorkQueue.clear();
    }

    /**
     * 删除指定线程
     * @param obj
     */
    public static void removeTaskFromQueue(final Object obj) {
        sPoolWorkQueue.remove(obj);
    }

    /**
     * 关闭，并等待任务执行完成，不接受新任务
     */
    public static void shutdown() {
        if (THREAD_POOL_EXECUTOR != null) {
            THREAD_POOL_EXECUTOR.shutdown();
        }
    }

    /**
     * 立即关闭，并挂起所有正在执行的线程，不接受新任务
     */
    public static void shutdownRightnow() {
        if (THREAD_POOL_EXECUTOR != null) {
            THREAD_POOL_EXECUTOR.shutdown();
            try {
                // 设置超时极短，强制关闭所有任务
                THREAD_POOL_EXECUTOR.awaitTermination(1, TimeUnit.MICROSECONDS);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行请求
     * @param r 请求
     */
    public void execute(final Runnable r) {
        if (r != null) {
            try {
                THREAD_POOL_EXECUTOR.execute(r);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

}
