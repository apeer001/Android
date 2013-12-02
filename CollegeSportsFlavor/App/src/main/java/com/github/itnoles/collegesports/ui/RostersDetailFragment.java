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

package com.github.itnoles.collegesports.ui;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.github.itnoles.collegesports.XMLContentLoader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

class RostersDetailFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<String>> {
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

        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(21, getArguments(), this).forceLoad();
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        return new XMLContentLoader<>(getActivity(), args.getString("url"), new RostersLoader());
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

    private static class RostersLoader implements XMLContentLoader.ResponseListener<String> {
        @Override
        public void onPostExecute(XmlPullParser parser, List<String> results) throws IOException, XmlPullParserException {
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    switch (parser.getName()) {
                        case "experience":
                            results.add("Experience: " + parser.nextText());
                            break;
                        case "eligibility":
                            results.add("Class: " + parser.nextText());
                            break;
                        case "height":
                            results.add("Height: " + parser.nextText());
                            break;
                        case "weight":
                            results.add("Weight: " +  parser.nextText());
                            break;
                        case "hometown":
                            results.add("Hometown: " + parser.nextText());
                            break;
                        case "position_event":
                            results.add(parser.nextText().replace("=>", ": "));
                            break;
                        default:
                    }
                }
            }
        }
    }
}