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

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.itnoles.flavored.AbstractContentListLoader;
import com.itnoles.flavored.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RostersDetailFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<String>> {
    private static final String LOG_TAG = "RostersDetailFragment";

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

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(21, getArguments(), this);
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
        // Clear the data in the adapter.
        mAdapter.clear();
    }

    static class RostersDetailLoader extends AbstractContentListLoader<String> {
        private final String mURL;

        RostersDetailLoader(Context context, String url) {
            super(context);
            mURL = url;
        }

        /**
         * This is where the bulk of our work is done. This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override
        public List<String> loadInBackground() {
            try {
                InputStreamReader reader = Utils.openUrlConnection(mURL);
                JsonReader jsonReader = new JsonReader(reader);
                return readRosters(jsonReader);
            } catch (IOException ioe) {
                Log.w(LOG_TAG, "Problem on i/o", ioe);
            }
            return null;
        }

        private List<String> readRosters(JsonReader reader) throws IOException {
            mResults = new ArrayList<String>();
            try {
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    boolean notNull = reader.peek() != JsonToken.NULL;
                    if ("experience".equals(name) && notNull) {
                        mResults.add("Experience: " + reader.nextString());
                    } else if ("eligibility".equals(name) && notNull) {
                        mResults.add("Class: " + reader.nextString());
                    } else if ("height".equals(name) && notNull) {
                       mResults.add("Height: " + reader.nextString());
                    } else if ("weight".equals(name) && notNull) {
                        mResults.add("Weight: " + reader.nextString());
                    } else if ("hometown".equals(name) && notNull) {
                       mResults.add("Hometown: " + reader.nextString());
                    } else if ("position_event".equals(name)) {
                        mResults.add(reader.nextString().replace("=>", ": "));
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } finally {
                reader.close();
            }
            return mResults;
        }
    }
}