/*
 * Copyright (C) 2011 Jonathan Steele
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.shared.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itnoles.shared.R;
import com.itnoles.shared.util.News;

import java.util.List;

public class NewsListAdapter extends ArrayAdapter<News> {
    private final LayoutInflater mLayoutInflater;

    public NewsListAdapter(Context context) {
        super(context, 0);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<News> data) {
        clear();
        if (data != null) {
            for (News news : data) {
                add(news);
            }
        }
    }

    /**
     * Populate new items in the list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid
        // unneccessary calls to findViewById() on each row.
        ViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.headlines_item, null);

            // Creates a ViewHolder and store references to the three
            // children views we want to bind data to.
            holder = new ViewHolder();
            holder.mTitle = (TextView) convertView.findViewById(R.id.title);
            holder.mDate = (TextView) convertView.findViewById(R.id.date);
            holder.mDesc = (TextView) convertView.findViewById(R.id.description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final News news = getItem(position);
        holder.mTitle.setText(news.getTitle());
        holder.mDate.setText(news.getPubDate());
        holder.mDesc.setText(news.getDesc());

        return convertView;
    }

    static class ViewHolder {
        TextView mTitle;
        TextView mDate;
        TextView mDesc;
    }
}