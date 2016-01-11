package com.infrastructure.cache;

import java.io.Serializable;

/**
 * 缓存项
 */
public class CacheItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String key;// 存储的key
    private String data;// JSON字符串
    private long timeStamp = 0L;// 过期时间的时间戳

    public CacheItem(final String key, final String data, final long expiredTime) {
        this.key = key;
        this.data = data;
        this.timeStamp = System.currentTimeMillis() + expiredTime * 1000;
    }

    public String getKey() {
        return key;
    }

    public String getData() {
        return data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "CacheItem{" +
                "key='" + key + '\'' +
                ", data='" + data + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
