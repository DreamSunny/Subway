package com.infrastructure.image;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.infrastructure.util.LogUtil;


/**
 * 网络图片加载
 */
public class ImageLoader {
    private static ImageLoader instance;

    private ImageCache mImageCache;

    private ImageLoader() {
        mImageCache = new ImageCache();
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    /**
     * 加载图片至ImageView
     *
     * @param imageUrl  图片URL
     * @param imageView 显示图片的ImageView
     */
    public void displayImage(final String imageUrl, final ImageView imageView) {
        LogUtil.d(LogUtil.TAG_IMAGE, imageUrl);
        displayImage(imageUrl, new ImageViewWrapper(imageView), new SimpleListener());
    }

    /**
     * @param imageUrl         图片URL
     * @param imageViewWrapper ImageView包装类
     * @param listener         图片加载Listener
     */
    public void displayImage(final String imageUrl, final ImageViewWrapper imageViewWrapper, final ImageLoadingListener listener) {
        Bitmap bitmap;
        if (mImageCache != null) {
            // 从内存中获取Bitmap
            bitmap = mImageCache.getBitmapFromMemCache(imageUrl);
            if (bitmap != null) {
                imageViewWrapper.setImageBitmap(bitmap);

                LogUtil.d(LogUtil.TAG_IMAGE, "From MemCache");
                return;
            }
            // 从硬盘中获取Bitmap
            bitmap = mImageCache.getBitmapFromDiskCache(imageUrl);
            if (bitmap != null) {
                mImageCache.addBitmapToMemCache(imageUrl, bitmap);
                imageViewWrapper.setImageBitmap(bitmap);

                LogUtil.d(LogUtil.TAG_IMAGE, "From DiskCache");
                return;
            }
        }
        // 网络请求Bitmap
        ImageRequest request = new ImageRequest(imageUrl, imageViewWrapper, listener, mImageCache);
        ImageThreadPool.getInstance().submit(request);
    }

    /**
     * 默认图片加载Listener
     */
    private static class SimpleListener implements ImageLoadingListener {
        @Override
        public void onFailed(final String imageUri, final ImageView imageView) {
        }

        @Override
        public void onComplete(final String imageUri, final ImageView imageView, final Bitmap loadedImage) {
            if (imageView != null) {
                imageView.setImageBitmap(loadedImage);
            }
        }
    }
}
