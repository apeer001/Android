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
    public RostersListAdapter(Context context, List<Rosters> data) {
        super(context, 0, data);
    }

    /**
     * Populate new items in the list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rosters_item, parent, false);
        }

        Rosters item = getItem(position);

        TextView firstname = ViewHolder.get(convertView, R.id.first_name);
        firstname.setText(item.firstName);

        TextView lastname = ViewHolder.get(convertView, R.id.last_name);
        lastname.setText(item.lastName);

        TextView rosterPos = ViewHolder.get(convertView, R.id.position);
        rosterPos.setText(item.position);

        return convertView;
    }
}