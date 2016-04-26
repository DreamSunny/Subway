package com.infrastructure.request;

import android.app.Activity;
import android.content.res.XmlResourceParser;

import com.dsunny.subwaylib.R;
import com.infrastructure.util.LogUtil;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

/**
 * 请求Url管理类
 */
public class UrlConfigManager {
    private static final String TAG_NODE = "Node";
    private static final String TAG_KEY = "Key";
    private static final String TAG_EXPIRES = "Expires";
    private static final String TAG_NETTYPE = "NetType";
    private static final String TAG_MOCKCLASS = "MockClass";
    private static final String TAG_URL = "Url";

    private static ArrayList<URLData> urlList;

    /**
     * 从配置文件中获取url信息
     *
     * @param activity 当前Activity
     */
    private static void fetchUrlDataFromXml(final Activity activity) {
        urlList = new ArrayList<>();

        final XmlResourceParser xmlParser = activity.getApplication().getResources().getXml(R.xml.url);

        int eventCode;
        try {
            eventCode = xmlParser.getEventType();
            while (eventCode != XmlPullParser.END_DOCUMENT) {
                switch (eventCode) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (TAG_NODE.equals(xmlParser.getName())) {
                            final URLData urlData = new URLData();
                            urlData.setKey(xmlParser.getAttributeValue(null, TAG_KEY));
                            urlData.setExpires(Long.parseLong(xmlParser.getAttributeValue(null, TAG_EXPIRES)));
                            urlData.setNetType(xmlParser.getAttributeValue(null, TAG_NETTYPE));
                            urlData.setMockClass(xmlParser.getAttributeValue(null, TAG_MOCKCLASS));
                            urlData.setUrl(xmlParser.getAttributeValue(null, TAG_URL));
                            urlList.add(urlData);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventCode = xmlParser.next();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            xmlParser.close();
        }
    }

    /**
     * 从配置文件中获取指定的url信息
     *
     * @param activity 当前Activity
     * @param findKey  指定的key值
     * @return url信息
     */
    public static URLData findURL(final Activity activity, final String findKey) {
        // 如果urlList还没有数据（第一次），或者被回收了，那么（重新）加载xml
        if (urlList == null || urlList.isEmpty()) {
            fetchUrlDataFromXml(activity);
        }

        for (URLData data : urlList) {
            if (findKey.equals(data.getKey())) {
                LogUtil.d(LogUtil.TAG_URL, data);
                return data;
            }
        }

        return null;
    }
}
