package com.infrastructure.image;

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.infrastructure.utils.BaseUtils;
import com.infrastructure.utils.UtilsLog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * ImageView包装类
 */
public class ImageViewWrapper {

    private WeakReference<ImageView> viewRef;
    private boolean checkActualViewSize;

    public ImageViewWrapper(ImageView imageView) {
        this(imageView, true);
    }

    public ImageViewWrapper(ImageView imageView, boolean checkActualViewSize) {
        this.viewRef = new WeakReference<>(imageView);
        this.checkActualViewSize = checkActualViewSize;
    }

    /**
     * 获取ImageView的宽
     *
     * @return ImageView的宽
     */
    public int getWidth() {
        ImageView imageView = viewRef.get();
        if (imageView != null) {
            final ViewGroup.LayoutParams params = imageView.getLayoutParams();
            int width = 0;
            if (checkActualViewSize && params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
                width = imageView.getWidth();
            }
            if (width <= 0 && params != null) {
                width = params.width;
            }
            if (width <= 0) {
                width = getImageViewFiledValue(imageView, "mMaxWidth");
            }
            if (width <= 0) {
                width = BaseUtils.GetScreenWidth();
            }
            return width;
        }
        return BaseUtils.GetScreenWidth();
    }

    /**
     * 获取ImageView的高
     *
     * @return ImageView的高
     */
    public int getHeight() {
        ImageView imageView = viewRef.get();
        if (imageView != null) {
            final ViewGroup.LayoutParams params = imageView.getLayoutParams();
            int height = 0;
            if (checkActualViewSize && params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = imageView.getHeight();
            }
            if (height <= 0 && params != null) {
                height = params.height;
            }
            if (height <= 0) {
                height = getImageViewFiledValue(imageView, "mMaxHeight");
            }
            if (height <= 0) {
                height = BaseUtils.GetScreenHeight();
            }
            return height;
        }
        return BaseUtils.GetScreenHeight();
    }


    /**
     * 获取ImageView
     *
     * @return 包装的ImageView
     */
    public ImageView getWrappedView() {
        return viewRef.get();
    }

    /**
     * 加载Bitmap至ImageView
     *
     * @param bitmap 加载的图片
     */
    protected void setImageBitmap(Bitmap bitmap) {
        ImageView imageView = viewRef.get();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 获取ImageView属性值
     *
     * @param object    ImageView对象
     * @param fieldName 属性
     * @return 属性值
     */
    private static int getImageViewFiledValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            UtilsLog.d(UtilsLog.TAG_IMAGE, e);
        }
        return value;
    }
}
