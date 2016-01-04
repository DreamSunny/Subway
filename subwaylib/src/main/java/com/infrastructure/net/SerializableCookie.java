package com.infrastructure.net;

import org.apache.http.cookie.Cookie;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

/**
 * Created by user on 2016/1/4.
 */
public class SerializableCookie implements Cookie, Externalizable {

    private static final int NAME = 0x01;
    private static final int VALUE = 0x02;
    private static final int COMMENT = 0x04;
    private static final int COMMENT_URL = 0x08;
    private static final int EXPIRY_DATE = 0x10;
    private static final int DOMAIN = 0x20;
    private static final int PATH = 0x40;
    private static final int PORTS = 0x80;

    private transient Cookie mCookie;
    private transient int mNullMask = 0;

    public SerializableCookie() {
        super();
    }

    public SerializableCookie(final Cookie cookie) {
        super();
        mCookie = cookie;
    }

    @Override
    public String getName() {
        return mCookie.getName();
    }

    @Override
    public String getValue() {
        return mCookie.getValue();
    }

    @Override
    public String getComment() {
        return mCookie.getComment();
    }

    @Override
    public String getCommentURL() {
        return mCookie.getCommentURL();
    }

    @Override
    public Date getExpiryDate() {
        return mCookie.getExpiryDate();
    }

    @Override
    public boolean isPersistent() {
        return mCookie.isPersistent();
    }

    @Override
    public String getDomain() {
        return mCookie.getDomain();
    }

    @Override
    public String getPath() {
        return mCookie.getPath();
    }

    @Override
    public int[] getPorts() {
        return mCookie.getPorts();
    }

    @Override
    public boolean isSecure() {
        return mCookie.isSecure();
    }

    @Override
    public int getVersion() {
        return mCookie.getVersion();
    }

    @Override
    public boolean isExpired(Date date) {
        return mCookie.isExpired(date);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {

    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {

    }
}
