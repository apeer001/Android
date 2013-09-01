/*
 * Copyright (C) 2013 Jonathan Steele
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
/**
 * Took some idea from
 * http://code.google.com/p/dailyburndroid/source/browse/branches/ui_changes/src/com/commonsware/android/listview/SectionedAdapter.java
 * https://github.com/commonsguy/cwac-merge/blob/master/src/com/commonsware/cwac/merge/MergeAdapter.java
 */
package com.itnoles.flavored;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A very simple adapter that adds sections to adapters written for ListView.
 *
 * Warning, There is a timing issue for this class that will crash by scrolling.
 * Make sure you call setListAdapter AFTER addSection.
 */
public class SectionedListAdapter extends BaseAdapter {
    private static final int TYPE_SECTION_HEADER = 0;

    private final LayoutInflater mLayoutInflater;
    private final List<Section> mSections = new ArrayList<Section>();

    public SectionedListAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class Section {
        final String caption;
        final ListAdapter adapter;

        Section(String headerCaption, ListAdapter listAdapter) {
            caption = headerCaption;
            adapter = listAdapter;
        }
    }

    public void addSection(String caption, ListAdapter adapter) {
        mSections.add(new Section(caption, adapter));
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                notifyDataSetInvalidated();
            }
        });
    }

    public ListAdapter getListAdapter(int position) {
        for (Section section : mSections) {
            int size = section.adapter.getCount() + 1;
            if (position < size) {
                return section.adapter;
            }
            position -= size;
        }
        return null;
    }

    public void clear() {
        mSections.clear();
    }

    @Override
    public Object getItem(int position) {
        for (Section section : mSections) {
            if (position == 0) {
                return section;
            }

            int size = section.adapter.getCount() + 1;
            if (position < size) {
                return section.adapter.getItem(position - 1);
            }
            position -= size;
        }
        return null;
    }

    @Override
    public int getCount() {
        int total = 0;
        for (Section section : mSections) {
            total += section.adapter.getCount() + 1; // add one for header
        }
        return total;
    }

    @Override
    public int getViewTypeCount() {
        int total = 1; // one for the header, plus those from sections
        for (Section section : mSections) {
            total += section.adapter.getViewTypeCount();
        }
        return total;
    }

    @Override
    public int getItemViewType(int position) {
        int typeOffset = 1; // start counting from here

        for (Section section : mSections) {
            if (position == 0) {
                return TYPE_SECTION_HEADER;
            }

            int size = section.adapter.getCount() + 1;
            if (position < size) {
                return typeOffset + section.adapter.getItemViewType(position - 1);
            }

            position -= size;
            typeOffset += section.adapter.getViewTypeCount();
        }

        return -1;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != TYPE_SECTION_HEADER;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        for (Section section : mSections) {
            if (position == 0) {
                TextView view = (TextView) convertView;
                if (view == null) {
                    view = (TextView) mLayoutInflater.inflate(R.layout.list_section_header, parent, false);
                }
                view.setText(section.caption);
                return view;
            }

            int size = section.adapter.getCount() + 1;
            if (position < size) {
                return section.adapter.getView(position - 1, convertView, parent);
            }
            position -= size;
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}