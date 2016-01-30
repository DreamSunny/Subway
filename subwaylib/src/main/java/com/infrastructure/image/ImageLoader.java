package com.infrastructure.image;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.infrastructure.utils.UtilsLog;


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
    public void displayImage(String imageUrl, ImageView imageView) {
        UtilsLog.d(UtilsLog.TAG_IMAGE, imageUrl);
        displayImage(imageUrl, new ImageViewWrapper(imageView), new SimpleListener());
    }

    /**
     * @param imageUrl         图片URL
     * @param imageViewWrapper ImageView包装类
     * @param listener         图片加载Listener
     */
    public void displayImage(String imageUrl, ImageViewWrapper imageViewWrapper, ImageLoadingListener listener) {
        Bitmap bitmap;
        if (mImageCache != null) {
            // 从内存中获取Bitmap
            bitmap = mImageCache.getBitmapFromMemCache(imageUrl);
            if (bitmap != null) {
                imageViewWrapper.setImageBitmap(bitmap);

                UtilsLog.d(UtilsLog.TAG_IMAGE, "From MemCache");
                return;
            }
            // 从硬盘中获取Bitmap
            bitmap = mImageCache.getBitmapFromDiskCache(imageUrl);
            if (bitmap != null) {
                mImageCache.addBitmapToMemCache(imageUrl, bitmap);
                imageViewWrapper.setImageBitmap(bitmap);

                UtilsLog.d(UtilsLog.TAG_IMAGE, "From DiskCache");
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
        public void onFailed(String imageUri, ImageView imageView) {
            if (imageView != null) {
                imageView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onComplete(String imageUri, ImageView imageView, Bitmap loadedImage) {
            if (imageView != null) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(loadedImage);
            }
        }
    }
}
