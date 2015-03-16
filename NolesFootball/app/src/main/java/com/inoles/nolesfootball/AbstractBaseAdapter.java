/*
 * Copyright (C) 2015 Jonathan Steele
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.inoles.nolesfootball;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.Collections;
import java.util.List;

abstract class AbstractBaseAdapter<T> extends BaseAdapter {
    List<T> mResult = Collections.emptyList();

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

