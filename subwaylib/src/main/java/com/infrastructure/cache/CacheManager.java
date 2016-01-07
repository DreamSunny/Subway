package com.infrastructure.cache;

import com.infrastructure.utils.BaseConstants;
import com.infrastructure.utils.BaseUtils;

import java.io.File;

/**
 * Created by user on 2016/1/4.
 */
public class CacheManager {
    // sdcard 最小空间，如果小于10M，不会再向sdcard里面写入任何数据
    public static final long SDCARD_MIN_SPACE = 1024 * 1024 * 10;

    public static CacheManager instance;

    private CacheManager() {
    }

    /**
     * 获取CacheManager实例
     */
    public static synchronized CacheManager getInstance() {
        if (instance == null) {
            instance = new CacheManager();
        }
        return instance;
    }

    /**
     * 初始化缓存文件
     */
    public void initCacheDir() {
        // sdcard已经挂载并且空间不小于10M，可以写入文件;小于10M时，清除缓存
        if (BaseUtils.IsSdcardMounted()) {
            if (BaseUtils.GetAvailableSdcardSize() < SDCARD_MIN_SPACE) {
                clearAllData();
            } else {
                final File dir = new File(BaseConstants.APP_CACHE_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
        }
    }

    /**
     * 缓存到文件
     */
    public void putFileCache(final String key, final String data, long expiredTime) {
        String md5Key = BaseUtils.GetMd5(key);
        final CacheItem item = new CacheItem(md5Key, data, expiredTime);
        putIntoCache(item);
    }

    /**
     * 从文件缓存中取出缓存，没有则返回空
     */
    public String getFileCache(final String key) {
        String md5Key = BaseUtils.GetMd5(key);
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
     * 将CacheItem缓存到磁盘
     */
    synchronized boolean putIntoCache(final CacheItem item) {
        if (BaseUtils.GetAvailableSdcardSize() > SDCARD_MIN_SPACE) {
            BaseUtils.SaveObject(BaseConstants.APP_CACHE_PATH + item.getKey(), item);
            return true;
        }
        return false;
    }

    /**
     * 将CacheItem从磁盘读取出来
     */
    synchronized CacheItem getFromCache(final String key) {
        CacheItem cacheItem = null;
        Object findItem = BaseUtils.restoreObject(BaseConstants.APP_CACHE_PATH + key);
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

    /**
     * 清除缓存文件
     */
    public void clearAllData() {
        File file = null;
        File[] files = null;
        if (BaseUtils.IsSdcardMounted()) {
            file = new File(BaseConstants.APP_CACHE_PATH);
            files = file.listFiles();
            if (files != null) {
                for (final File f : files) {
                    f.delete();
                }
            }
        }
    }
}
