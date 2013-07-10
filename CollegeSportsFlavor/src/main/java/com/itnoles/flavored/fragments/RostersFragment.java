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

package com.itnoles.flavored.fragment;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*; // Menu, MenuInflater, MenuItem, View and ViewGroup
import android.widget.*; //ArrayAdapter, ListView, SearchView and TextView

import com.android.volley.Response.Listener;
import com.itnoles.flavored.activities.RostersDetailActivity;
import com.itnoles.flavored.*; // R, Rosters, SectionedListAdapter, VolleyHelper and XMLRequest

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.itnoles.flavored.BuildConfig.SCHOOL_CODE;

public class RostersFragment extends ListFragment implements SearchView.OnQueryTextListener {
    private static final String LOG_TAG = "RostersFragment";

    private boolean mDualPane;
    private int mShownCheckPosition = -1;
    private SectionedListAdapter mAdapter;
    private List<Rosters> staffRosters = new ArrayList<Rosters>();
    private List<Rosters> playerRosters = new ArrayList<Rosters>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // The SectionedListAdapter has a header to group players and staff
        mAdapter = new SectionedListAdapter(getActivity(), R.layout.list_section_header);
        setListAdapter(mAdapter);

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
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load the Data if SectionedListAdapter with Section is empty.
        if (mAdapter.isEmpty()) {
            XMLRequest xr = new XMLRequest("http://grfx.cstv.com/schools/" + SCHOOL_CODE + "/data/xml/roster/m-footbl-2012.xml",
                createMyReqSuccessListener());
            VolleyHelper.getResultQueue().add(xr);
        }
    }

    private void getRostersResult(XmlPullParser parser) {
        try {
            // The Rosters that is currently being parsed
            Rosters currentRosters = null;
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if ("player".equals(name) || "asst_coach_lev1".equals(name) || "asst_coach_lev2".equals(name) || "asst_coach_lev3".equals(name)
                        || "head_coach".equals(name) || "other".equals(name)) {
                        currentRosters = new Rosters();
                    } else if (currentRosters != null) {
                        currentRosters.setValue(name, parser.nextText());
                    }
                } else if (parser.getEventType() == XmlPullParser.END_TAG) {
                    if ("asst_coach_lev1".equals(name) || "asst_coach_lev2".equals(name) || "asst_coach_lev3".equals(name)
                        || "head_coach".equals(name) || "other".equals(name)) {
                        staffRosters.add(currentRosters);
                    } else if ("player".equals(name)) {
                        playerRosters.add(currentRosters);
                    }
                }
            }
        } catch (XmlPullParserException e) {
            Log.w(LOG_TAG, "Malformed response for ", e);
        } catch (IOException ioe) {
            Log.w(LOG_TAG, "Problem on reading on file", ioe);
        }
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
            intent.putExtra("url", urlString);
            startActivity(intent);
        }
    }

    private Listener<XmlPullParser> createMyReqSuccessListener() {
        return new Listener<XmlPullParser>() {
            @Override
            public void onResponse(XmlPullParser response) {
                getRostersResult(response);
                mAdapter.addSection("2012 Athlete Roster", new RostersListAdapter(getActivity(), playerRosters));
                mAdapter.addSection("2012 Coaches and Staff", new RostersListAdapter(getActivity(), staffRosters));
                mAdapter.notifyDataSetChanged();
            }
        };
    }

    private class RostersListAdapter extends ArrayAdapter<Rosters> {
        public RostersListAdapter(Context context, List<Rosters> data) {
            super(context, 0, data);
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
                convertView = getActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_2, parent, false);

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
    }

    static class ViewHolder {
        TextView fullName;
        TextView position;
    }
}