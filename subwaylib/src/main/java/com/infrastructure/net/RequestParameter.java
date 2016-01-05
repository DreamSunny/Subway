package com.infrastructure.net;

/**
 * Created by user on 2016/1/4.
 */
public class RequestParameter {
    private String name;
    private String value;

    public RequestParameter(final String name, final String value) {
        this.name = name;
        this.value = value;
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

    @Override
    public String toString() {
        return "RequestParameter{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
