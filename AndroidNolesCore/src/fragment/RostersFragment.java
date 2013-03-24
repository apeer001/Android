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

package com.itnoles.shared.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.itnoles.shared.R;
//import com.itnoles.shared.SimpleSectionedListAdapter;
import com.itnoles.shared.io.RostersListLoader;
import com.itnoles.shared.io.model.Rosters;

import java.util.List;

public class RostersFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<List<Rosters>>  {
    private static final int ROSTERS_LOADER = 0x2;
    private RostersListAdapter mRostersAdapter;

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The RostersListAdapter is wrapped in a SimpleSectionedListAdapter so that
        // we can show list headers for staff/players
        mRostersAdapter = new RostersListAdapter(getActivity());
        setListAdapter(new SimpleSectionedListAdapter(getActivity(), R.layout.list_section_header, mRostersAdapter));
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create an empty adapter we will use to display the loaded data.
        mRostersAdapter = new RostersListAdapter(getActivity());
        setListAdapter(mRostersAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        final View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null) {
            detailsFrame.setVisibility(View.GONE);
        }

        getLoaderManager().initLoader(ROSTERS_LOADER, null, this);
    }

    @Override
    public Loader<List<Rosters>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        return new RostersListLoader(getActivity(), args.getString("schoolCode"));
    }

    @Override
    public void onLoadFinished(Loader<List<Rosters>> loader, List<Rosters> data) {
        // Set the new data in the adapter.
        mRostersAdapter.setData(data);

        // The list should now be shown.
        setListShown(true);
    }

    @Override
    public void onLoaderReset(Loader<List<Rosters>> loader) {
        mRostersAdapter.setData(null);
    }

    static class RostersListAdapter extends ArrayAdapter<Rosters> {
        private final LayoutInflater mLayoutInflater;

        public RostersListAdapter(Context context) {
            super(context, 0);
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<Rosters> data) {
            clear();
            if (data != null) {
                for (Rosters rosters : data) {
                    add(rosters);
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
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, null);
            }

            final Rosters item = getItem(position);

            final TextView fullName = ViewHolder.get(convertView, android.R.id.text1);
            fullName.setText(item.getFullName());

            final TextView itemPosition = ViewHolder.get(convertView, android.R.id.text2);
            itemPosition.setText(item.getPosition());

            return convertView;
        }
    }

    static class ViewHolder {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View res = viewHolder.get(id);
            if (res == null) {
                res = view.findViewById(id);
                viewHolder.put(id, res);
            }
            return (T) res;
        }
    }
}