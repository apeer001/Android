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
import android.content.Loader;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.itnoles.nolesfootball.io.RostersDetailLoader;

import java.util.List;

public class RostersDetailFragment extends ListFragment  implements LoaderManager.LoaderCallbacks<List<String>> {
    private static final int ROSTERS_DETAIL_LOADER = 0x31;

    // This is the Adapter being used to display the list's data.
    private ArrayAdapter<String> mAdapter;

    public static RostersDetailFragment newInstance(String urlString) {
        RostersDetailFragment f = new RostersDetailFragment();

        // Supply url input as an argument.
        Bundle args = new Bundle();
        args.putString("url", urlString);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String title = getArguments().getString("title");
        if (title != null) {
            getActivity().getActionBar().setTitle(title);
        }

        String subtitle = getArguments().getString("subtitle");
        if (subtitle != null) {
            getActivity().getActionBar().setSubtitle(subtitle);
        }

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(ROSTERS_DETAIL_LOADER, getArguments(), this);
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        return new RostersDetailLoader(getActivity(), args.getString("url"));
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
        // Set the new data in the adapter.
        mAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {
        mAdapter.clear();
    }
}