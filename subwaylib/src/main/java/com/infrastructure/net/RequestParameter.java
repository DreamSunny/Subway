package com.infrastructure.net;

import java.io.Serializable;

/**
 * Created by user on 2016/1/4.
 */
public class RequestParameter implements Serializable, Comparable<Object> {

    private static final long serialVersionUID = 1L;

    private String name;
    private String value;

    public RequestParameter(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public int compareTo(Object o) {
        // TODO
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        // TODO
        return super.equals(o);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
