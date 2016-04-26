package com.infrastructure.request;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

/**
 * 序列化Cookie，用于HttpClient请求
 */
public class SerializableCookie implements Cookie, Externalizable {

    private static final int MASK_NAME = 0x01;
    private static final int MASK_VALUE = 0x02;
    private static final int MASK_COMMENT = 0x04;
    private static final int MASK_COMMENT_URL = 0x08;
    private static final int MASK_EXPIRY_DATE = 0x10;
    private static final int MASK_DOMAIN = 0x20;
    private static final int MASK_PATH = 0x40;
    private static final int MASK_PORTS = 0x80;

    private transient Cookie mCookie;

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
    public void writeExternal(ObjectOutput out) throws IOException {
        int nullMask = 0;

        nullMask |= (getName() == null) ? MASK_NAME : 0;
        nullMask |= (getValue() == null) ? MASK_VALUE : 0;
        nullMask |= (getComment() == null) ? MASK_COMMENT : 0;
        nullMask |= (getCommentURL() == null) ? MASK_COMMENT_URL : 0;
        nullMask |= (getExpiryDate() == null) ? MASK_EXPIRY_DATE : 0;
        nullMask |= (getDomain() == null) ? MASK_DOMAIN : 0;
        nullMask |= (getPath() == null) ? MASK_PATH : 0;
        nullMask |= (getPorts() == null) ? MASK_PORTS : 0;

        out.writeInt(nullMask);

        if ((nullMask & MASK_NAME) == 0) {
            out.writeUTF(getName());
        }
        if ((nullMask & MASK_VALUE) == 0) {
            out.writeUTF(getValue());
        }
        if ((nullMask & MASK_COMMENT) == 0) {
            out.writeUTF(getComment());
        }
        if ((nullMask & MASK_COMMENT_URL) == 0) {
            out.writeUTF(getCommentURL());
        }
        if ((nullMask & MASK_EXPIRY_DATE) == 0) {
            out.writeLong(getExpiryDate().getTime());
        }

        out.writeBoolean(isPersistent());

        if ((nullMask & MASK_DOMAIN) == 0) {
            out.writeUTF(getDomain());
        }
        if ((nullMask & MASK_PATH) == 0) {
            out.writeUTF(getPath());
        }
        if ((nullMask & MASK_PORTS) == 0) {
            int[] ports = getPorts();
            if (ports != null) {
                out.writeInt(ports.length);
                for (final int port : ports) {
                    out.writeInt(port);
                }
            }
        }

        out.writeBoolean(isSecure());
        out.writeInt(getVersion());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int nullMask = in.readInt();

        String name = null;
        String value = null;
        String comment = null;
        Date expiryDate = null;
        String domain = null;
        String path = null;
        int[] ports = null;
        boolean isSecure = false;
        int version = 0;

        if ((nullMask & MASK_NAME) == 0) {
            name = in.readUTF();
        }
        if ((nullMask & MASK_VALUE) == 0) {
            value = in.readUTF();
        }
        if ((nullMask & MASK_COMMENT) == 0) {
            comment = in.readUTF();
        }
        if ((nullMask & MASK_COMMENT_URL) == 0) {
            in.readUTF();
        }
        if ((nullMask & MASK_EXPIRY_DATE) == 0) {
            expiryDate = new Date(in.readLong());
        }

        in.readBoolean();

        if ((nullMask & MASK_DOMAIN) == 0) {
            domain = in.readUTF();
        }
        if ((nullMask & MASK_PATH) == 0) {
            path = in.readUTF();
        }
        if ((nullMask & MASK_PORTS) == 0) {
            final int len = in.readInt();
            ports = new int[len];
            for (int i = 0; i < len; i++) {
                ports[i] = in.readInt();
            }
        }

        isSecure = in.readBoolean();
        version = in.readInt();

        final BasicClientCookie bc = new BasicClientCookie(name, value);
        bc.setComment(comment);
        bc.setDomain(domain);
        bc.setExpiryDate(expiryDate);
        bc.setPath(path);
        bc.setSecure(isSecure);
        bc.setVersion(version);

        mCookie = bc;
    }

    @Override
    public String toString() {
        if (mCookie == null) {
            return "null";
        } else {
            return mCookie.toString();
        }
    }
}
