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

import android.app.ListFragment;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.Response.Listener;
import com.itnoles.flavored.JsonRequest;
import com.itnoles.flavored.VolleyHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RostersDetailFragment extends ListFragment {
    private static final String LOG_TAG = "RostersDetailFragment";

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

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(adapter);

        JsonRequest xr = new JsonRequest(getArguments().getString("url"), new Listener<JsonReader>() {
            @Override
            public void onResponse(JsonReader response) {
                adapter.addAll(getResult(response));
            }
        });
        VolleyHelper.getResultQueue().add(xr);
    }

    private List<String> getResult(JsonReader reader) {
        List<String> results = new ArrayList<String>();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (reader.peek() == JsonToken.NULL) {
                    // Ignore null values
                    reader.skipValue();
                }
                if ("experience".equals(name)) {
                    results.add("Experience: " + reader.nextString());
                } else if ("eligibility".equals(name)) {
                    results.add("Class: " + reader.nextString());
                } else if ("height".equals(name)) {
                    results.add("Height: " + reader.nextString());
                } else if ("weight".equals(name)) {
                    results.add("Weight: " + reader.nextString());
                } else if ("hometown".equals(name)) {
                    results.add("Hometown: " + reader.nextString());
                } else if ("position_event".equals(name)) {
                    results.add(reader.nextString().replace("=>", ": "));
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            Log.w(LOG_TAG, "Problem on reading on file", e);
        } finally {
            try {
                reader.close();
            } catch (IOException ioe) {
                Log.w(LOG_TAG, "Can't close JsonReader", ioe);
            }
        }
        return results;
    }
}