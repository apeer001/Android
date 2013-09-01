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

package com.itnoles.flavored.fragments;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.*; // Menu, MenuInflater, MenuItem and View
import android.widget.ListView;
import android.widget.SearchView;

import com.itnoles.flavored.activities.RostersDetailActivity;
import com.itnoles.flavored.*;
import com.itnoles.flavored.model.Rosters;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.itnoles.flavored.BuildConfig.ROSTER_URL;

public class RostersFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Rosters>>, SearchView.OnQueryTextListener {
    private static final String LOG_TAG = "RostersFragment";

    private boolean mDualPane;
    private int mShownCheckPosition = -1;
    private SectionedListAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListShownNoAnimation(true);

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // The SectionedListAdapter has a header to group players and staff
        mAdapter = new SectionedListAdapter(getActivity());

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

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(20, null, this);
    }

    @Override
    public Loader<List<Rosters>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        return new RostersLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Rosters>> loader, List<Rosters> data) {
        // Set the new data in the adapter.
        List<Rosters> playerRosters = filter(new Predicate<Rosters>() {
            @Override
            public boolean apply(Rosters rosters) {
                return !rosters.isStaff;
            }
        }, data);
        mAdapter.addSection("2012 Athlete Roster", new RostersListAdapter(getActivity(), playerRosters));

        List<Rosters> staffRosters = filter(new Predicate<Rosters>() {
        	public boolean apply(Rosters rosters) {
        		return rosters.isStaff;
        	}
        }, data);
        mAdapter.addSection("2012 Coaches and Staff", new RostersListAdapter(getActivity(), staffRosters));
        setListAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Rosters>> loader) {
        // Clear the data in the adapter.
        mAdapter.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflate the menu
        inflater.inflate(R.menu.roster_fragment, menu);

        // find the search item
        MenuItem searchViewMenuItem = menu.findItem(R.id.menu_search);

        // Retrieve the Search View
        SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed. Since this
        // is a simple array adapter, we can just have it do the filtering.
        ((RostersListAdapter) mAdapter.getListAdapter(0)).getFilter().filter(newText);
        ((RostersListAdapter) mAdapter.getListAdapter(1)).getFilter().filter(newText);
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
        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(position, true);

            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                RostersDetailFragment rdf = RostersDetailFragment.newInstance(item.getFullURL());

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
            intent.putExtra("url", item.getFullURL());
            intent.putExtra("title", item.getFirstAndLastName());
            startActivity(intent);
        }
    }

    private static List<Rosters> filter(Predicate<Rosters> predicate, List<Rosters> source) {
        List<Rosters> destiny = new ArrayList<Rosters>();
        for (Rosters item : source) {
            if (predicate.apply(item)) {
                destiny.add(item);
            }
        }
        return destiny;
    }

    static class RostersLoader extends AbstractContentListLoader<Rosters> {
        RostersLoader(Context context) {
            super(context);
        }

        /**
         * This is where the bulk of our work is done. This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override
        public List<Rosters> loadInBackground() {
            mResults = new ArrayList<Rosters>();
            InputStreamReader reader = null;
            try {
                reader = Utils.openUrlConnection(ROSTER_URL);
                XmlPullParser parser = XMLUtils.parseXML(reader);
                // The Rosters that is currently being parsed
                Rosters currentRosters = null;
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    String name = parser.getName();
                    if (parser.getEventType() == XmlPullParser.START_TAG) {
                            if ("player".equals(name) || "asst_coach_lev1".equals(name) || "asst_coach_lev2".equals(name)
                                || "asst_coach_lev3".equals(name) || "head_coach".equals(name) || "other".equals(name)) {
                                currentRosters = new Rosters(!"player".equals(name));
                            } else if (currentRosters != null) {
                                currentRosters.setValue(name, parser.nextText());
                            }
                    } else if (parser.getEventType() == XmlPullParser.END_TAG && "asst_coach_lev1".equals(name)
                        || "asst_coach_lev2".equals(name) || "asst_coach_lev3".equals(name) || "head_coach".equals(name)
                        || "other".equals(name) || "player".equals(name)) {
                        mResults.add(currentRosters);
                    }
                }
            } catch (XmlPullParserException xppe) {
                Log.w(LOG_TAG, "Problem on parsing xml file", xppe);
            } catch (IOException ioe) {
                Log.w(LOG_TAG, "Problem on xml file", ioe);
            } finally {
                Utils.ignoreQuietly(reader);
            }
            return mResults;
        }
    }
}