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

<<<<<<< HEAD
package com.itnoles.flavored.fragments;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
=======
package com.itnoles.flavored.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.JsonReader;
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

<<<<<<< HEAD
import com.itnoles.flavored.AbstractContentListLoader;
import com.itnoles.flavored.model.Event;
import com.itnoles.flavored.R;
import com.itnoles.flavored.Utils;
import com.itnoles.flavored.XMLUtils;

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
=======
import com.android.volley.Response.Listener;
import com.itnoles.flavored.R;
import com.itnoles.flavored.SectionedListAdapter;
import com.itnoles.flavored.util.AbstractJsonRequest;
import com.itnoles.flavored.util.VolleyHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.itnoles.flavored.BuildConfig.SCHEDULE_URL;

public class ScheduleFragment extends ListFragment {
    String header;
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

<<<<<<< HEAD
        // Create an empty adapter we will use to display the loaded data.
        ScheduleListAdapter adapter = new ScheduleListAdapter(getActivity());
        setListAdapter(adapter);

=======
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
        // If this is under tablet, hide detail view.
        View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null) {
            detailsFrame.setVisibility(View.GONE);
        }

<<<<<<< HEAD
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
=======
        ScheduleRequests sr = new ScheduleRequests(new Listener<List<Schedule>>() {
            @Override
            public void onResponse(List<Schedule> response) {
                // The SectionedListAdapter is going to show schedule header for overall and conference records
                SectionedListAdapter adapter = new SectionedListAdapter(getActivity(), R.layout.list_section_header);
                adapter.addSection(header, new ScheduleListAdapter(getActivity(), response));
                setListAdapter(adapter);
            }
        });
        VolleyHelper.getResultQueue().add(sr);
    }

    class ScheduleRequests extends AbstractJsonRequest<List<Schedule>> {
        ScheduleRequests(Listener<List<Schedule>> listener) {
            super(SCHEDULE_URL, listener);
        }

        public List<Schedule> onPostNetworkResponse(JsonReader reader) throws IOException {
            List<Schedule> results = new ArrayList<Schedule>();
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if ("header".equals(name)) {
                    header = reader.nextString();
                } else if ("schedule".equals(name)) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        results.add(readScheduleObject(reader));
                    }
                    reader.endArray();
                }
            }
            reader.endObject();
            return results;
        }
    }

    private Schedule readScheduleObject(JsonReader reader) throws IOException {
        String date = null;
        String ht = null;
        String hs = null;
        String at = null;
        String as = null;
        String status = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if ("date".equals(name)) {
                date = reader.nextString();
            } else if ("at".equals(name)) {
                at = reader.nextString();
            } else if ("as".equals(name)) {
                as = reader.nextString();
            } else if ("ht".equals(name)) {
                ht = reader.nextString();
            } else if ("hs".equals(name)) {
                hs = reader.nextString();
            } else if ("status".equals(name)) {
                status = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Schedule(date, ht, hs, at, as, status);
    }

    static class Schedule {
        public String date;
        public String ht;
        public String hs;
        public String at;
        public String as;
        public String status;

        Schedule(String date_, String ht_, String hs_, String at_, String as_, String status_) {
            date = date_;
            ht = ht_;
            hs = hs_;
            at = at_;
            as = as_;
            status = status_;
        }
    }

    private class ScheduleListAdapter extends ArrayAdapter<Schedule> {
        public ScheduleListAdapter(Context context, List<Schedule> data) {
            super(context, 0, data);
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
<<<<<<< HEAD
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder = ViewHolder.get(convertView, parent);

            Event item = getItem(position);
            holder.date.setText(sdf.format(item.fullDate));
            holder.awayTeam.setText(item.vn);

            String vs = item.vs;
            if (vs != null) {
                holder.awayScore.setText(vs);
                holder.awayScore.setVisibility(View.VISIBLE);
            } else {
                holder.awayScore.setVisibility(View.GONE);
            }

            holder.homeTeam.setText(item.hn);

            String hs = item.hs;
            if (hs != null) {
                holder.homeScore.setText(hs);
                holder.homeScore.setVisibility(View.VISIBLE);
            } else {
                holder.homeScore.setVisibility(View.GONE);
            }

            return holder.root;
=======
            View view;

            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            if (convertView == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.schedule_item, parent, false);

                holder = new ViewHolder();
                holder.date = (TextView) view.findViewById(R.id.date);
                holder.at = (TextView) view.findViewById(R.id.away_team);
                holder.as = (TextView) view.findViewById(R.id.away_score);
                holder.ht = (TextView) view.findViewById(R.id.home_team);
                holder.hs = (TextView) view.findViewById(R.id.home_score);
                holder.status = (TextView) view.findViewById(R.id.status);

                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            Schedule item = getItem(position);
            holder.date.setText(item.date);
            holder.at.setText(item.at);
            holder.as.setText(item.as);
            holder.ht.setText(item.ht);
            holder.hs.setText(item.hs);
            holder.status.setText(item.status);

            return convertView;
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
        }
    }

    static class ViewHolder {
<<<<<<< HEAD
        public final View root;
        public final TextView date;
        public final TextView awayTeam;
        public final TextView awayScore;
        public final TextView homeTeam;
        public final TextView homeScore;

        private ViewHolder(ViewGroup parent) {
            root = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item, parent, false);
            root.setTag(this);

            date = (TextView) root.findViewById(R.id.date);
            awayTeam = (TextView) root.findViewById(R.id.away_team);
            awayScore = (TextView) root.findViewById(R.id.away_score);
            homeTeam = (TextView) root.findViewById(R.id.home_team);
            homeScore  = (TextView) root.findViewById(R.id.home_score);
        }

        public static ViewHolder get(View convertView, ViewGroup parent) {
            if (convertView == null) {
                return new ViewHolder(parent);
            }
            return (ViewHolder) convertView.getTag();
        }
=======
        TextView date;
        TextView at;
        TextView as;
        TextView ht;
        TextView hs;
        TextView school;
        TextView status;
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
    }
}