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
import android.widget.ArrayAdapter;

import com.android.volley.Response.Listener;
import com.itnoles.flavored.util.AbstractJsonRequest;
import com.itnoles.flavored.util.VolleyHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RostersDetailFragment extends ListFragment {
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

        RostersDetailRequests rdr = new RostersDetailRequests(getArguments().getString("url"), new Listener<List<String>>() {
            @Override
            public void onResponse(List<String> response) {
                adapter.addAll(response);
            }
        });
        VolleyHelper.getResultQueue().add(rdr);
    }

    static class RostersDetailRequests extends AbstractJsonRequest<List<String>> {
        RostersDetailRequests(String url, Listener<List<String>> listener) {
            super(url, listener);
        }

        public List<String> onPostNetworkResponse(JsonReader reader) throws IOException {
            List<String> results = new ArrayList<String>();
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                boolean notNull = reader.peek() != JsonToken.NULL;
                if ("experience".equals(name) && notNull) {
                    results.add("Experience: " + reader.nextString());
                } else if ("eligibility".equals(name) && notNull) {
                    results.add("Class: " + reader.nextString());
                } else if ("height".equals(name) && notNull) {
                    results.add("Height: " + reader.nextString());
                } else if ("weight".equals(name) && notNull) {
                    results.add("Weight: " + reader.nextString());
                } else if ("hometown".equals(name) && notNull) {
                    results.add("Hometown: " + reader.nextString());
                } else if ("position_event".equals(name)) {
                    results.add(reader.nextString().replace("=>", ": "));
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return results;
        }
    }
}