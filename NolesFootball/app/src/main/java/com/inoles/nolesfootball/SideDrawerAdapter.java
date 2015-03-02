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
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class SideDrawerAdapter extends BaseAdapter {
    private final String[] mPrimary;
    private final int mSelectedItem;
    private final LayoutInflater mInflater;

    SideDrawerAdapter(Context context, int selectedItem, String[] primary) {
        mInflater = LayoutInflater.from(context);
        mSelectedItem = selectedItem;
        mPrimary = primary;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public String getItem(int position) {
        return mPrimary[position];
    }

    @Override
    public int getCount() {
        return mPrimary.length;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return mSelectedItem == position ? 1 : 0;
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            if (getItemViewType(position) == 1) {
                view = mInflater.inflate(R.layout.drawer_item_active, viewGroup, false);
            } else {
                view = mInflater.inflate(R.layout.drawer_item, viewGroup, false);
            }
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.mTextView.setText(mPrimary[position]);

        return view;
    }

    /**
     * Custom view holder for our navigation drawers views.
     */
    public static class ViewHolder {
        public final TextView mTextView;
        ViewHolder(View view) {
            mTextView = (TextView) view;
        }
    }
}
