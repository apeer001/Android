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

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.itnoles.nolesfootball.activities.RostersDetailActivity;
import com.itnoles.nolesfootball.R;
import com.itnoles.nolesfootball.SectionedListAdapter;
import com.itnoles.nolesfootball.io.RostersListLoader;
import com.itnoles.nolesfootball.io.model.Rosters;
import com.itnoles.nolesfootball.util.Lists;

import java.util.List;

public class RostersFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Rosters>>, SearchView.OnQueryTextListener {
    private static final int ROSTERS_LOADER = 0x3;

    private boolean mDualPane;
    private int mShownCheckPosition = -1;

    // This is the Adapter being used to display the list's data.
    private SectionedListAdapter mAdapter;

    // If non-null, this is the current filter the user has provided.
    private String mCurFilter;

    private List<Rosters> staffRosters = Lists.newArrayList();
    private List<Rosters> playerRosters = Lists.newArrayList();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Don't do animation and progress indicatior, just show empty view
        setListShownNoAnimation(true);

        // The SectionedListAdapter has a header to group players and staff
        mAdapter = new SectionedListAdapter(getActivity(), R.layout.list_section_header);

        // Determine whether we are in single-pane or dual-pane mode by testing the visibility
        // of the detail view.
        View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null && detailsFrame.getVisibility() != View.VISIBLE) {
            detailsFrame.setVisibility(View.VISIBLE);
        }
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        getLoaderManager().initLoader(ROSTERS_LOADER, null, this);
    }

    @Override
    public Loader<List<Rosters>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        return new RostersListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Rosters>> loader, List<Rosters> data) {
        for (Rosters roster : data) {
            if (roster.isStaff) {
                staffRosters.add(roster);
            } else {
                playerRosters.add(roster);
            }
        }

        mAdapter.addSection("2012 Players", new RostersListAdapter(getActivity(), playerRosters));
        mAdapter.addSection("2012 Staff", new RostersListAdapter(getActivity(), staffRosters));
        setListAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Rosters>> loader) {
        ((RostersListAdapter) mAdapter.getListAdapter(0)).clear();
        ((RostersListAdapter) mAdapter.getListAdapter(1)).clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.roster_fragment, menu);
        MenuItem searchViewMenuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed. Since this
        // is a simple array adapter, we can just have it do the filtering.
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
        ((RostersListAdapter) mAdapter.getListAdapter(0)).getFilter().filter(mCurFilter);
        ((RostersListAdapter) mAdapter.getListAdapter(1)).getFilter().filter(mCurFilter);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return true;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Rosters item = (Rosters) getListAdapter().getItem(position);
        String urlString = item.details + "/" + item.bioId + ".xml";
        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(position, true);

            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                RostersDetailFragment rdf = RostersDetailFragment.newInstance(urlString);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_details, rdf)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
                mShownCheckPosition = position;
            }
        } else {
            Intent intent = new Intent(getActivity(), RostersDetailActivity.class);
            intent.putExtra("title", item.firstName + " " + item.lastName);
            intent.putExtra("subtitle", item.position);
            intent.putExtra("url", urlString);
            startActivity(intent);
        }
    }

    static class RostersListAdapter extends ArrayAdapter<Rosters> {
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
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_2, parent, false);

                holder = new ViewHolder();
                holder.fullName = (TextView) convertView.findViewById(android.R.id.text1);
                holder.position = (TextView) convertView.findViewById(android.R.id.text2);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Rosters item = getItem(position);
            holder.fullName.setText(item.lastName + ", " + item.firstName);
            holder.position.setText(item.position);

            return convertView;
        }

        static class ViewHolder {
            TextView fullName;
            TextView position;
        }
    }
}