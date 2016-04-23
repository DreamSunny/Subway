package com.dsunny.common;

import android.util.SparseArray;
import android.view.View;

/**
 * ListView适配器中保存view
 */
public class ViewHolder {
    private final SparseArray<View> mViews;
    private View mConvertView;

    private ViewHolder(final View convertView) {
        mViews = new SparseArray<>();
        mConvertView = convertView;
    }

    /**
     * 获取ViewHolder
     *
     * @param convertView Adapter中getView方法参数
     * @return convertView的ViewHolder
     */
    public static ViewHolder get(final View convertView) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        return viewHolder;
    }

    /**
     * 获取View
     *
     * @param viewId 视图ID
     * @param <T>    视图类型
     * @return 视图
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(final int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

}
