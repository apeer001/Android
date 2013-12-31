package com.inoles.collegesports;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.jetbrains.annotations.Nullable;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A fragment representing a list of Items.
 *
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 */
public class ScheduleFragment extends Fragment {
    private static final String LOG_TAG = "ScheduleFragment";

    static class Event {
        private final String date;
        String HomeTeam;
        String AwayTeam;
        String HomeScore;
        String AwayScore;
        String Time;

        public Event(String dateString) {
            date = dateString;
        }

        public void setTime(String timeString) {
            Date time = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            try {
                time = sdf.parse(date.concat(timeString));
            } catch (ParseException pe) {
                Log.w(LOG_TAG, "Can't parse this date", pe);
            }

            if (time == null) {
                return;
            }

            sdf.applyPattern("MMM dd 'at' hh:mma");
            Time = sdf.format(time);
        }
    }

    private static final String YEAR = "2013";

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ScheduleListAdapter mAdapter;
    private AbsListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ScheduleListAdapter(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, true);

        // Set the adapter if view is not null
        if (view != null) {
            mListView = (AbsListView) view.findViewById(android.R.id.list);
            mListView.setAdapter(mAdapter);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // if getActivity is null, return early for unfortunate situations
        if (getActivity() == null) {
            return;
        }

        View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null) {
            detailsFrame.setVisibility(View.GONE);
        }

        Ion.with(getActivity(), BuildConfig.SCHEDULE_URL).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String s) {
                load(s);
            }
        });
    }

    private void load(String xmlString) {
        StringReader sr = new StringReader(xmlString);
        try {
            XmlPullParser parser = ParserUtils.newPullParser(sr);
            List<Event> results = new ArrayList<>();
            // The Event that is currently being parsed
            Event currentEvents = null;
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if ("current_events".equals(name)
                            && !YEAR.equals(parser.getAttributeValue(null, "academicYear"))) {
                        break;
                    }

                    if ("event_date".equals(name)) {
                        currentEvents = new Event(parser.getAttributeValue(null, "date"));
                    } else if ("event".equals(name) && currentEvents != null) {
                        currentEvents.setTime(parser.getAttributeValue(null, "eastern_time"));
                        currentEvents.HomeTeam = parser.getAttributeValue(null, "hn");
                        currentEvents.HomeScore = parser.getAttributeValue(null, "hs");
                        currentEvents.AwayTeam = parser.getAttributeValue(null, "vn");
                        currentEvents.AwayScore = parser.getAttributeValue(null, "vs");
                    }
                } else if (parser.getEventType() == XmlPullParser.END_TAG
                        && "event_date".equals(name)) {
                    results.add(currentEvents);
                }
            }
            mAdapter.addAll(results);
        } catch (IOException | XmlPullParserException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            sr.close();
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();
        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    class ScheduleListAdapter extends ArrayAdapter<Event> {
        private LayoutInflater mInflater;

        public ScheduleListAdapter(Context context) {
            super(context, 0);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Nullable
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.schedule_item, parent, false);

                holder = new ViewHolder();
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.awayTeam = (TextView) convertView.findViewById(R.id.away_team);
                holder.awayScore = (TextView) convertView.findViewById(R.id.away_score);
                holder.homeTeam = (TextView) convertView.findViewById(R.id.home_team);
                holder.homeScore = (TextView) convertView.findViewById(R.id.home_score);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Event item = getItem(position);

            holder.date.setText(item.Time);
            holder.homeTeam.setText(item.HomeTeam);
            holder.awayTeam.setText(item.AwayTeam);

            String vs = item.AwayScore;
            if (vs == null) {
                holder.awayScore.setVisibility(View.GONE);
            } else {
                holder.awayScore.setText(vs);
                holder.awayScore.setVisibility(View.VISIBLE);
            }

            String hs = item.HomeScore;
            if (hs == null) {
                holder.homeScore.setVisibility(View.GONE);
            } else {
                holder.homeScore.setText(hs);
                holder.homeScore.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        private class ViewHolder {
            TextView date;
            TextView awayTeam;
            TextView awayScore;
            TextView homeTeam;
            TextView homeScore;
        }
    }
}
