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

package com.itnoles.flavored;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RostersListAdapter extends ArrayAdapter<Rosters> {
    private StringBuffer buffer = new StringBuffer();

    private final LayoutInflater mInflater;

    public RostersListAdapter(Context context, List<Rosters> data) {
        super(context, 0, data);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Populate new items in the list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        if (convertView == null) {
            view = mInflater.inflate(android.R.layout.simple_list_item_2, parent, false);

            holder = new ViewHolder();
            holder.fullName = (TextView) view.findViewById(android.R.id.text1);
            holder.position = (TextView) view.findViewById(android.R.id.text2);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        Rosters item = getItem(position);

        buffer.setLength(0);
        buffer.append(item.lastName);
        buffer.append(", ");
        buffer.append(item.firstName);
        holder.fullName.setText(buffer.toString());

        holder.position.setText(item.position);

        return view;
    }

    static class ViewHolder {
        TextView fullName;
        TextView position;
    }
}