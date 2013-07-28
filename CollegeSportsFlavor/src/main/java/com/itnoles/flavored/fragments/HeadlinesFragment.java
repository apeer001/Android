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

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response.Listener;
//import com.android.volley.toolbox.NetworkImageView;
import com.itnoles.flavored.activities.BrowserDetailActivity;
import com.itnoles.flavored.R;
import com.itnoles.flavored.UIUtils;
import com.itnoles.flavored.VolleyHelper;
import com.itnoles.flavored.XMLRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.itnoles.flavored.BuildConfig.NEWS_URL;

public class HeadlinesFragment extends ListFragment {
    private static final String LOG_TAG = "HeadlinesFragment";

    private boolean mDualPane;
    private int mShownCheckPosition = -1;
    private NewsListAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new NewsListAdapter(getActivity());
        setListAdapter(mAdapter);

        // Determine whether we are in single-pane or dual-pane mode by testing the visibility
        // of the detail view.
        View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null && detailsFrame.getVisibility() != View.VISIBLE) {
            detailsFrame.setVisibility(View.VISIBLE);
        }
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        loadData();
    }

    private void loadData() {
        // Start default url load with Volley.
        XMLRequest xr = new XMLRequest(NEWS_URL, new Listener<XmlPullParser>() {
            @Override
            public void onResponse(XmlPullParser response) {
                getHeadlinesResult(response);
            }
        });
        VolleyHelper.getResultQueue().add(xr);
    }

    private void getHeadlinesResult(XmlPullParser parser) {
        List<News> results = new ArrayList<News>();
        try {
            // The News that is currently being parsed
            News currentNews = null;
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if ("item".equals(name)) {
                        currentNews = new News();
                    } else if (currentNews != null) {
                        //if ("enclosure".equals(name)) {
                            //currentNews.setValue("enclosure", parser.getAttributeValue(null, "url"));
                        //} else {
                            currentNews.setValue(name, parser.nextText());
                        //}
                    }
                } else if (parser.getEventType() == XmlPullParser.END_TAG && "item".equals(parser.getName())) {
                    results.add(currentNews);
                }
            }
        } catch (XmlPullParserException e) {
            Log.w(LOG_TAG, "Malformed response for ", e);
        } catch (IOException ioe) {
            Log.w(LOG_TAG, "Problem on reading on file", ioe);
        }
        mAdapter.addAll(results);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        News news = (News) getListAdapter().getItem(position);
        String urlString = news.link;
        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(position, true);

            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                BrowserDetailFragment df = BrowserDetailFragment.newInstance(urlString);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_details, df)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
                mShownCheckPosition = position;
            }
        } else {
            Intent intent = new Intent(getActivity(), BrowserDetailActivity.class);
            intent.putExtra("url", urlString);
            startActivity(intent);
        }
    }

    private class NewsListAdapter extends ArrayAdapter<News> {
        public NewsListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            if (convertView == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.headlines_item, parent, false);

                holder = new ViewHolder();
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.date = (TextView) view.findViewById(R.id.date);
                holder.desc = (TextView) view.findViewById(R.id.desc);

                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            News news = getItem(position);
            holder.title.setText(news.title);
            holder.date.setText(news.pubDate);
            UIUtils.setTextMaybeHtml(holder.desc, news.desc);

            return view;
        }
    }

    static class News {
        public String title;
        public String link;
        public String desc;
        public String pubDate;
        //public String imageURL;

        void setValue(String key, String value) {
            if ("title".equals(key)) {
                title = value;
            } else if ("pubDate".equals(key)) {
                pubDate = value;
            } else if ("link".equals(key)) {
                link = value;
            } else if ("description".equals(key)) {
                desc = value;
            } /*else if ("enclosure".equals(key)) {
                imageURL = value;
            }*/
        }
    }

    static class ViewHolder {
        TextView title;
        TextView date;
        TextView desc;
        //NetworkImageView thumbnail;
    }
}