package com.dsunny.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * ListAdapter基类
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected List<T> mValues;

    public BaseListAdapter(List<T> values) {
        mValues = values;
    }

    @Override
    public int getCount() {
        return mValues != null ? mValues.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mValues != null && position < mValues.size() ? mValues.get(position) : null;
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