package com.inoles.collegesports;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A fragment representing a list of schedule items.
 */
public class ScheduleFragment extends ListFragment {
    static class Event {
        String HomeTeam;
        String AwayTeam;
        String HomeScore;
        String AwayScore;
        Date Time;
    }

    private static final String LOG_TAG = "ScheduleFragment";
    private static final String YEAR = "2013";

    private List<Event> mResult = new ArrayList<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ScheduleListAdapter adapter = new ScheduleListAdapter(getActivity());
        setListAdapter(adapter);

        StringReaderRequest xr = new StringReaderRequest(BuildConfig.SCHEDULE_URL, new Response.Listener<StringReader>() {
            @Override
            public void onResponse(StringReader response) {
                load(response);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOG_TAG, error.getMessage());
            }
        });
        xr.setTag(this);

        MainActivity.sQueue.add(xr);
    }

    void load(StringReader reader) {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(reader);

            // The Event that is currently being parsed
            Event currentEvents = null;
            String tempDate = null;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if ("current_events".equals(name)
                            && !YEAR.equals(parser.getAttributeValue(null, "academicYear"))) {
                        break;
                    }

                    if ("event_date".equals(name)) {
                        currentEvents = new Event();
                        tempDate = parser.getAttributeValue(null, "date");
                    } else if ("event".equals(name) && currentEvents != null) {
                        if (tempDate != null) {
                            currentEvents.Time = sdf.parse(
                                    tempDate.concat(parser.getAttributeValue(null, "eastern_time")),
                                    new ParsePosition(0)
                            );
                        }
                        currentEvents.HomeTeam = parser.getAttributeValue(null, "hn");
                        currentEvents.HomeScore = parser.getAttributeValue(null, "hs");
                        currentEvents.AwayTeam = parser.getAttributeValue(null, "vn");
                        currentEvents.AwayScore = parser.getAttributeValue(null, "vs");
                    }
                } else if (parser.getEventType() == XmlPullParser.END_TAG
                        && "event_date".equals(name)) {
                    mResult.add(currentEvents);
                }
            }
        } catch (IOException | XmlPullParserException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            reader.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mResult.clear();
        mResult = null;
    }

    class ScheduleListAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private final SimpleDateFormat mDateFormat;

        ScheduleListAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mDateFormat = new SimpleDateFormat("MMM dd 'at' hh:mma");
        }

        @Override
        public int getCount() {
            return mResult.size();
        }

        @Override
        public Event getItem(int position) {
            return mResult.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Nullable
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unnecessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to re-inflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.schedule_item, parent, false);

                // Creates a ViewHolder and store references to the three children views
                // we want to bind data to.
                holder = new ViewHolder(convertView);
            } else {
                // Get the ViewHolder back to get fast access to one TextView
                // and two ScheduleTextView
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            Event item = getItem(position);
            holder.date.setText(mDateFormat.format(item.Time));
            holder.homeTeam.setText(item.HomeTeam, item.HomeScore);
            holder.awayTeam.setText(item.AwayTeam, item.AwayScore);

            return convertView;
        }

        private class ViewHolder {
            final TextView date;
            final ScheduleTextView awayTeam;
            final ScheduleTextView homeTeam;

            public ViewHolder(View view) {
                date = (TextView) view.findViewById(R.id.date);
                awayTeam = (ScheduleTextView) view.findViewById(R.id.away_team);
                homeTeam = (ScheduleTextView) view.findViewById(R.id.home_team);
                view.setTag(this);
            }
        }
    }
}
