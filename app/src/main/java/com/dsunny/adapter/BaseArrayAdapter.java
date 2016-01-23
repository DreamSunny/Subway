package com.dsunny.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * ArrayAdapter基类
 */
public abstract class BaseArrayAdapter<T> extends BaseAdapter {

    protected T[] mValues;

    public BaseArrayAdapter(T[] values) {
        mValues = values;
    }

    @Override
    public int getCount() {
        return mValues != null ? mValues.length : 0;
    }

    @Override
    public T getItem(int position) {
        return mValues != null && position < mValues.length ? mValues[position] : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getConvertView(position, convertView);
    }

    abstract View getConvertView(int position, View convertView);
}
