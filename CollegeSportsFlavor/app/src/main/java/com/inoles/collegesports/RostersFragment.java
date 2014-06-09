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

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Xml;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class RostersFragment extends ListFragment implements SearchView.OnQueryTextListener {
    static class Rosters {
        String FirstName;
        String LastName;
        String Position;

        public void setValue(String key, String value) {
            switch (key) {
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

    private RostersListAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        mAdapter = new RostersListAdapter(getActivity());
        setListAdapter(mAdapter);

        StringReaderRequest sr = new StringReaderRequest(BuildConfig.ROSTER_URL, new Response.Listener<StringReader>() {
            @Override
            public void onResponse(StringReader response) {
                load(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOG_TAG, error.getMessage());
            }
        });
        sr.setTag(this);

        MainActivity.sQueue.add(sr);
    }

    void load(StringReader reader) {
        List<Rosters> results = new ArrayList<>();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(reader);
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
             reader.close();
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
    public void onDestroy() {
        super.onDestroy();

        mAdapter.clear();
    }

    class RostersListAdapter extends ArrayAdapter<Rosters> {
        private class ViewHolder {
            final RostersTextView name;
            final TextView position;

            public ViewHolder(View view) {
                name = (RostersTextView) view.findViewById(android.R.id.text1);
                position = (TextView) view.findViewById(android.R.id.text2);
                view.setTag(this);
            }
        }

        private final LayoutInflater mInflater;

        public RostersListAdapter(Context context) {
            super(context, 0);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Nullable
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unnecessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to re-inflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.rosters_item, parent, false);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder(convertView);
            } else {
                // Get the ViewHolder back to get fast access to two TextView
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            Rosters item = getItem(position);
            holder.name.setText(item.FirstName, item.LastName);
            holder.position.setText(item.Position);

            return convertView;
        }
    }
}