package com.infrastructure.net;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 异步下载
 */
public abstract class DownloadAsyncTask extends AsyncTask<String, Void, InputStream> {

    public static final int DOWNLOAD_IMAGE_BITMAP = 1;// 下载图片

    private int mDownloadType;

    public DownloadAsyncTask(int type) {
        this.mDownloadType = type;
    }

    public abstract void onSuccess(Object object);

    public abstract void onFail(String msg);

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected InputStream doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.connect();
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = urlConn.getInputStream();
                urlConn.disconnect();
                return inputStream;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(InputStream inputStream) {
        super.onPostExecute(inputStream);
        if (inputStream == null) {
            onFail("下载失败");
        } else {
            Object object = null;
            switch (mDownloadType) {
                case DOWNLOAD_IMAGE_BITMAP:
                    object = BitmapFactory.decodeStream(inputStream);
                    break;
            }
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            onSuccess(object);
        }
    }
}
