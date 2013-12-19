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

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class HeadlinesFragment extends ListFragment {
    static class News {
        String Title;
        String Link;
        String PubDate;
        String ImageURL;

        void setValue(String key, XmlPullParser parser) throws XmlPullParserException, IOException {
            switch (key) {
                case "title":
                    Title = parser.nextText();
                    break;
                case "pubDate":
                    PubDate = parser.nextText();
                    break;
                case "link":
                    Link = parser.nextText();
                    break;
                case "enclosure":
                    ImageURL = parser.getAttributeValue(null, "url");
                    break;
                default:
            }
        }
    }

    private static final String LOG_TAG = "HeadlinesFragment";

    private boolean mDualPane;
    private int mShownCheckPosition = -1;
    private NewsListAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // if getActivity is null, return early for unfortunate situations
        if (getActivity() == null) {
            return;
        }

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

        if (mDualPane && getListView() != null) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        Ion.with(getActivity(), BuildConfig.NEWS_URL).asString().setCallback(new FutureCallback<String>() {
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
            List<News> results = new ArrayList<>();
            // The News that is currently being parsed
            News currentNews = null;
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if ("item".equals(name)) {
                        currentNews = new News();
                    } else if (currentNews != null) {
                        currentNews.setValue(name, parser);
                    }
                } else if (parser.getEventType() == XmlPullParser.END_TAG && "item".equals(name)) {
                    results.add(currentNews);
                }
            }
            mAdapter.addAll(results);
        } catch (IOException | XmlPullParserException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            sr.close();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        News news = mAdapter.getItem(position);
        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            if (getListView() != null) {
                getListView().setItemChecked(position, true);
            }

            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                BrowserDetailFragment df = BrowserDetailFragment.newInstance(news.Link);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                if (getFragmentManager() == null) {
                    return;
                }
                getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_details, df)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
                mShownCheckPosition = position;
            }
        } else {
            Intent intent = new Intent(getActivity(), BrowserDetailActivity.class);
            intent.putExtra("url", news.Link);
            startActivity(intent);
        }
    }

    class NewsListAdapter extends ArrayAdapter<News> {
        private LayoutInflater mInflater;

        public NewsListAdapter(Context context) {
            super(context, 0);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.simple_list_item_3, parent, false);

                holder = new ViewHolder();
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            News item = getItem(position);

            ImageView iv = (ImageView) convertView.findViewById(R.id.thumbnail);
            Ion.with(iv).load(item.ImageURL);

            holder.date.setText(item.PubDate);
            holder.title.setText(item.Title);

            return convertView;
        }

        private class ViewHolder {
            TextView date;
            TextView title;
        }
    }
}