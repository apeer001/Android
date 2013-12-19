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

package com.inoles.collegesports;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class RostersFragment extends ListFragment implements SearchView.OnQueryTextListener {
    static class Rosters {
        String details;
        String bioId;
        String FirstName;
        String LastName;
        String Position;

        public void setValue(String key, String value) {
            switch (key) {
                case "bio_id":
                    bioId = value;
                    break;
                case "details":
                    details = value;
                    break;
                case "first_name":
                    FirstName = value;
                    break;
                case "last_name":
                    LastName = value;
                    break;
                case "position":
                    Position = value;
                    break;
                default:
            }
        }

        @Override
        public String toString() { return LastName; }
    }

    private static final String LOG_TAG = "RostersFragment";

    private boolean mDualPane;
    private int mShownCheckPosition = -1;
    private RostersListAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // if getActivity is null, return early for unfortunate situations
        if (getActivity() == null) {
            return;
        }

        mAdapter = new RostersListAdapter(getActivity());
        setListAdapter(mAdapter);

        // Determine whether we are in single-pane or dual-pane mode by testing the visibility
        // of the detail view.
        View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null && detailsFrame.getVisibility() != View.VISIBLE) {
            detailsFrame.setVisibility(View.VISIBLE);
        }
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (mDualPane && getListView() != null) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        Ion.with(getActivity(), BuildConfig.ROSTER_URL).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String s) {
                load(s);
            }
        });
    }

    private void load(String xmlString) {
        StringReader sr = new StringReader(xmlString);
        try {
            XmlPullParser parser = ParserUtils.newPullParser(sr);
            List<Rosters> results = new ArrayList<>();
            // The Rosters that is currently being parsed
            Rosters currentRosters = null;
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if ("player".equals(name) || "asst_coach_lev1".equals(name)
                            || "asst_coach_lev2".equals(name) || "asst_coach_lev3".equals(name)
                            || "head_coach".equals(name) || "other".equals(name)) {
                        currentRosters = new Rosters();
                    } else if (currentRosters != null) {
                        currentRosters.setValue(name, parser.nextText());
                    }
                } else if (parser.getEventType() == XmlPullParser.END_TAG
                        && "asst_coach_lev1".equals(name) || "asst_coach_lev2".equals(name)
                        || "asst_coach_lev3".equals(name) || "head_coach".equals(name)
                        || "other".equals(name) || "player".equals(name)) {
                    results.add(currentRosters);
                }
            }
            mAdapter.addAll(results);
        } catch (IOException | XmlPullParserException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            sr.close();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflate the menu
        inflater.inflate(R.menu.roster_fragment, menu);

        // find the search item
        MenuItem searchViewMenuItem = menu.findItem(R.id.menu_search);
        if (searchViewMenuItem == null) {
            return;
        }

        // Retrieve the Search View
        SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed. Since this
        // is a simple array adapter, we can just have it do the filtering.
        mAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return true;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Rosters item = mAdapter.getItem(position);
        String url = item.details + '/' + item.bioId + ".json";
        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            if (getListView() != null) {
                getListView().setItemChecked(position, true);
            }

            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                RostersDetailFragment rdf = RostersDetailFragment.newInstance(url);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                if (getFragmentManager() == null) {
                    return;
                }
                getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_details, rdf)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
                mShownCheckPosition = position;
            }
        } else {
            Intent intent = new Intent(getActivity(), RostersDetailActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        }
    }

    class RostersListAdapter extends ArrayAdapter<Rosters> {
        private LayoutInflater mInflater;

        public RostersListAdapter(Context context) {
            super(context, 0);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.rosters_item, parent, false);

                holder = new ViewHolder();
                holder.name = (RostersTextView) convertView.findViewById(android.R.id.text1);
                holder.position = (TextView) convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Rosters item = getItem(position);
            holder.name.setText(item.FirstName, item.LastName);
            holder.position.setText(item.Position);

            return convertView;
        }

        private class ViewHolder {
            RostersTextView name;
            TextView position;
        }
    }
}