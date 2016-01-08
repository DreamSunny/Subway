package com.infrastructure.utils;

import android.os.Environment;
import android.os.StatFs;

import com.infrastructure.net.Request;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by user on 2016/1/4.
 */
public class BaseUtils {

    private static final int MASK_BYTE_HIGH_FOUR_BIT = 0xf0;
    private static final int MASK_BYTE_LOW_FOUR_BIT = 0x0f;
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] SAFE_CHARACTERS = {'\'', '(', ')', '*', '-', '.', '_', '!'};

    /**
     * 编码
     */
    public static String UrlEncodeUnicode(final String s) {
        if (s == null) {
            return null;
        }
        final int length = s.length();
        final StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            final char ch = s.charAt(i);
            if ((ch & 0xff80) == 0) {
                if (IsSafe(ch)) {
                    builder.append(ch);
                } else if (ch == ' ') {
                    builder.append('+');
                } else {
                    builder.append('%');
                    builder.append(Int2Hex((ch >> 4) & MASK_BYTE_LOW_FOUR_BIT));
                    builder.append(Int2Hex(ch & MASK_BYTE_LOW_FOUR_BIT));
                }
            } else {
                builder.append("%u");
                builder.append(Int2Hex((ch >> 12) & MASK_BYTE_LOW_FOUR_BIT));
                builder.append(Int2Hex((ch >> 8) & MASK_BYTE_LOW_FOUR_BIT));
                builder.append(Int2Hex((ch >> 4) & MASK_BYTE_LOW_FOUR_BIT));
                builder.append(Int2Hex(ch & MASK_BYTE_LOW_FOUR_BIT));
            }
        }
        return builder.toString();
    }

    /**
     * @param n < 16
     * @return 十六进制字符
     */
    static char Int2Hex(final int n) {
        if (n < 10) {
            return (char) (n + '0');
        }
        return (char) ((n - 10) + 'a');
    }

    /**
     * 检查是否为安全字符
     */
    static boolean IsSafe(final char ch) {
        if ((((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))) || ((ch >= '0') && (ch <= '9'))) {
            return true;
        }
        for (char c : SAFE_CHARACTERS) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }

    /**
     * MD5运算
     */
    public static String GetMd5(final String s) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.trim().getBytes());
            final byte[] messageDigset = digest.digest();
            return Bytes2Hex(messageDigset);
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 字节数组转换为十六进制字符串
     */
    public static String Bytes2Hex(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (final byte element : bytes) {
            sb.append(HEX_DIGITS[(element & MASK_BYTE_HIGH_FOUR_BIT) >>> 4]);
            sb.append(HEX_DIGITS[(element & MASK_BYTE_LOW_FOUR_BIT)]);
        }
        return sb.toString();
    }

    /**
     * 检查是否安装了sd卡
     */
    public static boolean IsSdcardMounted() {
        final String state = Environment.getExternalStorageState();
        UtilsLog.d(UtilsLog.TAG_SDCARD, "state = " + state);
        return state.equals(Environment.MEDIA_MOUNTED) && !state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    /**
     * 获取SD卡剩余空间的大小(byte)
     */
    public static long GetAvailableSdcardSize() {
        final StatFs localStatFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        UtilsLog.d(UtilsLog.TAG_SDCARD, "AvailableBlocks = " + localStatFs.getAvailableBlocks());
        UtilsLog.d(UtilsLog.TAG_SDCARD, "BlockSize = " + localStatFs.getBlockSize());
        return (long) localStatFs.getAvailableBlocks() * localStatFs.getBlockSize();
    }

    /**
     * 将序列化对象保存到本地
     */
    public static final void SaveObject(String path, Object saveObject) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(saveObject);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从本地读取序列化对象
     */
    public static final Object restoreObject(String path) {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        Object object = null;
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);
            object = objectInputStream.readObject();
            return object;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    /**
     * InputStream转换为String
     */
    public static String InputStream2String(final InputStream is){
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取服务器时间
     */
    public static Date getServerTime() {
        return new Date(System.currentTimeMillis() + Request.DELTA_BETWEEN_SERVER_AND_CLIENT_TIME);
    }
}
