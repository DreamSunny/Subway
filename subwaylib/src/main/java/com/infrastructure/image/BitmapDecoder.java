package com.infrastructure.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;
import java.io.InputStream;

/**
 * 获取Bitmap
 */
public class BitmapDecoder {

    /**
     * 从Resource获取Bitmap
     *
     * @param res       资源
     * @param resId     资源文件ID
     * @param reqWidth  ImageView的宽
     * @param reqHeight ImageView的高
     * @return Bitmap位图
     */
    public static Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 从文件获取Bitmap
     *
     * @param filename  文件名
     * @param reqWidth  ImageView的宽
     * @param reqHeight ImageView的高
     * @return Bitmap位图
     */
    public static Bitmap decodeBitmapFromFile(String filename, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    /**
     * 从FileDescriptor获取Bitmap
     *
     * @param fileDescriptor 文件描述符
     * @param reqWidth       ImageView的宽
     * @param reqHeight      ImageView的高
     * @return Bitmap位图
     */
    public static Bitmap decodeBitmapFromDescriptor(final FileDescriptor fileDescriptor, final int reqWidth, final int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    /**
     * 从InputStream中获取图片的Options
     *
     * @param inputStream 输入流
     * @param reqWidth    ImageView的宽
     * @param reqHeight   ImageView的高
     * @return Bitmap位图
     */
    public static BitmapFactory.Options getBitmapFactoryOptions(final InputStream inputStream, final int reqWidth, final int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        return options;
    }

    /**
     * 从InputStream获取Bitmap
     *
     * @param inputStream 输入流
     * @param options     图片的Options
     * @return Bitmap位图
     */
    public static Bitmap decodeBitmapFromInputStream(final InputStream inputStream, final BitmapFactory.Options options) {
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    /**
     * 获取Options的inSampleSize值
     *
     * @param options   Bitmap的Options
     * @param reqWidth  ImageView的宽
     * @param reqHeight ImageView的高
     * @return inSampleSize值
     */
    public static int calculateInSampleSize(final BitmapFactory.Options options, final int reqWidth, final int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            long totalPixels = width * height / inSampleSize;
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }

        return inSampleSize;
    }
}
