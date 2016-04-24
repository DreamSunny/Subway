package com.infrastructure.image;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 下载图片线程池
 */
public class ImageThreadPool {

    private ExecutorService mExecutorService;

    private ImageThreadPool() {
        mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public static ImageThreadPool getInstance() {
        return ImageThreadPoolHolder.INSTANCE;
    }

    /**
     * 内部类实现单例
     */
    private static class ImageThreadPoolHolder {
        private static final ImageThreadPool INSTANCE = new ImageThreadPool();
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        if (mExecutorService != null) {
            mExecutorService.shutdown();
        }
    }

    /**
     * 执行线程
     *
     * @param r 下载图片线程
     */
    public void submit(final Runnable r) {
        if (r != null) {
            mExecutorService.submit(r);
        }
    }
}
