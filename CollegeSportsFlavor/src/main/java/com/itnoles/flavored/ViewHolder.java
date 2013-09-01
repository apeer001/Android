package com.itnoles.flavored;

import android.util.SparseArray;
import android.view.View;

public class ViewHolder {
    private ViewHolder() {}

    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View res = viewHolder.get(id);
        if (res == null) {
            res = view.findViewById(id);
            viewHolder.put(id, res);
        }
        return (T) res;
    }
}
