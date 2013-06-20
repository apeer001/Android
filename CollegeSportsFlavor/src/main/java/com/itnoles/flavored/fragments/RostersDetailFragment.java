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
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.Response.Listener;
import com.itnoles.flavored.VolleyHelper;
import com.itnoles.flavored.XMLRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

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

        XMLRequest xr = new XMLRequest(getArguments().getString("url"), new Listener<XmlPullParser>() {
            @Override
            public void onResponse(XmlPullParser response) {
                adapter.addAll(getResult(response));
            }
        });
        VolleyHelper.getResultQueue().add(xr);
    }

    private List<String> getResult(XmlPullParser parser) {
        List<String> results = new ArrayList<String>();
        try {
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if ("experience".equals(name)) {
                        results.add("Experience: " + parser.nextText());
                    } else if ("eligibility".equals(name)) {
                        results.add("Class: " + parser.nextText());
                    } else if ("height".equals(name)) {
                        results.add("Height: " + parser.nextText());
                    } else if ("weight".equals(name)) {
                        results.add("Weight: " + parser.nextText());
                    } else if ("hometown".equals(name)) {
                        results.add("Hometown: " + parser.nextText());
                    }
                }
            }
        } catch (XmlPullParserException e) {
            Log.w(LOG_TAG, "Malformed response for ", e);
        } catch (IOException ioe) {
            Log.w(LOG_TAG, "Problem on reading on file", ioe);
        }
        return results;
    }
}