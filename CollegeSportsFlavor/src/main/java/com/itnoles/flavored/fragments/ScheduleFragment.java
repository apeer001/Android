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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itnoles.flavored.*;
import com.itnoles.flavored.model.Event;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.itnoles.flavored.BuildConfig.SCHEDULE_URL;

public class ScheduleFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Event>> {
    private static final String LOG_TAG = "ScheduleFragment";
    private static final String YEAR = "2013";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create an empty adapter we will use to display the loaded data.
        ScheduleListAdapter adapter = new ScheduleListAdapter(getActivity());
        setListAdapter(adapter);

        // If this is under tablet, hide detail view.
        View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null) {
            detailsFrame.setVisibility(View.GONE);
        }

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(10, null, this);
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        return new ScheduleLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {
        // Set the new data in the adapter.
        ((ScheduleListAdapter) getListAdapter()).addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {
        // Clear the data in the adapter.
        ((ScheduleListAdapter) getListAdapter()).clear();
    }

    static class ScheduleLoader extends AbstractContentListLoader<Event> {
        ScheduleLoader(Context context) {
            super(context);
        }

        /**
         * This is where the bulk of our work is done. This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override
        public List<Event> loadInBackground() {
            mResults = new ArrayList<Event>();
            InputStreamReader reader = null;
            try {
                reader = Utils.openUrlConnection(SCHEDULE_URL);
                XmlPullParser parser = XMLUtils.parseXML(reader);
                // The Event that is currently being parsed
                Event currentEvents = null;
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    String name = parser.getName();
                    if (parser.getEventType() == XmlPullParser.START_TAG) {
                        if ("current_events".equals(name) && !YEAR.equals(parser.getAttributeValue(null, "academicYear"))) {
                            break;
                        }

                        if ("event_date".equals(name)) {
                            currentEvents = new Event(parser.getAttributeValue(null, "date"));
                        } else if ("event".equals(name)) {
                            currentEvents.setFullDate(parser.getAttributeValue(null, "eastern_time"));
                            //currentEvents.id = parser.getAttributeValue(null, "id");
                            currentEvents.hn = parser.getAttributeValue(null, "hn");
                            currentEvents.hs = parser.getAttributeValue(null, "hs");
                            currentEvents.vn = parser.getAttributeValue(null, "vn");
                            currentEvents.vs = parser.getAttributeValue(null, "vs");
                        }
                    } else if (parser.getEventType() == XmlPullParser.END_TAG && "event_date".equals(name)) {
                        mResults.add(currentEvents);
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

    private class ScheduleListAdapter extends ArrayAdapter<Event> {
        private final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd 'at' hh:mm", Locale.US);

        public ScheduleListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_item, parent, false);
            }

            Event item = getItem(position);

            TextView date = ViewHolder.get(convertView, R.id.date);
            date.setText(sdf.format(item.fullDate));

            TextView awayTeam = ViewHolder.get(convertView, R.id.away_team);
            awayTeam.setText(item.vn);

            TextView awayScore = ViewHolder.get(convertView, R.id.away_score);
            String vs = item.vs;
            if (vs == null) {
            	awayScore.setVisibility(View.GONE);
            } else {
                awayScore.setText(vs);
                awayScore.setVisibility(View.VISIBLE);
            }

            TextView homeTeam = ViewHolder.get(convertView, R.id.home_team);
            homeTeam.setText(item.hn);

            TextView homeScore = ViewHolder.get(convertView, R.id.home_score);
            String hs = item.hs;
            if (hs == null) {
            	homeScore.setVisibility(View.GONE);
            } else {
                homeScore.setText(hs);
                homeScore.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }
}