package com.inoles.nolesfootball;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.Collections;
import java.util.List;

abstract class AbstractBaseAdapter<T> extends BaseAdapter {
    private List<T> mResult = Collections.emptyList();

    final LayoutInflater mInflater;

    AbstractBaseAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mResult.size();
    }

    @Override
    public T getItem(int position) { return mResult.get(position); }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void add(List<T> list) { mResult = list; }
}

