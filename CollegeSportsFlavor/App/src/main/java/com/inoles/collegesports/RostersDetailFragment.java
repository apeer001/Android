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
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class RostersDetailFragment extends ListFragment {
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

        if (getActivity() != null) {
            mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
            setListAdapter(mAdapter);
        }

        if (getArguments() != null) {
            Ion.with(getActivity(), getArguments().getString("url")).asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            load(s);
                        }
                    });
        }
    }

    private void load(String xmlString) {
        StringReader sr = new StringReader(xmlString);
        try {
            XmlPullParser parser = ParserUtils.newPullParser(sr);
            List<String> results = new ArrayList<>();
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
            mAdapter.addAll(results);
        } catch (IOException | XmlPullParserException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            sr.close();
        }
    }
}