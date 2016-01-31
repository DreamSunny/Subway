package com.infrastructure.image;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.infrastructure.cache.CacheManager;
import com.infrastructure.utils.BaseConstants;
import com.infrastructure.utils.BaseUtils;
import com.infrastructure.utils.UtilsLog;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 图片缓存类
 */
public class ImageCache {

    // 内存缓存，默认为内存的四分之一
    private static final int DEFAULT_MEM_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 1024) / 4;
    // 磁盘缓存，默认为10MB
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10;

    private static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    private static final int DEFAULT_COMPRESS_QUALITY = 70;
    private static final int DISK_CACHE_INDEX = 0;

    private DiskLruCache mDiskLruCache;
    private LruCache<String, Bitmap> mMemoryCache;

    public ImageCache() {
        initMemoryCache();
        InitDiskCache();
    }

    /**
     * 初始化内存缓存
     */
    private void initMemoryCache() {
        mMemoryCache = new LruCache<String, Bitmap>(DEFAULT_MEM_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    /**
     * 初始化硬盘缓存
     */
    private void InitDiskCache() {
        if (BaseUtils.IsSdcardMounted()) {
            if (BaseUtils.GetAvailableSdcardSize() < DEFAULT_DISK_CACHE_SIZE) {
                CacheManager.getInstance().clearImageData();
            }
            File file = new File(BaseConstants.APP_IMAGE_CACHE_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                mDiskLruCache = DiskLruCache.open(file, 1, 1, DEFAULT_DISK_CACHE_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加Bitmap至内存
     *
     * @param imageUrl 图片URL
     * @param bitmap   图片Bitmap
     */
    public void addBitmapToMemCache(final String imageUrl, final Bitmap bitmap) {
        final String md5Key = BaseUtils.GetMd5(imageUrl);
        if (mMemoryCache != null) {
            mMemoryCache.put(md5Key, bitmap);
        }
        UtilsLog.d(UtilsLog.TAG_IMAGE, "Add Bitmap To MemCache");
    }

    /**
     * 添加Bitmap至硬盘
     *
     * @param imageUrl 图片URL
     * @param bitmap   图片Bitmap
     */
    public void addBitmapToDiskCache(final String imageUrl, final Bitmap bitmap) {
        if (mDiskLruCache != null) {
            final String md5Key = BaseUtils.GetMd5(imageUrl);
            OutputStream out = null;
            try {
                DiskLruCache.Snapshot snapshot = mDiskLruCache.get(md5Key);
                if (snapshot == null) {
                    final DiskLruCache.Editor editor = mDiskLruCache.edit(md5Key);
                    if (editor != null) {
                        out = editor.newOutputStream(DISK_CACHE_INDEX);
                        bitmap.compress(DEFAULT_COMPRESS_FORMAT, DEFAULT_COMPRESS_QUALITY, out);
                        editor.commit();
                        out.close();
                    }
                } else {
                    snapshot.getInputStream(DISK_CACHE_INDEX).close();
                }
                mDiskLruCache.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                BaseUtils.closeStream(out);
            }
        }
        UtilsLog.d(UtilsLog.TAG_IMAGE, "Add Bitmap To DiskCache");
    }

    /**
     * 从内存中获取图片Bitmap
     *
     * @param imageUrl 图片URL
     * @return 图片Bitmap
     */
    public Bitmap getBitmapFromMemCache(final String imageUrl) {
        if (mMemoryCache != null) {
            final String md5Key = BaseUtils.GetMd5(imageUrl);
            return mMemoryCache.get(md5Key);
        }
        return null;
    }

    /**
     * 从硬盘中获取图片Bitmap
     *
     * @param imageUrl 图片URL
     * @return 图片Bitmap
     */
    public Bitmap getBitmapFromDiskCache(final String imageUrl) {
        final String md5Key = BaseUtils.GetMd5(imageUrl);
        Bitmap bitmap = null;

        if (mDiskLruCache != null) {
            InputStream inputStream = null;
            try {
                final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(md5Key);
                if (snapshot != null) {
                    inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
                    if (inputStream != null) {
                        FileDescriptor fd = ((FileInputStream) inputStream).getFD();
                        bitmap = BitmapDecoder.decodeBitmapFromDescriptor(fd, Integer.MAX_VALUE, Integer.MAX_VALUE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                BaseUtils.closeStream(inputStream);
            }
        }
        return bitmap;
    }

}
