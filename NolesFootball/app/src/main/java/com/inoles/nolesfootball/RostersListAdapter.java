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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.inoles.nolesfootball.model.Rosters;

import java.util.ArrayList;
import java.util.List;

class RostersListAdapter extends AbstractBaseAdapter<Rosters> implements Filterable
{
    private ArrayFilter mFilter;
    private List<Rosters> mOriginalValues;

    RostersListAdapter(Context context) { super(context); }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = mInflater.inflate(R.layout.rosters_item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Rosters rosters = getItem(position);

        viewHolder.mLastName.setText(rosters.mLastName);
        viewHolder.mFirstName.setText(rosters.mFirstName);
        viewHolder.mPosition.setText(rosters.mPosition);
        viewHolder.mNumber.setText(rosters.mShirtNumber);

        return view;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    static class ViewHolder {
        public final TextView mFirstName;
        public final TextView mLastName;
        public final TextView mPosition;
        public final TextView mNumber;

        ViewHolder(View v) {
            mFirstName = (TextView) v.findViewById(R.id.firstName);
            mLastName = (TextView) v.findViewById(R.id.lastName);
            mPosition = (TextView) v.findViewById(R.id.position);
            mNumber = (TextView) v.findViewById(R.id.shirtNumber);
        }
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<>(mResult);
            }
            FilterResults results = new FilterResults();
            if (charSequence != null && charSequence.length() > 0) {
                List<Rosters> filterList = new ArrayList<>();
                for (Rosters rostersFilter : mOriginalValues) {
                    if (rostersFilter.mLastName.contains(charSequence)) {
                        filterList.add(rostersFilter);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mOriginalValues.size();
                results.values = mOriginalValues;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            //noinspection unchecked
            mResult = (List<Rosters>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
