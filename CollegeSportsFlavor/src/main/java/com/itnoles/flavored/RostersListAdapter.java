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

import com.itnoles.flavored.model.Rosters;

import java.util.List;

public class RostersListAdapter extends ArrayAdapter<Rosters> {
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
        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder = ViewHolder.get(convertView, parent);

        Rosters item = getItem(position);
        holder.firstname.setText(item.firstName);
        holder.lastname.setText(item.lastName);
        holder.position.setText(item.position);

        return holder.root;
    }

    static class ViewHolder {
        public final View root;
        public final TextView firstname;
        public final TextView lastname;
        public final TextView position;

        private ViewHolder(ViewGroup parent) {
            root = LayoutInflater.from(parent.getContext()).inflate(R.layout.rosters_item, parent, false);
            root.setTag(this);

            firstname = (TextView) root.findViewById(R.id.first_name);
            lastname = (TextView) root.findViewById(R.id.last_name);
            position = (TextView) root.findViewById(R.id.position);
        }

        public static ViewHolder get(View convertView, ViewGroup parent) {
            if (convertView == null) {
                return new ViewHolder(parent);
            }
            return (ViewHolder) convertView.getTag();
        }
    }
}