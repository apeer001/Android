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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.nolesfootball.fragment;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itnoles.nolesfootball.R;
import com.itnoles.nolesfootball.SectionedListAdapter;
import com.itnoles.nolesfootball.io.ScheduleListLoader;
import com.itnoles.nolesfootball.io.model.Schedule;

import java.util.List;

public class ScheduleFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Schedule>> {
    private static final int SCHEDULE_LOADER = 0x2;

    private SectionedListAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Don't do animation and progress indicatior, just show empty view
        setListShownNoAnimation(true);

        // The SectionedListAdapter is going to show schedule header for overall and conference records
        mAdapter = new SectionedListAdapter(getActivity(), R.layout.list_section_header);

        // If this is under tablet, hide detail view.
        View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null) {
            detailsFrame.setVisibility(View.GONE);
        }

        getLoaderManager().initLoader(SCHEDULE_LOADER, null, this);
    }

    @Override
    public Loader<List<Schedule>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        return new ScheduleListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Schedule>> loader, List<Schedule> data) {
        // Set the new data in the adapter.
        String header = ((ScheduleListLoader) loader).getHeader();
        mAdapter.addSection(header, new ScheduleListAdapter(getActivity(), data));
        setListAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Schedule>> loader) {
        ((ScheduleListAdapter) mAdapter.getListAdapter(0)).clear();
    }

    static class ScheduleListAdapter extends ArrayAdapter<Schedule> {
        private final LayoutInflater mLayoutInflater;

        public ScheduleListAdapter(Context context, List<Schedule> data) {
            super(context, 0, data);
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * Populate new items in the list.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.schedule_item, parent, false);

                holder = new ViewHolder();
                holder.school = (TextView) convertView.findViewById(R.id.school);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.tv = (TextView) convertView.findViewById(R.id.tv);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Schedule item = getItem(position);
            holder.school.setText(item.school);
            holder.date.setText(item.date);
            holder.time.setText(item.time);
            holder.tv.setText(item.tv);

            return convertView;
        }

        static class ViewHolder {
            TextView school;
            TextView date;
            TextView time;
            TextView tv;
        }
    }
}