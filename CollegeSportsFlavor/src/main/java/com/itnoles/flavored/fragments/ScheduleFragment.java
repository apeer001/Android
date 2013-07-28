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
import android.content.Context;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.itnoles.flavored.JsonRequest;
import com.itnoles.flavored.R;
import com.itnoles.flavored.SectionedListAdapter;
import com.itnoles.flavored.VolleyHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.itnoles.flavored.BuildConfig.SCHEDULE_URL;

public class ScheduleFragment extends ListFragment {
    private static final String LOG_TAG = "ScheduleFragment";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListShownNoAnimation(true);

        // If this is under tablet, hide detail view.
        View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null) {
            detailsFrame.setVisibility(View.GONE);
        }

        JsonRequest jr = new JsonRequest(SCHEDULE_URL, new Listener<JsonReader>() {
            @Override
            public void onResponse(JsonReader response) {
                getScheduleResult(response);
            }
        });
        VolleyHelper.getResultQueue().add(jr);
    }

    private void getScheduleResult(JsonReader reader) {
        String header = null;
        List<Schedule> results = new ArrayList<Schedule>();
        try {
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
        } catch (IOException e) {
           Log.w(LOG_TAG, "Problem on reading on file", e);
        } finally {
            try {
                reader.close();
            } catch (IOException ioe) {
                Log.w(LOG_TAG, "Can't close JsonReader", ioe);
            }
        }

        // The SectionedListAdapter is going to show schedule header for overall and conference records
        SectionedListAdapter adapter = new SectionedListAdapter(getActivity(), R.layout.list_section_header);
        adapter.addSection(header, new ScheduleListAdapter(getActivity(), results));
        setListAdapter(adapter);
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
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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
        }
    }

    static class ViewHolder {
        TextView date;
        TextView at;
        TextView as;
        TextView ht;
        TextView hs;
        TextView school;
        TextView status;
    }
}