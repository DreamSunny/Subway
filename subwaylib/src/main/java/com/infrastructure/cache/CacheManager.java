package com.infrastructure.cache;

import com.infrastructure.commom.BaseConstants;
import com.infrastructure.util.BaseUtil;

import java.io.File;

/**
 * 缓存管理类
 */
public class CacheManager {
    // sdcard最小空间，如果小于10M，不会再向sdcard里面写入任何数据
    public static final long SDCARD_MIN_SPACE = 1024 * 1024 * 10;

    private CacheManager() {
    }

    /**
     * 获取CacheManager实例
     *
     * @return CacheManager的实例
     */
    public static CacheManager getInstance() {
        return CacheManagerHolder.INSTANCE;
    }

    /**
     * 内部类实现单例
     */
    private static class CacheManagerHolder {
        private static final CacheManager INSTANCE = new CacheManager();
    }

    /**
     * 初始化缓存文件
     */
    public void initCacheDir() {
        // sdcard已经挂载并且空间不小于10M，可以写入文件;小于10M时，清除缓存
        if (BaseUtil.IsSdcardMounted()) {
            if (BaseUtil.GetAvailableSdcardSize() < SDCARD_MIN_SPACE) {
                clearAllData();
            }
            final File dir = new File(BaseConstants.APP_CACHE_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            final File imageDir = new File(BaseConstants.APP_IMAGE_CACHE_PATH);
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
        }
    }

    /**
     * 缓存到文件
     *
     * @param key         缓存项的key值
     * @param data        缓存项的data值
     * @param expiredTime 缓存项的过期时间戳
     */
    public void putFileCache(final String key, final String data, long expiredTime) {
        String md5Key = BaseUtil.GetMd5(key);
        final CacheItem item = new CacheItem(md5Key, data, expiredTime);
        putIntoCache(item);
    }

    /**
     * 从文件缓存中取出缓存，没有则返回空
     *
     * @param key 缓存项的key值
     * @return 缓存项的data值
     */
    public String getFileCache(final String key) {
        String md5Key = BaseUtil.GetMd5(key);
        final File file = new File(BaseConstants.APP_CACHE_PATH + md5Key);
        if (file.exists()) {
            final CacheItem item = getFromCache(md5Key);
            if (item != null) {
                return item.getData();
            }
        }
        return null;
    }

    /**
     * 清除缓存文件
     */
    public void clearAllData() {
        clearRequestData();
        clearImageData();
    }

    /**
     * 清除网络请求缓存数据
     */
    public void clearRequestData() {
        if (BaseUtil.IsSdcardMounted()) {
            File file = new File(BaseConstants.APP_CACHE_PATH);
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }
    }

    /**
     * 清除图片缓存数据
     */
    public void clearImageData() {
        if (BaseUtil.IsSdcardMounted()) {
            File file = new File(BaseConstants.APP_IMAGE_CACHE_PATH);
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }
    }

    /**
     * 将CacheItem缓存到磁盘
     *
     * @param item 缓存项
     * @return 缓存结果
     */
    private synchronized boolean putIntoCache(final CacheItem item) {
        if (BaseUtil.GetAvailableSdcardSize() > SDCARD_MIN_SPACE) {
            BaseUtil.SaveObject(BaseConstants.APP_CACHE_PATH + item.getKey(), item);
            return true;
        }
        return false;
    }

    /**
     * 将CacheItem从磁盘读取出来
     *
     * @param key 缓存项的key值
     * @return 缓存项
     */
    private synchronized CacheItem getFromCache(final String key) {
        CacheItem cacheItem = null;
        Object findItem = BaseUtil.RestoreObject(BaseConstants.APP_CACHE_PATH + key);
        if (findItem == null) {
            // 缓存不存在
            return null;
        }
        cacheItem = (CacheItem) findItem;
        if (System.currentTimeMillis() > cacheItem.getTimeStamp()) {
            // 缓存过期
            return null;
        }
        return cacheItem;
    }
}
