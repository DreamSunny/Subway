package com.infrastructure.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.infrastructure.util.BaseUtil;
import com.infrastructure.util.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载图片
 */
public class ImageRequest implements Runnable {

    private static final int TIME_OUT_MILLISECOND = 15 * 1000;// 连接超时时间
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int DISK_CACHE_INDEX = 0;

    private String mImageUrl;
    private ImageViewWrapper mImageViewWrapper;
    private ImageLoadingListener mListener;
    private ImageCache mImageCache;
    private Handler mHandler;

    public ImageRequest(final String imageUrl, final ImageViewWrapper imageViewWrapper, final ImageLoadingListener listener, final ImageCache imageCache) {
        mImageUrl = imageUrl;
        mImageViewWrapper = imageViewWrapper;
        mListener = listener;
        mImageCache = imageCache;
        mHandler = new Handler();
    }

    @Override
    public void run() {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(mImageUrl).openConnection();
            conn.setConnectTimeout(TIME_OUT_MILLISECOND);
            conn.setReadTimeout(TIME_OUT_MILLISECOND);
            conn.setDoInput(true);
            conn.connect();

            is = conn.getInputStream();

            // 将InputStream转换为ByteArrayOutputStream
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();

            final BitmapFactory.Options options = BitmapDecoder.getBitmapFactoryOptions(new ByteArrayInputStream(baos.toByteArray()), mImageViewWrapper.getWidth(), mImageViewWrapper.getHeight());
            final Bitmap bitmap = BitmapDecoder.decodeBitmapFromInputStream(new ByteArrayInputStream(baos.toByteArray()), options);
            LogUtil.d(LogUtil.TAG_IMAGE, "Download image " + (bitmap != null ? "success" : "fail"));

            if (mListener != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap != null) {
                            mListener.onComplete(mImageUrl, mImageViewWrapper.getWrappedView(), bitmap);
                        } else {
                            mListener.onFailed(mImageUrl, mImageViewWrapper.getWrappedView());
                        }
                    }
                });
            }

            // 添加缓存
            if (bitmap != null) {
                mImageCache.addBitmapToMemCache(mImageUrl, bitmap);
                mImageCache.addBitmapToDiskCache(mImageUrl, bitmap);
            }
        } catch (IOException e) {
            if (mListener != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onFailed(mImageUrl, mImageViewWrapper.getWrappedView());
                    }
                });
            }
        } finally {
            BaseUtil.closeStream(is);
            BaseUtil.closeStream(baos);
        }
    }
}
