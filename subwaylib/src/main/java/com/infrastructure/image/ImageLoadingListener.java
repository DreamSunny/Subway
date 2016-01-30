package com.infrastructure.image;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 图片加载监听
 */
public interface ImageLoadingListener {
    void onFailed(String imageUri, ImageView imageView);

    void onComplete(String imageUri, ImageView imageView, Bitmap loadedImage);
}
